package cn.iocoder.yudao.module.restaurant.service.dividend.flowable;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.dividend.approve.vo.DividendApproveTaskPageReqVO;
import cn.iocoder.yudao.module.restaurant.controller.admin.dividend.approve.vo.DividendApproveTaskRespVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.dividend.DividendPeriodDO;
import cn.iocoder.yudao.module.restaurant.dal.mysql.dividend.DividendPeriodMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants.*;
import static cn.iocoder.yudao.module.restaurant.framework.flowable.RestaurantDividendFlowableConstants.*;

/**
 * 餐饮分红 Flowable 工作流 Service 实现。
 *
 * <p>职责边界：</p>
 * <ul>
 *     <li>Flowable 负责流程实例、审批任务、候选人、任务完成与历史记录。</li>
 *     <li>餐饮业务表负责账期状态、审批记录、操作日志和业务通知。</li>
 * </ul>
 *
 * @author zhangyoming
 */
@Slf4j
@Service
@Validated
public class RestaurantDividendFlowableServiceImpl implements RestaurantDividendFlowableService {

    @Resource
    private RuntimeService runtimeService;

    @Resource
    private TaskService taskService;

    @Resource
    private DividendPeriodMapper dividendPeriodMapper;

    @Override
    public RestaurantDividendApproveProcessInfo startApproveProcess(DividendPeriodDO period,
                                                                     Long recordId,
                                                                     Long submitUserId,
                                                                     Collection<Long> approveUserIds) {
        if (period == null || period.getId() == null || recordId == null || CollUtil.isEmpty(approveUserIds)) {
            throw exception(DIVIDEND_FLOWABLE_START_FAIL);
        }

        try {
            Map<String, Object> variables = buildStartVariables(period, recordId, submitUserId, approveUserIds);
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
                    PROCESS_KEY_DIVIDEND_APPROVE,
                    String.valueOf(period.getId()),
                    variables);

            Task task = taskService.createTaskQuery()
                    .processInstanceId(processInstance.getId())
                    .taskDefinitionKey(TASK_KEY_APPROVE)
                    .active()
                    .singleResult();
            if (task == null) {
                throw exception(DIVIDEND_FLOWABLE_TASK_NOT_EXISTS);
            }

            // 双保险：BPMN 已使用 candidateUsers 表达式，这里再显式添加候选人，避免表达式解析差异导致待办为空。
            for (Long approveUserId : approveUserIds) {
                if (approveUserId != null) {
                    taskService.addCandidateUser(task.getId(), String.valueOf(approveUserId));
                }
            }

            log.info("[startApproveProcess][启动餐饮分红 Flowable 审批流程成功，periodId({}) recordId({}) processInstanceId({}) taskId({}) approveUserIds({})]",
                    period.getId(), recordId, processInstance.getId(), task.getId(), approveUserIds);
            return new RestaurantDividendApproveProcessInfo(processInstance.getId(), task.getId());
        } catch (Exception ex) {
            log.error("[startApproveProcess][启动餐饮分红 Flowable 审批流程失败，periodId({}) recordId({}) approveUserIds({})]",
                    period.getId(), recordId, approveUserIds, ex);
            if (ex instanceof cn.iocoder.yudao.framework.common.exception.ServiceException serviceException) {
                throw serviceException;
            }
            throw exception(DIVIDEND_FLOWABLE_START_FAIL);
        }
    }

    @Override
    public RestaurantDividendApproveProcessInfo completeApproveTask(Long periodId,
                                                                    Long approveUserId,
                                                                    Boolean approved,
                                                                    String approveReason) {
        Task task = getActiveApproveTask(periodId, approveUserId);
        if (task == null) {
            throw exception(DIVIDEND_FLOWABLE_TASK_NOT_EXISTS);
        }

        try {
            if (task.getAssignee() == null) {
                taskService.claim(task.getId(), String.valueOf(approveUserId));
            } else if (!String.valueOf(approveUserId).equals(task.getAssignee())) {
                throw exception(DIVIDEND_APPROVE_USER_NOT_ALLOWED);
            }

            Map<String, Object> variables = new HashMap<>();
            variables.put(VAR_APPROVED, Boolean.TRUE.equals(approved));
            variables.put(VAR_APPROVE_USER_ID, approveUserId);
            variables.put(VAR_APPROVE_REASON, approveReason);
            taskService.complete(task.getId(), variables);

            log.info("[completeApproveTask][完成餐饮分红 Flowable 审批任务，periodId({}) taskId({}) approveUserId({}) approved({})]",
                    periodId, task.getId(), approveUserId, approved);
            return new RestaurantDividendApproveProcessInfo(task.getProcessInstanceId(), task.getId());
        } catch (Exception ex) {
            log.error("[completeApproveTask][完成餐饮分红 Flowable 审批任务失败，periodId({}) taskId({}) approveUserId({}) approved({})]",
                    periodId, task.getId(), approveUserId, approved, ex);
            if (ex instanceof cn.iocoder.yudao.framework.common.exception.ServiceException serviceException) {
                throw serviceException;
            }
            throw exception(DIVIDEND_FLOWABLE_TASK_COMPLETE_FAIL);
        }
    }

    @Override
    public boolean hasActiveApproveTask(Long periodId, Long approveUserId) {
        return getActiveApproveTask(periodId, approveUserId) != null;
    }

    @Override
    public PageResult<DividendApproveTaskRespVO> getTodoTaskPage(DividendApproveTaskPageReqVO pageReqVO, Long loginUserId) {
        if (loginUserId == null) {
            return PageResult.empty();
        }

        TaskQuery query = taskService.createTaskQuery()
                .processDefinitionKey(PROCESS_KEY_DIVIDEND_APPROVE)
                .taskDefinitionKey(TASK_KEY_APPROVE)
                .taskCandidateOrAssigned(String.valueOf(loginUserId))
                .active();
        if (pageReqVO.getStoreId() != null) {
            query.processVariableValueEquals(VAR_STORE_ID, pageReqVO.getStoreId());
        }
        if (pageReqVO.getPeriodMonth() != null) {
            query.processVariableValueEquals(VAR_PERIOD_MONTH, pageReqVO.getPeriodMonth());
        }

        long total = query.count();
        if (total == 0) {
            return PageResult.empty();
        }

        int offset = (pageReqVO.getPageNo() - 1) * pageReqVO.getPageSize();
        List<Task> tasks = query.orderByTaskCreateTime().desc().listPage(offset, pageReqVO.getPageSize());
        List<DividendApproveTaskRespVO> list = tasks.stream()
                .map(this::buildTaskRespVO)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return new PageResult<>(list, total);
    }

    private Task getActiveApproveTask(Long periodId, Long approveUserId) {
        if (periodId == null || approveUserId == null) {
            return null;
        }
        return taskService.createTaskQuery()
                .processDefinitionKey(PROCESS_KEY_DIVIDEND_APPROVE)
                .processInstanceBusinessKey(String.valueOf(periodId))
                .taskDefinitionKey(TASK_KEY_APPROVE)
                .taskCandidateOrAssigned(String.valueOf(approveUserId))
                .active()
                .singleResult();
    }

    private Map<String, Object> buildStartVariables(DividendPeriodDO period,
                                                    Long recordId,
                                                    Long submitUserId,
                                                    Collection<Long> approveUserIds) {
        Map<String, Object> variables = new HashMap<>();
        variables.put(VAR_PERIOD_ID, period.getId());
        variables.put(VAR_RECORD_ID, recordId);
        variables.put(VAR_STORE_ID, period.getStoreId());
        variables.put(VAR_DEPT_ID, period.getDeptId());
        variables.put(VAR_PERIOD_MONTH, period.getPeriodMonth());
        variables.put(VAR_SUBMIT_USER_ID, submitUserId);
        variables.put(VAR_APPROVE_USER_IDS, approveUserIds.stream()
                .filter(Objects::nonNull)
                .map(String::valueOf)
                .collect(Collectors.joining(",")));
        return variables;
    }

    private DividendApproveTaskRespVO buildTaskRespVO(Task task) {
        Map<String, Object> variables = taskService.getVariables(task.getId());
        Long periodId = toLong(variables.get(VAR_PERIOD_ID));
        DividendPeriodDO period = periodId != null ? dividendPeriodMapper.selectById(periodId) : null;

        DividendApproveTaskRespVO respVO = new DividendApproveTaskRespVO();
        respVO.setTaskId(task.getId());
        respVO.setTaskName(task.getName());
        respVO.setProcessInstanceId(task.getProcessInstanceId());
        respVO.setRecordId(toLong(variables.get(VAR_RECORD_ID)));
        respVO.setPeriodId(periodId);
        respVO.setStoreId(toLong(variables.get(VAR_STORE_ID)));
        respVO.setDeptId(toLong(variables.get(VAR_DEPT_ID)));
        respVO.setPeriodMonth((String) variables.get(VAR_PERIOD_MONTH));
        respVO.setSubmitUserId(toLong(variables.get(VAR_SUBMIT_USER_ID)));
        if (task.getCreateTime() != null) {
            respVO.setTaskCreateTime(LocalDateTime.ofInstant(task.getCreateTime().toInstant(), ZoneId.systemDefault()));
        }
        if (period != null) {
            respVO.setProfitAmount(period.getProfitAmount());
            respVO.setDistributableProfit(period.getDistributableProfit());
        }
        return respVO;
    }

    private Long toLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Long longValue) {
            return longValue;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.valueOf(String.valueOf(value));
    }

}

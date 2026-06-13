package cn.iocoder.yudao.module.restaurant.service.dividend;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.restaurant.controller.admin.dividend.approve.vo.DividendApproveAuditReqVO;
import cn.iocoder.yudao.module.restaurant.controller.admin.dividend.approve.vo.DividendApproveRecordPageReqVO;
import cn.iocoder.yudao.module.restaurant.controller.admin.dividend.approve.vo.DividendApproveRecordRespVO;
import cn.iocoder.yudao.module.restaurant.controller.admin.dividend.approve.vo.DividendApproveSubmitReqVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.dividend.DividendApproveRecordDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.dividend.DividendPeriodDO;
import cn.iocoder.yudao.module.restaurant.dal.mysql.dividend.DividendApproveRecordMapper;
import cn.iocoder.yudao.module.restaurant.dal.mysql.dividend.DividendPeriodMapper;
import cn.iocoder.yudao.module.restaurant.enums.DividendApproveStatusEnum;
import cn.iocoder.yudao.module.restaurant.enums.DividendPeriodStatusEnum;
import cn.iocoder.yudao.module.restaurant.enums.RestaurantOperateTypeEnum;
import cn.iocoder.yudao.module.restaurant.service.notify.RestaurantNotifyService;
import cn.iocoder.yudao.module.restaurant.service.operatelog.RestaurantOperateLogService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants.*;

/**
 * 分红审批 Service 实现类。
 *
 * <p>审批主流程只负责提交、审核、状态流转和日志通知；
 * 审批人解析统一交给 {@link DividendApproveUserService}，避免继续写死 userId。</p>
 *
 * @author zhangyoming
 */
@Service
@Validated
public class DividendApproveServiceImpl implements DividendApproveService {

    @Resource
    private DividendApproveRecordMapper dividendApproveRecordMapper;

    @Resource
    private DividendPeriodMapper dividendPeriodMapper;

    @Resource
    private RestaurantOperateLogService restaurantOperateLogService;

    @Resource
    private RestaurantNotifyService restaurantNotifyService;

    @Resource
    private DividendApproveUserService dividendApproveUserService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submitDividendApprove(DividendApproveSubmitReqVO submitReqVO) {
        DividendPeriodDO period = validateDividendPeriodExists(submitReqVO.getPeriodId());

        // 只有“已确认”或“审批驳回”的账期允许提交审批
        if (!DividendPeriodStatusEnum.CONFIRMED.getStatus().equals(period.getStatus())
                && !DividendPeriodStatusEnum.REJECTED.getStatus().equals(period.getStatus())) {
            throw exception(DIVIDEND_APPROVE_PERIOD_STATUS_NOT_ALLOW_SUBMIT);
        }

        // 同一个账期不能重复存在审批中的记录
        DividendApproveRecordDO approvingRecord = dividendApproveRecordMapper.selectApprovingByPeriodId(period.getId());
        if (approvingRecord != null) {
            throw exception(DIVIDEND_APPROVE_RECORD_APPROVING_EXISTS);
        }

        Long submitUserId = getLoginUserIdSafe();
        Collection<Long> approveUserIds = dividendApproveUserService.getApproveUserIds(period, submitUserId);
        if (CollUtil.isEmpty(approveUserIds)) {
            throw exception(DIVIDEND_APPROVE_USER_NOT_CONFIGURED);
        }

        Integer beforeStatus = period.getStatus();
        Integer afterStatus = DividendPeriodStatusEnum.APPROVING.getStatus();
        LocalDateTime now = LocalDateTime.now();

        // 1. 创建审批记录
        DividendApproveRecordDO record = DividendApproveRecordDO.builder()
                .periodId(period.getId())
                .storeId(period.getStoreId())
                .deptId(period.getDeptId())
                .periodMonth(period.getPeriodMonth())
                .approveStatus(DividendApproveStatusEnum.APPROVING.getStatus())
                .submitUserId(submitUserId)
                .submitTime(now)
                .remark(submitReqVO.getRemark())
                .build();
        dividendApproveRecordMapper.insert(record);

        // 2. 修改账期状态为审批中
        DividendPeriodDO updateObj = new DividendPeriodDO();
        updateObj.setId(period.getId());
        updateObj.setStatus(afterStatus);
        dividendPeriodMapper.updateById(updateObj);

        // 3. 记录操作日志
        period.setStatus(afterStatus);
        restaurantOperateLogService.createDividendPeriodLog(period,
                RestaurantOperateTypeEnum.DIVIDEND_SUBMIT_APPROVE.getType(),
                beforeStatus,
                afterStatus,
                "提交分红发放审批，审批人：" + approveUserIds);

        // 4. WebSocket 通知审批人
        restaurantNotifyService.notifyDividendApproveSubmit(period, approveUserIds);

        return record.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditDividendApprove(DividendApproveAuditReqVO auditReqVO) {
        DividendPeriodDO period = validateDividendPeriodExists(auditReqVO.getPeriodId());

        if (!DividendPeriodStatusEnum.APPROVING.getStatus().equals(period.getStatus())) {
            throw exception(DIVIDEND_APPROVE_PERIOD_STATUS_NOT_ALLOW_AUDIT);
        }

        DividendApproveRecordDO record = dividendApproveRecordMapper.selectApprovingByPeriodId(period.getId());
        if (record == null) {
            throw exception(DIVIDEND_APPROVE_RECORD_NOT_APPROVING);
        }

        Long approveUserId = getLoginUserIdSafe();
        if (!dividendApproveUserService.isApproveUser(period, record.getSubmitUserId(), approveUserId)) {
            throw exception(DIVIDEND_APPROVE_USER_NOT_ALLOWED);
        }

        Integer beforeStatus = period.getStatus();
        Integer afterStatus = Boolean.TRUE.equals(auditReqVO.getApproved())
                ? DividendPeriodStatusEnum.APPROVED.getStatus()
                : DividendPeriodStatusEnum.REJECTED.getStatus();

        Integer approveStatus = Boolean.TRUE.equals(auditReqVO.getApproved())
                ? DividendApproveStatusEnum.APPROVED.getStatus()
                : DividendApproveStatusEnum.REJECTED.getStatus();

        LocalDateTime now = LocalDateTime.now();

        // 1. 更新审批记录
        DividendApproveRecordDO updateRecord = new DividendApproveRecordDO();
        updateRecord.setId(record.getId());
        updateRecord.setApproveStatus(approveStatus);
        updateRecord.setApproveUserId(approveUserId);
        updateRecord.setApproveTime(now);
        updateRecord.setApproveReason(auditReqVO.getApproveReason());
        dividendApproveRecordMapper.updateById(updateRecord);

        // 2. 更新分红账期状态
        DividendPeriodDO updatePeriod = new DividendPeriodDO();
        updatePeriod.setId(period.getId());
        updatePeriod.setStatus(afterStatus);
        dividendPeriodMapper.updateById(updatePeriod);

        // 3. 记录操作日志
        period.setStatus(afterStatus);
        restaurantOperateLogService.createDividendPeriodLog(period,
                Boolean.TRUE.equals(auditReqVO.getApproved())
                        ? RestaurantOperateTypeEnum.DIVIDEND_APPROVE_PASS.getType()
                        : RestaurantOperateTypeEnum.DIVIDEND_APPROVE_REJECT.getType(),
                beforeStatus,
                afterStatus,
                Boolean.TRUE.equals(auditReqVO.getApproved()) ? "分红审批通过" : "分红审批驳回");

        // 4. WebSocket 通知提交人
        if (Boolean.TRUE.equals(auditReqVO.getApproved())) {
            restaurantNotifyService.notifyDividendApprovePass(period, record.getSubmitUserId());
        } else {
            restaurantNotifyService.notifyDividendApproveReject(period, record.getSubmitUserId(), auditReqVO.getApproveReason());
        }
    }

    @Override
    public PageResult<DividendApproveRecordDO> getApproveRecordPage(DividendApproveRecordPageReqVO pageReqVO) {
        return dividendApproveRecordMapper.selectPage(pageReqVO);
    }

    @Override
    public List<DividendApproveRecordRespVO> buildApproveRecordRespList(List<DividendApproveRecordDO> list) {
        if (CollUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        return list.stream().map(record -> {
            DividendApproveRecordRespVO respVO = BeanUtils.toBean(record, DividendApproveRecordRespVO.class);
            respVO.setApproveStatusName(DividendApproveStatusEnum.getNameByStatus(record.getApproveStatus()));
            return respVO;
        }).collect(Collectors.toList());
    }

    private DividendPeriodDO validateDividendPeriodExists(Long id) {
        DividendPeriodDO period = dividendPeriodMapper.selectById(id);
        if (period == null) {
            throw exception(DIVIDEND_PERIOD_NOT_EXISTS);
        }
        return period;
    }

    private Long getLoginUserIdSafe() {
        try {
            return SecurityFrameworkUtils.getLoginUserId();
        } catch (Exception ex) {
            return null;
        }
    }

}

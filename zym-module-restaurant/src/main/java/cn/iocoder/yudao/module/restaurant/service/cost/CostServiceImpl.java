package cn.iocoder.yudao.module.restaurant.service.cost;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.restaurant.controller.admin.cost.vo.*;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.cost.CostDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.store.StoreDO;
import cn.iocoder.yudao.module.restaurant.dal.mysql.cost.CostMapper;
import cn.iocoder.yudao.module.restaurant.dal.mysql.dividend.DividendPeriodMapper;
import cn.iocoder.yudao.module.restaurant.enums.CostStatusEnum;
import cn.iocoder.yudao.module.restaurant.enums.DividendPeriodStatusEnum;
import cn.iocoder.yudao.module.restaurant.enums.RestaurantNotifyTypeEnum;
import cn.iocoder.yudao.module.restaurant.enums.RestaurantOperateBizTypeConstants;
import cn.iocoder.yudao.module.restaurant.enums.RestaurantOperateTypeEnum;
import cn.iocoder.yudao.module.restaurant.service.notify.RestaurantNotifyService;
import cn.iocoder.yudao.module.restaurant.service.operatelog.RestaurantOperateLogService;
import cn.iocoder.yudao.module.restaurant.service.store.StoreService;
import cn.iocoder.yudao.module.system.service.dict.DictDataService;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants.*;

/**
 * 成本支出 Service 实现类
 *
 * @author zhangyoming
 */
@Service
@Validated
public class CostServiceImpl implements CostService {

    /**
     * 成本类型字典
     */
    private static final String DICT_TYPE_COST_TYPE = "restaurant_cost_type";

    @Resource
    private CostMapper costMapper;

    @Resource
    private StoreService storeService;

    @Resource
    private DictDataService dictDataService;

    @Resource
    private DividendPeriodMapper dividendPeriodMapper;

    @Resource
    private RestaurantOperateLogService restaurantOperateLogService;

    @Resource
    private RestaurantNotifyService restaurantNotifyService;

    @Override
    public Long createCost(CostSaveReqVO createReqVO) {
        validateCostForCreateOrUpdate(null, createReqVO);

        StoreDO store = storeService.getStore(createReqVO.getStoreId());

        CostDO cost = BeanUtils.toBean(createReqVO, CostDO.class);
        cost.setDeptId(store.getDeptId());
        costMapper.insert(cost);
        return cost.getId();
    }
    @Override
    public void updateCost(CostSaveReqVO updateReqVO) {
        CostDO oldCost = validateCostExists(updateReqVO.getId());

        if (CostStatusEnum.CONFIRMED.getStatus().equals(oldCost.getStatus())) {
            throw exception(COST_CONFIRMED_NOT_ALLOW_UPDATE);
        }

        validateCostForCreateOrUpdate(updateReqVO.getId(), updateReqVO);

        StoreDO store = storeService.getStore(updateReqVO.getStoreId());

        CostDO updateObj = BeanUtils.toBean(updateReqVO, CostDO.class);
        updateObj.setDeptId(store.getDeptId());
        costMapper.updateById(updateObj);
    }

    @Override
    public void deleteCost(Long id) {
        CostDO cost = validateCostExists(id);

        // 已确认成本不允许删除，避免历史利润数据丢失
        if (CostStatusEnum.CONFIRMED.getStatus().equals(cost.getStatus())) {
            throw exception(COST_CONFIRMED_NOT_ALLOW_DELETE);
        }

        costMapper.deleteById(id);
    }

    @Override
    public void confirmCost(Long id) {
        CostDO cost = validateCostExists(id);

        if (!CostStatusEnum.WAIT_CONFIRM.getStatus().equals(cost.getStatus())) {
            throw exception(COST_STATUS_NOT_ALLOW_CONFIRM);
        }

        CostDO updateObj = new CostDO();
        updateObj.setId(id);
        updateObj.setStatus(CostStatusEnum.CONFIRMED.getStatus());
        costMapper.updateById(updateObj);
    }

    @Override
    public void cancelCost(Long id) {
        CostDO cost = validateCostExists(id);

        if (CostStatusEnum.CANCELED.getStatus().equals(cost.getStatus())) {
            throw exception(COST_STATUS_NOT_ALLOW_CANCEL);
        }

        // 已参与分红账期的成本不允许作废
        validateCostNotInDividendPeriod(cost.getStoreId(), cost.getBizDate());

        CostDO updateObj = new CostDO();
        updateObj.setId(id);
        updateObj.setStatus(CostStatusEnum.CANCELED.getStatus());
        costMapper.updateById(updateObj);
    }
    /**
     * 校验成本支出是否已经参与分红账期。
     *
     * 注意：这里不要调用 DividendPeriodService，避免 CostServiceImpl 和
     * DividendPeriodServiceImpl 互相注入导致 Spring 循环依赖。
     */
    private void validateCostNotInDividendPeriod(Long storeId, LocalDate bizDate) {
        Long count = dividendPeriodMapper.selectCountByStoreIdAndBizDate(
                storeId,
                bizDate,
                List.of(
                        DividendPeriodStatusEnum.GENERATED.getStatus(),
                        DividendPeriodStatusEnum.CONFIRMED.getStatus(),
                        DividendPeriodStatusEnum.APPROVING.getStatus(),
                        DividendPeriodStatusEnum.APPROVED.getStatus(),
                        DividendPeriodStatusEnum.PAID.getStatus()
                )
        );
        if (count != null && count > 0) {
            throw exception(COST_ALREADY_IN_DIVIDEND_PERIOD);
        }
    }

    private void validateCostForCreateOrUpdate(Long id, CostSaveReqVO reqVO) {
        // 校验自己存在
        if (id != null) {
            validateCostExists(id);
        }

        // 校验门店存在且启用
        storeService.validateStoreList(Collections.singleton(reqVO.getStoreId()));

        // 校验成本金额
        validateCostAmount(reqVO.getAmount());

        // 校验成本类型是否为有效字典值
        validateCostType(reqVO.getCostType());

        // 校验同一门店、同一日期、同一成本类型不能重复
        validateCostUnique(id, reqVO.getStoreId(), reqVO.getBizDate(), reqVO.getCostType());
    }
    private void validateCostImportBasic(CostImportExcelVO importVO) {
        if (importVO == null) {
            throw new IllegalArgumentException("导入数据不能为空");
        }
        if (StrUtil.isBlank(importVO.getStoreCode())) {
            throw new IllegalArgumentException("门店编码不能为空");
        }
        if (importVO.getBizDate() == null) {
            throw new IllegalArgumentException("成本日期不能为空");
        }
        if (StrUtil.isBlank(importVO.getCostType())) {
            throw new IllegalArgumentException("成本类型不能为空");
        }
        if (importVO.getAmount() == null || importVO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("成本金额必须大于 0");
        }
    }
    private StoreDO validateImportStore(String storeCode) {
        StoreDO store = storeService.getStoreByCode(storeCode);
        if (store == null) {
            throw new IllegalArgumentException("门店不存在");
        }
        if (!CommonStatusEnum.ENABLE.getStatus().equals(store.getStatus())) {
            throw new IllegalArgumentException("门店已禁用");
        }
        return store;
    }
    private void validateImportCostType(String costType) {
        try {
            dictDataService.validateDictDataList(DICT_TYPE_COST_TYPE, java.util.Collections.singleton(costType));
        } catch (Exception ex) {
            throw new IllegalArgumentException("成本类型字典值不存在或已禁用");
        }
    }
    private String buildImportRowKey(int rowNum, CostImportExcelVO importVO) {
        if (importVO == null) {
            return "第" + rowNum + "行";
        }
        return "第" + rowNum + "行：" +
                StrUtil.blankToDefault(importVO.getStoreCode(), "空门店") + "-" +
                importVO.getBizDate() + "-" +
                StrUtil.blankToDefault(importVO.getCostType(), "空成本类型");
    }
    private String buildCostBusinessKey(Long storeId, java.time.LocalDate bizDate, String costType) {
        return storeId + "_" + bizDate + "_" + costType;
    }
    private Long getLoginUserIdSafe() {
        try {
            return SecurityFrameworkUtils.getLoginUserId();
        } catch (Exception ex) {
            return null;
        }
    }
    @VisibleForTesting
    CostDO validateCostExists(Long id) {
        if (id == null) {
            throw exception(COST_NOT_EXISTS);
        }
        CostDO cost = costMapper.selectById(id);
        if (cost == null) {
            throw exception(COST_NOT_EXISTS);
        }
        return cost;
    }

    @VisibleForTesting
    void validateCostAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw exception(COST_AMOUNT_INVALID);
        }
    }

    @VisibleForTesting
    void validateCostType(String costType) {
        dictDataService.validateDictDataList(DICT_TYPE_COST_TYPE, Collections.singleton(costType));
    }

    @VisibleForTesting
    void validateCostUnique(Long id, Long storeId, LocalDate bizDate, String costType) {
        CostDO cost = costMapper.selectByStoreIdAndBizDateAndCostType(storeId, bizDate, costType);
        if (cost == null) {
            return;
        }
        if (id == null) {
            throw exception(COST_DUPLICATE);
        }
        if (!cost.getId().equals(id)) {
            throw exception(COST_DUPLICATE);
        }
    }

    @Override
    public CostDO getCost(Long id) {
        return costMapper.selectById(id);
    }

    @Override
    public PageResult<CostDO> getCostPage(CostPageReqVO pageReqVO) {
        return costMapper.selectPage(pageReqVO);
    }

    @Override
    public List<CostDO> getCostList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return costMapper.selectListByIds(ids);
    }

    @Override
    public List<CostDO> getCostListByStoreIdAndDateRange(Long storeId, LocalDate startDate, LocalDate endDate) {
        storeService.validateStoreList(Collections.singleton(storeId));
        return costMapper.selectListByStoreIdAndBizDateBetween(storeId, startDate, endDate);
    }

    @Override
    public BigDecimal getCostSummaryAmount(Long storeId, LocalDate startDate, LocalDate endDate) {
        storeService.validateStoreList(Collections.singleton(storeId));
        return costMapper.selectSumAmountByStoreIdAndBizDateBetween(
                storeId, startDate, endDate, CostStatusEnum.CONFIRMED.getStatus());
    }

    @Override
    public CostSummaryRespVO getCostSummary(Long storeId, LocalDate startDate, LocalDate endDate) {
        BigDecimal totalAmount = getCostSummaryAmount(storeId, startDate, endDate);

        CostSummaryRespVO respVO = new CostSummaryRespVO();
        respVO.setStoreId(storeId);
        respVO.setStartDate(startDate);
        respVO.setEndDate(endDate);
        respVO.setTotalAmount(totalAmount);
        return respVO;
    }
    @Override
    public CostImportRespVO importCostList(List<CostImportExcelVO> importList, Boolean updateSupport) {
        if (CollUtil.isEmpty(importList)) {
            throw exception(COST_IMPORT_LIST_IS_EMPTY);
        }

        CostImportRespVO respVO = new CostImportRespVO();

        // 用于校验 Excel 内部重复
        Set<String> importKeys = new HashSet<>();

        for (int i = 0; i < importList.size(); i++) {
            CostImportExcelVO importVO = importList.get(i);
            int rowNum = i + 2; // 第 1 行是表头，所以数据从第 2 行开始

            String rowKey = buildImportRowKey(rowNum, importVO);

            try {
                // 1. 基础字段校验
                validateCostImportBasic(importVO);

                // 2. 校验门店
                StoreDO store = validateImportStore(importVO.getStoreCode());

                // 3. 校验成本类型字典
                validateImportCostType(importVO.getCostType());

                // 4. 校验 Excel 内部重复
                String businessKey = buildCostBusinessKey(store.getId(), importVO.getBizDate(), importVO.getCostType());
                if (!importKeys.add(businessKey)) {
                    respVO.getFailureRows().put(rowKey, "Excel 中存在重复的门店、日期、成本类型");
                    continue;
                }

                // 5. 查询数据库是否已有相同成本
                CostDO existsCost = costMapper.selectByStoreIdAndBizDateAndCostType(
                        store.getId(), importVO.getBizDate(), importVO.getCostType());

                if (existsCost == null) {
                    // 6. 不存在则新增
                    CostDO cost = new CostDO();
                    cost.setStoreId(store.getId());
                    cost.setDeptId(store.getDeptId());
                    cost.setBizDate(importVO.getBizDate());
                    cost.setCostType(importVO.getCostType());
                    cost.setAmount(importVO.getAmount());
                    cost.setStatus(CostStatusEnum.WAIT_CONFIRM.getStatus());
                    cost.setRemark(importVO.getRemark());
                    costMapper.insert(cost);

                    respVO.getCreateRows().add(rowKey);
                    continue;
                }

                // 7. 已存在，判断是否允许更新
                if (!Boolean.TRUE.equals(updateSupport)) {
                    respVO.getFailureRows().put(rowKey, "该门店该日期该成本类型的成本支出已存在");
                    continue;
                }

                // 8. 已确认成本不允许被导入覆盖
                if (CostStatusEnum.CONFIRMED.getStatus().equals(existsCost.getStatus())) {
                    respVO.getFailureRows().put(rowKey, "该成本支出已确认，不允许导入覆盖");
                    continue;
                }

                // 9. 已参与分红账期的数据不允许更新
                validateCostNotInDividendPeriod(
                        existsCost.getStoreId(), existsCost.getBizDate());

                // 10. 更新已有数据
                CostDO updateObj = new CostDO();
                updateObj.setId(existsCost.getId());
                updateObj.setStoreId(store.getId());
                updateObj.setDeptId(store.getDeptId());
                updateObj.setBizDate(importVO.getBizDate());
                updateObj.setCostType(importVO.getCostType());
                updateObj.setAmount(importVO.getAmount());
                updateObj.setStatus(CostStatusEnum.WAIT_CONFIRM.getStatus());
                updateObj.setRemark(importVO.getRemark());
                costMapper.updateById(updateObj);

                respVO.getUpdateRows().add(rowKey);
            } catch (Exception ex) {
                respVO.getFailureRows().put(rowKey, ex.getMessage());
            }
        }
        restaurantOperateLogService.createOperateLog(
                RestaurantOperateBizTypeConstants.COST_IMPORT,
                null,
                null,
                null,
                null,
                null,
                RestaurantOperateTypeEnum.COST_IMPORT.getType(),
                null,
                null,
                "导入成本支出：新增 " + respVO.getCreateRows().size()
                        + " 条，更新 " + respVO.getUpdateRows().size()
                        + " 条，失败 " + respVO.getFailureRows().size() + " 条"
        );
        restaurantNotifyService.notifyImportFinish(
                getLoginUserIdSafe(),
                RestaurantNotifyTypeEnum.COST_IMPORT_FINISH.getType(),
                "成本支出导入完成",
                "成本支出导入完成：新增 " + respVO.getCreateRows().size()
                        + " 条，更新 " + respVO.getUpdateRows().size()
                        + " 条，失败 " + respVO.getFailureRows().size() + " 条"
        );
        return respVO;
    }

}
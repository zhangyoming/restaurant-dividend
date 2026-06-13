package cn.iocoder.yudao.module.restaurant.service.revenue;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.restaurant.controller.admin.revenue.vo.*;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.revenue.RevenueDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.store.StoreDO;
import cn.iocoder.yudao.module.restaurant.dal.mysql.dividend.DividendPeriodMapper;
import cn.iocoder.yudao.module.restaurant.dal.mysql.revenue.RevenueMapper;
import cn.iocoder.yudao.module.restaurant.enums.RestaurantNotifyTypeEnum;
import cn.iocoder.yudao.module.restaurant.enums.RestaurantOperateBizTypeConstants;
import cn.iocoder.yudao.module.restaurant.enums.RestaurantOperateTypeEnum;
import cn.iocoder.yudao.module.restaurant.enums.RevenueStatusEnum;
import cn.iocoder.yudao.module.restaurant.enums.DividendPeriodStatusEnum;
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
 * 营业收入 Service 实现类
 *
 * @author zhangyoming
 */
@Service
@Validated
public class RevenueServiceImpl implements RevenueService {

    @Resource
    private RevenueMapper revenueMapper;

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

    /**
     * 营业收入来源字典
     */
    private static final String DICT_TYPE_REVENUE_SOURCE = "restaurant_revenue_source";
    @Override
    public Long createRevenue(RevenueSaveReqVO createReqVO) {
        // 校验正确性
        validateRevenueForCreateOrUpdate(null, createReqVO);

        // 获取门店，写入 deptId 快照
        StoreDO store = storeService.getStore(createReqVO.getStoreId());

        RevenueDO revenue = BeanUtils.toBean(createReqVO, RevenueDO.class);
        revenue.setDeptId(store.getDeptId());
        revenueMapper.insert(revenue);
        return revenue.getId();
    }

    @Override
    public void updateRevenue(RevenueSaveReqVO updateReqVO) {
        RevenueDO oldRevenue = validateRevenueExists(updateReqVO.getId());

        if (RevenueStatusEnum.CONFIRMED.getStatus().equals(oldRevenue.getStatus())) {
            throw exception(REVENUE_CONFIRMED_NOT_ALLOW_UPDATE);
        }

        validateRevenueForCreateOrUpdate(updateReqVO.getId(), updateReqVO);

        StoreDO store = storeService.getStore(updateReqVO.getStoreId());

        RevenueDO updateObj = BeanUtils.toBean(updateReqVO, RevenueDO.class);
        updateObj.setDeptId(store.getDeptId());
        revenueMapper.updateById(updateObj);
    }

    @Override
    public void deleteRevenue(Long id) {
        RevenueDO revenue = validateRevenueExists(id);

        // 已确认收入不允许删除，避免历史利润数据丢失
        if (RevenueStatusEnum.CONFIRMED.getStatus().equals(revenue.getStatus())) {
            throw exception(REVENUE_CONFIRMED_NOT_ALLOW_DELETE);
        }

        revenueMapper.deleteById(id);
    }

    @Override
    public void confirmRevenue(Long id) {
        RevenueDO revenue = validateRevenueExists(id);

        if (!RevenueStatusEnum.WAIT_CONFIRM.getStatus().equals(revenue.getStatus())) {
            throw exception(REVENUE_STATUS_NOT_ALLOW_CONFIRM);
        }

        RevenueDO updateObj = new RevenueDO();
        updateObj.setId(id);
        updateObj.setStatus(RevenueStatusEnum.CONFIRMED.getStatus());
        revenueMapper.updateById(updateObj);
    }

    @Override
    public void cancelRevenue(Long id) {
        RevenueDO revenue = validateRevenueExists(id);

        if (RevenueStatusEnum.CANCELED.getStatus().equals(revenue.getStatus())) {
            throw exception(REVENUE_STATUS_NOT_ALLOW_CANCEL);
        }

        // 已参与分红账期的收入不允许作废
        validateRevenueNotInDividendPeriod(revenue.getStoreId(), revenue.getBizDate());

        RevenueDO updateObj = new RevenueDO();
        updateObj.setId(id);
        updateObj.setStatus(RevenueStatusEnum.CANCELED.getStatus());
        revenueMapper.updateById(updateObj);
    }
    /**
     * 校验营业收入是否已经参与分红账期。
     *
     * 注意：这里不要调用 DividendPeriodService，避免 RevenueServiceImpl 和
     * DividendPeriodServiceImpl 互相注入导致 Spring 循环依赖。
     */
    private void validateRevenueNotInDividendPeriod(Long storeId, LocalDate bizDate) {
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
            throw exception(REVENUE_ALREADY_IN_DIVIDEND_PERIOD);
        }
    }

    private void validateRevenueForCreateOrUpdate(Long id, RevenueSaveReqVO reqVO) {
        // 校验自己存在
        if (id != null) {
            validateRevenueExists(id);
        }

        // 校验门店存在且启用
        storeService.validateStoreList(Collections.singleton(reqVO.getStoreId()));

        // 校验收入金额
        validateRevenueAmount(reqVO.getAmount());

        // 校验同一门店、同一日期、同一来源不能重复
        validateRevenueUnique(id, reqVO.getStoreId(), reqVO.getBizDate(), reqVO.getSource());
    }
    private void validateRevenueImportBasic(RevenueImportExcelVO importVO) {
        if (importVO == null) {
            throw new IllegalArgumentException("导入数据不能为空");
        }
        if (StrUtil.isBlank(importVO.getStoreCode())) {
            throw new IllegalArgumentException("门店编码不能为空");
        }
        if (importVO.getBizDate() == null) {
            throw new IllegalArgumentException("收入日期不能为空");
        }
        if (StrUtil.isBlank(importVO.getSource())) {
            throw new IllegalArgumentException("收入来源不能为空");
        }
        if (importVO.getAmount() == null || importVO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("收入金额必须大于 0");
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
    private void validateRevenueSource(String source) {
        try {
            dictDataService.validateDictDataList(DICT_TYPE_REVENUE_SOURCE, java.util.Collections.singleton(source));
        } catch (Exception ex) {
            throw new IllegalArgumentException("收入来源字典值不存在或已禁用");
        }
    }
    private String buildImportRowKey(int rowNum, RevenueImportExcelVO importVO) {
        if (importVO == null) {
            return "第" + rowNum + "行";
        }
        return "第" + rowNum + "行：" +
                StrUtil.blankToDefault(importVO.getStoreCode(), "空门店") + "-" +
                importVO.getBizDate() + "-" +
                StrUtil.blankToDefault(importVO.getSource(), "空来源");
    }
    private String buildRevenueBusinessKey(Long storeId, java.time.LocalDate bizDate, String source) {
        return storeId + "_" + bizDate + "_" + source;
    }
    private Long getLoginUserIdSafe() {
        try {
            return SecurityFrameworkUtils.getLoginUserId();
        } catch (Exception ex) {
            return null;
        }
    }
    @VisibleForTesting
    RevenueDO validateRevenueExists(Long id) {
        if (id == null) {
            throw exception(REVENUE_NOT_EXISTS);
        }
        RevenueDO revenue = revenueMapper.selectById(id);
        if (revenue == null) {
            throw exception(REVENUE_NOT_EXISTS);
        }
        return revenue;
    }

    @VisibleForTesting
    void validateRevenueAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw exception(REVENUE_AMOUNT_INVALID);
        }
    }

    @VisibleForTesting
    void validateRevenueUnique(Long id, Long storeId, LocalDate bizDate, String source) {
        RevenueDO revenue = revenueMapper.selectByStoreIdAndBizDateAndSource(storeId, bizDate, source);
        if (revenue == null) {
            return;
        }
        if (id == null) {
            throw exception(REVENUE_DUPLICATE);
        }
        if (!revenue.getId().equals(id)) {
            throw exception(REVENUE_DUPLICATE);
        }
    }

    @Override
    public RevenueDO getRevenue(Long id) {
        return revenueMapper.selectById(id);
    }

    @Override
    public PageResult<RevenueDO> getRevenuePage(RevenuePageReqVO pageReqVO) {
        return revenueMapper.selectPage(pageReqVO);
    }

    @Override
    public List<RevenueDO> getRevenueList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return revenueMapper.selectListByIds(ids);
    }

    @Override
    public List<RevenueDO> getRevenueListByStoreIdAndDateRange(Long storeId, LocalDate startDate, LocalDate endDate) {
        storeService.validateStoreList(Collections.singleton(storeId));
        return revenueMapper.selectListByStoreIdAndBizDateBetween(storeId, startDate, endDate);
    }

    @Override
    public BigDecimal getRevenueSummaryAmount(Long storeId, LocalDate startDate, LocalDate endDate) {
        storeService.validateStoreList(Collections.singleton(storeId));
        return revenueMapper.selectSumAmountByStoreIdAndBizDateBetween(
                storeId, startDate, endDate, RevenueStatusEnum.CONFIRMED.getStatus());
    }

    @Override
    public RevenueSummaryRespVO getRevenueSummary(Long storeId, LocalDate startDate, LocalDate endDate) {
        BigDecimal totalAmount = getRevenueSummaryAmount(storeId, startDate, endDate);

        RevenueSummaryRespVO respVO = new RevenueSummaryRespVO();
        respVO.setStoreId(storeId);
        respVO.setStartDate(startDate);
        respVO.setEndDate(endDate);
        respVO.setTotalAmount(totalAmount);
        return respVO;
    }
    @Override
    public RevenueImportRespVO importRevenueList(List<RevenueImportExcelVO> importList, Boolean updateSupport) {
        if (CollUtil.isEmpty(importList)) {
            throw exception(REVENUE_IMPORT_LIST_IS_EMPTY);
        }

        RevenueImportRespVO respVO = new RevenueImportRespVO();

        // 用于校验 Excel 内部重复
        Set<String> importKeys = new HashSet<>();

        for (int i = 0; i < importList.size(); i++) {
            RevenueImportExcelVO importVO = importList.get(i);
            int rowNum = i + 2; // 第 1 行是表头，所以数据从第 2 行开始

            String rowKey = buildImportRowKey(rowNum, importVO);

            try {
                // 1. 基础字段校验
                validateRevenueImportBasic(importVO);

                // 2. 校验门店
                StoreDO store = validateImportStore(importVO.getStoreCode());

                // 3. 校验收入来源字典
                validateRevenueSource(importVO.getSource());

                // 4. 校验 Excel 内部重复
                String businessKey = buildRevenueBusinessKey(store.getId(), importVO.getBizDate(), importVO.getSource());
                if (!importKeys.add(businessKey)) {
                    respVO.getFailureRows().put(rowKey, "Excel 中存在重复的门店、日期、收入来源");
                    continue;
                }

                // 5. 查询数据库是否已有相同收入
                RevenueDO existsRevenue = revenueMapper.selectByStoreIdAndBizDateAndSource(
                        store.getId(), importVO.getBizDate(), importVO.getSource());

                if (existsRevenue == null) {
                    // 6. 不存在则新增
                    RevenueDO revenue = new RevenueDO();
                    revenue.setStoreId(store.getId());
                    revenue.setDeptId(store.getDeptId());
                    revenue.setBizDate(importVO.getBizDate());
                    revenue.setSource(importVO.getSource());
                    revenue.setAmount(importVO.getAmount());
                    revenue.setStatus(RevenueStatusEnum.WAIT_CONFIRM.getStatus());
                    revenue.setRemark(importVO.getRemark());
                    revenueMapper.insert(revenue);

                    respVO.getCreateRows().add(rowKey);
                    continue;
                }

                // 7. 已存在，判断是否允许更新
                if (!Boolean.TRUE.equals(updateSupport)) {
                    respVO.getFailureRows().put(rowKey, "该门店该日期该收入来源的营业收入已存在");
                    continue;
                }

                // 8. 已确认收入不允许被导入覆盖
                if (RevenueStatusEnum.CONFIRMED.getStatus().equals(existsRevenue.getStatus())) {
                    respVO.getFailureRows().put(rowKey, "该营业收入已确认，不允许导入覆盖");
                    continue;
                }

                // 9. 已参与分红账期的数据也不允许更新
                validateRevenueNotInDividendPeriod(
                        existsRevenue.getStoreId(), existsRevenue.getBizDate());

                // 10. 更新已有数据
                RevenueDO updateObj = new RevenueDO();
                updateObj.setId(existsRevenue.getId());
                updateObj.setStoreId(store.getId());
                updateObj.setDeptId(store.getDeptId());
                updateObj.setBizDate(importVO.getBizDate());
                updateObj.setSource(importVO.getSource());
                updateObj.setAmount(importVO.getAmount());
                updateObj.setStatus(RevenueStatusEnum.WAIT_CONFIRM.getStatus());
                updateObj.setRemark(importVO.getRemark());
                revenueMapper.updateById(updateObj);

                respVO.getUpdateRows().add(rowKey);
            } catch (Exception ex) {
                respVO.getFailureRows().put(rowKey, ex.getMessage());
            }
        }
        restaurantOperateLogService.createOperateLog(
                RestaurantOperateBizTypeConstants.REVENUE_IMPORT,
                null,
                null,
                null,
                null,
                null,
                RestaurantOperateTypeEnum.REVENUE_IMPORT.getType(),
                null,
                null,
                "导入营业收入：新增 " + respVO.getCreateRows().size()
                        + " 条，更新 " + respVO.getUpdateRows().size()
                        + " 条，失败 " + respVO.getFailureRows().size() + " 条"
        );
        restaurantNotifyService.notifyImportFinish(
                getLoginUserIdSafe(),
                RestaurantNotifyTypeEnum.REVENUE_IMPORT_FINISH.getType(),
                "营业收入导入完成",
                "营业收入导入完成：新增 " + respVO.getCreateRows().size()
                        + " 条，更新 " + respVO.getUpdateRows().size()
                        + " 条，失败 " + respVO.getFailureRows().size() + " 条"
        );
        return respVO;
    }

}
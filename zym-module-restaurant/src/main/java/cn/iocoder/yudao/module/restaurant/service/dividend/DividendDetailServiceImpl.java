package cn.iocoder.yudao.module.restaurant.service.dividend;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.restaurant.controller.admin.dividend.detail.vo.DividendDetailPageReqVO;
import cn.iocoder.yudao.module.restaurant.controller.admin.dividend.detail.vo.DividendDetailRespVO;
import cn.iocoder.yudao.module.restaurant.controller.admin.dividend.detail.vo.ShareholderDividendStatementRespVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.dividend.DividendDetailDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.dividend.DividendPeriodDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.store.StoreDO;
import cn.iocoder.yudao.module.restaurant.dal.mysql.dividend.DividendDetailMapper;
import cn.iocoder.yudao.module.restaurant.dal.mysql.dividend.DividendPeriodMapper;
import cn.iocoder.yudao.module.restaurant.enums.DividendDetailStatusEnum;
import cn.iocoder.yudao.module.restaurant.enums.DividendPeriodStatusEnum;
import cn.iocoder.yudao.module.restaurant.service.dividend.bo.DividendDetailCalculateRespBO;
import cn.iocoder.yudao.module.restaurant.service.shareholder.ShareholderService;
import cn.iocoder.yudao.module.restaurant.service.store.StoreService;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants.*;

/**
 * 分红明细 Service 实现类
 *
 * @author zhangyoming
 */
@Service
@Validated
public class DividendDetailServiceImpl implements DividendDetailService {

    @Resource
    private DividendDetailMapper dividendDetailMapper;

    @Resource
    private DividendPeriodMapper dividendPeriodMapper;

    @Resource
    private ShareholderService shareholderService;

    @Resource
    private DividendCalculateService dividendCalculateService;

    @Resource
    private StoreService storeService;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer generateDividendDetails(Long periodId) {
        // 1. 校验账期存在
        DividendPeriodDO period = validateDividendPeriodExists(periodId);

        // 2. 只有已生成状态的账期才能生成明细
        if (!DividendPeriodStatusEnum.GENERATED.getStatus().equals(period.getStatus())) {
            throw exception(DIVIDEND_DETAIL_PERIOD_NOT_GENERATED);
        }

        // 3. 幂等校验：同一个账期不能重复生成明细
        validateDividendDetailNotGenerated(periodId);

        // 4. 调用分红计算引擎，计算分红明细
        List<DividendDetailCalculateRespBO> calculateResults = dividendCalculateService.calculateDividendDetails(period);

        // 5. 落库分红明细
        // 捕获 DuplicateKeyException：防止并发重复生成时直接抛数据库异常
        try {
            calculateResults.forEach(calculateResult -> {
                DividendDetailDO detail = DividendDetailDO.builder()
                        .periodId(period.getId())
                        .storeId(period.getStoreId())
                        .deptId(period.getDeptId())
                        .periodMonth(period.getPeriodMonth())
                        .storeShareholderId(calculateResult.getStoreShareholderId())
                        .shareholderId(calculateResult.getShareholderId())
                        .shareholderName(calculateResult.getShareholderName())
                        .shareRatio(calculateResult.getShareRatio())
                        .profitAmount(calculateResult.getProfitAmount())
                        .dividendAmount(calculateResult.getDividendAmount())
                        .roundingDiffAmount(calculateResult.getRoundingDiffAmount())
                        .status(DividendDetailStatusEnum.GENERATED.getStatus())
                        .remark(period.getRemark())
                        .build();

                dividendDetailMapper.insert(detail);
            });
        } catch (DuplicateKeyException ex) {
            throw exception(DIVIDEND_DETAIL_GENERATING_DUPLICATE);
        }

        return calculateResults.size();
    }

    @Override
    public void deleteDividendDetail(Long id) {
        DividendDetailDO detail = validateDividendDetailExists(id);

        DividendPeriodDO period = validateDividendPeriodExists(detail.getPeriodId());
        if (!DividendPeriodStatusEnum.GENERATED.getStatus().equals(period.getStatus())) {
            throw exception(DIVIDEND_DETAIL_PERIOD_NOT_ALLOW_DELETE);
        }

        dividendDetailMapper.deleteById(id);
    }

    @Override
    public DividendDetailDO getDividendDetail(Long id) {
        return dividendDetailMapper.selectById(id);
    }

    @Override
    public PageResult<DividendDetailDO> getDividendDetailPage(DividendDetailPageReqVO pageReqVO) {
        return dividendDetailMapper.selectPage(pageReqVO);
    }

    @Override
    public List<DividendDetailDO> getDividendDetailListByPeriodId(Long periodId) {
        validateDividendPeriodExists(periodId);
        return dividendDetailMapper.selectListByPeriodId(periodId);
    }

    @Override
    public List<DividendDetailDO> getDividendDetailListByShareholderId(Long shareholderId) {
        shareholderService.validateShareholderList(Collections.singleton(shareholderId));
        return dividendDetailMapper.selectListByShareholderId(shareholderId);
    }

    @Override
    public List<DividendDetailDO> getDividendDetailList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return dividendDetailMapper.selectListByIds(ids);
    }

    @Override
    public long getDividendDetailCountByPeriodId(Long periodId) {
        if (periodId == null) {
            return 0;
        }
        Long count = dividendDetailMapper.selectCountByPeriodId(periodId);
        return count == null ? 0 : count;
    }

    @Override
    public void deleteDividendDetailsByPeriodId(Long periodId) {
        if (periodId == null) {
            return;
        }
        dividendDetailMapper.deleteByPeriodId(periodId);
    }

    @Override
    public void payDividendDetailsByPeriodId(Long periodId) {
        List<DividendDetailDO> details = dividendDetailMapper.selectListByPeriodId(periodId);
        if (CollUtil.isEmpty(details)) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        details.forEach(detail -> {
            DividendDetailDO updateObj = new DividendDetailDO();
            updateObj.setId(detail.getId());
            updateObj.setStatus(DividendDetailStatusEnum.PAID.getStatus());
            updateObj.setPaidTime(now);
            dividendDetailMapper.updateById(updateObj);
        });
    }

    @Override
    public void cancelDividendDetailsByPeriodId(Long periodId) {
        List<DividendDetailDO> details = dividendDetailMapper.selectListByPeriodId(periodId);
        if (CollUtil.isEmpty(details)) {
            return;
        }

        details.forEach(detail -> {
            DividendDetailDO updateObj = new DividendDetailDO();
            updateObj.setId(detail.getId());
            updateObj.setStatus(DividendDetailStatusEnum.CANCELED.getStatus());
            dividendDetailMapper.updateById(updateObj);
        });
    }
    @Override
    public List<DividendDetailRespVO> buildDividendDetailRespList(List<DividendDetailDO> list) {
        if (CollUtil.isEmpty(list)) {
            return Collections.emptyList();
        }

        // 批量查询门店，避免循环查库
        List<Long> storeIds = list.stream()
                .map(DividendDetailDO::getStoreId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, StoreDO> storeMap = storeService.getStoreMap(storeIds);

        return list.stream().map(detail -> {
            DividendDetailRespVO respVO = BeanUtils.toBean(detail, DividendDetailRespVO.class);

            StoreDO store = storeMap.get(detail.getStoreId());
            if (store != null) {
                respVO.setStoreName(store.getName());
            }

            respVO.setStatusName(DividendDetailStatusEnum.getNameByStatus(detail.getStatus()));
            return respVO;
        }).collect(Collectors.toList());
    }
    @Override
    public List<ShareholderDividendStatementRespVO> buildShareholderStatementRespList(List<DividendDetailDO> list) {
        if (CollUtil.isEmpty(list)) {
            return Collections.emptyList();
        }

        // 批量查询门店，避免循环查库
        List<Long> storeIds = list.stream()
                .map(DividendDetailDO::getStoreId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, StoreDO> storeMap = storeService.getStoreMap(storeIds);

        return list.stream().map(detail -> {
            ShareholderDividendStatementRespVO respVO = new ShareholderDividendStatementRespVO();
            respVO.setShareholderId(detail.getShareholderId());
            respVO.setShareholderName(detail.getShareholderName());
            respVO.setStoreId(detail.getStoreId());

            StoreDO store = storeMap.get(detail.getStoreId());
            if (store != null) {
                respVO.setStoreName(store.getName());
            }

            respVO.setPeriodMonth(detail.getPeriodMonth());
            respVO.setShareRatio(detail.getShareRatio());
            respVO.setProfitAmount(detail.getProfitAmount());
            respVO.setDividendAmount(detail.getDividendAmount());
            respVO.setRoundingDiffAmount(detail.getRoundingDiffAmount());
            respVO.setStatus(detail.getStatus());
            respVO.setStatusName(DividendDetailStatusEnum.getNameByStatus(detail.getStatus()));
            respVO.setPaidTime(detail.getPaidTime());
            respVO.setCreateTime(detail.getCreateTime());
            return respVO;
        }).collect(Collectors.toList());
    }

    @VisibleForTesting
    DividendDetailDO validateDividendDetailExists(Long id) {
        if (id == null) {
            throw exception(DIVIDEND_DETAIL_NOT_EXISTS);
        }
        DividendDetailDO detail = dividendDetailMapper.selectById(id);
        if (detail == null) {
            throw exception(DIVIDEND_DETAIL_NOT_EXISTS);
        }
        return detail;
    }

    @VisibleForTesting
    DividendPeriodDO validateDividendPeriodExists(Long periodId) {
        if (periodId == null) {
            throw exception(DIVIDEND_PERIOD_NOT_EXISTS);
        }
        DividendPeriodDO period = dividendPeriodMapper.selectById(periodId);
        if (period == null) {
            throw exception(DIVIDEND_PERIOD_NOT_EXISTS);
        }
        return period;
    }

    @VisibleForTesting
    void validateDividendDetailNotGenerated(Long periodId) {
        Long count = dividendDetailMapper.selectCountByPeriodId(periodId);
        if (count != null && count > 0) {
            throw exception(DIVIDEND_DETAIL_DUPLICATE);
        }
    }

}
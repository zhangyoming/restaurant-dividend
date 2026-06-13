package cn.iocoder.yudao.module.restaurant.service.mydividend;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.restaurant.controller.admin.mydividend.vo.MyDividendDetailPageReqVO;
import cn.iocoder.yudao.module.restaurant.controller.admin.mydividend.vo.MyDividendDetailRespVO;
import cn.iocoder.yudao.module.restaurant.controller.admin.mydividend.vo.MyDividendHoldingRespVO;
import cn.iocoder.yudao.module.restaurant.controller.admin.mydividend.vo.MyDividendSummaryRespVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.dividend.DividendDetailDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.shareholder.ShareholderDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.store.StoreDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.storeshareholder.StoreShareholderDO;
import cn.iocoder.yudao.module.restaurant.dal.mysql.dividend.DividendDetailMapper;
import cn.iocoder.yudao.module.restaurant.dal.mysql.shareholder.ShareholderMapper;
import cn.iocoder.yudao.module.restaurant.dal.mysql.store.StoreMapper;
import cn.iocoder.yudao.module.restaurant.dal.mysql.storeshareholder.StoreShareholderMapper;
import cn.iocoder.yudao.module.restaurant.enums.DividendDetailStatusEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants.MY_DIVIDEND_SHAREHOLDER_NOT_BIND;

/**
 * 我的分红 Service 实现类。
 *
 * <p>核心安全边界：不接收前端传入 shareholderId，而是根据当前登录用户 userId 查询股东身份，
 * 后续所有持股和分红查询都使用该 shareholderId，防止股东越权查看别人分红。</p>
 *
 * @author zhangyoming
 */
@Service
@Validated
public class MyDividendServiceImpl implements MyDividendService {

    @Resource
    private ShareholderMapper shareholderMapper;
    @Resource
    private StoreShareholderMapper storeShareholderMapper;
    @Resource
    private DividendDetailMapper dividendDetailMapper;
    @Resource
    private StoreMapper storeMapper;

    @Override
    public MyDividendSummaryRespVO getSummary() {
        ShareholderDO shareholder = getCurrentShareholder();
        List<StoreShareholderDO> holdingList = storeShareholderMapper.selectListByShareholderId(shareholder.getId());
        List<DividendDetailDO> detailList = dividendDetailMapper.selectListByShareholderId(shareholder.getId());

        BigDecimal totalInvestAmount = holdingList.stream()
                .map(StoreShareholderDO::getInvestAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalDividendAmount = sumDividendAmount(detailList, null);
        BigDecimal paidDividendAmount = sumDividendAmount(detailList, DividendDetailStatusEnum.PAID.getStatus());
        BigDecimal unpaidDividendAmount = totalDividendAmount.subtract(paidDividendAmount);

        MyDividendSummaryRespVO respVO = new MyDividendSummaryRespVO();
        respVO.setShareholderId(shareholder.getId());
        respVO.setShareholderName(shareholder.getName());
        respVO.setPhone(shareholder.getPhone());
        respVO.setHoldingStoreCount(holdingList.size());
        respVO.setActiveHoldingStoreCount((int) holdingList.stream()
                .filter(item -> CommonStatusEnum.ENABLE.getStatus().equals(item.getStatus()))
                .count());
        respVO.setTotalInvestAmount(totalInvestAmount);
        respVO.setTotalDividendAmount(totalDividendAmount);
        respVO.setPaidDividendAmount(paidDividendAmount);
        respVO.setUnpaidDividendAmount(unpaidDividendAmount);
        respVO.setLatestPeriodMonth(detailList.stream()
                .map(DividendDetailDO::getPeriodMonth)
                .filter(Objects::nonNull)
                .max(String::compareTo)
                .orElse(null));
        return respVO;
    }

    @Override
    public List<MyDividendHoldingRespVO> getHoldingList() {
        ShareholderDO shareholder = getCurrentShareholder();
        List<StoreShareholderDO> holdingList = storeShareholderMapper.selectListByShareholderId(shareholder.getId());
        if (CollUtil.isEmpty(holdingList)) {
            return List.of();
        }

        Map<Long, StoreDO> storeMap = getStoreMap(holdingList.stream()
                .map(StoreShareholderDO::getStoreId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet()));

        return holdingList.stream()
                .sorted(Comparator.comparing(StoreShareholderDO::getStatus)
                        .thenComparing(StoreShareholderDO::getId, Comparator.reverseOrder()))
                .map(item -> buildHoldingRespVO(item, storeMap.get(item.getStoreId())))
                .collect(Collectors.toList());
    }

    @Override
    public PageResult<MyDividendDetailRespVO> getDetailPage(MyDividendDetailPageReqVO pageReqVO) {
        ShareholderDO shareholder = getCurrentShareholder();
        PageResult<DividendDetailDO> pageResult = dividendDetailMapper.selectMyDividendPage(pageReqVO, shareholder.getId());
        if (CollUtil.isEmpty(pageResult.getList())) {
            return new PageResult<>(List.of(), pageResult.getTotal());
        }

        Map<Long, StoreDO> storeMap = getStoreMap(pageResult.getList().stream()
                .map(DividendDetailDO::getStoreId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet()));

        List<MyDividendDetailRespVO> list = pageResult.getList().stream()
                .map(detail -> buildDetailRespVO(detail, storeMap.get(detail.getStoreId())))
                .collect(Collectors.toList());
        return new PageResult<>(list, pageResult.getTotal());
    }

    private ShareholderDO getCurrentShareholder() {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        ShareholderDO shareholder = shareholderMapper.selectByUserId(userId);
        if (shareholder == null || !CommonStatusEnum.ENABLE.getStatus().equals(shareholder.getStatus())) {
            throw exception(MY_DIVIDEND_SHAREHOLDER_NOT_BIND);
        }
        return shareholder;
    }

    private BigDecimal sumDividendAmount(List<DividendDetailDO> detailList, Integer status) {
        if (CollUtil.isEmpty(detailList)) {
            return BigDecimal.ZERO;
        }
        return detailList.stream()
                .filter(item -> status == null || status.equals(item.getStatus()))
                .map(DividendDetailDO::getDividendAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Map<Long, StoreDO> getStoreMap(Set<Long> storeIds) {
        if (CollUtil.isEmpty(storeIds)) {
            return new HashMap<>();
        }
        List<StoreDO> stores = storeMapper.selectListByIds(storeIds);
        if (CollUtil.isEmpty(stores)) {
            return new HashMap<>();
        }
        return stores.stream().collect(Collectors.toMap(StoreDO::getId, item -> item, (a, b) -> a));
    }

    private MyDividendHoldingRespVO buildHoldingRespVO(StoreShareholderDO item, StoreDO store) {
        MyDividendHoldingRespVO respVO = new MyDividendHoldingRespVO();
        respVO.setStoreShareholderId(item.getId());
        respVO.setStoreId(item.getStoreId());
        respVO.setStoreName(store != null ? store.getName() : null);
        respVO.setStoreCode(store != null ? store.getCode() : null);
        respVO.setShareRatio(item.getShareRatio());
        respVO.setInvestAmount(item.getInvestAmount());
        respVO.setJoinTime(item.getJoinTime());
        respVO.setExitTime(item.getExitTime());
        respVO.setStatus(item.getStatus());
        return respVO;
    }

    private MyDividendDetailRespVO buildDetailRespVO(DividendDetailDO detail, StoreDO store) {
        MyDividendDetailRespVO respVO = BeanUtils.toBean(detail, MyDividendDetailRespVO.class);
        respVO.setStoreName(store != null ? store.getName() : null);
        return respVO;
    }

}

package cn.iocoder.yudao.module.restaurant.service.storeshareholder;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.restaurant.controller.admin.storeshareholder.vo.StoreShareholderPageReqVO;
import cn.iocoder.yudao.module.restaurant.controller.admin.storeshareholder.vo.StoreShareholderSaveReqVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.store.StoreDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.storeshareholder.StoreShareholderDO;
import cn.iocoder.yudao.module.restaurant.dal.mysql.dividend.DividendDetailMapper;
import cn.iocoder.yudao.module.restaurant.dal.mysql.storeshareholder.StoreShareholderMapper;
import cn.iocoder.yudao.module.restaurant.service.shareholder.ShareholderService;
import cn.iocoder.yudao.module.restaurant.enums.RestaurantOperateTypeEnum;
import cn.iocoder.yudao.module.restaurant.service.operatelog.RestaurantOperateLogService;
import cn.iocoder.yudao.module.restaurant.service.store.StoreService;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.yudao.module.restaurant.enums.ErrorCodeConstants.*;
import static cn.iocoder.yudao.module.restaurant.enums.RestaurantOperateBizTypeConstants.STORE_SHAREHOLDER;

/**
 * 门店股东持股 Service 实现类
 *
 * @author zhangyoming
 */
@Service
@Validated
public class StoreShareholderServiceImpl implements StoreShareholderService {

    /**
     * 最大持股比例：100%
     */
    private static final BigDecimal MAX_SHARE_RATIO = new BigDecimal("100.00");

    /**
     * 最小持股比例：0%
     */
    private static final BigDecimal MIN_SHARE_RATIO = BigDecimal.ZERO;

    @Resource
    private StoreShareholderMapper storeShareholderMapper;

    @Resource
    private StoreService storeService;

    @Resource
    private ShareholderService shareholderService;

    @Resource
    private DividendDetailMapper dividendDetailMapper;

    @Resource
    private RestaurantOperateLogService restaurantOperateLogService;

    @Override
    public Long createStoreShareholder(StoreShareholderSaveReqVO createReqVO) {
        validateStoreShareholderForCreateOrUpdate(null, createReqVO);

        StoreDO store = storeService.getStore(createReqVO.getStoreId());

        StoreShareholderDO storeShareholder = BeanUtils.toBean(createReqVO, StoreShareholderDO.class);
        storeShareholder.setDeptId(store.getDeptId());
        storeShareholderMapper.insert(storeShareholder);
        return storeShareholder.getId();
    }

    @Override
    public void updateStoreShareholder(StoreShareholderSaveReqVO updateReqVO) {
        StoreShareholderDO oldStoreShareholder = validateStoreShareholderExists(updateReqVO.getId());

        // 已退出的持股关系属于历史依据，不允许再通过普通修改入口变更
        if (!CommonStatusEnum.ENABLE.getStatus().equals(oldStoreShareholder.getStatus())) {
            throw exception(STORE_SHAREHOLDER_EXITED_NOT_ALLOW_UPDATE);
        }

        // 状态变更必须走“股东退出”接口，避免绕过退出时间和操作日志
        if (!CommonStatusEnum.ENABLE.getStatus().equals(updateReqVO.getStatus())) {
            throw exception(STORE_SHAREHOLDER_STATUS_NOT_ALLOW_UPDATE);
        }

        // 已参与分红，不允许直接修改比例，前面已经做过历史保护
        if (ObjectUtil.notEqual(oldStoreShareholder.getShareRatio(), updateReqVO.getShareRatio())) {
            validateStoreShareholderRatioCanUpdate(updateReqVO.getId());
        }

        validateStoreShareholderForCreateOrUpdate(updateReqVO.getId(), updateReqVO);

        StoreDO store = storeService.getStore(updateReqVO.getStoreId());

        StoreShareholderDO updateObj = BeanUtils.toBean(updateReqVO, StoreShareholderDO.class);
        updateObj.setDeptId(store.getDeptId());
        // 正常持股不应该存在退出时间，退出时间由 exitStoreShareholder 统一维护
        updateObj.setExitTime(null);
        storeShareholderMapper.updateById(updateObj);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void exitStoreShareholder(Long id) {
        StoreShareholderDO storeShareholder = validateStoreShareholderExists(id);

        if (!CommonStatusEnum.ENABLE.getStatus().equals(storeShareholder.getStatus())) {
            throw exception(STORE_SHAREHOLDER_ALREADY_EXIT);
        }

        Integer beforeStatus = storeShareholder.getStatus();
        Integer afterStatus = CommonStatusEnum.DISABLE.getStatus();
        LocalDateTime exitTime = LocalDateTime.now();

        int updateCount = storeShareholderMapper.updateExitByIdAndStatus(id, beforeStatus, afterStatus, exitTime);
        if (updateCount == 0) {
            throw exception(STORE_SHAREHOLDER_ALREADY_EXIT);
        }

        restaurantOperateLogService.createOperateLog(
                STORE_SHAREHOLDER,
                storeShareholder.getId(),
                storeShareholder.getStoreId(),
                storeShareholder.getDeptId(),
                null,
                null,
                RestaurantOperateTypeEnum.STORE_SHAREHOLDER_EXIT.getType(),
                beforeStatus,
                afterStatus,
                "股东退出门店，后续分红计算不再包含该持股关系，历史分红明细继续保留");
    }

    @Override
    public void deleteStoreShareholder(Long id) {
        // 校验存在
        validateStoreShareholderExists(id);

        // 已参与分红的持股关系，不允许物理删除
        // 后续推荐做“退出股东”接口：修改 status + exitTime，而不是删除历史数据
        validateStoreShareholderNotInDividend(id);

        // 删除持股关系
        storeShareholderMapper.deleteById(id);
    }

    private void validateStoreShareholderForCreateOrUpdate(Long id, StoreShareholderSaveReqVO reqVO) {
        // 校验自己存在
        validateStoreShareholderExists(id);

        // 校验门店存在且启用
        storeService.validateStoreList(Collections.singleton(reqVO.getStoreId()));

        // 校验股东存在且启用
        shareholderService.validateShareholderList(Collections.singleton(reqVO.getShareholderId()));

        // 校验同一门店不能重复绑定同一股东
        validateStoreShareholderUnique(id, reqVO.getStoreId(), reqVO.getShareholderId());

        // 校验持股比例基础合法性
        validateShareRatio(reqVO.getShareRatio());

        // 校验同一门店正常股东持股比例总和不能超过 100%
        validateStoreShareRatioTotal(id, reqVO.getStoreId(), reqVO.getShareRatio(), reqVO.getStatus());
    }

    @VisibleForTesting
    StoreShareholderDO validateStoreShareholderExists(Long id) {
        if (id == null) {
            return null;
        }
        StoreShareholderDO storeShareholder = storeShareholderMapper.selectById(id);
        if (storeShareholder == null) {
            throw exception(STORE_SHAREHOLDER_NOT_EXISTS);
        }
        return storeShareholder;
    }

    @VisibleForTesting
    void validateStoreShareholderUnique(Long id, Long storeId, Long shareholderId) {
        StoreShareholderDO storeShareholder = storeShareholderMapper.selectByStoreIdAndShareholderId(storeId, shareholderId);
        if (storeShareholder == null) {
            return;
        }
        if (id == null) {
            throw exception(STORE_SHAREHOLDER_DUPLICATE);
        }
        if (!storeShareholder.getId().equals(id)) {
            throw exception(STORE_SHAREHOLDER_DUPLICATE);
        }
    }

    @VisibleForTesting
    void validateShareRatio(BigDecimal shareRatio) {
        if (shareRatio == null) {
            throw exception(STORE_SHARE_RATIO_INVALID);
        }
        if (shareRatio.compareTo(MIN_SHARE_RATIO) <= 0 || shareRatio.compareTo(MAX_SHARE_RATIO) > 0) {
            throw exception(STORE_SHARE_RATIO_INVALID);
        }
    }

    @VisibleForTesting
    void validateStoreShareRatioTotal(Long id, Long storeId, BigDecimal currentShareRatio, Integer status) {
        // 如果当前持股关系不是正常状态，则不参与 100% 校验
        if (!CommonStatusEnum.ENABLE.getStatus().equals(status)) {
            return;
        }

        List<StoreShareholderDO> list = storeShareholderMapper.selectListByStoreIdAndStatus(
                storeId, CommonStatusEnum.ENABLE.getStatus());

        BigDecimal totalShareRatio = list.stream()
                // 修改时排除自己
                .filter(item -> !Objects.equals(item.getId(), id))
                .map(StoreShareholderDO::getShareRatio)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal newTotalShareRatio = totalShareRatio.add(currentShareRatio);
        if (newTotalShareRatio.compareTo(MAX_SHARE_RATIO) > 0) {
            throw exception(STORE_SHARE_RATIO_OVER_LIMIT);
        }
    }

    @VisibleForTesting
    void validateStoreShareholderNotInDividend(Long storeShareholderId) {
        Long count = dividendDetailMapper.selectCountByStoreShareholderId(storeShareholderId);
        if (count != null && count > 0) {
            throw exception(STORE_SHAREHOLDER_HAS_DIVIDEND_DATA);
        }
    }

    @VisibleForTesting
    void validateStoreShareholderRatioCanUpdate(Long storeShareholderId) {
        Long count = dividendDetailMapper.selectCountByStoreShareholderId(storeShareholderId);
        if (count != null && count > 0) {
            throw exception(STORE_SHAREHOLDER_CONFIRMED_NOT_ALLOW_UPDATE_RATIO);
        }
    }

    @Override
    public StoreShareholderDO getStoreShareholder(Long id) {
        return storeShareholderMapper.selectById(id);
    }

    @Override
    public PageResult<StoreShareholderDO> getStoreShareholderPage(StoreShareholderPageReqVO pageReqVO) {
        return storeShareholderMapper.selectPage(pageReqVO);
    }

    @Override
    public List<StoreShareholderDO> getStoreShareholderListByStoreId(Long storeId) {
        // 校验门店有效
        storeService.validateStoreList(Collections.singleton(storeId));
        return storeShareholderMapper.selectListByStoreId(storeId);
    }

    @Override
    public List<StoreShareholderDO> getStoreShareholderListByShareholderId(Long shareholderId) {
        // 校验股东有效
        shareholderService.validateShareholderList(Collections.singleton(shareholderId));
        return storeShareholderMapper.selectListByShareholderId(shareholderId);
    }

    @Override
    public List<StoreShareholderDO> getStoreShareholderList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return storeShareholderMapper.selectListByIds(ids);
    }

    @Override
    public void validateStoreShareholderList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        List<StoreShareholderDO> list = storeShareholderMapper.selectListByIds(ids);
        Map<Long, StoreShareholderDO> map = convertMap(list, StoreShareholderDO::getId);

        ids.forEach(id -> {
            StoreShareholderDO storeShareholder = map.get(id);
            if (storeShareholder == null) {
                throw exception(STORE_SHAREHOLDER_NOT_EXISTS);
            }
            if (!CommonStatusEnum.ENABLE.getStatus().equals(storeShareholder.getStatus())) {
                throw exception(STORE_SHAREHOLDER_NOT_EXISTS);
            }
        });
    }

}
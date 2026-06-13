package cn.iocoder.yudao.module.restaurant.service.storeshareholder;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.storeshareholder.vo.StoreShareholderPageReqVO;
import cn.iocoder.yudao.module.restaurant.controller.admin.storeshareholder.vo.StoreShareholderSaveReqVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.storeshareholder.StoreShareholderDO;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;

/**
 * 门店股东持股 Service 接口
 *
 * @author zhangyoming
 */
public interface StoreShareholderService {

    /**
     * 创建门店股东持股关系
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createStoreShareholder(StoreShareholderSaveReqVO createReqVO);

    /**
     * 更新门店股东持股关系
     *
     * @param updateReqVO 更新信息
     */
    void updateStoreShareholder(StoreShareholderSaveReqVO updateReqVO);

    /**
     * 股东退出门店。
     *
     * <p>不删除历史持股关系，只把状态改为禁用/退出，并记录退出时间。
     * 已参与分红的持股关系也允许退出，因为退出只影响后续分红计算，不影响历史账期追溯。
     *
     * @param id 持股关系编号
     */
    void exitStoreShareholder(Long id);

    /**
     * 删除门店股东持股关系
     *
     * @param id 编号
     */
    void deleteStoreShareholder(Long id);

    /**
     * 获得门店股东持股关系
     *
     * @param id 编号
     * @return 持股关系
     */
    StoreShareholderDO getStoreShareholder(Long id);

    /**
     * 获得门店股东持股分页
     *
     * @param pageReqVO 分页查询
     * @return 分页结果
     */
    PageResult<StoreShareholderDO> getStoreShareholderPage(StoreShareholderPageReqVO pageReqVO);

    /**
     * 获得门店的股东持股列表
     *
     * @param storeId 门店编号
     * @return 持股列表
     */
    List<StoreShareholderDO> getStoreShareholderListByStoreId(Long storeId);

    /**
     * 获得股东投资的门店持股列表
     *
     * @param shareholderId 股东编号
     * @return 持股列表
     */
    List<StoreShareholderDO> getStoreShareholderListByShareholderId(Long shareholderId);

    /**
     * 获得门店股东持股列表
     *
     * @param ids 编号集合
     * @return 持股列表
     */
    List<StoreShareholderDO> getStoreShareholderList(@Nullable Collection<Long> ids);

    /**
     * 获得门店股东持股 Map
     *
     * @param ids 编号集合
     * @return 持股 Map
     */
    default Map<Long, StoreShareholderDO> getStoreShareholderMap(Collection<Long> ids) {
        return convertMap(getStoreShareholderList(ids), StoreShareholderDO::getId);
    }

    /**
     * 校验持股关系是否有效
     *
     * @param ids 编号集合
     */
    void validateStoreShareholderList(Collection<Long> ids);

}
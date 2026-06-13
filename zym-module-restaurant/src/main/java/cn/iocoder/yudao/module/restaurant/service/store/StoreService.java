package cn.iocoder.yudao.module.restaurant.service.store;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.store.vo.StorePageReqVO;
import cn.iocoder.yudao.module.restaurant.controller.admin.store.vo.StoreSaveReqVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.store.StoreDO;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;

/**
 * 餐饮门店 Service 接口
 *
 * @author zhangyoming
 */
public interface StoreService {

    /**
     * 创建门店
     *
     * @param createReqVO 创建信息
     * @return 门店编号
     */
    Long createStore(StoreSaveReqVO createReqVO);

    /**
     * 更新门店
     *
     * @param updateReqVO 更新信息
     */
    void updateStore(StoreSaveReqVO updateReqVO);

    /**
     * 删除门店
     *
     * @param id 门店编号
     */
    void deleteStore(Long id);

    /**
     * 获得门店
     *
     * @param id 门店编号
     * @return 门店
     */
    StoreDO getStore(Long id);

    /**
     * 获得门店分页
     *
     * @param pageReqVO 分页查询
     * @return 门店分页
     */
    PageResult<StoreDO> getStorePage(StorePageReqVO pageReqVO);

    /**
     * 获得门店列表
     *
     * @param ids 门店编号集合
     * @return 门店列表
     */
    List<StoreDO> getStoreList(@Nullable Collection<Long> ids);

    /**
     * 获得启用门店列表
     *
     * @return 启用门店列表
     */
    List<StoreDO> getSimpleStoreList();

    /**
     * 获得门店 Map
     *
     * @param ids 门店编号集合
     * @return 门店 Map
     */
    default Map<Long, StoreDO> getStoreMap(Collection<Long> ids) {
        return convertMap(getStoreList(ids), StoreDO::getId);
    }

    /**
     * 校验门店是否有效
     *
     * 无效情况：
     * 1. 门店不存在
     * 2. 门店被禁用
     *
     * @param ids 门店编号集合
     */
    void validateStoreList(Collection<Long> ids);
    /**
     * 根据门店编码获得门店
     *
     * @param code 门店编码
     * @return 门店
     */
    StoreDO getStoreByCode(String code);
    /**
     * 获得指定状态的门店列表
     *
     * @param status 状态
     * @return 门店列表
     */
    List<StoreDO> getStoreListByStatus(Integer status);



}
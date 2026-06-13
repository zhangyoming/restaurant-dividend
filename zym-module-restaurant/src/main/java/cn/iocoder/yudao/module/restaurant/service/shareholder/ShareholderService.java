package cn.iocoder.yudao.module.restaurant.service.shareholder;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.shareholder.vo.ShareholderPageReqVO;
import cn.iocoder.yudao.module.restaurant.controller.admin.shareholder.vo.ShareholderSaveReqVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.shareholder.ShareholderDO;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;

/**
 * 餐饮股东 Service 接口
 *
 * @author zhangyoming
 */
public interface ShareholderService {

    /**
     * 创建股东
     *
     * @param createReqVO 创建信息
     * @return 股东编号
     */
    Long createShareholder(ShareholderSaveReqVO createReqVO);

    /**
     * 更新股东
     *
     * @param updateReqVO 更新信息
     */
    void updateShareholder(ShareholderSaveReqVO updateReqVO);

    /**
     * 删除股东
     *
     * @param id 股东编号
     */
    void deleteShareholder(Long id);

    /**
     * 获得股东
     *
     * @param id 股东编号
     * @return 股东
     */
    ShareholderDO getShareholder(Long id);

    /**
     * 获得股东分页
     *
     * @param pageReqVO 分页查询
     * @return 股东分页
     */
    PageResult<ShareholderDO> getShareholderPage(ShareholderPageReqVO pageReqVO);

    /**
     * 获得股东列表
     *
     * @param ids 股东编号集合
     * @return 股东列表
     */
    List<ShareholderDO> getShareholderList(@Nullable Collection<Long> ids);

    /**
     * 获得启用股东列表
     *
     * @return 启用股东列表
     */
    List<ShareholderDO> getSimpleShareholderList();

    /**
     * 获得股东 Map
     *
     * @param ids 股东编号集合
     * @return 股东 Map
     */
    default Map<Long, ShareholderDO> getShareholderMap(Collection<Long> ids) {
        return convertMap(getShareholderList(ids), ShareholderDO::getId);
    }

    /**
     * 校验股东是否有效
     *
     * 无效情况：
     * 1. 股东不存在
     * 2. 股东被禁用
     *
     * @param ids 股东编号集合
     */
    void validateShareholderList(Collection<Long> ids);

}
package cn.iocoder.yudao.module.restaurant.dal.mysql.storeshareholder;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.restaurant.controller.admin.storeshareholder.vo.StoreShareholderPageReqVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.storeshareholder.StoreShareholderDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Mapper
public interface StoreShareholderMapper extends BaseMapperX<StoreShareholderDO> {

    default PageResult<StoreShareholderDO> selectPage(StoreShareholderPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<StoreShareholderDO>()
                .eqIfPresent(StoreShareholderDO::getStoreId, reqVO.getStoreId())
                .eqIfPresent(StoreShareholderDO::getShareholderId, reqVO.getShareholderId())
                .eqIfPresent(StoreShareholderDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(StoreShareholderDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(StoreShareholderDO::getJoinTime, reqVO.getJoinTime())
                .betweenIfPresent(StoreShareholderDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(StoreShareholderDO::getId));
    }

    default StoreShareholderDO selectByStoreIdAndShareholderId(Long storeId, Long shareholderId) {
        return selectOne(StoreShareholderDO::getStoreId, storeId,
                StoreShareholderDO::getShareholderId, shareholderId);
    }

    default List<StoreShareholderDO> selectListByStoreId(Long storeId) {
        return selectList(StoreShareholderDO::getStoreId, storeId);
    }

    default List<StoreShareholderDO> selectListByShareholderId(Long shareholderId) {
        return selectList(StoreShareholderDO::getShareholderId, shareholderId);
    }

    default List<StoreShareholderDO> selectListByStoreIdAndStatus(Long storeId, Integer status) {
        return selectList(new LambdaQueryWrapperX<StoreShareholderDO>()
                .eq(StoreShareholderDO::getStoreId, storeId)
                .eqIfPresent(StoreShareholderDO::getStatus, status));
    }

    /**
     * 原子化退出门店股东持股关系。
     *
     * <p>通过 id + 当前状态作为更新条件，避免多人重复点击“退出”导致重复更新。
     */
    default int updateExitByIdAndStatus(Long id, Integer beforeStatus, Integer afterStatus, LocalDateTime exitTime) {
        StoreShareholderDO updateObj = new StoreShareholderDO();
        updateObj.setStatus(afterStatus);
        updateObj.setExitTime(exitTime);
        return update(updateObj, new LambdaQueryWrapperX<StoreShareholderDO>()
                .eq(StoreShareholderDO::getId, id)
                .eq(StoreShareholderDO::getStatus, beforeStatus));
    }

    default List<StoreShareholderDO> selectListByIds(Collection<Long> ids) {
        return selectByIds(ids);
    }

    default Long selectCountByStoreId(Long storeId) {
        return selectCount(StoreShareholderDO::getStoreId, storeId);
    }

    default Long selectCountByShareholderId(Long shareholderId) {
        return selectCount(StoreShareholderDO::getShareholderId, shareholderId);
    }
}
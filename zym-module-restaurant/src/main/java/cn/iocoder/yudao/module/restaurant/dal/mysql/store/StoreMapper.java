package cn.iocoder.yudao.module.restaurant.dal.mysql.store;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.restaurant.controller.admin.store.vo.StorePageReqVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.store.StoreDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper
public interface StoreMapper extends BaseMapperX<StoreDO> {

    default PageResult<StoreDO> selectPage(StorePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<StoreDO>()
                .likeIfPresent(StoreDO::getName, reqVO.getName())
                .likeIfPresent(StoreDO::getCode, reqVO.getCode())
                .eqIfPresent(StoreDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(StoreDO::getManagerUserId, reqVO.getManagerUserId())
                .eqIfPresent(StoreDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(StoreDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(StoreDO::getId));
    }

    default StoreDO selectByCode(String code) {
        return selectOne(StoreDO::getCode, code);
    }

    default StoreDO selectByName(String name) {
        return selectOne(StoreDO::getName, name);
    }

    default List<StoreDO> selectListByStatus(Integer status) {
        return selectList(new LambdaQueryWrapperX<StoreDO>()
                .eqIfPresent(StoreDO::getStatus, status)
                .orderByAsc(StoreDO::getId));
    }

    default List<StoreDO> selectListByIds(Collection<Long> ids) {
        return selectByIds(ids);
    }

}
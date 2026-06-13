package cn.iocoder.yudao.module.restaurant.dal.mysql.operatelog;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.restaurant.controller.admin.operatelog.vo.RestaurantOperateLogPageReqVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.operatelog.RestaurantOperateLogDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RestaurantOperateLogMapper extends BaseMapperX<RestaurantOperateLogDO> {

    default PageResult<RestaurantOperateLogDO> selectPage(RestaurantOperateLogPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<RestaurantOperateLogDO>()
                .eqIfPresent(RestaurantOperateLogDO::getBizType, reqVO.getBizType())
                .eqIfPresent(RestaurantOperateLogDO::getBizId, reqVO.getBizId())
                .eqIfPresent(RestaurantOperateLogDO::getStoreId, reqVO.getStoreId())
                .eqIfPresent(RestaurantOperateLogDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(RestaurantOperateLogDO::getPeriodId, reqVO.getPeriodId())
                .eqIfPresent(RestaurantOperateLogDO::getPeriodMonth, reqVO.getPeriodMonth())
                .eqIfPresent(RestaurantOperateLogDO::getOperateType, reqVO.getOperateType())
                .eqIfPresent(RestaurantOperateLogDO::getOperateUserId, reqVO.getOperateUserId())
                .betweenIfPresent(RestaurantOperateLogDO::getOperateTime, reqVO.getOperateTime())
                .orderByDesc(RestaurantOperateLogDO::getId));
    }

}
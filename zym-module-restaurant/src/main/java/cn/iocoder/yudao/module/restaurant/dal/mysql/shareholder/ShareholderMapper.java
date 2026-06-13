package cn.iocoder.yudao.module.restaurant.dal.mysql.shareholder;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.restaurant.controller.admin.shareholder.vo.ShareholderPageReqVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.shareholder.ShareholderDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper
public interface ShareholderMapper extends BaseMapperX<ShareholderDO> {

    default PageResult<ShareholderDO> selectPage(ShareholderPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ShareholderDO>()
                .likeIfPresent(ShareholderDO::getName, reqVO.getName())
                .likeIfPresent(ShareholderDO::getPhone, reqVO.getPhone())
                .eqIfPresent(ShareholderDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ShareholderDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ShareholderDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ShareholderDO::getId));
    }

    default ShareholderDO selectByPhone(String phone) {
        return selectOne(ShareholderDO::getPhone, phone);
    }

    default ShareholderDO selectByUserId(Long userId) {
        return selectOne(ShareholderDO::getUserId, userId);
    }

    default List<ShareholderDO> selectListByStatus(Integer status) {
        return selectList(ShareholderDO::getStatus, status);
    }

    default List<ShareholderDO> selectListByIds(Collection<Long> ids) {
        return selectByIds(ids);
    }

}
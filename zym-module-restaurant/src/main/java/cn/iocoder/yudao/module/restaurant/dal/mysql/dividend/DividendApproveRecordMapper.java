package cn.iocoder.yudao.module.restaurant.dal.mysql.dividend;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.restaurant.controller.admin.dividend.approve.vo.DividendApproveRecordPageReqVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.dividend.DividendApproveRecordDO;
import cn.iocoder.yudao.module.restaurant.enums.DividendApproveStatusEnum;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DividendApproveRecordMapper extends BaseMapperX<DividendApproveRecordDO> {

    default PageResult<DividendApproveRecordDO> selectPage(DividendApproveRecordPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<DividendApproveRecordDO>()
                .eqIfPresent(DividendApproveRecordDO::getPeriodId, reqVO.getPeriodId())
                .eqIfPresent(DividendApproveRecordDO::getStoreId, reqVO.getStoreId())
                .eqIfPresent(DividendApproveRecordDO::getDeptId, reqVO.getDeptId())
                .eqIfPresent(DividendApproveRecordDO::getPeriodMonth, reqVO.getPeriodMonth())
                .eqIfPresent(DividendApproveRecordDO::getApproveStatus, reqVO.getApproveStatus())
                .eqIfPresent(DividendApproveRecordDO::getSubmitUserId, reqVO.getSubmitUserId())
                .eqIfPresent(DividendApproveRecordDO::getApproveUserId, reqVO.getApproveUserId())
                .betweenIfPresent(DividendApproveRecordDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(DividendApproveRecordDO::getId));
    }

    default DividendApproveRecordDO selectApprovingByPeriodId(Long periodId) {
        return selectOne(new LambdaQueryWrapperX<DividendApproveRecordDO>()
                .eq(DividendApproveRecordDO::getPeriodId, periodId)
                .eq(DividendApproveRecordDO::getApproveStatus, DividendApproveStatusEnum.APPROVING.getStatus()));
    }

}
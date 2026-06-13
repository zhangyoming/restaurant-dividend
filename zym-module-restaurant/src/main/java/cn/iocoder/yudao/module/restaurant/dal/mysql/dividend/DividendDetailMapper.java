package cn.iocoder.yudao.module.restaurant.dal.mysql.dividend;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.restaurant.controller.admin.dividend.detail.vo.DividendDetailPageReqVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.dividend.DividendDetailDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper
public interface DividendDetailMapper extends BaseMapperX<DividendDetailDO> {

    default PageResult<DividendDetailDO> selectPage(DividendDetailPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<DividendDetailDO>()
                .eqIfPresent(DividendDetailDO::getPeriodId, reqVO.getPeriodId())
                .eqIfPresent(DividendDetailDO::getStoreId, reqVO.getStoreId())
                .eqIfPresent(DividendDetailDO::getPeriodMonth, reqVO.getPeriodMonth())
                .eqIfPresent(DividendDetailDO::getShareholderId, reqVO.getShareholderId())
                .eqIfPresent(DividendDetailDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(DividendDetailDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(DividendDetailDO::getPeriodMonth)
                .orderByDesc(DividendDetailDO::getId));
    }

    default List<DividendDetailDO> selectListByPeriodId(Long periodId) {
        return selectList(new LambdaQueryWrapperX<DividendDetailDO>()
                .eq(DividendDetailDO::getPeriodId, periodId)
                .orderByAsc(DividendDetailDO::getId));
    }

    default List<DividendDetailDO> selectListByShareholderId(Long shareholderId) {
        return selectList(new LambdaQueryWrapperX<DividendDetailDO>()
                .eq(DividendDetailDO::getShareholderId, shareholderId)
                .orderByDesc(DividendDetailDO::getPeriodMonth)
                .orderByDesc(DividendDetailDO::getId));
    }

    default Long selectCountByPeriodId(Long periodId) {
        return selectCount(DividendDetailDO::getPeriodId, periodId);
    }
    default void deleteByPeriodId(Long periodId) {
        delete(new LambdaQueryWrapperX<DividendDetailDO>()
                .eq(DividendDetailDO::getPeriodId, periodId));
    }
    default List<DividendDetailDO> selectListByIds(Collection<Long> ids) {
        return selectByIds(ids);
    }
    default Long selectCountByStoreShareholderId(Long storeShareholderId) {
        return selectCount(DividendDetailDO::getStoreShareholderId, storeShareholderId);
    }

    default Long selectCountByShareholderId(Long shareholderId) {
        return selectCount(DividendDetailDO::getShareholderId, shareholderId);
    }

    default Long selectCountByStoreId(Long storeId) {
        return selectCount(DividendDetailDO::getStoreId, storeId);
    }
    default List<DividendDetailDO> selectListByPeriodMonthBetween(String startPeriodMonth, String endPeriodMonth,
                                                                  Long storeId, Long shareholderId) {
        return selectList(new LambdaQueryWrapperX<DividendDetailDO>()
                .eqIfPresent(DividendDetailDO::getStoreId, storeId)
                .eqIfPresent(DividendDetailDO::getShareholderId, shareholderId)
                .between(DividendDetailDO::getPeriodMonth, startPeriodMonth, endPeriodMonth));
    }
}
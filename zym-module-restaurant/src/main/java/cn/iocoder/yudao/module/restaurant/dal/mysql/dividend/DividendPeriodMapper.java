package cn.iocoder.yudao.module.restaurant.dal.mysql.dividend;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.restaurant.controller.admin.dividend.period.vo.DividendPeriodPageReqVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.dividend.DividendPeriodDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Mapper
public interface DividendPeriodMapper extends BaseMapperX<DividendPeriodDO> {

    default PageResult<DividendPeriodDO> selectPage(DividendPeriodPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<DividendPeriodDO>()
                .eqIfPresent(DividendPeriodDO::getStoreId, reqVO.getStoreId())
                .eqIfPresent(DividendPeriodDO::getPeriodMonth, reqVO.getPeriodMonth())
                .eqIfPresent(DividendPeriodDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(DividendPeriodDO::getStartDate, reqVO.getStartDate())
                .betweenIfPresent(DividendPeriodDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(DividendPeriodDO::getPeriodMonth)
                .orderByDesc(DividendPeriodDO::getId));
    }

    default DividendPeriodDO selectByStoreIdAndPeriodMonth(Long storeId, String periodMonth) {
        return selectOne(DividendPeriodDO::getStoreId, storeId,
                DividendPeriodDO::getPeriodMonth, periodMonth);
    }

    default List<DividendPeriodDO> selectListByStoreId(Long storeId) {
        return selectList(DividendPeriodDO::getStoreId, storeId);
    }

    default List<DividendPeriodDO> selectListByIds(Collection<Long> ids) {
        return selectByIds(ids);
    }
    default Long selectCountByStoreId(Long storeId) {
        return selectCount(DividendPeriodDO::getStoreId, storeId);
    }

    default Long selectCountByStoreIdAndBizDate(Long storeId, LocalDate bizDate, Collection<Integer> statuses) {
        return selectCount(new LambdaQueryWrapperX<DividendPeriodDO>()
                .eq(DividendPeriodDO::getStoreId, storeId)
                .le(DividendPeriodDO::getStartDate, bizDate)
                .ge(DividendPeriodDO::getEndDate, bizDate)
                .in(DividendPeriodDO::getStatus, statuses));
    }

    default Long selectCountByStoreIdAndPeriodMonth(Long storeId, String periodMonth) {
        return selectCount(new LambdaQueryWrapperX<DividendPeriodDO>()
                .eq(DividendPeriodDO::getStoreId, storeId)
                .eq(DividendPeriodDO::getPeriodMonth, periodMonth));
    }
    default List<DividendPeriodDO> selectListByStartDateBetween(LocalDate startDate, LocalDate endDate,
                                                                Long storeId) {
        return selectList(new LambdaQueryWrapperX<DividendPeriodDO>()
                .eqIfPresent(DividendPeriodDO::getStoreId, storeId)
                .between(DividendPeriodDO::getStartDate, startDate, endDate));
    }
}
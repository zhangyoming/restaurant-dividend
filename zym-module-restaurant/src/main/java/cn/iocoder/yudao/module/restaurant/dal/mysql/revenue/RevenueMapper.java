package cn.iocoder.yudao.module.restaurant.dal.mysql.revenue;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.restaurant.controller.admin.revenue.vo.RevenuePageReqVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.revenue.RevenueDO;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Mapper
public interface RevenueMapper extends BaseMapperX<RevenueDO> {

    default PageResult<RevenueDO> selectPage(RevenuePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<RevenueDO>()
                .eqIfPresent(RevenueDO::getStoreId, reqVO.getStoreId())
                .betweenIfPresent(RevenueDO::getBizDate, reqVO.getBizDate())
                .eqIfPresent(RevenueDO::getSource, reqVO.getSource())
                .eqIfPresent(RevenueDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(RevenueDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(RevenueDO::getBizDate)
                .orderByDesc(RevenueDO::getId));
    }

    default RevenueDO selectByStoreIdAndBizDateAndSource(Long storeId, LocalDate bizDate, String source) {
        return selectOne(RevenueDO::getStoreId, storeId,
                RevenueDO::getBizDate, bizDate,
                RevenueDO::getSource, source);
    }

    default List<RevenueDO> selectListByStoreIdAndBizDateBetween(Long storeId, LocalDate startDate, LocalDate endDate) {
        return selectList(new LambdaQueryWrapperX<RevenueDO>()
                .eq(RevenueDO::getStoreId, storeId)
                .between(RevenueDO::getBizDate, startDate, endDate)
                .orderByAsc(RevenueDO::getBizDate));
    }

    default List<RevenueDO> selectListByIds(Collection<Long> ids) {
        return selectByIds(ids);
    }

    default BigDecimal selectSumAmountByStoreIdAndBizDateBetween(Long storeId, LocalDate startDate, LocalDate endDate,
                                                                 Integer status) {
        List<RevenueDO> list = selectList(new LambdaQueryWrapperX<RevenueDO>()
                .eq(RevenueDO::getStoreId, storeId)
                .eqIfPresent(RevenueDO::getStatus, status)
                .between(RevenueDO::getBizDate, startDate, endDate));
        return list.stream()
                .map(RevenueDO::getAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    default Long selectCountByStoreId(Long storeId) {
        return selectCount(RevenueDO::getStoreId, storeId);
    }
    default List<RevenueDO> selectListByBizDateBetween(LocalDate startDate, LocalDate endDate,
                                                       Integer status, Long storeId) {
        return selectList(new LambdaQueryWrapperX<RevenueDO>()
                .eqIfPresent(RevenueDO::getStoreId, storeId)
                .eqIfPresent(RevenueDO::getStatus, status)
                .between(RevenueDO::getBizDate, startDate, endDate));
    }
}
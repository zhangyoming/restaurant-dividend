package cn.iocoder.yudao.module.restaurant.dal.mysql.cost;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.restaurant.controller.admin.cost.vo.CostPageReqVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.cost.CostDO;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Mapper
public interface CostMapper extends BaseMapperX<CostDO> {

    default PageResult<CostDO> selectPage(CostPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CostDO>()
                .eqIfPresent(CostDO::getStoreId, reqVO.getStoreId())
                .betweenIfPresent(CostDO::getBizDate, reqVO.getBizDate())
                .eqIfPresent(CostDO::getCostType, reqVO.getCostType())
                .eqIfPresent(CostDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(CostDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(CostDO::getBizDate)
                .orderByDesc(CostDO::getId));
    }

    default CostDO selectByStoreIdAndBizDateAndCostType(Long storeId, LocalDate bizDate, String costType) {
        return selectOne(CostDO::getStoreId, storeId,
                CostDO::getBizDate, bizDate,
                CostDO::getCostType, costType);
    }

    default List<CostDO> selectListByStoreIdAndBizDateBetween(Long storeId, LocalDate startDate, LocalDate endDate) {
        return selectList(new LambdaQueryWrapperX<CostDO>()
                .eq(CostDO::getStoreId, storeId)
                .between(CostDO::getBizDate, startDate, endDate)
                .orderByAsc(CostDO::getBizDate));
    }

    default List<CostDO> selectListByIds(Collection<Long> ids) {
        return selectByIds(ids);
    }

    default BigDecimal selectSumAmountByStoreIdAndBizDateBetween(Long storeId, LocalDate startDate, LocalDate endDate,
                                                                 Integer status) {
        List<CostDO> list = selectList(new LambdaQueryWrapperX<CostDO>()
                .eq(CostDO::getStoreId, storeId)
                .eqIfPresent(CostDO::getStatus, status)
                .between(CostDO::getBizDate, startDate, endDate));
        return list.stream()
                .map(CostDO::getAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    default Long selectCountByStoreId(Long storeId) {
        return selectCount(CostDO::getStoreId, storeId);
    }
    default List<CostDO> selectListByBizDateBetween(LocalDate startDate, LocalDate endDate,
                                                    Integer status, Long storeId) {
        return selectList(new LambdaQueryWrapperX<CostDO>()
                .eqIfPresent(CostDO::getStoreId, storeId)
                .eqIfPresent(CostDO::getStatus, status)
                .between(CostDO::getBizDate, startDate, endDate));
    }
}
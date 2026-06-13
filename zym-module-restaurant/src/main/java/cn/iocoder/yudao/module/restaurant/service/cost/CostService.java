package cn.iocoder.yudao.module.restaurant.service.cost;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.cost.vo.*;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.cost.CostDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.store.StoreDO;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;

/**
 * 成本支出 Service 接口
 *
 * @author zhangyoming
 */
public interface CostService {

    Long createCost(CostSaveReqVO createReqVO);

    void updateCost(CostSaveReqVO updateReqVO);

    void deleteCost(Long id);

    void confirmCost(Long id);

    void cancelCost(Long id);

    CostDO getCost(Long id);

    PageResult<CostDO> getCostPage(CostPageReqVO pageReqVO);

    List<CostDO> getCostList(@Nullable Collection<Long> ids);

    default Map<Long, CostDO> getCostMap(Collection<Long> ids) {
        return convertMap(getCostList(ids), CostDO::getId);
    }

    List<CostDO> getCostListByStoreIdAndDateRange(Long storeId, LocalDate startDate, LocalDate endDate);

    BigDecimal getCostSummaryAmount(Long storeId, LocalDate startDate, LocalDate endDate);

    CostSummaryRespVO getCostSummary(Long storeId, LocalDate startDate, LocalDate endDate);
    /**
     * 导入成本支出
     *
     * @param importList 导入数据
     * @param updateSupport 是否支持更新
     * @return 导入结果
     */
    CostImportRespVO importCostList(List<CostImportExcelVO> importList, Boolean updateSupport);
}
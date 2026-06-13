package cn.iocoder.yudao.module.restaurant.service.dividend;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.dividend.period.vo.DividendPeriodGenerateReqVO;
import cn.iocoder.yudao.module.restaurant.controller.admin.dividend.period.vo.DividendPeriodPageReqVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.dividend.DividendPeriodDO;
import org.springframework.lang.Nullable;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;

/**
 * 分红账期 Service 接口
 *
 * @author zhangyoming
 */
public interface DividendPeriodService {

    Long generateDividendPeriod(DividendPeriodGenerateReqVO generateReqVO);

    void confirmDividendPeriod(Long id);

    void payDividendPeriod(Long id);

    void cancelDividendPeriod(Long id);

    void deleteDividendPeriod(Long id);

    DividendPeriodDO getDividendPeriod(Long id);

    PageResult<DividendPeriodDO> getDividendPeriodPage(DividendPeriodPageReqVO pageReqVO);

    List<DividendPeriodDO> getDividendPeriodList(@Nullable Collection<Long> ids);

    default Map<Long, DividendPeriodDO> getDividendPeriodMap(Collection<Long> ids) {
        return convertMap(getDividendPeriodList(ids), DividendPeriodDO::getId);
    }

    /**
     * 校验收入是否已经参与分红账期
     *
     * @param storeId 门店编号
     * @param bizDate 收入日期
     */
    void validateRevenueNotInDividendPeriod(Long storeId, LocalDate bizDate);

    /**
     * 校验成本是否已经参与分红账期
     *
     * @param storeId 门店编号
     * @param bizDate 成本日期
     */
    void validateCostNotInDividendPeriod(Long storeId, LocalDate bizDate);

    /**
     * 获得门店分红账期数量
     *
     * @param storeId 门店编号
     * @return 分红账期数量
     */
    long getDividendPeriodCountByStoreId(Long storeId);

}
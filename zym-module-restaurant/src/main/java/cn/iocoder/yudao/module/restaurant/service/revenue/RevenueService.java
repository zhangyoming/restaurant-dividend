package cn.iocoder.yudao.module.restaurant.service.revenue;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.revenue.vo.*;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.revenue.RevenueDO;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;

/**
 * 营业收入 Service 接口
 *
 * @author zhangyoming
 */
public interface RevenueService {

    /**
     * 创建营业收入
     *
     * @param createReqVO 创建信息
     * @return 收入编号
     */
    Long createRevenue(RevenueSaveReqVO createReqVO);

    /**
     * 更新营业收入
     *
     * @param updateReqVO 更新信息
     */
    void updateRevenue(RevenueSaveReqVO updateReqVO);

    /**
     * 删除营业收入
     *
     * @param id 收入编号
     */
    void deleteRevenue(Long id);

    /**
     * 确认营业收入
     *
     * @param id 收入编号
     */
    void confirmRevenue(Long id);

    /**
     * 作废营业收入
     *
     * @param id 收入编号
     */
    void cancelRevenue(Long id);

    /**
     * 获得营业收入
     *
     * @param id 收入编号
     * @return 收入记录
     */
    RevenueDO getRevenue(Long id);

    /**
     * 获得营业收入分页
     *
     * @param pageReqVO 分页查询
     * @return 分页结果
     */
    PageResult<RevenueDO> getRevenuePage(RevenuePageReqVO pageReqVO);

    /**
     * 获得营业收入列表
     *
     * @param ids 收入编号集合
     * @return 收入列表
     */
    List<RevenueDO> getRevenueList(@Nullable Collection<Long> ids);

    /**
     * 获得营业收入 Map
     *
     * @param ids 收入编号集合
     * @return 收入 Map
     */
    default Map<Long, RevenueDO> getRevenueMap(Collection<Long> ids) {
        return convertMap(getRevenueList(ids), RevenueDO::getId);
    }

    /**
     * 获得门店指定日期范围收入列表
     *
     * @param storeId 门店编号
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 收入列表
     */
    List<RevenueDO> getRevenueListByStoreIdAndDateRange(Long storeId, LocalDate startDate, LocalDate endDate);

    /**
     * 汇总门店指定日期范围收入金额
     *
     * @param storeId 门店编号
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 总金额
     */
    BigDecimal getRevenueSummaryAmount(Long storeId, LocalDate startDate, LocalDate endDate);

    /**
     * 获得营业收入汇总信息
     *
     * @param storeId 门店编号
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 汇总结果
     */
    RevenueSummaryRespVO getRevenueSummary(Long storeId, LocalDate startDate, LocalDate endDate);
    /**
     * 导入营业收入
     *
     * @param importList 导入数据
     * @param updateSupport 是否支持更新
     * @return 导入结果
     */
    RevenueImportRespVO importRevenueList(List<RevenueImportExcelVO> importList, Boolean updateSupport);
}
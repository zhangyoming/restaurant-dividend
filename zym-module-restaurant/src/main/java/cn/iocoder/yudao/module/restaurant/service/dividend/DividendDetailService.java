package cn.iocoder.yudao.module.restaurant.service.dividend;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.dividend.detail.vo.DividendDetailPageReqVO;
import cn.iocoder.yudao.module.restaurant.controller.admin.dividend.detail.vo.DividendDetailRespVO;
import cn.iocoder.yudao.module.restaurant.controller.admin.dividend.detail.vo.ShareholderDividendStatementRespVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.dividend.DividendDetailDO;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;

/**
 * 分红明细 Service 接口
 *
 * @author zhangyoming
 */
public interface DividendDetailService {

    /**
     * 根据分红账期生成分红明细
     *
     * @param periodId 分红账期编号
     * @return 生成明细数量
     */
    Integer generateDividendDetails(Long periodId);

    /**
     * 删除分红明细
     *
     * @param id 分红明细编号
     */
    void deleteDividendDetail(Long id);

    /**
     * 获得分红明细
     *
     * @param id 分红明细编号
     * @return 分红明细
     */
    DividendDetailDO getDividendDetail(Long id);

    /**
     * 获得分红明细分页
     *
     * @param pageReqVO 分页请求
     * @return 分页结果
     */
    PageResult<DividendDetailDO> getDividendDetailPage(DividendDetailPageReqVO pageReqVO);

    /**
     * 获得指定账期的分红明细列表
     *
     * @param periodId 分红账期编号
     * @return 分红明细列表
     */
    List<DividendDetailDO> getDividendDetailListByPeriodId(Long periodId);

    /**
     * 获得指定股东的分红明细列表
     *
     * @param shareholderId 股东编号
     * @return 分红明细列表
     */
    List<DividendDetailDO> getDividendDetailListByShareholderId(Long shareholderId);

    /**
     * 获得分红明细列表
     *
     * @param ids 分红明细编号集合
     * @return 分红明细列表
     */
    List<DividendDetailDO> getDividendDetailList(@Nullable Collection<Long> ids);

    /**
     * 获得分红明细 Map
     *
     * @param ids 分红明细编号集合
     * @return 分红明细 Map
     */
    default Map<Long, DividendDetailDO> getDividendDetailMap(Collection<Long> ids) {
        return convertMap(getDividendDetailList(ids), DividendDetailDO::getId);
    }
    /**
     * 获得指定账期的分红明细数量
     *
     * @param periodId 分红账期编号
     * @return 明细数量
     */
    long getDividendDetailCountByPeriodId(Long periodId);

    /**
     * 删除指定账期下的分红明细
     *
     * 只建议在账期仍处于“已生成”状态时使用。
     *
     * @param periodId 分红账期编号
     */
    void deleteDividendDetailsByPeriodId(Long periodId);
    /**
     * 将指定账期下的分红明细标记为已发放
     *
     * 后续分红账期 pay 时可以调用。
     *
     * @param periodId 分红账期编号
     */
    void payDividendDetailsByPeriodId(Long periodId);

    /**
     * 将指定账期下的分红明细标记为已作废
     *
     * 后续分红账期 cancel 时可以调用。
     *
     * @param periodId 分红账期编号
     */
    void cancelDividendDetailsByPeriodId(Long periodId);
    /**
     * 构建分红明细导出列表
     *
     * @param list 分红明细 DO 列表
     * @return 导出 VO 列表
     */
    List<DividendDetailRespVO> buildDividendDetailRespList(List<DividendDetailDO> list);

    /**
     * 构建股东分红对账单列表
     *
     * @param list 分红明细 DO 列表
     * @return 股东分红对账单列表
     */
    List<ShareholderDividendStatementRespVO> buildShareholderStatementRespList(List<DividendDetailDO> list);
}
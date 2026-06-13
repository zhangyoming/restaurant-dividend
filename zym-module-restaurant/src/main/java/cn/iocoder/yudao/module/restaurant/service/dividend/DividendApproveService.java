package cn.iocoder.yudao.module.restaurant.service.dividend;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.restaurant.controller.admin.dividend.approve.vo.DividendApproveAuditReqVO;
import cn.iocoder.yudao.module.restaurant.controller.admin.dividend.approve.vo.DividendApproveRecordPageReqVO;
import cn.iocoder.yudao.module.restaurant.controller.admin.dividend.approve.vo.DividendApproveRecordRespVO;
import cn.iocoder.yudao.module.restaurant.controller.admin.dividend.approve.vo.DividendApproveSubmitReqVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.dividend.DividendApproveRecordDO;

import java.util.List;

/**
 * 分红审批 Service 接口
 *
 * @author zhangyoming
 */
public interface DividendApproveService {

    /**
     * 提交分红发放审批
     *
     * @param submitReqVO 提交审批请求
     * @return 审批记录编号
     */
    Long submitDividendApprove(DividendApproveSubmitReqVO submitReqVO);

    /**
     * 审批分红发放
     *
     * @param auditReqVO 审批请求
     */
    void auditDividendApprove(DividendApproveAuditReqVO auditReqVO);

    /**
     * 获得审批记录分页
     */
    PageResult<DividendApproveRecordDO> getApproveRecordPage(DividendApproveRecordPageReqVO pageReqVO);

    /**
     * 构建审批记录响应列表
     */
    List<DividendApproveRecordRespVO> buildApproveRecordRespList(List<DividendApproveRecordDO> list);

}
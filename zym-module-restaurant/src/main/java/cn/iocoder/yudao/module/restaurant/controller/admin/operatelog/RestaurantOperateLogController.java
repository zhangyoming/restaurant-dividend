package cn.iocoder.yudao.module.restaurant.controller.admin.operatelog;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.restaurant.controller.admin.operatelog.vo.RestaurantOperateLogPageReqVO;
import cn.iocoder.yudao.module.restaurant.controller.admin.operatelog.vo.RestaurantOperateLogRespVO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.operatelog.RestaurantOperateLogDO;
import cn.iocoder.yudao.module.restaurant.service.operatelog.RestaurantOperateLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 餐饮业务操作日志")
@RestController
@RequestMapping("/restaurant/operate-log")
@Validated
public class RestaurantOperateLogController {

    @Resource
    private RestaurantOperateLogService restaurantOperateLogService;

    @GetMapping("/page")
    @Operation(summary = "获得餐饮业务操作日志分页")
    @PreAuthorize("@ss.hasPermission('restaurant:operate-log:query')")
    public CommonResult<PageResult<RestaurantOperateLogRespVO>> getOperateLogPage(
            @Valid RestaurantOperateLogPageReqVO pageReqVO) {
        PageResult<RestaurantOperateLogDO> pageResult = restaurantOperateLogService.getOperateLogPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, RestaurantOperateLogRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出餐饮业务操作日志 Excel")
    @PreAuthorize("@ss.hasPermission('restaurant:operate-log:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportOperateLogExcel(HttpServletResponse response,
                                      @Valid RestaurantOperateLogPageReqVO exportReqVO) throws IOException {
        exportReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<RestaurantOperateLogDO> list = restaurantOperateLogService.getOperateLogPage(exportReqVO).getList();
        ExcelUtils.write(response, "餐饮业务操作日志.xls", "操作日志", RestaurantOperateLogRespVO.class,
                BeanUtils.toBean(list, RestaurantOperateLogRespVO.class));
    }

}
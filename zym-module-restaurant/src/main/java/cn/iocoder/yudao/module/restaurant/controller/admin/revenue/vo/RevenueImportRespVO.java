package cn.iocoder.yudao.module.restaurant.controller.admin.revenue.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Schema(description = "管理后台 - 营业收入 Excel 导入 Response VO")
@Data
public class RevenueImportRespVO {

    @Schema(description = "创建成功的数据")
    private List<String> createRows = new ArrayList<>();

    @Schema(description = "更新成功的数据")
    private List<String> updateRows = new ArrayList<>();

    @Schema(description = "导入失败的数据，key 为行标识，value 为失败原因")
    private Map<String, String> failureRows = new LinkedHashMap<>();

}
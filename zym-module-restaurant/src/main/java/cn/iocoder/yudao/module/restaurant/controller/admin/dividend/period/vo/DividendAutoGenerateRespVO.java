package cn.iocoder.yudao.module.restaurant.controller.admin.dividend.period.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Schema(description = "管理后台 - 自动生成分红结果 Response VO")
@Data
public class DividendAutoGenerateRespVO {

    @Schema(description = "账期月份", example = "2026-06")
    private String periodMonth;

    @Schema(description = "门店总数", example = "10")
    private Integer totalStoreCount;

    @Schema(description = "成功数量", example = "6")
    private Integer successCount;

    @Schema(description = "跳过数量", example = "3")
    private Integer skipCount;

    @Schema(description = "失败数量", example = "1")
    private Integer failureCount;

    @Schema(description = "成功门店")
    private List<String> successStores = new ArrayList<>();

    @Schema(description = "跳过门店，key 为门店，value 为跳过原因")
    private Map<String, String> skipStores = new LinkedHashMap<>();

    @Schema(description = "失败门店，key 为门店，value 为失败原因")
    private Map<String, String> failureStores = new LinkedHashMap<>();

}
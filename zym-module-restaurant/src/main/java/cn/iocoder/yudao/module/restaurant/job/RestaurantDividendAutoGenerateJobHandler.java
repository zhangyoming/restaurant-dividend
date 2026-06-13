package cn.iocoder.yudao.module.restaurant.job;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.quartz.core.handler.JobHandler;
import cn.iocoder.yudao.module.restaurant.controller.admin.dividend.period.vo.DividendAutoGenerateRespVO;
import cn.iocoder.yudao.module.restaurant.service.dividend.DividendAutoGenerateService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 分红自动生成 JobHandler
 *
 * 定时任务参数：
 * 1. 不传：默认生成上个月
 * 2. 传 yyyy-MM：生成指定账期月份
 *
 * @author zhangyoming
 */
@Slf4j
@Component
public class RestaurantDividendAutoGenerateJobHandler implements JobHandler {

    @Resource
    private DividendAutoGenerateService dividendAutoGenerateService;

    @Override
    public String execute(String param) {
        String periodMonth = StrUtil.blankToDefault(param, null);

        log.info("[execute][开始自动生成分红，参数({})]", periodMonth);

        DividendAutoGenerateRespVO result = dividendAutoGenerateService.generateByPeriodMonth(periodMonth);

        log.info("[execute][自动生成分红完成，账期({}) 成功({}) 跳过({}) 失败({})]",
                result.getPeriodMonth(), result.getSuccessCount(), result.getSkipCount(), result.getFailureCount());

        return "自动生成分红完成：账期 " + result.getPeriodMonth()
                + "，门店总数 " + result.getTotalStoreCount()
                + "，成功 " + result.getSuccessCount()
                + "，跳过 " + result.getSkipCount()
                + "，失败 " + result.getFailureCount();
    }

}
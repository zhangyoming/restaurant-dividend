package cn.iocoder.yudao.module.restaurant.framework.approve.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 餐饮分红审批自动配置。
 *
 * @author zhangyoming
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(RestaurantDividendApproveProperties.class)
public class RestaurantDividendApproveConfiguration {
}

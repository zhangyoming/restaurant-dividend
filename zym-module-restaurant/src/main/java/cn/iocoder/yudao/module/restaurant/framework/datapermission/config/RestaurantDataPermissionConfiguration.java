package cn.iocoder.yudao.module.restaurant.framework.datapermission.config;

import cn.iocoder.yudao.framework.datapermission.core.rule.dept.DeptDataPermissionRuleCustomizer;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.cost.CostDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.dividend.DividendApproveRecordDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.dividend.DividendDetailDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.dividend.DividendPeriodDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.operatelog.RestaurantOperateLogDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.revenue.RevenueDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.store.StoreDO;
import cn.iocoder.yudao.module.restaurant.dal.dataobject.storeshareholder.StoreShareholderDO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * restaurant 模块的数据权限 Configuration
 *
 * 基于 dept_id 实现门店级数据权限。
 *
 * @author zhangyoming
 */
@Configuration(proxyBeanMethods = false)
public class RestaurantDataPermissionConfiguration {

    @Bean
    public DeptDataPermissionRuleCustomizer restaurantDeptDataPermissionRuleCustomizer() {
        return rule -> {
            rule.addDeptColumn(StoreDO.class);
            rule.addDeptColumn(RevenueDO.class);
            rule.addDeptColumn(CostDO.class);
            rule.addDeptColumn(StoreShareholderDO.class);
            rule.addDeptColumn(DividendPeriodDO.class);
            rule.addDeptColumn(DividendDetailDO.class);
            rule.addDeptColumn(RestaurantOperateLogDO.class);

            // 分红审批记录表：通过 dept_id 做门店级数据权限
            rule.addDeptColumn(DividendApproveRecordDO.class);
        };

    }

}
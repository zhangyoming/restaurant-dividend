-- 餐饮共享股东分红系统：定时任务初始化脚本
-- handler_name 必须等于 Spring Bean 名称：restaurantDividendAutoGenerateJobHandler。
-- handler_param 为空表示默认生成上个月；也可以临时改为 2026-06 生成指定账期。

SET NAMES utf8mb4;

DELETE FROM infra_job WHERE handler_name = 'restaurantDividendAutoGenerateJobHandler';

INSERT INTO infra_job
(id, name, status, handler_name, handler_param, cron_expression, retry_count, retry_interval, monitor_timeout, creator, create_time, updater, update_time, deleted)
VALUES
    (60, '餐饮分红自动生成 Job', 2, 'restaurantDividendAutoGenerateJobHandler', '', '0 0 2 1 * ?', 3, 60, 120, '1', NOW(), '1', NOW(), b'0');

-- 说明：status=2 表示暂停，建议首次上线保持暂停，确认测试数据无误后在“基础设施 -> 定时任务”手动启动。
-- cron：每月 1 日凌晨 02:00 自动生成上个月分红账期。

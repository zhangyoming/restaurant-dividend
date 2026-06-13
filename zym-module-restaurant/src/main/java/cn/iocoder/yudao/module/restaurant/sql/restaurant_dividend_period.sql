CREATE TABLE restaurant_dividend_period (
                                            id BIGINT NOT NULL AUTO_INCREMENT COMMENT '分红账期编号',
                                            store_id BIGINT NOT NULL COMMENT '门店编号',
                                            period_month VARCHAR(7) NOT NULL COMMENT '账期月份，例如 2026-06',
                                            start_date DATE NOT NULL COMMENT '账期开始日期',
                                            end_date DATE NOT NULL COMMENT '账期结束日期',
                                            total_revenue DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '收入总额',
                                            total_cost DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '成本总额',
                                            profit_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '利润金额',
                                            reserve_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '预留金额',
                                            distributable_profit DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '可分红金额',
                                            status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0已生成 1已确认 2已发放 3已作废',
                                            generated_time DATETIME NOT NULL COMMENT '生成时间',
                                            confirmed_time DATETIME DEFAULT NULL COMMENT '确认时间',
                                            paid_time DATETIME DEFAULT NULL COMMENT '发放时间',
                                            canceled_time DATETIME DEFAULT NULL COMMENT '作废时间',
                                            remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
                                            creator VARCHAR(64) DEFAULT '' COMMENT '创建者',
                                            create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                            updater VARCHAR(64) DEFAULT '' COMMENT '更新者',
                                            update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                            deleted BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                                            tenant_id BIGINT NOT NULL DEFAULT 0 COMMENT '租户编号',
                                            PRIMARY KEY (id),
                                            UNIQUE KEY uk_store_period_month_tenant (store_id, period_month, tenant_id),
                                            KEY idx_store_id (store_id),
                                            KEY idx_period_month (period_month),
                                            KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='餐饮分红账期表';
ALTER TABLE restaurant_dividend_period
    ADD COLUMN dept_id BIGINT NOT NULL COMMENT '部门编号' AFTER store_id;

CREATE INDEX idx_dividend_period_dept_id ON restaurant_dividend_period(dept_id);
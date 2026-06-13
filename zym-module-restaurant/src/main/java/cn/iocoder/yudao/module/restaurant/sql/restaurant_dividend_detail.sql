CREATE TABLE restaurant_dividend_detail (
                                            id BIGINT NOT NULL AUTO_INCREMENT COMMENT '分红明细编号',
                                            period_id BIGINT NOT NULL COMMENT '分红账期编号',
                                            store_id BIGINT NOT NULL COMMENT '门店编号',
                                            period_month VARCHAR(7) NOT NULL COMMENT '账期月份，例如 2026-06',
                                            store_shareholder_id BIGINT NOT NULL COMMENT '门店股东持股关系编号',
                                            shareholder_id BIGINT NOT NULL COMMENT '股东编号',
                                            shareholder_name VARCHAR(100) NOT NULL COMMENT '股东姓名快照',
                                            share_ratio DECIMAL(5,2) NOT NULL COMMENT '持股比例快照，例如 40.00 表示 40%',
                                            profit_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '可分红金额快照',
                                            dividend_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '股东分红金额',
                                            rounding_diff_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '尾差金额' ,
                                            status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0已生成 1已发放 2已作废',
                                            paid_time DATETIME DEFAULT NULL COMMENT '发放时间',
                                            remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
                                            creator VARCHAR(64) DEFAULT '' COMMENT '创建者',
                                            create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                            updater VARCHAR(64) DEFAULT '' COMMENT '更新者',
                                            update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                            deleted BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                                            tenant_id BIGINT NOT NULL DEFAULT 0 COMMENT '租户编号',
                                            PRIMARY KEY (id),
                                            UNIQUE KEY uk_period_shareholder_tenant (period_id, shareholder_id, tenant_id),
                                            KEY idx_period_id (period_id),
                                            KEY idx_store_id (store_id),
                                            KEY idx_period_month (period_month),
                                            KEY idx_shareholder_id (shareholder_id),
                                            KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='餐饮分红明细表';
ALTER TABLE restaurant_dividend_detail
    ADD COLUMN dept_id BIGINT NOT NULL COMMENT '部门编号' AFTER store_id;

CREATE INDEX idx_dividend_detail_dept_id ON restaurant_dividend_detail(dept_id);


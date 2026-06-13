CREATE TABLE restaurant_revenue (
                                    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '营业收入编号',
                                    store_id BIGINT NOT NULL COMMENT '门店编号',
                                    biz_date DATE NOT NULL COMMENT '营业日期',
                                    source VARCHAR(50) NOT NULL COMMENT '收入来源',
                                    amount DECIMAL(18,2) NOT NULL COMMENT '收入金额',
                                    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0待确认 1已确认 2已作废',
                                    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
                                    creator VARCHAR(64) DEFAULT '' COMMENT '创建者',
                                    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    updater VARCHAR(64) DEFAULT '' COMMENT '更新者',
                                    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                    deleted BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                                    tenant_id BIGINT NOT NULL DEFAULT 0 COMMENT '租户编号',
                                    PRIMARY KEY (id),
                                    UNIQUE KEY uk_store_date_source_tenant (store_id, biz_date, source, tenant_id),
                                    KEY idx_store_id (store_id),
                                    KEY idx_biz_date (biz_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='餐饮营业收入表';
ALTER TABLE restaurant_revenue
    ADD COLUMN dept_id BIGINT NOT NULL COMMENT '部门编号' AFTER store_id;

CREATE INDEX idx_revenue_dept_id ON restaurant_revenue(dept_id);
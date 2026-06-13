CREATE TABLE restaurant_store_shareholder (
                                              id BIGINT NOT NULL AUTO_INCREMENT COMMENT '门店股东持股编号',
                                              store_id BIGINT NOT NULL COMMENT '门店编号',
                                              shareholder_id BIGINT NOT NULL COMMENT '股东编号',
                                              share_ratio DECIMAL(5,2) NOT NULL COMMENT '持股比例，例如 40.00 表示 40%',
                                              invest_amount DECIMAL(18,2) DEFAULT NULL COMMENT '出资金额',
                                              join_time DATETIME NOT NULL COMMENT '入股时间',
                                              exit_time DATETIME DEFAULT NULL COMMENT '退出时间',
                                              status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0正常 1退出/禁用',
                                              remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
                                              creator VARCHAR(64) DEFAULT '' COMMENT '创建者',
                                              create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                              updater VARCHAR(64) DEFAULT '' COMMENT '更新者',
                                              update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                              deleted BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
                                              tenant_id BIGINT NOT NULL DEFAULT 0 COMMENT '租户编号',
                                              PRIMARY KEY (id),
                                              UNIQUE KEY uk_store_shareholder_tenant (store_id, shareholder_id, tenant_id),
                                              KEY idx_store_id (store_id),
                                              KEY idx_shareholder_id (shareholder_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='餐饮门店股东持股表';
ALTER TABLE restaurant_store_shareholder
    ADD COLUMN dept_id BIGINT NOT NULL COMMENT '部门编号' AFTER store_id;

CREATE INDEX idx_store_shareholder_dept_id ON restaurant_store_shareholder(dept_id);
-- 餐饮共享股东分红系统：演示数据初始化脚本
-- 用途：快速让经营看板、收入成本、分红账期、分红明细、审批、操作日志有数据可演示。
-- 前提：已经执行 01_restaurant_schema.sql、02_restaurant_dict.sql、03_restaurant_menu.sql。
-- 租户：默认使用 tenant_id = 1。

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 1. 演示部门：用于门店级数据权限演示
DELETE FROM system_dept WHERE id IN (6200, 6201, 6202, 6203);
INSERT INTO system_dept (id, name, parent_id, sort, leader_user_id, phone, email, status, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
                                                                                                                                                              (6200, '餐饮事业部', 100, 1, 1, '15800000000', 'restaurant@example.com', 0, '1', NOW(), '1', NOW(), b'0', 1),
                                                                                                                                                              (6201, '春熙路火锅店', 6200, 1, 100, '15800000001', 'cx@example.com', 0, '1', NOW(), '1', NOW(), b'0', 1),
                                                                                                                                                              (6202, '天府广场烤肉店', 6200, 2, 103, '15800000002', 'tf@example.com', 0, '1', NOW(), '1', NOW(), b'0', 1),
                                                                                                                                                              (6203, '财务审批组', 6200, 3, 104, '15800000003', 'finance@example.com', 0, '1', NOW(), '1', NOW(), b'0', 1);

-- 2. 给项目原有演示用户绑定餐饮角色，不修改账号密码。
-- 如果你不知道这些账号密码，在后台“系统管理 -> 用户管理”重置密码即可。
DELETE FROM system_user_role WHERE role_id IN (8101, 8102, 8103, 8104);
INSERT INTO system_user_role (user_id, role_id, creator, create_time, updater, update_time, deleted, tenant_id)
SELECT 103, 8101, '1', NOW(), '1', NOW(), b'0', 1 WHERE EXISTS (SELECT 1 FROM system_users WHERE id = 103);
INSERT INTO system_user_role (user_id, role_id, creator, create_time, updater, update_time, deleted, tenant_id)
SELECT 104, 8102, '1', NOW(), '1', NOW(), b'0', 1 WHERE EXISTS (SELECT 1 FROM system_users WHERE id = 104);
INSERT INTO system_user_role (user_id, role_id, creator, create_time, updater, update_time, deleted, tenant_id)
SELECT 100, 8103, '1', NOW(), '1', NOW(), b'0', 1 WHERE EXISTS (SELECT 1 FROM system_users WHERE id = 100);

-- 3. 清理旧演示数据
DELETE FROM restaurant_operate_log WHERE id BETWEEN 900001 AND 900099;
DELETE FROM restaurant_dividend_approve_record WHERE id BETWEEN 900001 AND 900099;
DELETE FROM restaurant_dividend_detail WHERE id BETWEEN 900001 AND 900099;
DELETE FROM restaurant_dividend_period WHERE id BETWEEN 900001 AND 900099;
DELETE FROM restaurant_cost WHERE id BETWEEN 900001 AND 900099;
DELETE FROM restaurant_revenue WHERE id BETWEEN 900001 AND 900099;
DELETE FROM restaurant_store_shareholder WHERE id BETWEEN 900001 AND 900099;
DELETE FROM restaurant_shareholder WHERE id BETWEEN 900001 AND 900099;
DELETE FROM restaurant_store WHERE id BETWEEN 900001 AND 900099;

-- 4. 门店、股东、持股关系
INSERT INTO restaurant_store (id, name, code, dept_id, manager_user_id, address, phone, open_date, status, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
                                                                                                                                                                                       (900001, '春熙路火锅店', 'CX-HG-001', 6201, 100, '成都市锦江区春熙路 88 号', '028-88880001', '2025-05-01', 0, '演示门店：火锅', '1', NOW(), '1', NOW(), b'0', 1),
                                                                                                                                                                                       (900002, '天府广场烤肉店', 'TF-KR-001', 6202, 103, '成都市青羊区天府广场 66 号', '028-88880002', '2025-08-01', 0, '演示门店：烤肉', '1', NOW(), '1', NOW(), b'0', 1);

INSERT INTO restaurant_shareholder (id, user_id, dept_id, name, phone, id_card, bank_name, bank_account, status, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
                                                                                                                                                                                             (900001, 103, 6201, '张三', '13900000001', NULL, '招商银行成都分行', '6222000000000001', 0, '演示股东，绑定用户 103', '1', NOW(), '1', NOW(), b'0', 1),
                                                                                                                                                                                             (900002, 104, 6201, '李四', '13900000002', NULL, '建设银行成都分行', '6222000000000002', 0, '演示股东，绑定用户 104', '1', NOW(), '1', NOW(), b'0', 1),
                                                                                                                                                                                             (900003, NULL, 6202, '王五', '13900000003', NULL, '工商银行成都分行', '6222000000000003', 0, '演示股东', '1', NOW(), '1', NOW(), b'0', 1);

INSERT INTO restaurant_store_shareholder (id, store_id, dept_id, shareholder_id, share_ratio, invest_amount, join_time, exit_time, status, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
                                                                                                                                                                                                                       (900001, 900001, 6201, 900001, 50.00, 500000.00, '2025-05-01 00:00:00', NULL, 0, '春熙路店持股 50%', '1', NOW(), '1', NOW(), b'0', 1),
                                                                                                                                                                                                                       (900002, 900001, 6201, 900002, 30.00, 300000.00, '2025-05-01 00:00:00', NULL, 0, '春熙路店持股 30%', '1', NOW(), '1', NOW(), b'0', 1),
                                                                                                                                                                                                                       (900003, 900001, 6201, 900003, 20.00, 200000.00, '2025-05-01 00:00:00', NULL, 0, '春熙路店持股 20%', '1', NOW(), '1', NOW(), b'0', 1),
                                                                                                                                                                                                                       (900004, 900002, 6202, 900001, 60.00, 600000.00, '2025-08-01 00:00:00', NULL, 0, '天府广场店持股 60%', '1', NOW(), '1', NOW(), b'0', 1),
                                                                                                                                                                                                                       (900005, 900002, 6202, 900003, 40.00, 400000.00, '2025-08-01 00:00:00', NULL, 0, '天府广场店持股 40%', '1', NOW(), '1', NOW(), b'0', 1);

-- 5. 已确认收入和成本：经营看板、分红计算需要 status=1
INSERT INTO restaurant_revenue (id, store_id, dept_id, biz_date, source, amount, status, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
                                                                                                                                                                     (900001, 900001, 6201, '2026-06-01', 'DINE_IN', 38000.00, 1, '演示：堂食收入', '1', NOW(), '1', NOW(), b'0', 1),
                                                                                                                                                                     (900002, 900001, 6201, '2026-06-02', 'TAKE_OUT', 12000.00, 1, '演示：外卖收入', '1', NOW(), '1', NOW(), b'0', 1),
                                                                                                                                                                     (900003, 900001, 6201, '2026-06-03', 'GROUPON', 18000.00, 1, '演示：团购收入', '1', NOW(), '1', NOW(), b'0', 1),
                                                                                                                                                                     (900004, 900002, 6202, '2026-06-01', 'DINE_IN', 32000.00, 1, '演示：堂食收入', '1', NOW(), '1', NOW(), b'0', 1),
                                                                                                                                                                     (900005, 900002, 6202, '2026-06-02', 'TAKE_OUT', 8000.00, 1, '演示：外卖收入', '1', NOW(), '1', NOW(), b'0', 1),
                                                                                                                                                                     (900006, 900002, 6202, '2026-06-03', 'GROUPON', 16000.00, 1, '演示：团购收入', '1', NOW(), '1', NOW(), b'0', 1);

INSERT INTO restaurant_cost (id, store_id, dept_id, biz_date, cost_type, amount, status, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
                                                                                                                                                                     (900001, 900001, 6201, '2026-06-01', 'FOOD_MATERIAL', 15000.00, 1, '演示：食材采购', '1', NOW(), '1', NOW(), b'0', 1),
                                                                                                                                                                     (900002, 900001, 6201, '2026-06-02', 'LABOR', 8000.00, 1, '演示：人工工资', '1', NOW(), '1', NOW(), b'0', 1),
                                                                                                                                                                     (900003, 900001, 6201, '2026-06-03', 'WATER_ELECTRICITY', 2500.00, 1, '演示：水电燃气', '1', NOW(), '1', NOW(), b'0', 1),
                                                                                                                                                                     (900004, 900002, 6202, '2026-06-01', 'FOOD_MATERIAL', 13000.00, 1, '演示：食材采购', '1', NOW(), '1', NOW(), b'0', 1),
                                                                                                                                                                     (900005, 900002, 6202, '2026-06-02', 'LABOR', 7000.00, 1, '演示：人工工资', '1', NOW(), '1', NOW(), b'0', 1),
                                                                                                                                                                     (900006, 900002, 6202, '2026-06-03', 'WATER_ELECTRICITY', 2200.00, 1, '演示：水电燃气', '1', NOW(), '1', NOW(), b'0', 1);

-- 6. 分红账期、明细、审批记录：用于看板和分红页面直接有可见数据
INSERT INTO restaurant_dividend_period (id, store_id, dept_id, period_month, start_date, end_date, total_revenue, total_cost, profit_amount, reserve_amount, distributable_profit, status, generated_time, confirmed_time, paid_time, canceled_time, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
                                                                                                                                                                                                                                                                                                                                 (900001, 900001, 6201, '2026-06', '2026-06-01', '2026-06-30', 68000.00, 25500.00, 42500.00, 2500.00, 40000.00, 2, NOW(), NOW(), NOW(), NULL, '演示：春熙路店 2026-06 已发放', '1', NOW(), '1', NOW(), b'0', 1),
                                                                                                                                                                                                                                                                                                                                 (900002, 900002, 6202, '2026-06', '2026-06-01', '2026-06-30', 56000.00, 22200.00, 33800.00, 1800.00, 32000.00, 4, NOW(), NOW(), NULL, NULL, '演示：天府广场店 2026-06 审批中', '1', NOW(), '1', NOW(), b'0', 1);

INSERT INTO restaurant_dividend_detail (id, period_id, store_id, dept_id, period_month, store_shareholder_id, shareholder_id, shareholder_name, share_ratio, profit_amount, dividend_amount, rounding_diff_amount, status, paid_time, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
                                                                                                                                                                                                                                                                                                                  (900001, 900001, 900001, 6201, '2026-06', 900001, 900001, '张三', 50.00, 40000.00, 20000.00, 0.00, 1, NOW(), '演示：已发放', '1', NOW(), '1', NOW(), b'0', 1),
                                                                                                                                                                                                                                                                                                                  (900002, 900001, 900001, 6201, '2026-06', 900002, 900002, '李四', 30.00, 40000.00, 12000.00, 0.00, 1, NOW(), '演示：已发放', '1', NOW(), '1', NOW(), b'0', 1),
                                                                                                                                                                                                                                                                                                                  (900003, 900001, 900001, 6201, '2026-06', 900003, 900003, '王五', 20.00, 40000.00, 8000.00, 0.00, 1, NOW(), '演示：已发放', '1', NOW(), '1', NOW(), b'0', 1),
                                                                                                                                                                                                                                                                                                                  (900004, 900002, 900002, 6202, '2026-06', 900004, 900001, '张三', 60.00, 32000.00, 19200.00, 0.00, 0, NULL, '演示：待发放', '1', NOW(), '1', NOW(), b'0', 1),
                                                                                                                                                                                                                                                                                                                  (900005, 900002, 900002, 6202, '2026-06', 900005, 900003, '王五', 40.00, 32000.00, 12800.00, 0.00, 0, NULL, '演示：待发放', '1', NOW(), '1', NOW(), b'0', 1);

INSERT INTO restaurant_dividend_approve_record (id, period_id, store_id, dept_id, period_month, approve_status, submit_user_id, submit_time, approve_user_id, approve_time, approve_reason, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
                                                                                                                                                                                                                                                                        (900001, 900001, 900001, 6201, '2026-06', 1, 103, NOW(), 104, NOW(), '演示：审批通过', '春熙路店分红审批通过', '1', NOW(), '1', NOW(), b'0', 1),
                                                                                                                                                                                                                                                                        (900002, 900002, 900002, 6202, '2026-06', 0, 103, NOW(), 104, NULL, NULL, '天府广场店分红待审批', '1', NOW(), '1', NOW(), b'0', 1);

INSERT INTO restaurant_operate_log (id, biz_type, biz_id, store_id, dept_id, period_id, period_month, operate_type, before_status, after_status, operate_user_id, operate_time, remark, creator, create_time, updater, update_time, deleted, tenant_id) VALUES
                                                                                                                                                                                                                                                            (900001, 'DIVIDEND_PERIOD', 900001, 900001, 6201, 900001, '2026-06', 'DIVIDEND_GENERATE', NULL, 0, 1, NOW(), '演示：生成春熙路店 2026-06 分红账期', '1', NOW(), '1', NOW(), b'0', 1),
                                                                                                                                                                                                                                                            (900002, 'DIVIDEND_PERIOD', 900001, 900001, 6201, 900001, '2026-06', 'DIVIDEND_PAY', 5, 2, 1, NOW(), '演示：发放春熙路店 2026-06 分红', '1', NOW(), '1', NOW(), b'0', 1),
                                                                                                                                                                                                                                                            (900003, 'DIVIDEND_PERIOD', 900002, 900002, 6202, 900002, '2026-06', 'DIVIDEND_SUBMIT_APPROVE', 1, 4, 103, NOW(), '演示：提交天府广场店 2026-06 分红审批', '1', NOW(), '1', NOW(), b'0', 1);

SET FOREIGN_KEY_CHECKS = 1;

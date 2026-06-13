package cn.iocoder.yudao.module.restaurant.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * Restaurant 错误码枚举类
 *
 * 餐饮模块错误码区间建议使用 1-030-000-000 段。
 */
public interface ErrorCodeConstants {

    // ========== 门店 1-030-001-000 ==========
    ErrorCode STORE_NOT_EXISTS = new ErrorCode(1_030_001_000, "门店不存在");
    ErrorCode STORE_CODE_DUPLICATE = new ErrorCode(1_030_001_001, "门店编码已经存在");
    ErrorCode STORE_NAME_DUPLICATE = new ErrorCode(1_030_001_002, "同名门店已经存在");
    ErrorCode STORE_NOT_ENABLE = new ErrorCode(1_030_001_003, "门店【{}】已被禁用");
    ErrorCode STORE_DEPT_NOT_EXISTS = new ErrorCode(1_030_001_010, "门店所属部门不存在或已禁用");
    // ========== 股东 1-030-002-000 ==========
    ErrorCode SHAREHOLDER_NOT_EXISTS = new ErrorCode(1_030_002_000, "股东不存在");
    ErrorCode SHAREHOLDER_PHONE_DUPLICATE = new ErrorCode(1_030_002_001, "股东手机号已经存在");
    ErrorCode SHAREHOLDER_USER_DUPLICATE = new ErrorCode(1_030_002_002, "该系统用户已经绑定其他股东");
    ErrorCode SHAREHOLDER_NOT_ENABLE = new ErrorCode(1_030_002_003, "股东【{}】已被禁用");
    ErrorCode SHAREHOLDER_HAS_STORE_RELATION = new ErrorCode(1_030_002_004, "股东已绑定门店持股关系，不允许删除");
    // ========== 门店股东持股 1-030-003-000 ==========
    ErrorCode STORE_SHAREHOLDER_NOT_EXISTS = new ErrorCode(1_030_003_000, "门店股东持股关系不存在");
    ErrorCode STORE_SHAREHOLDER_DUPLICATE = new ErrorCode(1_030_003_001, "该门店已经绑定该股东");
    ErrorCode STORE_SHARE_RATIO_OVER_LIMIT = new ErrorCode(1_030_003_002, "门店股东持股比例总和不能超过 100%");
    ErrorCode STORE_SHARE_RATIO_INVALID = new ErrorCode(1_030_003_003, "持股比例必须大于 0 且不能超过 100%");
    ErrorCode STORE_SHAREHOLDER_HAS_DIVIDEND = new ErrorCode(1_030_003_004, "该持股关系已经参与分红，不允许删除");
    // ========== 营业收入 1-030-004-000 ==========
    ErrorCode REVENUE_NOT_EXISTS = new ErrorCode(1_030_004_000, "营业收入记录不存在");
    ErrorCode REVENUE_AMOUNT_INVALID = new ErrorCode(1_030_004_001, "营业收入金额必须大于 0");
    ErrorCode REVENUE_DUPLICATE = new ErrorCode(1_030_004_002, "该门店在该日期、该来源的营业收入已存在");
    ErrorCode REVENUE_CONFIRMED_NOT_ALLOW_UPDATE = new ErrorCode(1_030_004_003, "已确认的营业收入不允许修改");
    ErrorCode REVENUE_CONFIRMED_NOT_ALLOW_DELETE = new ErrorCode(1_030_004_004, "已确认的营业收入不允许删除");
    ErrorCode REVENUE_STATUS_NOT_ALLOW_CONFIRM = new ErrorCode(1_030_004_005, "当前营业收入状态不允许确认");
    ErrorCode REVENUE_STATUS_NOT_ALLOW_CANCEL = new ErrorCode(1_030_004_006, "当前营业收入状态不允许作废");
    // ========== 成本支出 1-030-005-000 ==========
    ErrorCode COST_NOT_EXISTS = new ErrorCode(1_030_005_000, "成本支出记录不存在");
    ErrorCode COST_AMOUNT_INVALID = new ErrorCode(1_030_005_001, "成本金额必须大于 0");
    ErrorCode COST_DUPLICATE = new ErrorCode(1_030_005_002, "该门店在该日期、该成本类型的成本支出已存在");
    ErrorCode COST_CONFIRMED_NOT_ALLOW_UPDATE = new ErrorCode(1_030_005_003, "已确认的成本支出不允许修改");
    ErrorCode COST_CONFIRMED_NOT_ALLOW_DELETE = new ErrorCode(1_030_005_004, "已确认的成本支出不允许删除");
    ErrorCode COST_STATUS_NOT_ALLOW_CONFIRM = new ErrorCode(1_030_005_005, "当前成本支出状态不允许确认");
    ErrorCode COST_STATUS_NOT_ALLOW_CANCEL = new ErrorCode(1_030_005_006, "当前成本支出状态不允许作废");

    // ========== 分红账期 1-030-006-000 ==========
    ErrorCode DIVIDEND_PERIOD_NOT_EXISTS = new ErrorCode(1_030_006_000, "分红账期不存在");
    ErrorCode DIVIDEND_PERIOD_DUPLICATE = new ErrorCode(1_030_006_001, "该门店该账期月份的分红账期已经存在");
    ErrorCode DIVIDEND_PERIOD_FORMAT_ERROR = new ErrorCode(1_030_006_002, "账期月份格式错误，请使用 yyyy-MM 格式");
    ErrorCode DIVIDEND_PERIOD_NO_SHAREHOLDER = new ErrorCode(1_030_006_003, "该门店没有正常持股股东，不能生成分红账期");
    ErrorCode DIVIDEND_PERIOD_STATUS_NOT_ALLOW_CONFIRM = new ErrorCode(1_030_006_004, "当前分红账期状态不允许确认");
    ErrorCode DIVIDEND_PERIOD_STATUS_NOT_ALLOW_PAY = new ErrorCode(1_030_006_005, "当前分红账期状态不允许发放");
    ErrorCode DIVIDEND_PERIOD_STATUS_NOT_ALLOW_CANCEL = new ErrorCode(1_030_006_006, "当前分红账期状态不允许作废");
    ErrorCode DIVIDEND_PERIOD_CONFIRMED_NOT_ALLOW_DELETE = new ErrorCode(1_030_006_007, "已确认或已发放的分红账期不允许删除");
    ErrorCode DIVIDEND_PERIOD_PAID_NOT_ALLOW_CANCEL = new ErrorCode(1_030_006_008, "已发放的分红账期不允许作废");
    ErrorCode DIVIDEND_PERIOD_DETAIL_NOT_EXISTS = new ErrorCode(1_030_006_009, "分红账期还没有生成分红明细，请先生成分红明细");
    // ========== 分红明细 1-030-007-000 ==========
    ErrorCode DIVIDEND_DETAIL_NOT_EXISTS = new ErrorCode(1_030_007_000, "分红明细不存在");
    ErrorCode DIVIDEND_DETAIL_DUPLICATE = new ErrorCode(1_030_007_001, "该分红账期已经生成过分红明细");
    ErrorCode DIVIDEND_DETAIL_PERIOD_NOT_GENERATED = new ErrorCode(1_030_007_002, "只有已生成状态的分红账期才能生成分红明细");
    ErrorCode DIVIDEND_DETAIL_PERIOD_NOT_ALLOW_DELETE = new ErrorCode(1_030_007_003, "当前分红账期状态不允许删除分红明细");
    ErrorCode DIVIDEND_DETAIL_NO_SHAREHOLDER = new ErrorCode(1_030_007_004, "该门店没有正常持股股东，不能生成分红明细");
    ErrorCode DIVIDEND_DETAIL_PROFIT_NOT_POSITIVE = new ErrorCode(1_030_007_005, "可分红金额必须大于 0，才能生成分红明细");
    ErrorCode DIVIDEND_DETAIL_SHARE_RATIO_INVALID = new ErrorCode(1_030_007_006, "股东持股比例异常，不能生成分红明细");
    // ========== 幂等与历史数据保护 1-030-008-000 ==========
    ErrorCode DIVIDEND_PERIOD_GENERATING_DUPLICATE = new ErrorCode(1_030_008_000, "该门店该账期正在生成或已经生成，请勿重复操作");
    ErrorCode DIVIDEND_DETAIL_GENERATING_DUPLICATE = new ErrorCode(1_030_008_001, "该账期分红明细正在生成或已经生成，请勿重复操作");

    ErrorCode REVENUE_ALREADY_IN_DIVIDEND_PERIOD = new ErrorCode(1_030_008_002, "该营业收入已参与分红账期，不允许作废");
    ErrorCode COST_ALREADY_IN_DIVIDEND_PERIOD = new ErrorCode(1_030_008_003, "该成本支出已参与分红账期，不允许作废");

    ErrorCode STORE_HAS_BUSINESS_DATA = new ErrorCode(1_030_008_004, "该门店已有收入、成本或分红数据，不允许删除，请改为禁用");
    ErrorCode SHAREHOLDER_HAS_BUSINESS_DATA = new ErrorCode(1_030_008_005, "该股东已有持股关系或分红记录，不允许删除，请改为禁用");
    ErrorCode STORE_SHAREHOLDER_HAS_DIVIDEND_DATA = new ErrorCode(1_030_008_006, "该持股关系已参与分红，不允许删除，请改为退出状态");
    ErrorCode STORE_SHAREHOLDER_CONFIRMED_NOT_ALLOW_UPDATE_RATIO = new ErrorCode(1_030_008_007, "该持股关系已参与分红，不允许直接修改持股比例，请走股权变更流程");
    // ========== 营业收入导入 1-030-009-000 ==========
    ErrorCode REVENUE_IMPORT_LIST_IS_EMPTY = new ErrorCode(1_030_009_000, "导入营业收入数据不能为空");
    // ========== 成本支出导入 1-030-010-000 ==========
    ErrorCode COST_IMPORT_LIST_IS_EMPTY = new ErrorCode(1_030_010_000, "导入成本支出数据不能为空");
    // ========== Redis 分布式锁 1-030-011-000 ==========
    ErrorCode DIVIDEND_LOCK_PERIOD_MONTH_EMPTY = new ErrorCode(1_030_011_000, "分红锁账期月份不能为空");
    ErrorCode DIVIDEND_LOCK_STORE_ID_EMPTY = new ErrorCode(1_030_011_001, "分红锁门店编号不能为空");
    ErrorCode DIVIDEND_AUTO_GENERATE_LOCKED = new ErrorCode(1_030_011_002, "当前账期正在自动生成分红，请勿重复执行");
    ErrorCode DIVIDEND_STORE_PERIOD_GENERATE_LOCKED = new ErrorCode(1_030_011_003, "该门店该账期正在生成分红，请勿重复操作");
    // ========== 分红审批 1-030-012-000 ==========
    ErrorCode DIVIDEND_APPROVE_RECORD_NOT_EXISTS = new ErrorCode(1_030_012_000, "分红审批记录不存在");
    ErrorCode DIVIDEND_APPROVE_PERIOD_STATUS_NOT_ALLOW_SUBMIT = new ErrorCode(1_030_012_001, "当前分红账期状态不允许提交审批");
    ErrorCode DIVIDEND_APPROVE_PERIOD_STATUS_NOT_ALLOW_AUDIT = new ErrorCode(1_030_012_002, "当前分红账期状态不允许审批");
    ErrorCode DIVIDEND_APPROVE_RECORD_APPROVING_EXISTS = new ErrorCode(1_030_012_003, "当前分红账期已有审批中的记录，请勿重复提交");
    ErrorCode DIVIDEND_APPROVE_RECORD_NOT_APPROVING = new ErrorCode(1_030_012_004, "当前分红账期没有审批中的记录");
    ErrorCode DIVIDEND_PERIOD_NOT_APPROVED_NOT_ALLOW_PAY = new ErrorCode(1_030_012_005, "分红账期未审批通过，不允许发放");
    ErrorCode DIVIDEND_APPROVE_USER_NOT_CONFIGURED = new ErrorCode(1_030_012_006, "当前账期没有可用审批人，请检查门店部门负责人、审批角色或审批用户配置");
    ErrorCode DIVIDEND_APPROVE_USER_NOT_ALLOWED = new ErrorCode(1_030_012_007, "当前用户不是该账期的审批人，不能审批");
}
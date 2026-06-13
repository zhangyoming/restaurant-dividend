package cn.iocoder.yudao.module.restaurant.enums;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 分红账期状态枚举
 *
 * @author zhangyoming
 */
@Getter
@AllArgsConstructor
public enum DividendPeriodStatusEnum implements ArrayValuable<Integer> {

    GENERATED(0, "已生成"),
    CONFIRMED(1, "已确认"),
    PAID(2, "已发放"),
    CANCELED(3, "已作废"),

    APPROVING(4, "审批中"),
    APPROVED(5, "审批通过"),
    REJECTED(6, "审批驳回");

    public static final Integer[] ARRAYS = Arrays.stream(values())
            .map(DividendPeriodStatusEnum::getStatus)
            .toArray(Integer[]::new);

    /**
     * 状态
     */
    private final Integer status;

    /**
     * 状态名
     */
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

    public static String getNameByStatus(Integer status) {
        if (status == null) {
            return "";
        }
        return Arrays.stream(values())
                .filter(item -> item.getStatus().equals(status))
                .findFirst()
                .map(DividendPeriodStatusEnum::getName)
                .orElse("");
    }

}
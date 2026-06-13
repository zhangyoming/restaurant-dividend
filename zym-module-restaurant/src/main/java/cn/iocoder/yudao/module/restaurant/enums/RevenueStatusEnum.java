package cn.iocoder.yudao.module.restaurant.enums;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 营业收入状态枚举
 *
 * @author zhangyoming
 */
@Getter
@AllArgsConstructor
public enum RevenueStatusEnum implements ArrayValuable<Integer> {

    WAIT_CONFIRM(0, "待确认"),
    CONFIRMED(1, "已确认"),
    CANCELED(2, "已作废");

    public static final Integer[] ARRAYS = Arrays.stream(values())
            .map(RevenueStatusEnum::getStatus)
            .toArray(Integer[]::new);

    /**
     * 状态值
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

}
package cn.iocoder.yudao.module.restaurant.enums;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 分红明细状态枚举
 *
 * @author zhangyoming
 */
@Getter
@AllArgsConstructor
public enum DividendDetailStatusEnum implements ArrayValuable<Integer> {

    GENERATED(0, "已生成"),
    PAID(1, "已发放"),
    CANCELED(2, "已作废");

    public static final Integer[] ARRAYS = Arrays.stream(values())
            .map(DividendDetailStatusEnum::getStatus)
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
                .map(DividendDetailStatusEnum::getName)
                .orElse("");
    }

}
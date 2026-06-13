package cn.iocoder.yudao.module.restaurant.enums;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 分红审批状态枚举
 *
 * @author zhangyoming
 */
@Getter
@AllArgsConstructor
public enum DividendApproveStatusEnum implements ArrayValuable<Integer> {

    APPROVING(0, "审批中"),
    APPROVED(1, "审批通过"),
    REJECTED(2, "审批驳回");

    public static final Integer[] ARRAYS = Arrays.stream(values())
            .map(DividendApproveStatusEnum::getStatus)
            .toArray(Integer[]::new);

    private final Integer status;

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
                .map(DividendApproveStatusEnum::getName)
                .orElse("");
    }

}
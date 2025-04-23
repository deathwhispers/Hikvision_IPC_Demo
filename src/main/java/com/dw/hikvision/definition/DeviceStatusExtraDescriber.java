package com.dw.hikvision.definition;


/**
 * 定义设备额外属性值与描述的映射
 * <p>
 * ⚠️需要注意，实现该接口的状态类中，必须使用 {@link ApiModelProperty} 注解来描述每个属性，以便能准确的映射到 {@link DeviceStatusExtra} 中
 * 具体的转化实现请查看 {@link DeviceStatusExtraConverter}
 */
public interface DeviceStatusExtraDescriber {

    /**
     * 将状态类中的字段映射到 VmsExtra 中，定义每个字段值的具体含义。
     * 比如：电压状态 的值为 1 时表示 正常，值为 0 时表示异常
     */
    String describeValue(String value);
}

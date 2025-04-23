package com.dw.hikvision.domain;

import com.dw.hikvision.definition.DeviceStatusExtraDescriber;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 海康威视工作状态：用于接受 DEV_WORK_STATE_CB 中的状态变化
 *
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/4/17 17:25
 */
@Data
public class HikvisionWorkStatus implements DeviceStatusExtraDescriber {

    @ApiModelProperty(name = "设备状态", value = "0:正常, 1:异常（CPU占用率太高,超过85%, 2:硬件错误,例如串口死掉")
    private Integer deviceStatus;
    @ApiModelProperty(name = "本地显示状态", value = "0:正常, 1:异常")
    private Integer localDisplay;
    @ApiModelProperty(name = "硬盘状态", value = "0:正常, 1:异常")
    private Integer diskStatus;
    @ApiModelProperty(name = "语音通道状态", value = "0:所有通道都在使用中, 1:至少有一个通道未使用")
    private Integer audioChanStatus;
    @ApiModelProperty(name = " 告警输入状态", value = "0:正常, 1:异常")
    private Integer alarmInStatus;
    @ApiModelProperty(name = "告警输出状态", value = "0:正常, 1:异常")
    private Integer alarmOutStatus;

    @Override
    public String describeValue(String value) {
        if ("0".equals(value)) {
            return "正常";
        } else if ("1".equals(value)) {
            return "异常";
        }
        return "未知";
    }
}

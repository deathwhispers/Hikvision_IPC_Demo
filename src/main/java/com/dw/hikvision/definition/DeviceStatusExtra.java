package com.dw.hikvision.definition;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 设备额外状态信息
 */
@Data
@ApiModel("设备额外状态信息")
public class DeviceStatusExtra {

    @ApiModelProperty(value = "项", example = "fan")
    private String item;
    @ApiModelProperty(value = "名称", example = "风扇开关")
    private String name;
    @ApiModelProperty(value = "描述", example = "情报板风扇是否开启")
    private String description;
    @ApiModelProperty(value = "状态值", example = " 31")
    private String value;
    @ApiModelProperty(value = "状态值的描述", example = "异常")
    private String valueDesc;
}

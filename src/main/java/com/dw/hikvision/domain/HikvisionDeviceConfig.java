package com.dw.hikvision.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/4/22 14:39
 */
@Data
public class HikvisionDeviceConfig {
    @ApiModelProperty(name = "设备名称", value = "设备名称")
    private String dvrName;
    @ApiModelProperty(name = "序列号", value = "序列号")
    private String serialNumber;
    @ApiModelProperty(name = "DVR 通道个数", value = "DVR 通道个数")
    private Integer chanNum;
    @ApiModelProperty(name = "软件版本号", value = "软件版本号")
    private String softwareVersion;
    @ApiModelProperty(name = "软件生成日期", value = "软件生成日期")
    private String softwareBuildDate;
    @ApiModelProperty(name = "DSP软件版本", value = "DSP软件版本")
    private String DSPSoftwareBuildVersion;
    @ApiModelProperty(name = "DSP软件生成日期", value = "DSP软件生成日期")
    private String DSPSoftwareBuildDate;

}

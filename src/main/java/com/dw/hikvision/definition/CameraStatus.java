package com.dw.hikvision.definition;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 摄像机状态
 *
 * @author yanggj
 * @version 1.0.0
 * @date 2022/8/8 14:06
 */
@Data
@ApiModel(value = "摄像机状态")
public class CameraStatus {

    @ApiModelProperty("网络是否连通(true:网络连通,false:网络不通)")
    private Boolean attachable;

    @ApiModelProperty("端口是否可达(true:端口可达,false:端口不可达)")
    private Boolean reachable;

    @ApiModelProperty("在线状态(true:在线,false:不在线)")
    private Boolean onlineState;

    @ApiModelProperty("电源状态(true:开,false:关)")
    private Boolean powerState;

    @ApiModelProperty("版本")
    private String version;

    @ApiModelProperty("设备类型")
    private String deviceType;

    @ApiModelProperty("连接状态")
    private Integer connState;

    @ApiModelProperty("磁盘数量")
    private Integer diskNum;

    @ApiModelProperty("软件版本号")
    private String softwareVersion;

    @ApiModelProperty("硬件版本号")
    private String hardwareVersion;

    @ApiModelProperty("web版本号")
    private String webVersion;

    @ApiModelProperty("亮度可调 1:可, 0:不可")
    private Integer brightnessEn;

    @ApiModelProperty("对比度可调 1:可, 0:不可")
    private Integer contrastEn;

    @ApiModelProperty("色度可调 1:可, 0:不可")
    private Integer colorEn;

    @ApiModelProperty("增益可调 1:可, 0:不可")
    private Integer gainEn;

    @ApiModelProperty("饱和度可调 1:可, 0:不可")
    private Integer saturationEn;

    @ApiModelProperty("背光补偿 0:不支持背光补偿,1:支持一级补偿(开,关)，2:支持两级补偿(关,高,低)，3:支持三级补偿(关,高,中,低)")
    private Integer backlightEn;

    @ApiModelProperty("镜像 1:支持,0:不支持")
    private Integer mirrorEn;

    @ApiModelProperty("翻转 1:支持,0:不支持")
    private Integer flipEn;

    @ApiModelProperty("90度旋转 1:支持,0:不支持")
    private Integer rotate90;

    @ApiModelProperty("其他属性")
    private List<DeviceStatusExtra> extras;

}

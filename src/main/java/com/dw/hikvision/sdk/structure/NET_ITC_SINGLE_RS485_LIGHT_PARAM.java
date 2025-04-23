package com.dw.hikvision.sdk.structure;

/**
 * 单个485接入信号灯参数
 *
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/4/21 15:59
 */
public class NET_ITC_SINGLE_RS485_LIGHT_PARAM extends SdkStructure {
    public byte byLightType; //交通灯导向类型，0-左转灯，1-直行灯，2-右转灯
    public byte byRelatedLightChan; //关联的红绿灯检测器通道号
    public byte byInputLight;    //接入的信号灯类型，0-接红灯，1-接绿灯
    public byte byRelatedYLightChan; //关联的黄灯检测器通道号
    public byte[] byRes = new byte[16];
}
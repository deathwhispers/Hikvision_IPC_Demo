package com.dw.hikvision.sdk.structure;

/**
 * 单个IO接入信号灯参数
 *
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/4/21 15:57
 */
public class NET_ITC_SINGLE_IO_LIGHT_PARAM extends SdkStructure {
    public byte byLightType; //交通灯导向类型,0-左转灯,1-直行灯,2-右转灯
    public byte byRelatedIO; //关联的IO口号
    public byte byRedLightState; //红灯电平状态，0-高电平红灯，1-低电平红灯
    public byte[] byRes = new byte[17];
}
package com.dw.hikvision.sdk.structure;

/**
 * 交通信号灯参数结构
 *
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/4/21 15:54
 */
public class NET_ITC_TRAFFIC_LIGHT_PARAM extends SdkStructure {
    public byte bySource; //交通信号灯接入源，0-IO接入，1-RS485接入
    public byte[] byRes1 = new byte[3];
    //信号灯接入参数
    public NET_ITC_LIGHT_ACCESSPARAM_UNION struLightAccess = new NET_ITC_LIGHT_ACCESSPARAM_UNION();
    public byte[] byRes = new byte[32];
}

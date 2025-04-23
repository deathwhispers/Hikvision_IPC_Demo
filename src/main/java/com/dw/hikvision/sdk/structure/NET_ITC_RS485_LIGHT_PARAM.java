package com.dw.hikvision.sdk.structure;

import static com.dw.hikvision.sdk.HCNetSDK.MAX_LIGHT_NUM;

/**
 * 485接入信号灯参数
 *
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/4/21 15:58
 */
public class NET_ITC_RS485_LIGHT_PARAM extends SdkStructure {
    public NET_ITC_SINGLE_RS485_LIGHT_PARAM[] struRS485Light = new NET_ITC_SINGLE_RS485_LIGHT_PARAM[MAX_LIGHT_NUM]; //单个485接入信号灯参数
    public byte[] byRes = new byte[8];
}
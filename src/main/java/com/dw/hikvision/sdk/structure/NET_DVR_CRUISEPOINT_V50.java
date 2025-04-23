package com.dw.hikvision.sdk.structure;

/**
 * 巡航路径配置结构体
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/4/21 14:31
 */
public class NET_DVR_CRUISEPOINT_V50  extends SdkStructure {
    public int dwSize;
    public NET_DVR_CRUISEPOINT_PARAM[] struCruisePoint = (NET_DVR_CRUISEPOINT_PARAM[]) new NET_DVR_CRUISEPOINT_PARAM().toArray(256);
    public byte[] byRes = new byte[64];
}
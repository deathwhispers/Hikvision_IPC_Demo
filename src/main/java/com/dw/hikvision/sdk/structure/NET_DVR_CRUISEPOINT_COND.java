package com.dw.hikvision.sdk.structure;

/**
 * 巡航路径配置条件结构体
 *
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/4/21 14:33
 */
public class NET_DVR_CRUISEPOINT_COND extends SdkStructure {
    public int dwSize;
    public int dwChan;
    public short wRouteNo;
    public byte[] byRes = new byte[30];
}
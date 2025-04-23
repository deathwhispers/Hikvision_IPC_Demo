package com.dw.hikvision.sdk.structure;

/**
 * GBT28181协议的设备编码通道配置
 *
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/4/21 14:34
 */
public class NET_DVR_GBT28181_CHANINFO_CFG extends SdkStructure {
    public int dwSize;
    public byte[] szVideoChannelNumID = new byte[64];//设备视频通道编码ID：64字节字符串，仅限数字
    public byte[] byRes = new byte[256];
}
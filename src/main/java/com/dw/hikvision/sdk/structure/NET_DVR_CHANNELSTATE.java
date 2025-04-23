package com.dw.hikvision.sdk.structure;

import com.dw.hikvision.sdk.HCNetSDK;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/4/21 14:10
 */
public class NET_DVR_CHANNELSTATE extends SdkStructure {//通道状态
    public byte byRecordStatic; //通道是否在录像,0-不录像,1-录像
    public byte bySignalStatic; //连接的信号状态,0-正常,1-信号丢失
    public byte byHardwareStatic;//通道硬件状态,0-正常,1-异常,例如DSP死掉
    public byte reservedData;        //保留
    public int dwBitRate;//实际码率
    public int dwLinkNum;//客户端连接的个数
    public int[] dwClientIP = new int[HCNetSDK.MAX_LINK];//客户端的IP地址
}
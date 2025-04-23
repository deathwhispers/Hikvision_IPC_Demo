package com.dw.hikvision.sdk.structure;

import com.dw.hikvision.sdk.HCNetSDK;

/**
 * 通道状态(9000扩展)
 *
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/4/21 14:09
 */
public class NET_DVR_CHANNELSTATE_V30 extends SdkStructure {//通道状态(9000扩展)
    public byte byRecordStatic; //通道是否在录像,0-不录像,1-录像
    public byte bySignalStatic; //连接的信号状态,0-正常,1-信号丢失
    public byte byHardwareStatic;//通道硬件状态,0-正常,1-异常,例如DSP死掉
    public byte byRes1;        //保留
    public int dwBitRate;//实际码率
    public int dwLinkNum;//客户端连接的个数
    public NET_DVR_IPADDR[] struClientIP = new NET_DVR_IPADDR[HCNetSDK.MAX_LINK];//客户端的IP地址
    public int dwIPLinkNum;//如果该通道为IP接入，那么表示IP接入当前的连接数
    public byte byExceedMaxLink;        // 是否超出了单路6路连接数 0 - 未超出, 1-超出
    public byte[] byRes = new byte[3];        // 保留字节
    public int dwAllBitRate;      //所有实际码率之和
    public int dwChannelNo;    //当前的通道号，0xffffffff表示无效
}
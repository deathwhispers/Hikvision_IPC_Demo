package com.dw.hikvision.sdk.structure;

import static com.dw.hikvision.sdk.HCNetSDK.*;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/4/21 14:39
 */
public class NET_DVR_CMS_PARAM extends SdkStructure {
    public int dwSize;
    public NET_DVR_IPADDR struAddr = new NET_DVR_IPADDR();                    // 平台服务器IP
    public short wServerPort;                   // 平台服务器侦听端口，
    public byte bySeverProtocolType;            //平台协议类型 1-私有，2-Ehome
    public byte byStatus;                         //设备注册到该平台的状态，1-未注册，2-已注册
    public byte[] sDeviceId = new byte[NAME_LEN/*32*/];     //设备ID，由平台提供
    public byte[] sPassWord = new byte[PASSWD_LEN];  //密码
    /*********
     * IPC5.1.7 新增参数 Begin 2014-03-21
     ***********/
    public byte[] sPlatformEhomeVersion = new byte[NAME_LEN];//平台EHOME协议版本
    /*********
     * IPC5.1.7 新增参数 end 2014-03-21
     ***********/
    public byte byNetWork;                //网络类型：0- 无意义，1-自动，2-有线网络优先，3-有线网络，4-3G网络（无线网络），5-有线网络1，6-有线网络2
    public byte byAddressType;            //0 - 无意义, 1 - ipv4/ipv6地址，2 - 域名
    public byte byProtocolVersion;            //协议版本 0 - 无意义, 1 – v2.0，2 – v4.0,3-v2.6
    public byte byRes1;
    public byte[] sDomainName = new byte[MAX_DOMAIN_NAME/*64*/]; //平台服务器域名，byAddressType为2时有效
    public byte byEnable;      //0-关闭，1-开启
    public byte[] byRes = new byte[139];          // 保留字节
}
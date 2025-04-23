package com.dw.hikvision.sdk.structure;

import com.dw.hikvision.sdk.callback.FLoginResultCallBack;
import com.sun.jna.Pointer;

import static com.dw.hikvision.sdk.HCNetSDK.*;


/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/4/21 14:13
 */
public class NET_DVR_USER_LOGIN_INFO extends SdkStructure {
    public byte[] sDeviceAddress = new byte[NET_DVR_DEV_ADDRESS_MAX_LEN];
    public byte byUseTransport;
    public short wPort;
    public byte[] sUserName = new byte[NET_DVR_LOGIN_USERNAME_MAX_LEN];
    public byte[] sPassword = new byte[NET_DVR_LOGIN_PASSWD_MAX_LEN];
    public FLoginResultCallBack cbLoginResult;
    public Pointer pUser;
    public boolean bUseAsynLogin;
    public byte byProxyType; //0:不使用代理，1：使用标准代理，2：使用EHome代理
    public byte byUseUTCTime;    //0-不进行转换，默认,1-接口上输入输出全部使用UTC时间,SDK完成UTC时间与设备时区的转换,2-接口上输入输出全部使用平台本地时间，SDK完成平台本地时间与设备时区的转换
    public byte byLoginMode; //0-Private 1-ISAPI 2-自适应
    public byte byHttps;    //0-不适用tls，1-使用tls 2-自适应
    public int iProxyID;    //代理服务器序号，添加代理服务器信息时，相对应的服务器数组下表值
    public byte byVerifyMode;  //认证方式，0-不认证，1-双向认证，2-单向认证；认证仅在使用TLS的时候生效;
    public byte[] byRes2 = new byte[119];
}

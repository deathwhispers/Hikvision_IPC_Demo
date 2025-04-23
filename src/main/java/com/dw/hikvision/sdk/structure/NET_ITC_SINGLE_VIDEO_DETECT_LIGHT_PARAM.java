package com.dw.hikvision.sdk.structure;

/**
 * 单组视频检测交通信号灯参数结构
 *
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/4/21 16:01
 */
public class NET_ITC_SINGLE_VIDEO_DETECT_LIGHT_PARAM extends SdkStructure {
    public byte byLightNum; //交通灯个数
    public byte byStraightLight; //是否有直行标志灯，0-否 ，1-是
    public byte byLeftLight; //是否有左转标志灯，0-否，1-是
    public byte byRightLight; //是否有右转标志灯，0-否，1-是
    public byte byRedLight;//是否有红灯，0-否，1-是
    public byte byGreenLight; //是否有绿灯，0-否，1-是
    public byte byYellowLight; //是否有黄灯，0-否，1-是
    public byte byYellowLightTime;//取值范围（0～10s）（ITC3.7Ver）
    public NET_POS_PARAM struLightRect; //交通灯区域
    public byte[] byRes = new byte[24];
}
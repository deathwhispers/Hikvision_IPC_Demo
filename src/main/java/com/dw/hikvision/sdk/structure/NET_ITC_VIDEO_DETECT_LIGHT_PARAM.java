package com.dw.hikvision.sdk.structure;

import static com.dw.hikvision.sdk.HCNetSDK.MAX_VIDEO_DETECT_LIGHT_NUM;

/**
 * 视频检测交通信号灯参数结构(最大可有12个区域检测，488字节)
 *
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/4/21 16:00
 */
public class NET_ITC_VIDEO_DETECT_LIGHT_PARAM extends SdkStructure {
    public NET_ITC_SINGLE_VIDEO_DETECT_LIGHT_PARAM[] struTrafficLight = new NET_ITC_SINGLE_VIDEO_DETECT_LIGHT_PARAM[MAX_VIDEO_DETECT_LIGHT_NUM]; //单个视频检测信号灯参数
    public byte[] byRes = new byte[8];
}








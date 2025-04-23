package com.dw.hikvision.sdk.structure;

import com.sun.jna.Union;

/**
 * 交通信号灯接入参数
 *
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/4/21 15:56
 */
public class NET_ITC_LIGHT_ACCESSPARAM_UNION extends Union {
    public int[] uLen = new int[122];
    public NET_ITC_IO_LIGHT_PARAM struIOLight; //IO接入信号灯参数
    public NET_ITC_RS485_LIGHT_PARAM struRS485Light; //485接入信号灯参数
    public NET_ITC_VIDEO_DETECT_LIGHT_PARAM struVideoDelectLight; //视频检测信号灯参数
}
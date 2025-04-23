package com.dw.hikvision.sdk.structure;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/4/21 14:39
 */
public class NET_ITC_VIDEO_TRIGGER_PARAM extends SdkStructure {
    public int dwSize;
    public int dwMode; //触发模式，详见ITC_TRIGGERMODE_TYPE
    public NET_ITC_VIDEO_TRIGGER_PARAM_UNION uVideoTrigger = new NET_ITC_VIDEO_TRIGGER_PARAM_UNION(); //触发模式参数
    public byte[] byRes = new byte[32];
}

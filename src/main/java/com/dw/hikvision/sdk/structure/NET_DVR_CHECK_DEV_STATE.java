package com.dw.hikvision.sdk.structure;

import com.dw.hikvision.sdk.callback.DEV_WORK_STATE_CB;
import com.sun.jna.Pointer;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/4/21 14:16
 */
public class NET_DVR_CHECK_DEV_STATE extends SdkStructure {
    // 定时检测设备工作状态，单位ms，为0时，表示使用默认值(30000)。最小值为1000
    public int dwTimeout;
    public DEV_WORK_STATE_CB fnStateCB;
    public Pointer pUserData;
    public byte[] byRes = new byte[60];
}
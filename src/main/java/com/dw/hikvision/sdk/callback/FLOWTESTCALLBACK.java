package com.dw.hikvision.sdk.callback;

import com.dw.hikvision.sdk.HCNetSDK;
import com.sun.jna.Callback;
import com.sun.jna.Pointer;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/4/24 09:48
 */
public interface FLOWTESTCALLBACK extends Callback {
    public void invoke(int lFlowHandle, HCNetSDK.NET_DVR_FLOW_INFO pFlowInfo,
                       Pointer pUser);
}


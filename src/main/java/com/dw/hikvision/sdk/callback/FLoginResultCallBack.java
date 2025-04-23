package com.dw.hikvision.sdk.callback;

import com.dw.hikvision.sdk.structure.NET_DVR_DEVICEINFO_V30;
import com.sun.jna.Callback;
import com.sun.jna.Pointer;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/4/21 16:04
 */
public interface FLoginResultCallBack extends Callback {
    public int invoke(int lUserID, int dwResult, NET_DVR_DEVICEINFO_V30 lpDeviceinfo, Pointer pUser);
}

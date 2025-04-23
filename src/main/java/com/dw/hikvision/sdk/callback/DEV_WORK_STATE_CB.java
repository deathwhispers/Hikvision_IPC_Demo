package com.dw.hikvision.sdk.callback;

import com.dw.hikvision.sdk.structure.NET_DVR_WORKSTATE_V40;
import com.sun.jna.Callback;
import com.sun.jna.Pointer;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/4/21 14:23
 */
public interface DEV_WORK_STATE_CB extends Callback {
    boolean invoke(Pointer pUserdata, int iUserID, NET_DVR_WORKSTATE_V40 lpWorkState);

}

package com.dw.hikvision.sdk.callback;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/4/24 09:44
 */
public interface FExceptionCallBack extends Callback {
    public void invoke(int dwType, int lUserID, int lHandle, Pointer pUser);
}
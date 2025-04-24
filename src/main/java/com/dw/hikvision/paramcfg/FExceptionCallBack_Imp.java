package com.dw.hikvision.paramcfg;

import com.dw.hikvision.sdk.callback.FExceptionCallBack;
import com.sun.jna.Pointer;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/4/14 17:39
 */
public class FExceptionCallBack_Imp implements FExceptionCallBack {
    public void invoke(int dwType, int lUserID, int lHandle, Pointer pUser) {
        System.out.println("异常事件类型:" + dwType);
        return;
    }
}
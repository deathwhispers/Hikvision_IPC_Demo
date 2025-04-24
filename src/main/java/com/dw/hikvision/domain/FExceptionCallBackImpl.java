package com.dw.hikvision.domain;

import com.dw.hikvision.sdk.HCNetSDK;
import com.dw.hikvision.sdk.callback.FExceptionCallBack;
import com.sun.jna.Pointer;

/**
 * 异常 callback
 *
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/4/14 17:39
 */
public class FExceptionCallBackImpl implements FExceptionCallBack {
    public void invoke(int dwType, int lUserID, int lHandle, Pointer pUser) {
        System.out.println("异常事件类型:" + dwType);
        return;
    }
}
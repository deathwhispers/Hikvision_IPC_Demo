package com.dw.hikvision.audio;

import com.dw.hikvision.demo.HCNetSDK;
import com.sun.jna.Pointer;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/4/14 17:21
 */
public class VoiceDataCallBack  implements HCNetSDK.FVoiceDataCallBack_V30 {
    @Override
    public void invoke(int lVoiceComHandle, Pointer pRecvDataBuffer, int dwBufSize, byte byAudioFlag, Pointer pUser) {
        //回调函数里保存语音对讲中双方通话语音数据
        System.out.println("语音对讲数据回调....");

    }
}
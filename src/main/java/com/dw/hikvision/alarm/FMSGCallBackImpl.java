package com.dw.hikvision.alarm;

import com.dw.hikvision.sdk.HCNetSDK;
import com.sun.jna.Pointer;


/**
 * @author jiangxin
 * 2022-08-15-17:26
 */
public class FMSGCallBackImpl implements HCNetSDK.FMSGCallBack {
    //报警信息回调函数
    public void invoke(int lCommand, HCNetSDK.NET_DVR_ALARMER pAlarmer, Pointer pAlarmInfo, int dwBufLen, Pointer pUser) {
        AlarmDataParse.alarmDataHandle(lCommand, pAlarmer, pAlarmInfo, dwBufLen, pUser);
        System.out.println("FMSGCallBack");
        return;
    }
}

package com.dw.hikvision.paramcfg;

import com.dw.hikvision.demo.HCNetSDK;
import com.sun.jna.Pointer;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/4/14 17:38
 */
public class dev_work_state_cb implements HCNetSDK.DEV_WORK_STATE_CB {
    public boolean invoke(Pointer pUserdata, int iUserID, HCNetSDK.NET_DVR_WORKSTATE_V40 lpWorkState) {

        lpWorkState.read();
        System.out.println("设备状态:" + lpWorkState.dwDeviceStatic);
        for (int i = 0; i < HCNetSDK.MAX_CHANNUM_V40; i++) {
            int channel = i + 1;
            System.out.println("第" + channel + "通道是否在录像：" + lpWorkState.struChanStatic[i].byRecordStatic);
        }
        return true;
    }

}

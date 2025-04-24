package com.dw.hikvision.paramcfg;

import com.dw.hikvision.sdk.HCNetSDK;
import com.dw.hikvision.sdk.callback.FLOWTESTCALLBACK;
import com.sun.jna.Pointer;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/4/14 17:39
 */
public class FlowTestcallback implements FLOWTESTCALLBACK {
    public void invoke(int lFlowHandle, HCNetSDK.NET_DVR_FLOW_INFO pFlowInfo,
                       Pointer pUser) {
        pFlowInfo.read();
        System.out.println("发送的流量数据：" + pFlowInfo.dwSendFlowSize);
        System.out.println("接收的流量数据：" + pFlowInfo.dwRecvFlowSize);

    }
}
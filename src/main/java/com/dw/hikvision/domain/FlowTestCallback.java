package com.dw.hikvision.domain;

import com.dw.hikvision.sdk.HCNetSDK;
import com.sun.jna.Pointer;

/**
 * 流量测试 callback
 *
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/4/14 17:39
 */
public class FlowTestCallback implements HCNetSDK.FLOWTESTCALLBACK {
    public void invoke(int lFlowHandle, HCNetSDK.NET_DVR_FLOW_INFO pFlowInfo,
                       Pointer pUser) {
        pFlowInfo.read();
        System.out.println("发送的流量数据：" + pFlowInfo.dwSendFlowSize);
        System.out.println("接收的流量数据：" + pFlowInfo.dwRecvFlowSize);
    }
}
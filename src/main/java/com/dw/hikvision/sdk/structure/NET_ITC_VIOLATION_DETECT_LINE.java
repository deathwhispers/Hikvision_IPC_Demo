package com.dw.hikvision.sdk.structure;

/**
 * 违规检测线参数结构
 *
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/4/21 15:44
 */
public class NET_ITC_VIOLATION_DETECT_LINE extends SdkStructure {
    public NET_ITC_LINE struLaneLine = new NET_ITC_LINE(); //车道线参数
    public NET_ITC_LINE struStopLine = new NET_ITC_LINE(); //停止线参数
    public NET_ITC_LINE struRedLightLine = new NET_ITC_LINE(); //闯红灯触发线参数
    public NET_ITC_LINE struCancelLine = new NET_ITC_LINE(); //直行触发位置取消线
    public NET_ITC_LINE struWaitLine = new NET_ITC_LINE(); //待行区停止线参数
    public NET_ITC_LINE[] struRes = new NET_ITC_LINE[8];
}

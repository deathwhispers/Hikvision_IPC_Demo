package com.dw.hikvision.sdk.structure;

/**
 * 视频电警线结构
 *
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/4/21 15:44
 */
public class NET_ITC_LINE extends SdkStructure {
    public NET_VCA_LINE struLine = new NET_VCA_LINE(); //线参数
    public byte byLineType; //线类型，详见ITC_LINE_TYPE
    public byte[] byRes = new byte[7];
}
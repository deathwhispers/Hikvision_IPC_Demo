package com.dw.hikvision.sdk.structure;

/**
 * 球机位置信息
 *
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/4/21 14:42
 */
public class NET_DVR_PTZPOS_PARAM extends SdkStructure {
    public float fPanPos;//水平参数，精确到小数点后1位
    public float fTiltPos;//垂直参数，精确到小数点后1位
    public float fZoomPos;//变倍参数，精确到小数点后1位
    public byte[] byRes = new byte[16];
}

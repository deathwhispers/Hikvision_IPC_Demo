package com.dw.hikvision.sdk.structure;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/4/21 14:43
 */
public class NET_DVR_SENSOR_PARAM extends SdkStructure {
    public byte bySensorType;//SensorType:0-CCD,1-CMOS
    public byte[] byRes = new byte[31];
    public float fHorWidth;//水平宽度 精确到小数点后两位 *10000
    public float fVerWidth;//垂直宽度 精确到小数点后两位 *10000
    public float fFold;//zoom=1没变时的焦距 精确到小数点后两位 *100
}
package com.dw.hikvision.sdk.structure;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/4/21 14:34
 */
public class NET_DVR_GIS_INFO extends SdkStructure {
    public int dwSize;
    public float fAzimuth;
    public float fHorizontalValue;
    public float fVerticalValue;
    public float fVisibleRadius;
    public float fMaxViewRadius;
    public byte byLatitudeType;
    public byte byLongitudeType;
    public byte byPTZPosExEnable;
    public byte byRes1;
    public NET_DVR_LLI_PARAM struLatitude = new NET_DVR_LLI_PARAM();
    public NET_DVR_LLI_PARAM struLongitude = new NET_DVR_LLI_PARAM();
    public NET_DVR_PTZPOS_PARAM struPtzPos = new NET_DVR_PTZPOS_PARAM();
    public NET_DVR_SENSOR_PARAM struSensorParam = new NET_DVR_SENSOR_PARAM();
    public NET_PTZ_INFO struPtzPosEx = new NET_PTZ_INFO();
    public float fMinHorizontalValue;
    public float fMaxHorizontalValue;
    public float fMinVerticalValue;
    public float fMaxVerticalValue;
    public byte[] byRes = new byte[220];
}
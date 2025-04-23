package com.dw.hikvision.sdk.structure;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/4/21 14:44
 */
public class NET_PTZ_INFO extends SdkStructure {
    public float fPan;
    public float fTilt;
    public float fZoom;
    public int dwFocus;// 聚焦参数，聚焦范围：归一化0-100000
    public byte[] byRes = new byte[4];
}

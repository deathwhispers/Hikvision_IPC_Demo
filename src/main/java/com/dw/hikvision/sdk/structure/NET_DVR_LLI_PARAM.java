package com.dw.hikvision.sdk.structure;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/4/21 14:42
 */
public class NET_DVR_LLI_PARAM extends SdkStructure {
    public float fSec;//秒[0.000000,60.000000]
    public byte byDegree;//度:纬度[0,90] 经度[0,180]
    public byte byMinute;//分[0,59]
    public byte[] byRes = new byte[6];
}

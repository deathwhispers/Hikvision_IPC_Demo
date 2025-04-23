package com.dw.hikvision.sdk.structure;

/**
 * 巡航点参数结构体
 *
 * @author yanggj
 * @version 1.0.0
 * @date 2025/4/18 15:10
 */
public class NET_DVR_CRUISEPOINT_PARAM extends SdkStructure {
    public short wPresetNo;
    public short wDwell;
    public byte bySpeed;
    public byte bySupport256PresetNo;
    public byte[] byRes = new byte[6];
}

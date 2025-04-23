package com.dw.hikvision.sdk.structure;

/**
 * 设置完全获取出厂值
 *
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/4/21 14:37
 */
public class NET_DVR_COMPLETE_RESTORE_INFO extends SdkStructure {
    public int dwSize; //结构体长度
    public int dwChannel; //通道号
    public byte[] byRes = new byte[64];
}


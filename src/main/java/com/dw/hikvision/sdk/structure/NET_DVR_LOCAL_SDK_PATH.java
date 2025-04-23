package com.dw.hikvision.sdk.structure;

import static com.dw.hikvision.sdk.HCNetSDK.NET_SDK_MAX_FILE_PATH;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/4/21 14:38
 */
public class NET_DVR_LOCAL_SDK_PATH  extends SdkStructure {
    public byte[] sPath = new byte[NET_SDK_MAX_FILE_PATH];//组件库地址
    public byte[] byRes = new byte[128];
}
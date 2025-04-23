package com.dw.hikvision.sdk.structure;

import com.sun.jna.Pointer;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/4/21 14:37
 */
public class NET_DVR_STD_ABILITY extends SdkStructure {
    public Pointer lpCondBuffer;    //[in]条件参数(码字格式),例如通道号等.可以为NULL
    public int dwCondSize;        //[in] dwCondSize指向的内存大小
    public Pointer lpOutBuffer;    //[out]输出参数(XML格式),不为NULL
    public int dwOutSize;        //[in] lpOutBuffer指向的内存大小
    public Pointer lpStatusBuffer;    //[out]返回的状态参数(XML格式),获取成功时不会赋值,如果不需要,可以置NULL
    public int dwStatusSize;    //[in] lpStatusBuffer指向的内存大小
    public int dwRetSize;        //[out]获取到的数据长度(lpOutBuffer或者lpStatusBuffer指向的实际数据长度)
    public byte[] byRes = new byte[32];        //保留字节
}
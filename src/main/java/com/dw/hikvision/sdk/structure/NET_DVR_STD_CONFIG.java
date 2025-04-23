package com.dw.hikvision.sdk.structure;

import com.sun.jna.Pointer;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/4/21 14:36
 */
public class NET_DVR_STD_CONFIG extends SdkStructure {
    public Pointer lpCondBuffer;        //[in]条件参数(结构体格式),例如通道号等.可以为NULL
    public int dwCondSize;            //[in] lpCondBuffer指向的内存大小
    public Pointer lpInBuffer;            //[in]输入参数(结构体格式),设置时不为NULL，获取时为NULL
    public int dwInSize;            //[in] lpInBuffer指向的内存大小
    public Pointer lpOutBuffer;        //[out]输出参数(结构体格式),获取时不为NULL,设置时为NULL
    public int dwOutSize;            //[in] lpOutBuffer指向的内存大小
    public Pointer lpStatusBuffer;        //[out]返回的状态参数(XML格式),获取成功时不会赋值,如果不需要,可以置NULL
    public int dwStatusSize;        //[in] lpStatusBuffer指向的内存大小
    public Pointer lpXmlBuffer;    //[in/out]byDataType = 1时有效,xml格式数据
    public int dwXmlSize;      //[in/out]lpXmlBuffer指向的内存大小,获取时同时作为输入和输出参数，获取成功后会修改会实际长度，设置时表示实际长度，而不是整个内存大小
    public byte byDataType;     //[in]输入/输出参数类型,0-使用结构体类型lpInBuffer/lpOutBuffer有效,1-使用XML类型lpXmlBuffer有效
    public byte[] byRes = new byte[23];
}
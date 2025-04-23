package com.dw.hikvision.sdk.structure;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/4/21 14:12
 */
public class NET_DVR_DISKSTATE extends SdkStructure {//硬盘状态
    public int dwVolume;//硬盘的容量
    public int dwFreeSpace;//硬盘的剩余空间
    public int dwHardDiskStatic; //硬盘的状态,按位:1-休眠,2-不正常,3-休眠硬盘出错
}
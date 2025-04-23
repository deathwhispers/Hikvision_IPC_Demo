package com.dw.hikvision.sdk.structure;

import com.sun.jna.Structure;

import static com.dw.hikvision.sdk.HCNetSDK.MAX_INTERVAL_NUM;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/4/21 15:50
 */
public class NET_ITC_INTERVAL_PARAM extends Structure {
    public byte byIntervalType;    //间隔类型（默认按时间），0-时间起效,1-距离起效
    public byte[] byRes1 = new byte[3];
    public short[] wInterval = new short[MAX_INTERVAL_NUM];//连拍间隔时间（单位ms）或连拍间隔距离（单位分米），当byIntervalType为0时，表示间隔时间，当byIntervalType为1时，表示距离
    public byte[] byRes = new byte[8];
}

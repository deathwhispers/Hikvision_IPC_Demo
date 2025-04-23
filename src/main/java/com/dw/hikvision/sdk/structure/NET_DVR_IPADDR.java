package com.dw.hikvision.sdk.structure;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/4/21 14:08
 */
public class NET_DVR_IPADDR extends SdkStructure {
    public byte[] sIpV4 = new byte[16];
    public byte[] byRes = new byte[128];

    public String toString() {
        return "NET_DVR_IPADDR.sIpV4: " + new String(sIpV4) + "\n" + "NET_DVR_IPADDR.byRes: " + new String(byRes) + "\n";
    }

}
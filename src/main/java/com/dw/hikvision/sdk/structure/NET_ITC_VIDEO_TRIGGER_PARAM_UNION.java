package com.dw.hikvision.sdk.structure;

import com.sun.jna.Union;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/4/21 14:46
 */
public class NET_ITC_VIDEO_TRIGGER_PARAM_UNION extends Union {
    public int[] uLen = new int[1150];
    public NET_ITC_VIDEO_EPOLICE_PARAM struVideoEP = new NET_ITC_VIDEO_EPOLICE_PARAM(); //视频电警参数
}

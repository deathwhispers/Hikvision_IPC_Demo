package com.dw.hikvision.sdk.structure;

import static com.dw.hikvision.sdk.HCNetSDK.MAX_ITC_LANE_NUM;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/4/21 14:46
 */
public class NET_ITC_VIDEO_EPOLICE_PARAM extends SdkStructure {
    public byte byEnable;    //是否启用，0-不启用，1-启用
    public byte byLaneNum; //识别的车道个数
    public byte byLogicJudge;//闯红灯违规判断逻辑，设置值为：0-按方向，1-按车道
    public byte byRes1;
    public NET_ITC_PLATE_RECOG_PARAM struPlateRecog; //牌识参数
    public NET_ITC_TRAFFIC_LIGHT_PARAM struTrafficLight; //交通信号灯参数
    public NET_ITC_LANE_VIDEO_EPOLICE_PARAM[] struLaneParam = new NET_ITC_LANE_VIDEO_EPOLICE_PARAM[MAX_ITC_LANE_NUM]; //单车道参数
    public NET_ITC_LINE struLaneBoundaryLine; //车道边界线（最右边车道的边界线）
    public NET_ITC_LINE struLeftLine; //左转弯分界线
    public NET_ITC_LINE struRightLine; //右转弯分界线
    public NET_ITC_LINE struTopZebraLine; //上部斑马线
    public NET_ITC_LINE struBotZebraLine; //下部斑马线
    public byte[] byRes = new byte[32];
}

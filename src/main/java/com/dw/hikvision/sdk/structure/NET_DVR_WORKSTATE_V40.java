package com.dw.hikvision.sdk.structure;

import static com.dw.hikvision.sdk.HCNetSDK.*;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/4/21 14:16
 */
public class NET_DVR_WORKSTATE_V40 extends SdkStructure {
    public int dwSize;            //结构体大小
    public int dwDeviceStatic;      //设备的状态,0-正常,1-CPU占用率太高,超过85%,2-硬件错误,例如串口死掉
    public NET_DVR_DISKSTATE[] struHardDiskStatic = new NET_DVR_DISKSTATE[MAX_DISKNUM_V30];   //硬盘状态,一次最多只能获取33个硬盘信息
    public NET_DVR_CHANNELSTATE_V30[] struChanStatic = new NET_DVR_CHANNELSTATE_V30[MAX_CHANNUM_V40/*512*/];//通道的状态，从前往后顺序排列
    public int[] dwHasAlarmInStatic = new int[MAX_ALARMIN_V40]; //有报警的报警输入口，按值表示，按下标值顺序排列，值为0xffffffff时当前及后续值无效
    public int[] dwHasAlarmOutStatic = new int[MAX_ALARMOUT_V40]; //有报警输出的报警输出口，按值表示，按下标值顺序排列，值为0xffffffff时当前及后续值无效
    public int dwLocalDisplay;            //本地显示状态,0-正常,1-不正常
    public byte[] byAudioInChanStatus = new byte[MAX_AUDIO_V30/*2*/];        //按位表示语音通道的状态 0-未使用，1-使用中，第0位表示第1个语音通道
    public byte[] byRes1 = new byte[2];
    public float fHumidity;    //传感器获知的湿度,范围:0.0 ~100.0
    public float fTemperature;    //传感器获知的温度，范围：-20.0 ~ 90.0
    public byte[] byRes = new byte[116];                 //保留
}
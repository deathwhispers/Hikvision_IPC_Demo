package com.dw.hikvision.alarm;

import com.dw.hikvision.sdk.HCNetSDK;
import com.sun.jna.Pointer;

import java.util.Scanner;


public class Alarm {

    static HCNetSDK hCNetSDK = null;
    static int lUserID = -1;//用户句柄 实现对设备登录
    static int lAlarmHandle = -1;//报警布防句柄
    static int lAlarmHandle_V50 = -1; //v50报警布防句柄
    static int lListenHandle = -1;//报警监听句柄
    static FMSGCallBack_V31Impl fMSFCallBack_V31 = null;
    static FMSGCallBackImpl fMSFCallBack = null;


    /**
     * @param args
     */
    public static void main(String[] args) throws InterruptedException {
        /* 设备上传的报警信息是COMM_VCA_ALARM(0x4993)类型，
         在SDK初始化之后增加调用NET_DVR_SetSDKLocalCfg(enumType为NET_DVR_LOCAL_CFG_TYPE_GENERAL)设置通用参数NET_DVR_LOCAL_GENERAL_CFG的byAlarmJsonPictureSeparate为1，
         将Json数据和图片数据分离上传，这样设置之后，报警布防回调函数里面接收到的报警信息类型为COMM_ISAPI_ALARM(0x6009)，
         报警信息结构体为NET_DVR_ALARM_ISAPI_INFO（与设备无关，SDK封装的数据结构），更便于解析。
         */
        HCNetSDK.NET_DVR_LOCAL_GENERAL_CFG struNET_DVR_LOCAL_GENERAL_CFG = new HCNetSDK.NET_DVR_LOCAL_GENERAL_CFG();
        struNET_DVR_LOCAL_GENERAL_CFG.byAlarmJsonPictureSeparate = 1;   //设置JSON透传报警数据和图片分离
        struNET_DVR_LOCAL_GENERAL_CFG.write();
        Pointer pStrNET_DVR_LOCAL_GENERAL_CFG = struNET_DVR_LOCAL_GENERAL_CFG.getPointer();
        hCNetSDK.NET_DVR_SetSDKLocalCfg(17, pStrNET_DVR_LOCAL_GENERAL_CFG);

        Alarm.setAlarm();//报警布防，和报警监听二选一即可
//        setAlarm_V50();
//        Alarm.startListen("100.65.62.37",(short)8000);//报警监听，不需要登陆设备
        while (true) {
            //这里加入控制台输入控制，是为了保持连接状态，当输入Y表示布防结束
            System.out.print("请选择是否撤出布防(Y/N)：\n");
            Scanner input = new Scanner(System.in);
            String str = input.next();
            if (str.equals("Y")) {
                break;
            }
        }
        Alarm.logout();
        //释放SDK
        hCNetSDK.NET_DVR_Cleanup();
        return;
    }

    /**
     * 报警布防接口
     *
     * @param
     */
    public static void setAlarm() {
        if (lAlarmHandle < 0)//尚未布防,需要布防
        {
            //报警布防参数设置
            HCNetSDK.NET_DVR_SETUPALARM_PARAM m_strAlarmInfo = new HCNetSDK.NET_DVR_SETUPALARM_PARAM();
            m_strAlarmInfo.dwSize = m_strAlarmInfo.size();
            m_strAlarmInfo.byLevel = 0;  //布防等级
            m_strAlarmInfo.byAlarmInfoType = 1;   // 智能交通报警信息上传类型：0- 老报警信息（NET_DVR_PLATE_RESULT），1- 新报警信息(NET_ITS_PLATE_RESULT)
            m_strAlarmInfo.byDeployType = 0;   //布防类型：0-客户端布防，1-实时布防
            m_strAlarmInfo.write();
            lAlarmHandle = hCNetSDK.NET_DVR_SetupAlarmChan_V41(lUserID, m_strAlarmInfo);
            System.out.println("lAlarmHandle: " + lAlarmHandle);
            if (lAlarmHandle == -1) {
                System.out.println("布防失败，错误码为" + hCNetSDK.NET_DVR_GetLastError());
                return;
            } else {
                System.out.println("布防成功");
            }
        } else {
            System.out.println("设备已经布防，请先撤防！");
        }
        return;
    }

    /**
     * 报警布防V50接口，功能和V41一致
     *
     * @param
     */
    public static void setAlarm_V50() {

        if (lAlarmHandle_V50 < 0)//尚未布防,需要布防
        {
            //报警布防参数设置
            HCNetSDK.NET_DVR_SETUPALARM_PARAM_V50 m_strAlarmInfo = new HCNetSDK.NET_DVR_SETUPALARM_PARAM_V50();
            m_strAlarmInfo.dwSize = m_strAlarmInfo.size();
            m_strAlarmInfo.byLevel = 1;  //布防等级
            m_strAlarmInfo.byAlarmInfoType = 1;   // 智能交通报警信息上传类型：0- 老报警信息（NET_DVR_PLATE_RESULT），1- 新报警信息(NET_ITS_PLATE_RESULT)
            m_strAlarmInfo.byDeployType = 1;   //布防类型 0：客户端布防 1：实时布防
            m_strAlarmInfo.write();
            lAlarmHandle_V50 = hCNetSDK.NET_DVR_SetupAlarmChan_V50(lUserID, m_strAlarmInfo, Pointer.NULL, 0);
            System.out.println("lAlarmHandle_V50: " + lAlarmHandle_V50);
            if (lAlarmHandle_V50 == -1) {
                System.out.println("布防失败，错误码为" + hCNetSDK.NET_DVR_GetLastError());
                return;
            } else {
                System.out.println("布防成功");
            }
        } else {
            System.out.println("设备已经布防，请先撤防！");
        }
        return;
    }

    /**
     * 开启监听
     *
     * @param ip   监听IP
     * @param port 监听端口
     */
    public static void startListen(String ip, short port) {
        if (fMSFCallBack == null) {
            fMSFCallBack = new FMSGCallBackImpl();
        }
        lListenHandle = hCNetSDK.NET_DVR_StartListen_V30(ip, port, fMSFCallBack, null);
        if (lListenHandle == -1) {
            System.out.println("监听失败" + hCNetSDK.NET_DVR_GetLastError());
            return;
        } else {
            System.out.println("监听成功");
        }
    }

    /**
     * 设备撤防，设备注销
     *
     * @param
     */
    public static void logout() {

        if (lAlarmHandle > -1) {
            if (hCNetSDK.NET_DVR_CloseAlarmChan(lAlarmHandle)) {
                System.out.println("撤防成功");
            }
        }

        if (lAlarmHandle_V50 > -1) {
            if (hCNetSDK.NET_DVR_CloseAlarmChan(lAlarmHandle_V50)) {
                System.out.println("v50 撤防成功");
            }
        }
        if (lListenHandle > -1) {
            if (hCNetSDK.NET_DVR_StopListen_V30(lListenHandle)) {
                System.out.println("停止监听成功");
            }
        }
        if (lUserID > -1) {
            if (hCNetSDK.NET_DVR_Logout(lUserID)) {
                System.out.println("注销成功");
            }
        }
        return;
    }

}

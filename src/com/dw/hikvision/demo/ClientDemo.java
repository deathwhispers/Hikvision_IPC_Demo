package com.dw.hikvision.demo;

import com.dw.hikvision.commom.osSelect;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

import java.util.Timer;

/**
 * @create 2020-12-24-17:55
 */
public class ClientDemo {

    int iErr = 0;
    static HCNetSDK hCNetSDK = null;
    static PlayCtrl playControl = null;
    static int lUserID = -1;//用户句柄
    static int lDChannel;  //预览通道号
    static boolean bSaveHandle = false;
    Timer Playbacktimer;//回放用定时器

    static FExceptionCallBack_Imp fExceptionCallBack;
    static int FlowHandle;

    static class FExceptionCallBack_Imp implements HCNetSDK.FExceptionCallBack {
        public void invoke(int dwType, int lUserID, int lHandle, Pointer pUser) {
            System.out.println("异常事件类型:"+dwType);
            return;
        }
    }

    /**
     * 动态库加载
     *
     * @return
     */
    private static boolean createSDKInstance() {
        if (hCNetSDK == null) {
            synchronized (HCNetSDK.class) {
                String strDllPath = "";
                try {
                    if (osSelect.isWindows())
                        //win系统加载库路径
                        strDllPath = System.getProperty("user.dir") + "\\lib\\win\\HCNetSDK.dll";

                    else if (osSelect.isLinux())
                        //Linux系统加载库路径
                        strDllPath = System.getProperty("user.dir") + "/lib/linux/libhcnetsdk.so";
                    hCNetSDK = (HCNetSDK) Native.loadLibrary(strDllPath, HCNetSDK.class);
                } catch (Exception ex) {
                    System.out.println("loadLibrary: " + strDllPath + " Error: " + ex.getMessage());
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 播放库加载
     *
     * @return
     */
    private static boolean createPlayInstance() {
        if (playControl == null) {
            synchronized (PlayCtrl.class) {
                String strPlayPath = "";
                try {
                    if (osSelect.isWindows())
                        //win系统加载库路径
                        strPlayPath = System.getProperty("user.dir") + "\\lib\\win\\PlayCtrl.dll";
                    else if (osSelect.isLinux())
                        //Linux系统加载库路径
                        strPlayPath = System.getProperty("user.dir") + "/lib/linux/libPlayCtrl.so";
                    playControl=(PlayCtrl) Native.loadLibrary(strPlayPath,PlayCtrl.class);

                } catch (Exception ex) {
                    System.out.println("loadLibrary: " + strPlayPath + " Error: " + ex.getMessage());
                    return false;
                }
            }
        }
        return true;
    }


    public static void main(String[] args) throws InterruptedException {

        if (hCNetSDK == null&&playControl==null) {
            if (!createSDKInstance()) {
                System.out.println("Load SDK fail");
                return;
            }
            if (!createPlayInstance()) {
                System.out.println("Load PlayCtrl fail");
                return;
            }
        }
        //linux系统建议调用以下接口加载组件库
        if (osSelect.isLinux()) {
            HCNetSDK.BYTE_ARRAY ptrByteArray1 = new HCNetSDK.BYTE_ARRAY(256);
            HCNetSDK.BYTE_ARRAY ptrByteArray2 = new HCNetSDK.BYTE_ARRAY(256);
            //这里是库的绝对路径，请根据实际情况修改，注意改路径必须有访问权限
            String strPath1 = System.getProperty("user.dir") + "/lib/linux/libcrypto.so.1.1";
            String strPath2 = System.getProperty("user.dir") + "/lib/linux/libssl.so.1.1";

            System.arraycopy(strPath1.getBytes(), 0, ptrByteArray1.byValue, 0, strPath1.length());
            ptrByteArray1.write();
            hCNetSDK.NET_DVR_SetSDKInitCfg(3, ptrByteArray1.getPointer());

            System.arraycopy(strPath2.getBytes(), 0, ptrByteArray2.byValue, 0, strPath2.length());
            ptrByteArray2.write();
            hCNetSDK.NET_DVR_SetSDKInitCfg(4, ptrByteArray2.getPointer());

            String strPathCom = System.getProperty("user.dir") + "/lib/linux/";
            HCNetSDK.NET_DVR_LOCAL_SDK_PATH struComPath = new HCNetSDK.NET_DVR_LOCAL_SDK_PATH();
            System.arraycopy(strPathCom.getBytes(), 0, struComPath.sPath, 0, strPathCom.length());
            struComPath.write();
            hCNetSDK.NET_DVR_SetSDKInitCfg(2, struComPath.getPointer());
        }

        //SDK初始化，一个程序只需要调用一次
        boolean initSuc = hCNetSDK.NET_DVR_Init();

        //异常消息回调
        if(fExceptionCallBack == null)
        {
            fExceptionCallBack = new FExceptionCallBack_Imp();
        }
        Pointer pUser = null;
        if (!hCNetSDK.NET_DVR_SetExceptionCallBack_V30(0, 0, fExceptionCallBack, pUser)) {
            return ;
        }
        System.out.println("设置异常消息回调成功");

        //启动SDK写日志
        hCNetSDK.NET_DVR_SetLogToFile(3, "./sdkLog", false);

        login_V40("10.16.36.12",(short) 8000,"admin","hik12345");

        
        //注释掉的代码也可以参考，去掉注释可以运行
        //VideoDemo.getIPChannelInfo(lUserID); //获取IP通道             

        //实时取流
        VideoDemo.realPlay(lUserID,lDChannel);

        //按时间回放和下载
//          new VideoDemo().playBackBytime(lUserID,33);

        //按时间下载录像
//        new VideoDemo().dowmloadRecordByTime(lUserID);

        //按时间回放和下载录像，需要等待回放和下载完成后调用注销和释放接口
//        while (true)
//        {
//
//        }

        //按录像文件回放和下载
//        VideoDemo.downloadRecordByFile(lUserID, 33);//录像文件查找下载
//
//        VideoDemo.playBackByfile(lUserID,33);
        Thread.sleep(3000);
        
        //退出程序时调用，每一台设备分别注销
        if (hCNetSDK.NET_DVR_Logout(lUserID)) {
            System.out.println("注销成功");
        }
        
        //SDK反初始化，释放资源，只需要退出时调用一次
        hCNetSDK.NET_DVR_Cleanup();
        return;

    }

    /**
     * 设备登录V40 与V30功能一致
     * @param ip   设备IP
     * @param port SDK端口，默认设备的8000端口
     * @param user 设备用户名
     * @param psw  设备密码
     */
    public static void login_V40(String ip, short port, String user, String psw) {
        //注册
        HCNetSDK.NET_DVR_USER_LOGIN_INFO m_strLoginInfo = new HCNetSDK.NET_DVR_USER_LOGIN_INFO();//设备登录信息
        HCNetSDK.NET_DVR_DEVICEINFO_V40 m_strDeviceInfo = new HCNetSDK.NET_DVR_DEVICEINFO_V40();//设备信息

        String m_sDeviceIP = ip;//设备ip地址
        m_strLoginInfo.sDeviceAddress = new byte[HCNetSDK.NET_DVR_DEV_ADDRESS_MAX_LEN];
        System.arraycopy(m_sDeviceIP.getBytes(), 0, m_strLoginInfo.sDeviceAddress, 0, m_sDeviceIP.length());

        String m_sUsername = user;//设备用户名
        m_strLoginInfo.sUserName = new byte[HCNetSDK.NET_DVR_LOGIN_USERNAME_MAX_LEN];
        System.arraycopy(m_sUsername.getBytes(), 0, m_strLoginInfo.sUserName, 0, m_sUsername.length());

        String m_sPassword = psw;//设备密码
        m_strLoginInfo.sPassword = new byte[HCNetSDK.NET_DVR_LOGIN_PASSWD_MAX_LEN];
        System.arraycopy(m_sPassword.getBytes(), 0, m_strLoginInfo.sPassword, 0, m_sPassword.length());

        m_strLoginInfo.wPort = port;
        m_strLoginInfo.bUseAsynLogin = false; //是否异步登录：0- 否，1- 是
        m_strLoginInfo.byLoginMode=0;  //0- SDK私有协议，1- ISAPI协议
        m_strLoginInfo.write();

        lUserID = hCNetSDK.NET_DVR_Login_V40(m_strLoginInfo, m_strDeviceInfo);
        if (lUserID== -1) {
            System.out.println("登录失败，错误码为" + hCNetSDK.NET_DVR_GetLastError());
            return;
        } else {
            System.out.println(ip + ":设备登录成功！");
            //相机一般只有一个通道号，热成像相机有2个通道号，通道号为1或1,2
            //byStartDChan为IP通道起始通道号, 预览回放NVR的IP通道时需要根据起始通道号进行取值
            if ((int)m_strDeviceInfo.struDeviceV30.byStartDChan == 1 && (int)m_strDeviceInfo.struDeviceV30.byStartDChan == 33)
            {
                //byStartDChan为IP通道起始通道号, 预览回放NVR的IP通道时需要根据起始通道号进行取值,NVR起始通道号一般是33或者1开始
                lDChannel = (int)m_strDeviceInfo.struDeviceV30.byStartDChan;
                System.out.println("预览起始通道号："+lDChannel);
            }
            return;
        }
    }

}




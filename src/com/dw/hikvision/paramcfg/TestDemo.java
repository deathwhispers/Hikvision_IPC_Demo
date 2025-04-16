package com.dw.hikvision.paramcfg;

import com.dw.hikvision.commom.osSelect;
import com.dw.hikvision.demo.HCNetSDK;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

/**
 * @create 2020-07-27-10:42
 */
@SuppressWarnings("all")
public class TestDemo {
    static HCNetSDK hCNetSDK = null;
    static int lUserID = -1; //用户句柄
    public static FlowTestcallback flowcallback;
    public static dev_work_state_cb workStateCb;
    public static FExceptionCallBack_Imp fExceptionCallBack;

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
                        //win系统加载SDK库路径
                        strDllPath = System.getProperty("user.dir") + "\\lib\\win\\HCNetSDK.dll";

                    else if (osSelect.isLinux())
                        //Linux系统加载SDK库路径
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

    public static void main(String[] args) throws IOException, InterruptedException {
        if (hCNetSDK == null) {
            if (!createSDKInstance()) {
                System.out.println("Load SDK fail");
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

        // SDK初始化，一个程序进程只需要调用一次
        hCNetSDK.NET_DVR_Init();

        if (fExceptionCallBack == null) {
            fExceptionCallBack = new FExceptionCallBack_Imp();
        }
        Pointer pUser = null;
        if (!hCNetSDK.NET_DVR_SetExceptionCallBack_V30(0, 0, fExceptionCallBack, pUser)) {
            return;
        }
        System.out.println("设置异常消息回调成功");

        //启用SDK写日志
        hCNetSDK.NET_DVR_SetLogToFile(3, "./sdkLog", false);

        //登录设备，每一台设备只需要登录一次
        lUserID = TestDemo.login_V40("100.65.62.23", (short) 8000, "admin", "xhy12345");

        // 2. 获取SDK版本信息
        System.out.println("== SDK版本信息 ==");

        // (a) 获取基础版本号
        int version = hCNetSDK.NET_DVR_GetSDKVersion();
        int major = (version >> 16) & 0xFFFF;
        int minor = version & 0xFFFF;
        System.out.println("基础版本: " + major + "." + minor);

        // (b) 获取构建版本（如补丁号、日期）
/*        HCNetSDK.NET_DVR_SDK_BUILD_VERSION buildVersion = new HCNetSDK.NET_DVR_SDKABL();
        buildVersion.dwSize = buildVersion.size(); // 必须设置结构体大小
        int result = IHCP_SDK.INSTANCE.NET_DVR_GetSDKBuildVersion(buildVersion);
        if (result == 1) {
            System.out.println("构建版本号: " + buildVersion.dwBuildVersion);
            System.out.println("构建日期: " + buildVersion.dwBuildDate);
            System.out.println("备注信息: " + new String(buildVersion.szBuildNote));
        } else {
            System.out.println("获取构建版本失败: " + IHCP_SDK.INSTANCE.NET_DVR_GetLastError());
        }*/

        // 2. 获取SDK状态信息
        System.out.println("\n== SDK状态信息 ==");
        HCNetSDK.NET_DVR_SDKSTATE sdkState = new HCNetSDK.NET_DVR_SDKSTATE();

        boolean result = hCNetSDK.NET_DVR_GetSDKState(sdkState);
        if (result) {
            // 清晰输出每个字段
            System.out.printf("当前登录用户数: %d%n", sdkState.dwTotalLoginNum);
            System.out.printf("实时预览路数: %d%n", sdkState.dwTotalRealPlayNum);
            System.out.printf("回放/下载路数: %d%n", sdkState.dwTotalPlayBackNum);
            System.out.printf("报警通道数: %d%n", sdkState.dwTotalAlarmChanNum);
            System.out.printf("硬盘格式化路数: %d%n", sdkState.dwTotalFormatNum);
            System.out.printf("文件搜索路数: %d%n", sdkState.dwTotalFileSearchNum);
            System.out.printf("日志搜索路数: %d%n", sdkState.dwTotalLogSearchNum);
            System.out.printf("透明通道路数: %d%n", sdkState.dwTotalSerialNum);
            System.out.printf("升级路数: %d%n", sdkState.dwTotalUpgradeNum);
            System.out.printf("语音转发路数: %d%n", sdkState.dwTotalVoiceComNum);
            System.out.printf("语音广播路数: %d%n", sdkState.dwTotalBroadCastNum);
        } else {
            System.out.println("获取SDK状态失败，错误码: "
                    + hCNetSDK.NET_DVR_GetLastError());
        }

        // 4. 获取SDK功能支持信息
        System.out.println("\n== SDK功能信息 ==");
        HCNetSDK.NET_DVR_SDKABL sdkAbl = new HCNetSDK.NET_DVR_SDKABL();

        result = hCNetSDK.NET_DVR_GetSDKAbility(sdkAbl);
        if (result) {
            // 清晰输出每个字段
            System.out.printf("最大登录用户数: %d%n", sdkAbl.dwMaxLoginNum);
            System.out.printf("最大实时预览路数: %d%n", sdkAbl.dwMaxRealPlayNum);
            System.out.printf("最大回放/下载路数: %d%n", sdkAbl.dwMaxPlayBackNum);
            System.out.printf("最大报警通道路数: %d%n", sdkAbl.dwMaxAlarmChanNum);
            System.out.printf("最大硬盘格式化路数: %d%n", sdkAbl.dwMaxFormatNum);
            System.out.printf("最大文件搜索路数: %d%n", sdkAbl.dwMaxFileSearchNum);
            System.out.printf("最大日志搜索路数: %d%n", sdkAbl.dwMaxLogSearchNum);
            System.out.printf("最大透明通道路数: %d%n", sdkAbl.dwMaxSerialNum);
            System.out.printf("最大升级路数: %d%n", sdkAbl.dwMaxUpgradeNum);
            System.out.printf("最大语音转发路数: %d%n", sdkAbl.dwMaxVoiceComNum);
            System.out.printf("最大语音广播路数: %d%n", sdkAbl.dwMaxBroadCastNum);
        } else {
            System.out.println("获取SDK功能信息失败，错误码: "
                    + hCNetSDK.NET_DVR_GetLastError());
        }
        new Thread(() -> {
            regularInspection();
        }).start();


        getDeviceStatus(lUserID);
//        getIPChannelInfo(lUserID);
//        getGisInfo(lUserID);
//         getIP();
        getCfg(lUserID);

        new CountDownLatch(1).await();
        //程序退出的时候调用注销登录接口，每一台设备分别调用一次
//        if (hCNetSDK.NET_DVR_Logout(lUserID)) {
//            System.out.println("注销成功");
//        }

        //释放SDK资源，程序退出时调用，只需要调用一次
//        hCNetSDK.NET_DVR_Cleanup();
    }

    /**
     * 设备登录V30
     *
     * @param ip   设备IP
     * @param port SDK端口，默认设备的8000端口
     * @param user 设备用户名
     * @param psw  设备密码
     */
    public static int login_V30(String ip, short port, String user, String psw) {
        HCNetSDK.NET_DVR_DEVICEINFO_V30 m_strDeviceInfo = new HCNetSDK.NET_DVR_DEVICEINFO_V30();
        int iUserID = hCNetSDK.NET_DVR_Login_V30(ip, port, user, psw, m_strDeviceInfo);
        System.out.println("UserID:" + lUserID);
        if ((iUserID == -1) || (iUserID == 0xFFFFFFFF)) {
            System.out.println("登录失败，错误码为" + hCNetSDK.NET_DVR_GetLastError());
            return iUserID;
        } else {
            System.out.println(ip + ":设备登录成功！");
            return iUserID;
        }
    }

    /**
     * 设备登录V40 与V30功能一致
     *
     * @param ip   设备IP
     * @param port SDK端口，默认设备的8000端口
     * @param user 设备用户名
     * @param psw  设备密码
     */
    public static int login_V40(String ip, short port, String user, String psw) {
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
//        m_strLoginInfo.byLoginMode=1;  //ISAPI登录
        m_strLoginInfo.write();

        int iUserID = hCNetSDK.NET_DVR_Login_V40(m_strLoginInfo, m_strDeviceInfo);
        if (iUserID == -1) {
            System.out.println("登录失败，错误码为" + hCNetSDK.NET_DVR_GetLastError());
            return iUserID;
        } else {
            System.out.println(ip + ":设备登录成功！");
            return iUserID;
        }
    }

    //设备在线状态监测
    public static void getDeviceStatus(int iUserID) {
        boolean devStatus = hCNetSDK.NET_DVR_RemoteControl(iUserID, HCNetSDK.NET_DVR_CHECK_USER_STATUS, null, 0);
        if (devStatus == false) {
            System.out.println("设备不在线");

        } else {
            System.out.println("设备在线");
        }
    }

    //PC电脑有多网卡，绑定网卡，指定使用的实际网卡
    public static void getIP() {
        HCNetSDK.BYTE_TWODIM[] struByteArray = new HCNetSDK.BYTE_TWODIM[16];
        IntByReference pInt = new IntByReference(0);
        boolean pEnableBind = false;
        if (!hCNetSDK.NET_DVR_GetLocalIP(struByteArray, pInt, pEnableBind)) {
            System.out.println("NET_DVR_GetLocalIP失败，错误号:" + hCNetSDK.NET_DVR_GetLastError());
        } else {
            int inum = pInt.getValue();
            for (int i = 0; i < inum; i++) {
                System.out.println("网卡序号:" + i + ", 网卡IP: " + new String(struByteArray[i].strIP).trim());

//选择需要绑定的网卡
                if ("10.16.36.23".equals(new String(struByteArray[i].strIP))) {
                    hCNetSDK.NET_DVR_SetValidIP(i, true);
                }
            }
        }
    }

    //设备抓图
    public void circleGetPic(int iUserID) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
//        String curTime0 = sdf.format(new Date());
        Boolean result = false;
        int count = 0;
        while (!result) {
            try {
                Thread.sleep(1 * 1000); //设置暂停的时间 5 秒
                String curTime0 = sdf.format(new Date());
                count++;
                String filename = "C:\\PIC\\" + curTime0 + count + ".jpg";
                byte[] fileByte = filename.getBytes("UTF-8");
                HCNetSDK.BYTE_ARRAY byte_array = new HCNetSDK.BYTE_ARRAY(fileByte.length);
                byte_array.read();
                byte_array.byValue = fileByte;
                byte_array.write();

                HCNetSDK.NET_DVR_JPEGPARA strJpegParm = new HCNetSDK.NET_DVR_JPEGPARA();
                strJpegParm.read();
                strJpegParm.wPicSize = 2;
                strJpegParm.wPicQuality = 0;
                Pointer pStrjPegParm = strJpegParm.getPointer();
                strJpegParm.write();
                boolean b_Cap = hCNetSDK.NET_DVR_CaptureJPEGPicture(iUserID, 1, strJpegParm, fileByte);
                if (b_Cap == false) {
                    System.out.println("抓图失败,错误码为:" + hCNetSDK.NET_DVR_GetLastError());
                    return;
                }
                System.out.println(sdf.format(new Date()) + "--循环执行第" + count + "次");
                if (count == 3) {
                    result = true;
                    break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    //端口绑定
    public void bindPort() {
        HCNetSDK.NET_DVR_LOCAL_TCP_PORT_BIND_CFG strLocalTcpBind = new HCNetSDK.NET_DVR_LOCAL_TCP_PORT_BIND_CFG();
        strLocalTcpBind.read();
        strLocalTcpBind.wLocalBindTcpMinPort = 30000;
        strLocalTcpBind.wLocalBindTcpMaxPort = 30200;
        strLocalTcpBind.write();
        Pointer pStrLocalTcoBind = strLocalTcpBind.getPointer();
        if (hCNetSDK.NET_DVR_SetSDKLocalCfg(0, pStrLocalTcoBind) == false) {
            System.out.println("绑定失败，错误码为" + hCNetSDK.NET_DVR_GetLastError());
        }
        System.out.println("绑定成功");
    }

    //获取设备的基本参数
    public static void getCfg(int iUserID) {
        HCNetSDK.NET_DVR_DEVICECFG_V40 m_strDeviceCfg = new HCNetSDK.NET_DVR_DEVICECFG_V40();
        m_strDeviceCfg.dwSize = m_strDeviceCfg.size();
        m_strDeviceCfg.write();
        Pointer pStrDeviceCfg = m_strDeviceCfg.getPointer();
        IntByReference pInt = new IntByReference(0);
        boolean b_GetCfg = hCNetSDK.NET_DVR_GetDVRConfig(iUserID, HCNetSDK.NET_DVR_GET_DEVICECFG_V40,
                0Xffffffff, pStrDeviceCfg, m_strDeviceCfg.dwSize, pInt);
        if (b_GetCfg == false) {
            System.out.println("获取参数失败  错误码：" + hCNetSDK.NET_DVR_GetLastError());
        }
        System.out.println("获取参数成功");
        m_strDeviceCfg.read();
        System.out.println("设备名称:" + new String(m_strDeviceCfg.sDVRName).trim() + "设备序列号：" + new String(m_strDeviceCfg.sSerialNumber));
        System.out.println("模拟通道个数" + m_strDeviceCfg.byChanNum);
        parseVersion(m_strDeviceCfg.dwSoftwareVersion);
        parseBuildTime(m_strDeviceCfg.dwSoftwareBuildDate);
        parseDSPBuildDate(m_strDeviceCfg.dwDSPSoftwareBuildDate);

    }


    public static void parseBuildTime(int buildTime) {
        int year = ((buildTime & 0XFF << 16) >> 16) + 2000;
        int month = (buildTime & 0XFF << 8) >> 8;
        int data = buildTime & 0xFF;
        System.out.println("Build:" + year + "." + month + "." + data);


    }

    public static void parseDSPBuildDate(int DSPBuildDate) {
        int year = ((DSPBuildDate & 0XFF << 16) >> 16) + 2000;
        int month = (DSPBuildDate & 0XFF << 8) >> 8;
        int data = DSPBuildDate & 0xFF;
        System.out.println("DSPBuildDate:" + year + "." + month + "." + data);


    }

    //设备版本解析
    public static void parseVersion(int version) {
        int firstVersion = (version & 0XFF << 24) >> 24;
        int secondVersion = (version & 0XFF << 16) >> 16;
        int lowVersion = version & 0XFF;

        System.out.println("firstVersion:" + firstVersion);
        System.out.println("secondVersion:" + secondVersion);
        System.out.println("lowVersion:" + lowVersion);
    }

    //获取设备时间参数
    public void getDevTime(int iUserID) {
        HCNetSDK.NET_DVR_TIME m_Time = new HCNetSDK.NET_DVR_TIME();
        Pointer pTime = m_Time.getPointer();
        IntByReference pInt = new IntByReference(0);
        boolean b_GetTime = hCNetSDK.NET_DVR_GetDVRConfig(iUserID, HCNetSDK.NET_DVR_GET_TIMECFG, 0xffffffff, pTime, m_Time.size(), pInt);
        if (b_GetTime == false) {
            System.out.println("获取时间参数失败，错误码：" + hCNetSDK.NET_DVR_GetLastError());
        }
        m_Time.read();
        System.out.println("年：" + m_Time.dwYear + "月:" + m_Time.dwMonth + "日:" + m_Time.dwDay + "时：" + m_Time.dwHour +
                "分：" + m_Time.dwMinute + "秒：" + m_Time.dwSecond);
    }

    //获取设备的图像参数-移动侦测高亮显示
    public static void getPicCfg(int iUserID) {
        HCNetSDK.NET_DVR_PICCFG_V40 strPicCfg = new HCNetSDK.NET_DVR_PICCFG_V40();
        strPicCfg.dwSize = strPicCfg.size();
        Pointer pStrPicCfg = strPicCfg.getPointer();
        NativeLong lChannel = new NativeLong(18);
        IntByReference pInt = new IntByReference(0);
        boolean b_GetPicCfg = hCNetSDK.NET_DVR_GetDVRConfig(iUserID, HCNetSDK.NET_DVR_GET_PICCFG_V40, lChannel.intValue(),
                pStrPicCfg, strPicCfg.size(), pInt);
        if (b_GetPicCfg == false) {
            System.out.println("获取图像参数失败，错误码：" + hCNetSDK.NET_DVR_GetLastError());
        }
        strPicCfg.read();
        System.out.println("通道号：" + lChannel);
        System.out.println("通道名称：" + strPicCfg.sChanName);
        System.out.println("预览的图像是否显示OSD：" + strPicCfg.dwShowOsd);
        System.out.println("移动侦测高亮显示是否开启:" + strPicCfg.struMotion.byEnableDisplay);
        strPicCfg.read();
        strPicCfg.struMotion.byEnableDisplay = 0;
        strPicCfg.write();

        boolean b_SetPicCfg = hCNetSDK.NET_DVR_SetDVRConfig(iUserID, HCNetSDK.NET_DVR_SET_PICCFG_V40, lChannel.intValue(),
                pStrPicCfg, strPicCfg.size());
        if (b_SetPicCfg == false) {
            System.out.println("设置图像参数移动侦测高亮参数失败，错误码：" + hCNetSDK.NET_DVR_GetLastError());
        } else {
            System.out.println("设置移动侦测高亮参数成功");

        }
    }

    //获取用户参数
    public static void getUsrCfg(int iUserID) throws UnsupportedEncodingException {
        HCNetSDK.NET_DVR_USER_V30 usercfg = new HCNetSDK.NET_DVR_USER_V30();
        usercfg.dwSize = usercfg.size();
        Pointer pUserCfg = usercfg.getPointer();
        NativeLong lChannel = new NativeLong(1);
        IntByReference pInt = new IntByReference(0);
        boolean b_GetUserCfg = hCNetSDK.NET_DVR_GetDVRConfig(iUserID, HCNetSDK.NET_DVR_GET_USERCFG_V30, lChannel.intValue(),
                pUserCfg, usercfg.size(), pInt);
        if (b_GetUserCfg == false) {
            System.out.println("获取用户参数失败，错误码：" + hCNetSDK.NET_DVR_GetLastError());
        }
        usercfg.read();
        //System.out.println("用户名称：" + usercfg.struUser[1].sUserName);
        System.out.println("name： " + new String(usercfg.struUser[0].sUserName, "GBK").trim());
        System.out.println("password： " + new String(usercfg.struUser[0].sPassword, "GBK").trim());

    }

    //网络流量检测
    public void netFlowDec(int iUserID) throws InterruptedException {
        HCNetSDK.NET_DVR_FLOW_TEST_PARAM struFlowPam = new HCNetSDK.NET_DVR_FLOW_TEST_PARAM();
        struFlowPam.read();
        struFlowPam.dwSize = struFlowPam.size();
        struFlowPam.lCardIndex = 0;
        struFlowPam.dwInterval = 1;
        struFlowPam.write();
        Pointer pUser = null;
        if (flowcallback == null) {
            flowcallback = new FlowTestcallback();
        }
        int FlowHandle = hCNetSDK.NET_DVR_StartNetworkFlowTest(iUserID, struFlowPam, flowcallback, pUser);
        if (FlowHandle <= -1) {
            System.out.println("开启流量检测失败，错误码：" + hCNetSDK.NET_DVR_GetLastError());
        } else {
            System.out.println("开启流量检测成功");
        }
        Thread.sleep(20000);
        hCNetSDK.NET_DVR_StopNetworkFlowTest(FlowHandle);
    }

    //录像起止时间查询
    public static void searchRecordTime(int iUserID) {
        HCNetSDK.NET_DVR_RECORD_TIME_SPAN_INQUIRY struRecInq = new HCNetSDK.NET_DVR_RECORD_TIME_SPAN_INQUIRY();
        struRecInq.read();
        struRecInq.dwSize = struRecInq.size();
        struRecInq.byType = 0;
        struRecInq.write();
        HCNetSDK.NET_DVR_RECORD_TIME_SPAN struRecSpan = new HCNetSDK.NET_DVR_RECORD_TIME_SPAN();
        //通道号说明：一般IPC/IPD通道号为1，32路以及以下路数的NVR的IP通道通道号从33开始，64路及以上路数的NVR的IP通道通道号从1开始。
        if (hCNetSDK.NET_DVR_InquiryRecordTimeSpan(iUserID, 35, struRecInq, struRecSpan) == false) {
            System.out.println("录像起止时间查询失败，错误码：" + hCNetSDK.NET_DVR_GetLastError());
        } else {
            System.out.println("录像起止时间查询成功");
            struRecSpan.read();
            System.out.println("开启时间：" + "年：" + struRecSpan.strBeginTime.dwYear + "\n");
            System.out.println("开启时间：" + "月：" + struRecSpan.strBeginTime.dwMonth + "\n");
            System.out.println("开启时间：" + "日：" + struRecSpan.strBeginTime.dwDay + "\n");
            System.out.println("开启时间：" + "时：" + struRecSpan.strBeginTime.dwHour + "\n");
            System.out.println("停止时间：" + "年：" + struRecSpan.strEndTime.dwYear + "\n");
            System.out.println("停止时间：" + "月：" + struRecSpan.strEndTime.dwMonth + "\n");
            System.out.println("停止时间：" + "日：" + struRecSpan.strEndTime.dwDay + "\n");
            System.out.println("停止时间：" + "时：" + struRecSpan.strEndTime.dwHour + "\n");
        }
    }

    //月历录像查询
    public static void getRecMonth(int iUserID) {
        HCNetSDK.NET_DVR_MRD_SEARCH_PARAM struMrdSeaParam = new HCNetSDK.NET_DVR_MRD_SEARCH_PARAM();
        struMrdSeaParam.read();
        struMrdSeaParam.dwSize = struMrdSeaParam.size();
        struMrdSeaParam.wYear = 2021;
        struMrdSeaParam.byMonth = 1;
        //通道号说明：一般IPC/IPD通道号为1，32路以及以下路数的NVR的IP通道通道号从33开始，64路及以上路数的NVR的IP通道通道号从1开始。
        struMrdSeaParam.struStreamInfo.dwChannel = 33;
        struMrdSeaParam.write();
        HCNetSDK.NET_DVR_MRD_SEARCH_RESULT struMrdSeaResu = new HCNetSDK.NET_DVR_MRD_SEARCH_RESULT();
        struMrdSeaResu.read();
        struMrdSeaResu.dwSize = struMrdSeaResu.size();
        struMrdSeaResu.write();
        IntByReference list = new IntByReference(0);
        boolean b_GetResult = hCNetSDK.NET_DVR_GetDeviceConfig(iUserID, HCNetSDK.NET_DVR_GET_MONTHLY_RECORD_DISTRIBUTION, 0, struMrdSeaParam.getPointer(),
                struMrdSeaParam.size(), list.getPointer(), struMrdSeaResu.getPointer(), struMrdSeaResu.size());
        if (b_GetResult == false) {
            System.out.println("月历录像查询失败，错误码：" + hCNetSDK.NET_DVR_GetLastError());
        } else {
            struMrdSeaResu.read();
            for (int i = 0; i <= 32; i++) {
                int day = i + 1;
                System.out.println("" + day + "号是否录像文件" + struMrdSeaResu.byRecordDistribution[i]);
            }
        }
    }

    //球机GIS信息获取
    public static void getGisInfo(int iUserID) {
        HCNetSDK.NET_DVR_STD_CONFIG struStdCfg = new HCNetSDK.NET_DVR_STD_CONFIG();
        HCNetSDK.NET_DVR_GIS_INFO struGisInfo = new HCNetSDK.NET_DVR_GIS_INFO();
        struStdCfg.read();
        IntByReference lchannel = new IntByReference(1);
        struStdCfg.lpCondBuffer = lchannel.getPointer();
        struStdCfg.dwCondSize = 4;
        struStdCfg.lpOutBuffer = struGisInfo.getPointer();
        struStdCfg.dwOutSize = struGisInfo.size();
        struStdCfg.write();//设置前之前要write()
        boolean getSTDConfig = hCNetSDK.NET_DVR_GetSTDConfig(iUserID, HCNetSDK.NET_DVR_GET_GISINFO, struStdCfg);
        if (getSTDConfig == false) {
            System.out.println("查询GIS信息失败，错误码：" + hCNetSDK.NET_DVR_GetLastError());
        } else {
            struGisInfo.read();
            System.out.println("查询成功\n");
            System.out.println(struGisInfo.struPtzPos.fPanPos + "\n");
            System.out.println(struGisInfo.struPtzPos.fTiltPos + "\n");
            System.out.println(struGisInfo.struPtzPos.fZoomPos + "\n");
            System.out.println(struGisInfo.fHorizontalValue);
            System.out.println(struGisInfo.fVerticalValue);
        }

    }

    //球机PTZ参数获取设置
    public static void setPTZcfg(int iUserID) {
        HCNetSDK.NET_DVR_PTZPOS struPtTZPos = new HCNetSDK.NET_DVR_PTZPOS();
        IntByReference pUsers = new IntByReference(1);
        boolean b_GetPTZ = hCNetSDK.NET_DVR_GetDVRConfig(iUserID, HCNetSDK.NET_DVR_GET_PTZPOS, 1, struPtTZPos.getPointer(), struPtTZPos.size(), pUsers);
        if (b_GetPTZ == false) {
            System.out.println("获取PTZ坐标信息失败，错误码：" + hCNetSDK.NET_DVR_GetLastError());
        } else {
            struPtTZPos.read();
            int wPanPos = Integer.parseInt(Integer.toHexString(struPtTZPos.wPanPos).trim());
            float WPanPos = wPanPos * 0.1f;
            int wTiltPos = Integer.parseInt(Integer.toHexString(struPtTZPos.wTiltPos).trim());
            float WTiltPos = wTiltPos * 0.1f;
            int wZoomPos = Integer.parseInt(Integer.toHexString(struPtTZPos.wZoomPos).trim());
            float WZoomPos = wZoomPos * 0.1f;
            System.out.println("P参数：" + WPanPos + "\n");
            System.out.println("T参数：" + wTiltPos + "\n");
            System.out.println("Z参数：" + wZoomPos + "\n");
        }
//        struPtTZPos.wAction = 2;
        //本结构体中的wAction参数是设置时的操作类型，因此获取时该参数无效。实际显示的PTZ值是获取到的十六进制值的十分之一，
        // 如获取的水平参数P的值是0x1750，实际显示的P值为175度；获取到的垂直参数T的值是0x0789，实际显示的T值为78.9度，如果T未负值，获取的值减去360
        // 获取到的变倍参数Z的值是0x1100，实际显示的Z值为110倍。
//        String pHex="13669";
//        int pInter=Integer.parseInt(pHex);
//        short pInter = 13669;
//        System.out.println(pInter);
//        struPtTZPos.wPanPos = (short) pInter;
//        struPtTZPos.write();
//        boolean b_SetPTZ = hCNetSDK.NET_DVR_SetDVRConfig(iUserID, HCNetSDK.NET_DVR_SET_PTZPOS, 1, struPtTZPos.getPointer(), struPtTZPos.size());
//        if (b_GetPTZ == false) {
//            System.out.println("设置PTZ坐标信息失败，错误码：" + hCNetSDK.NET_DVR_GetLastError());
//        } else {
//
//            System.out.println("设置PTZ成功");
//        }

    }

    //设置云台锁定信息
    public static void setPTZLOCKCFG(int iUserID) {
        HCNetSDK.NET_DVR_PTZ_LOCKCFG struPtzLockCfg = new HCNetSDK.NET_DVR_PTZ_LOCKCFG();
        struPtzLockCfg.dwSize = struPtzLockCfg.size();
        Pointer pStrPtzLockCfg = struPtzLockCfg.getPointer();
        NativeLong lChannel = new NativeLong(1);
        IntByReference pInt = new IntByReference(0);
        boolean b_GetPtzLockCfg = hCNetSDK.NET_DVR_GetDVRConfig(iUserID, HCNetSDK.NET_DVR_GET_PTZLOCKCFG, lChannel.intValue(),
                pStrPtzLockCfg, struPtzLockCfg.size(), pInt);
        if (b_GetPtzLockCfg == false) {
            System.out.println("获取云台锁定信息失败，错误码：" + hCNetSDK.NET_DVR_GetLastError());
        }
        struPtzLockCfg.read();
        System.out.println("通道号：" + lChannel);
        System.out.println("云台锁定控制状态为：" + struPtzLockCfg.byWorkMode);

        struPtzLockCfg.read();
        struPtzLockCfg.byWorkMode = 1;    //0- 解锁，1- 锁定
        struPtzLockCfg.write();
        boolean b_SetPtzLockCfg = hCNetSDK.NET_DVR_SetDVRConfig(iUserID, HCNetSDK.NET_DVR_SET_PTZLOCKCFG, lChannel.intValue(),
                pStrPtzLockCfg, struPtzLockCfg.size());
        if (b_SetPtzLockCfg == false) {
            System.out.println("设置云台锁定信息失败，错误码：" + hCNetSDK.NET_DVR_GetLastError());
        } else {
            System.out.println("设置云台锁定信息成功");
            System.out.println("云台锁定控制状态当前为：" + struPtzLockCfg.byWorkMode);
        }
    }

//        public static void PTZControlOther(int iUserID){
//          boolean b_ptzcontrol=hCNetSDK.NET_DVR_PTZControl_Other(iUserID,1,HCNetSDK.TILT_UP,0);
//
//          if( b_ptzcontrol==false){
//              System.out.println("云台向上转动失败，错误码：" + hCNetSDK.NET_DVR_GetLastError());
//          }else{
//              System.out.println("设置向上转动成功");
//            }

    //      }

    //获取(设置)前端参数(扩展)
    public static void getCameraPara(int iUserID) {
        HCNetSDK.NET_DVR_CAMERAPARAMCFG_EX struCameraParam = new HCNetSDK.NET_DVR_CAMERAPARAMCFG_EX();
        Pointer pstruCameraParam = struCameraParam.getPointer();
        IntByReference ibrBytesReturned = new IntByReference(0);
        boolean b_GetCameraParam = hCNetSDK.NET_DVR_GetDVRConfig(iUserID, HCNetSDK.NET_DVR_GET_CCDPARAMCFG_EX, 1, pstruCameraParam, struCameraParam.size(), ibrBytesReturned);
        if (!b_GetCameraParam) {
            System.out.println("获取前端参数失败，错误码：" + hCNetSDK.NET_DVR_GetLastError());
        }
        struCameraParam.read();
        System.out.println("是否开启旋转：" + struCameraParam.struCorridorMode.byEnableCorridorMode);

        struCameraParam.struCorridorMode.byEnableCorridorMode = 1;
        struCameraParam.write();
        boolean b_SetCameraParam = hCNetSDK.NET_DVR_SetDVRConfig(iUserID, HCNetSDK.NET_DVR_SET_CCDPARAMCFG_EX, 1, pstruCameraParam, struCameraParam.size());
        if (!b_SetCameraParam) {
            System.out.println("设置前端参数失败，错误码：" + hCNetSDK.NET_DVR_GetLastError());
        }
        struCameraParam.read();
        System.out.println("设置成功");
    }

    //获取快球聚焦模式信息。
    public static void getFocusMode(int iUserID) {
        HCNetSDK.NET_DVR_FOCUSMODE_CFG struFocusMode = new HCNetSDK.NET_DVR_FOCUSMODE_CFG();
        struFocusMode.read();
        struFocusMode.dwSize = struFocusMode.size();
        struFocusMode.write();
        Pointer pFocusMode = struFocusMode.getPointer();
        IntByReference ibrBytesReturned = new IntByReference(0);
        boolean b_GetCameraParam = hCNetSDK.NET_DVR_GetDVRConfig(iUserID, HCNetSDK.NET_DVR_GET_FOCUSMODECFG, 1, pFocusMode, struFocusMode.size(), ibrBytesReturned);
        if (!b_GetCameraParam) {
            System.out.println("获取快球聚焦模式失败，错误码：" + hCNetSDK.NET_DVR_GetLastError());
        }
        struFocusMode.read();
        System.out.println("聚焦模式：" + struFocusMode.byFocusMode);
        struFocusMode.byFocusMode = 1;
        struFocusMode.byFocusDefinitionDisplay = 1;
        struFocusMode.byFocusSpeedLevel = 3;
        struFocusMode.write();
        boolean b_SetCameraParam = hCNetSDK.NET_DVR_SetDVRConfig(iUserID, HCNetSDK.NET_DVR_SET_FOCUSMODECFG, 1, pFocusMode, struFocusMode.size());
        if (!b_SetCameraParam) {
            System.out.println("设置快球聚焦模式失败，错误码：" + hCNetSDK.NET_DVR_GetLastError());
        }
        struFocusMode.read();
        System.out.println("设置成功");
    }

    //获取IP通道
    public static void getIPChannelInfo(int iUserID) throws UnsupportedEncodingException {
        IntByReference ibrBytesReturned = new IntByReference(0);//获取IP接入配置参数
        HCNetSDK.NET_DVR_IPPARACFG_V40 m_strIpparaCfg = new HCNetSDK.NET_DVR_IPPARACFG_V40();
        m_strIpparaCfg.write();
        //lpIpParaConfig 接收数据的缓冲指针
        Pointer lpIpParaConfig = m_strIpparaCfg.getPointer();
        boolean bRet = hCNetSDK.NET_DVR_GetDVRConfig(iUserID, HCNetSDK.NET_DVR_GET_IPPARACFG_V40, 0, lpIpParaConfig, m_strIpparaCfg.size(), ibrBytesReturned);
        m_strIpparaCfg.read();
        System.out.println("起始数字通道号：" + m_strIpparaCfg.dwStartDChan);

        for (int iChannum = 0; iChannum < m_strIpparaCfg.dwDChanNum; iChannum++) {
            int channum = iChannum + m_strIpparaCfg.dwStartDChan;
            HCNetSDK.NET_DVR_PICCFG_V40 strPicCfg = new HCNetSDK.NET_DVR_PICCFG_V40();
            strPicCfg.dwSize = strPicCfg.size();
            strPicCfg.write();
            Pointer pStrPicCfg = strPicCfg.getPointer();
            NativeLong lChannel = new NativeLong(channum);
            IntByReference pInt = new IntByReference(0);
            boolean b_GetPicCfg = hCNetSDK.NET_DVR_GetDVRConfig(iUserID, HCNetSDK.NET_DVR_GET_PICCFG_V40, lChannel.intValue(),
                    pStrPicCfg, strPicCfg.size(), pInt);
//            if (b_GetPicCfg == false) {
//                System.out.println("获取图像参数失败，错误码：" + hCNetSDK.NET_DVR_GetLastError());
//            }
            strPicCfg.read();
            m_strIpparaCfg.struStreamMode[iChannum].read();
            if (m_strIpparaCfg.struStreamMode[iChannum].byGetStreamType == 0) {
                m_strIpparaCfg.struStreamMode[iChannum].uGetStream.setType(HCNetSDK.NET_DVR_IPCHANINFO.class);
                m_strIpparaCfg.struStreamMode[iChannum].uGetStream.struChanInfo.read();

                System.out.println("--------------第" + (iChannum + 1) + "个通道------------------");
                int channel = m_strIpparaCfg.struStreamMode[iChannum].uGetStream.struChanInfo.byIPID + m_strIpparaCfg.struStreamMode[iChannum].uGetStream.struChanInfo.byIPIDHigh * 256;
                System.out.println("channel:" + channel);
                if (channel > 0) {
                    System.out.println("ip： " + new String(m_strIpparaCfg.struIPDevInfo[channel - 1].struIP.sIpV4).trim());
                }
                System.out.println("name： " + new String(strPicCfg.sChanName, "GBK").trim());
                if (m_strIpparaCfg.struStreamMode[iChannum].uGetStream.struChanInfo.byEnable == 1) {
                    System.out.println("IP通道" + channum + "在线");
                } else {
                    System.out.println("IP通道" + channum + "不在线");
                }
            }
        }
    }

    //定时巡检设备
    public static void regularInspection() {
        HCNetSDK.NET_DVR_CHECK_DEV_STATE struCheckStatus = new HCNetSDK.NET_DVR_CHECK_DEV_STATE();
        struCheckStatus.read();
        struCheckStatus.dwTimeout = 5000; //定时检测设备工作状态，单位：ms，0表示使用默认值(30000)，最小值为1000
        if (workStateCb == null) {
            workStateCb = new dev_work_state_cb();
        }
        struCheckStatus.fnStateCB = workStateCb;
        struCheckStatus.write();
        boolean b_state = hCNetSDK.NET_DVR_StartGetDevState(struCheckStatus);
        if (!b_state) {
            System.out.println("定时巡检设备状态失败：" + hCNetSDK.NET_DVR_GetLastError());
        }
    }

    //获取高精度PTZ绝对位置配置,一般热成像设备支持
    public static void getPTZAbsoluteEx(int iUserID) {
        HCNetSDK.NET_DVR_STD_CONFIG struSTDcfg = new HCNetSDK.NET_DVR_STD_CONFIG();
        HCNetSDK.NET_DVR_PTZABSOLUTEEX_CFG struPTZ = new HCNetSDK.NET_DVR_PTZABSOLUTEEX_CFG();
        struSTDcfg.read();
        IntByReference channel = new IntByReference(1);
        struSTDcfg.lpCondBuffer = channel.getPointer();
        struSTDcfg.dwCondSize = 4;
        struSTDcfg.lpOutBuffer = struPTZ.getPointer();
        struSTDcfg.dwOutSize = struPTZ.size();
        struSTDcfg.lpInBuffer = Pointer.NULL;
        struSTDcfg.dwInSize = 0;
        struSTDcfg.write();
        boolean bGetPTZ = hCNetSDK.NET_DVR_GetSTDConfig(iUserID, HCNetSDK.NET_DVR_GET_PTZABSOLUTEEX, struSTDcfg);
        if (bGetPTZ == false) {
            System.out.println("获取PTZ参数错误,错误码：" + hCNetSDK.NET_DVR_GetLastError());
            return;
        }
        struPTZ.read();
        System.out.println("焦距范围：" + struPTZ.dwFocalLen);
        System.out.println("聚焦参数：" + struPTZ.struPTZCtrl.dwFocus);
        return;
    }

    //获取GB28181参数
    public static void getGB28181Info(int iUserID) {

        HCNetSDK.NET_DVR_STREAM_INFO streamInfo = new HCNetSDK.NET_DVR_STREAM_INFO();
        streamInfo.read();
        streamInfo.dwSize = streamInfo.size(); //设置结构体大小
        streamInfo.dwChannel = 1; //设置通道
        streamInfo.write();
        Pointer lpInBuffer = streamInfo.getPointer();
        HCNetSDK.NET_DVR_GBT28181_CHANINFO_CFG gbt28181ChaninfoCfg = new HCNetSDK.NET_DVR_GBT28181_CHANINFO_CFG();
        gbt28181ChaninfoCfg.read();
        gbt28181ChaninfoCfg.dwSize = gbt28181ChaninfoCfg.size();
        gbt28181ChaninfoCfg.write();
        Pointer lpOutBuffer = gbt28181ChaninfoCfg.getPointer();
        IntByReference lpBytesReturned = new IntByReference(0);
        //3251对应它的宏定义
        boolean bRet = hCNetSDK.NET_DVR_GetDeviceConfig(iUserID, 3251, 1, lpInBuffer,
                streamInfo.size(), lpBytesReturned.getPointer(), lpOutBuffer, gbt28181ChaninfoCfg.size());
        gbt28181ChaninfoCfg.read();

        if (bRet == false) {
            System.out.println("获取失败,错误码：" + hCNetSDK.NET_DVR_GetLastError());
            return;
        }
    }

    //获取码流加密信息
    public static void getAESInfo(int iUserID) {
        HCNetSDK.NET_DVR_AES_KEY_INFO net_dvr_aes_key_info = new HCNetSDK.NET_DVR_AES_KEY_INFO();
        net_dvr_aes_key_info.read();
        Pointer pnet_dvr_aes_key_info = net_dvr_aes_key_info.getPointer();
        IntByReference pInt = new IntByReference(0);
        boolean b_GetCfg = hCNetSDK.NET_DVR_GetDVRConfig(iUserID, HCNetSDK.NET_DVR_GET_AES_KEY,
                0Xffffffff, pnet_dvr_aes_key_info, net_dvr_aes_key_info.size(), pInt);
        if (b_GetCfg == false) {
            System.out.println("获取码流加密失败  错误码：" + hCNetSDK.NET_DVR_GetLastError());
        }
        System.out.println("获取码流加密信息成功");


    }

    //设置球机预置点
    public static void getCruisePoint(int iUserID) {
        HCNetSDK.NET_DVR_CRUISEPOINT_COND struCruisepointCond = new HCNetSDK.NET_DVR_CRUISEPOINT_COND();
        struCruisepointCond.read();
        struCruisepointCond.dwSize = struCruisepointCond.size();
        struCruisepointCond.dwChan = 1;
        struCruisepointCond.wRouteNo = 1;
        struCruisepointCond.write();

        HCNetSDK.NET_DVR_CRUISEPOINT_V50 struCruisepointV40 = new HCNetSDK.NET_DVR_CRUISEPOINT_V50();
        struCruisepointV40.read();
        struCruisepointV40.dwSize = struCruisepointV40.size();
        struCruisepointV40.write();

        // 错误信息列表
        IntByReference pInt = new IntByReference(0);
        Pointer lpStatusList = pInt.getPointer();

        boolean flag = hCNetSDK.NET_DVR_GetDeviceConfig(iUserID, 6714, 1,
                struCruisepointCond.getPointer(), struCruisepointCond.size(), lpStatusList, struCruisepointV40.getPointer(), struCruisepointV40.size());
        if (flag == false) {
            int iErr = hCNetSDK.NET_DVR_GetLastError();
            System.out.println("NET_DVR_STDXMLConfig失败，错误号：" + iErr);
            return;
        }
        struCruisepointV40.read();


    }

    //抓图保存到缓冲区
    public static void getPictoPointer(int iUserID) {
        HCNetSDK.NET_DVR_JPEGPARA jpegpara = new HCNetSDK.NET_DVR_JPEGPARA();
        jpegpara.read();
        jpegpara.wPicSize = 255;
        jpegpara.wPicQuality = 0;
        jpegpara.write();
        HCNetSDK.BYTE_ARRAY byte_array = new HCNetSDK.BYTE_ARRAY(10 * 1024 * 1024);
        IntByReference ret = new IntByReference(0);
        boolean b = hCNetSDK.NET_DVR_CaptureJPEGPicture_NEW(iUserID, 1, jpegpara, byte_array.getPointer(), byte_array.size(), ret);
        if (b == false) {
            System.out.println("抓图失败：" + hCNetSDK.NET_DVR_GetLastError());
            return;
        }
        byte_array.read();
        System.out.println("抓图成功");
        return;
    }

    /**
     * 获取报警主机RS485参数
     *
     * @param lUserID
     */
    public static void getRs485Cfg(int lUserID) {
        HCNetSDK.NET_DVR_ALARM_RS485CFG rs485CFG = new HCNetSDK.NET_DVR_ALARM_RS485CFG();
        rs485CFG.dwSize = rs485CFG.size();
        Pointer pointer = rs485CFG.getPointer();
        IntByReference pInt1 = new IntByReference(0);
        rs485CFG.write();
        boolean bGetRs485 = hCNetSDK.NET_DVR_GetDVRConfig(lUserID, HCNetSDK.NET_DVR_GET_ALARM_RS485CFG, 3, pointer, rs485CFG.dwSize, pInt1);
        if (!bGetRs485) {
            System.out.println("获取报警主机RS485参数失败！错误号：" + hCNetSDK.NET_DVR_GetLastError());
            return;
        }
        rs485CFG.read();
        return;

    }

    public static void getRs485SlotInfo(int iUserID) {
        HCNetSDK.NET_DVR_ALARMHOST_RS485_SLOT_CFG strRs485SlotCFG = new HCNetSDK.NET_DVR_ALARMHOST_RS485_SLOT_CFG();
        strRs485SlotCFG.dwSize = strRs485SlotCFG.size();
        Pointer pRs485SlotCFG = strRs485SlotCFG.getPointer();
        IntByReference pInt1 = new IntByReference(0);
        strRs485SlotCFG.write();
        String Schannel = "0000000100000001";  //高2字节表示485通道号，低2字节表示槽位号，都从1开始
        int channel = Integer.parseInt(Schannel, 2);
        boolean bRs485Slot = hCNetSDK.NET_DVR_GetDVRConfig(iUserID, HCNetSDK.NET_DVR_GET_ALARMHOST_RS485_SLOT_CFG, channel, pRs485SlotCFG, strRs485SlotCFG.dwSize, pInt1);
        if (!bRs485Slot) {
            System.out.println("获取报警主机RS485槽位参数失败！错误号：" + hCNetSDK.NET_DVR_GetLastError());
            return;
        }
        strRs485SlotCFG.read();
        return;

    }


}



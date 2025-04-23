package com.dw.hikvision.domain;

import com.dw.hikvision.commom.HikvisionCameraHolder;
import com.dw.hikvision.definition.CameraBase;
import com.dw.hikvision.definition.CameraStatus;
import com.dw.hikvision.definition.DeviceStatusExtra;
import com.dw.hikvision.definition.DeviceStatusExtraConverter;
import com.dw.hikvision.paramcfg.DevWorkStateCallback;
import com.dw.hikvision.sdk.HCNetSDK;
import com.dw.hikvision.sdk.structure.*;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * 海康摄像机
 * Create at 2020-07-27-10:42
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class HikvisionCamera extends CameraBase {
    private static final HCNetSDK hCNetSDK = HCNetSDK.HC_NET_SDK;

    public static FlowTestCallback flowTestCallback;
    public static FExceptionCallBackImpl fExceptionCallBack;

    private final CameraStatus cameraStatus = new CameraStatus();

    public HikvisionCamera() {
        super();
    }

    /**
     * 定时巡检设备，会巡检所有已登录的设备
     * 当有新设备登录后，会将登录后的设备添加进去
     *
     * @param dwTimeout 巡检周期，单位：ms
     */
    public static void regularInspection(Integer dwTimeout) {
        NET_DVR_CHECK_DEV_STATE struCheckStatus = new NET_DVR_CHECK_DEV_STATE();
        struCheckStatus.read();
        // 定时检测设备工作状态，单位：ms，0表示使用默认值(30000)，最小值为1000
        struCheckStatus.dwTimeout = dwTimeout;
        struCheckStatus.fnStateCB = DevWorkStateCallback.getInstance();
        struCheckStatus.write();
        boolean b_state = hCNetSDK.NET_DVR_StartGetDevState(struCheckStatus);
        if (!b_state) {
            log.debug("定时巡检设备状态失败：{}", hCNetSDK.NET_DVR_GetLastError());
        }
    }

    /**
     * 设备登录V40
     */
    public int login() {
        // 设备登录信息
        NET_DVR_USER_LOGIN_INFO m_strLoginInfo = new NET_DVR_USER_LOGIN_INFO();
        // 设备信息
        NET_DVR_DEVICEINFO_V40 m_strDeviceInfo = new NET_DVR_DEVICEINFO_V40();
        // 设备ip地址
        String m_sDeviceIP = this.getIp();
        m_strLoginInfo.sDeviceAddress = new byte[HCNetSDK.NET_DVR_DEV_ADDRESS_MAX_LEN];
        System.arraycopy(m_sDeviceIP.getBytes(), 0, m_strLoginInfo.sDeviceAddress, 0, m_sDeviceIP.length());
        // 设备用户名
        String m_sUsername = this.getUsername();
        m_strLoginInfo.sUserName = new byte[HCNetSDK.NET_DVR_LOGIN_USERNAME_MAX_LEN];
        System.arraycopy(m_sUsername.getBytes(), 0, m_strLoginInfo.sUserName, 0, m_sUsername.length());
        // 设备密码
        String m_sPassword = this.getPassword();
        m_strLoginInfo.sPassword = new byte[HCNetSDK.NET_DVR_LOGIN_PASSWD_MAX_LEN];
        System.arraycopy(m_sPassword.getBytes(), 0, m_strLoginInfo.sPassword, 0, m_sPassword.length());
        // 端口
        m_strLoginInfo.wPort = (short) this.getPort();
        // 是否异步登录：0- 否，1- 是
        m_strLoginInfo.bUseAsynLogin = false;
        m_strLoginInfo.write();

        int iUserID = hCNetSDK.NET_DVR_Login_V40(m_strLoginInfo, m_strDeviceInfo);
        if (iUserID == -1) {
            log.debug("登录失败，错误码为{}", hCNetSDK.NET_DVR_GetLastError());
        } else {
            log.debug("{}:设备登录成功！", this.getIp());
            setLoginHandler((long) iUserID);
            HikvisionCameraHolder.registerDevice(this);
        }
        return iUserID;
    }

    public void logout() {
        if (hCNetSDK.NET_DVR_Logout(this.getLoginHandler().intValue())) {
            // 注销时，从 deviceHolder 中删除该设备
            HikvisionCameraHolder.removeByLoginHandler(this.getLoginHandler());
            log.info("注销成功!");
        }
    }

    public void updateCameraStatus(HikvisionWorkStatus hikvisionWorkStatus) {
        if (hikvisionWorkStatus == null) {
            log.warn("hikvisionWorkStatus is null, do nothing!");
            return;
        }
        List<DeviceStatusExtra> newExtras = Optional.of(DeviceStatusExtraConverter.convert(hikvisionWorkStatus))
                .orElseGet(Collections::emptyList);
        if (newExtras.isEmpty()) {
            log.warn("newExtras is null, do nothing!");
            return;
        }
        // 获取并初始化旧的状态列表
        List<DeviceStatusExtra> oldExtras = cameraStatus.getExtras();
        if (oldExtras == null || oldExtras.isEmpty()) {
            cameraStatus.setExtras(new ArrayList<>(newExtras)); // 防御性拷贝
            return;
        }
        // 使用Map优化查找性能
        Map<String, DeviceStatusExtra> extraMap = new HashMap<>(oldExtras.size() + newExtras.size());
        // 先将旧数据放入Map
        oldExtras.stream()
                .filter(extra -> extra != null && extra.getItem() != null)
                .forEach(extra -> extraMap.put(extra.getItem(), extra));
        newExtras.stream()
                .filter(extra -> extra != null && extra.getItem() != null)
                .forEach(newExtra -> {
                    DeviceStatusExtra existing = extraMap.get(newExtra.getItem());
                    if (existing != null) {
                        if (!Objects.equals(existing.getName(), newExtra.getName())) {
                            existing.setName(newExtra.getName());
                        }
                        if (!Objects.equals(existing.getDescription(), newExtra.getDescription())) {
                            existing.setDescription(newExtra.getDescription());
                        }
                        if (!Objects.equals(existing.getValue(), newExtra.getValue())) {
                            existing.setValue(newExtra.getValue());
                        }
                        if (!Objects.equals(existing.getValueDesc(), newExtra.getValueDesc())) {
                            existing.setValueDesc(newExtra.getValueDesc());
                        }
                    } else {
                        extraMap.put(newExtra.getItem(), newExtra);
                    }
                });
        for (DeviceStatusExtra newExtra : newExtras) {
            if (newExtra == null || newExtra.getItem() == null) {
                continue;
            }
            DeviceStatusExtra existingExtra = extraMap.get(newExtra.getItem());
            if (existingExtra != null) {
                existingExtra.setItem(newExtra.getItem());
                existingExtra.setName(newExtra.getName());
                existingExtra.setDescription(newExtra.getDescription());
                existingExtra.setValue(newExtra.getValue());
                existingExtra.setValueDesc(newExtra.getValueDesc());
            } else {
                extraMap.put(newExtra.getItem(), newExtra);
            }
        }
        cameraStatus.setExtras(new ArrayList<>(extraMap.values()));
    }

    /**
     * 获取设备状态
     */
    public CameraStatus getCameraStatus() {
        // 获取SDK版本信息
        int version = hCNetSDK.NET_DVR_GetSDKVersion();
        int major = (version >> 16) & 0xFFFF;
        int minor = version & 0xFFFF;
        this.cameraStatus.setVersion(major + "." + minor);

        // 获取SDK状态信息
        HCNetSDK.NET_DVR_SDKSTATE sdkState = new HCNetSDK.NET_DVR_SDKSTATE();
        boolean result = hCNetSDK.NET_DVR_GetSDKState(sdkState);
        if (result) {
            // 清晰输出每个字段
            log.info("SDK 当前状态：\n" +
                            "  当前登录用户数      : {}\n" +
                            "  实时预览路数        : {}\n" +
                            "  回放/下载路数       : {}\n" +
                            "  报警通道数          : {}\n" +
                            "  硬盘格式化路数      : {}\n" +
                            "  文件搜索路数        : {}\n" +
                            "  日志搜索路数        : {}\n" +
                            "  透明通道路数        : {}\n" +
                            "  升级路数            : {}\n" +
                            "  语音转发路数        : {}\n" +
                            "  语音广播路数        : {}",
                    sdkState.dwTotalLoginNum,
                    sdkState.dwTotalRealPlayNum,
                    sdkState.dwTotalPlayBackNum,
                    sdkState.dwTotalAlarmChanNum,
                    sdkState.dwTotalFormatNum,
                    sdkState.dwTotalFileSearchNum,
                    sdkState.dwTotalLogSearchNum,
                    sdkState.dwTotalSerialNum,
                    sdkState.dwTotalUpgradeNum,
                    sdkState.dwTotalVoiceComNum,
                    sdkState.dwTotalBroadCastNum);
        } else {
            log.debug("获取SDK状态失败，错误码: {}", hCNetSDK.NET_DVR_GetLastError());
        }

        // 获取SDK功能支持信息
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
            log.debug("获取SDK功能信息失败，错误码: {}", hCNetSDK.NET_DVR_GetLastError());
        }
        boolean devStatus = hCNetSDK.NET_DVR_RemoteControl(this.getLoginHandler().intValue(), HCNetSDK.NET_DVR_CHECK_USER_STATUS, null, 0);
        cameraStatus.setOnlineState(devStatus);
        // 获取设备的基本参数
//        HikvisionDeviceConfig deviceConfig = getDeviceConfig();
        return cameraStatus;
    }

    /**
     * 获取设备的基本参数
     */
    public HikvisionDeviceConfig getDeviceConfig() {
        HCNetSDK.NET_DVR_DEVICECFG_V40 m_strDeviceCfg = new HCNetSDK.NET_DVR_DEVICECFG_V40();
        m_strDeviceCfg.dwSize = m_strDeviceCfg.size();
        m_strDeviceCfg.write();
        Pointer pStrDeviceCfg = m_strDeviceCfg.getPointer();
        IntByReference pInt = new IntByReference(0);
        boolean b_GetCfg = hCNetSDK.NET_DVR_GetDVRConfig(this.getLoginHandler().intValue(), HCNetSDK.NET_DVR_GET_DEVICECFG_V40,
                0Xffffffff, pStrDeviceCfg, m_strDeviceCfg.dwSize, pInt);
        if (!b_GetCfg) {
            log.debug("获取参数失败  错误码：{}", hCNetSDK.NET_DVR_GetLastError());
        }
        log.debug("获取参数成功");
        m_strDeviceCfg.read();
        HikvisionDeviceConfig config = new HikvisionDeviceConfig();
        config.setDvrName(new String(m_strDeviceCfg.sDVRName).trim());
        config.setSerialNumber(new String(m_strDeviceCfg.sSerialNumber));
        String softwareVersion = parseVersion(m_strDeviceCfg.dwSoftwareVersion);
        String softwareBuildDate = parseBuildTime(m_strDeviceCfg.dwSoftwareBuildDate);
        String DSPSoftwareVersion = parseVersion(m_strDeviceCfg.dwDSPSoftwareVersion);
        String DSPSoftwareBuildDate = parseBuildTime(m_strDeviceCfg.dwDSPSoftwareBuildDate);
        config.setSoftwareVersion(softwareVersion);
        config.setSoftwareBuildDate(softwareBuildDate);
        config.setDSPSoftwareBuildVersion(DSPSoftwareVersion);
        config.setDSPSoftwareBuildDate(DSPSoftwareBuildDate);
        return config;
    }

    public String parseBuildTime(int buildTime) {
        int year = ((buildTime & 0XFF << 16) >> 16) + 2000;
        int month = (buildTime & 0XFF << 8) >> 8;
        int data = buildTime & 0xFF;
        log.debug("Build time:{}.{}.{}", year, month, data);
        return year + "-" + month + "-" + data;
    }

    // 设备版本解析
    public String parseVersion(int version) {
        int firstVersion = (version & 0XFF << 24) >> 24;
        int secondVersion = (version & 0XFF << 16) >> 16;
        int lowVersion = version & 0XFF;
        return firstVersion + "." + secondVersion + "." + lowVersion;
    }

    // 获取设备时间参数
    public void getDevTime(int iUserID) {
        HCNetSDK.NET_DVR_TIME m_Time = new HCNetSDK.NET_DVR_TIME();
        Pointer pTime = m_Time.getPointer();
        IntByReference pInt = new IntByReference(0);
        boolean b_GetTime = hCNetSDK.NET_DVR_GetDVRConfig(iUserID, HCNetSDK.NET_DVR_GET_TIMECFG, 0xffffffff, pTime, m_Time.size(), pInt);
        if (!b_GetTime) {
            log.debug("获取时间参数失败，错误码：{}", hCNetSDK.NET_DVR_GetLastError());
        }
        m_Time.read();
        log.debug("年：{}月:{}日:{}时：{}分：{}秒：{}", m_Time.dwYear, m_Time.dwMonth, m_Time.dwDay, m_Time.dwHour, m_Time.dwMinute, m_Time.dwSecond);
    }

    // 获取设备的图像参数-移动侦测高亮显示
    public void getPicCfg(int iUserID) {
        HCNetSDK.NET_DVR_PICCFG_V40 strPicCfg = new HCNetSDK.NET_DVR_PICCFG_V40();
        strPicCfg.dwSize = strPicCfg.size();
        Pointer pStrPicCfg = strPicCfg.getPointer();
        NativeLong lChannel = new NativeLong(18);
        IntByReference pInt = new IntByReference(0);
        boolean b_GetPicCfg = hCNetSDK.NET_DVR_GetDVRConfig(iUserID, HCNetSDK.NET_DVR_GET_PICCFG_V40, lChannel.intValue(),
                pStrPicCfg, strPicCfg.size(), pInt);
        if (!b_GetPicCfg) {
            log.debug("获取图像参数失败，错误码：{}", hCNetSDK.NET_DVR_GetLastError());
        }
        strPicCfg.read();
        log.debug("通道号：{}", lChannel);
        log.debug("通道名称：{}", strPicCfg.sChanName);
        log.debug("预览的图像是否显示OSD：{}", strPicCfg.dwShowOsd);
        log.debug("移动侦测高亮显示是否开启:{}", strPicCfg.struMotion.byEnableDisplay);
        strPicCfg.read();
        strPicCfg.struMotion.byEnableDisplay = 0;
        strPicCfg.write();

        boolean b_SetPicCfg = hCNetSDK.NET_DVR_SetDVRConfig(iUserID, HCNetSDK.NET_DVR_SET_PICCFG_V40, lChannel.intValue(),
                pStrPicCfg, strPicCfg.size());
        if (!b_SetPicCfg) {
            log.debug("设置图像参数移动侦测高亮参数失败，错误码：{}", hCNetSDK.NET_DVR_GetLastError());
        } else {
            log.debug("设置移动侦测高亮参数成功");

        }
    }

    // 获取用户参数
    public void getUsrCfg(int iUserID) throws UnsupportedEncodingException {
        HCNetSDK.NET_DVR_USER_V30 usercfg = new HCNetSDK.NET_DVR_USER_V30();
        usercfg.dwSize = usercfg.size();
        Pointer pUserCfg = usercfg.getPointer();
        NativeLong lChannel = new NativeLong(1);
        IntByReference pInt = new IntByReference(0);
        boolean b_GetUserCfg = hCNetSDK.NET_DVR_GetDVRConfig(iUserID, HCNetSDK.NET_DVR_GET_USERCFG_V30, lChannel.intValue(),
                pUserCfg, usercfg.size(), pInt);
        if (!b_GetUserCfg) {
            log.debug("获取用户参数失败，错误码：{}", hCNetSDK.NET_DVR_GetLastError());
        }
        usercfg.read();
        log.debug("name： {}", new String(usercfg.struUser[0].sUserName, "GBK").trim());
        log.debug("password： {}", new String(usercfg.struUser[0].sPassword, "GBK").trim());

    }

    // 网络流量检测
    public void netFlowDec(int iUserID) throws InterruptedException {
        HCNetSDK.NET_DVR_FLOW_TEST_PARAM struFlowPam = new HCNetSDK.NET_DVR_FLOW_TEST_PARAM();
        struFlowPam.read();
        struFlowPam.dwSize = struFlowPam.size();
        struFlowPam.lCardIndex = 0;
        struFlowPam.dwInterval = 1;
        struFlowPam.write();
        Pointer pUser = null;
        if (flowTestCallback == null) {
            flowTestCallback = new FlowTestCallback();
        }
        int FlowHandle = hCNetSDK.NET_DVR_StartNetworkFlowTest(iUserID, struFlowPam, flowTestCallback, pUser);
        if (FlowHandle <= -1) {
            log.debug("开启流量检测失败，错误码：{}", hCNetSDK.NET_DVR_GetLastError());
        } else {
            log.debug("开启流量检测成功");
        }
        Thread.sleep(20000);
        hCNetSDK.NET_DVR_StopNetworkFlowTest(FlowHandle);
    }

    // 录像起止时间查询
    public void searchRecordTime(int iUserID) {
        HCNetSDK.NET_DVR_RECORD_TIME_SPAN_INQUIRY struRecInq = new HCNetSDK.NET_DVR_RECORD_TIME_SPAN_INQUIRY();
        struRecInq.read();
        struRecInq.dwSize = struRecInq.size();
        struRecInq.byType = 0;
        struRecInq.write();
        HCNetSDK.NET_DVR_RECORD_TIME_SPAN struRecSpan = new HCNetSDK.NET_DVR_RECORD_TIME_SPAN();
        //通道号说明：一般IPC/IPD通道号为1，32路以及以下路数的NVR的IP通道通道号从33开始，64路及以上路数的NVR的IP通道通道号从1开始。
        if (!hCNetSDK.NET_DVR_InquiryRecordTimeSpan(iUserID, 35, struRecInq, struRecSpan)) {
            log.debug("录像起止时间查询失败，错误码：{}", hCNetSDK.NET_DVR_GetLastError());
        } else {
            log.debug("录像起止时间查询成功");
            struRecSpan.read();
            log.debug("开启时间：{}-{}-{} {}",
                    struRecSpan.strBeginTime.dwYear,
                    struRecSpan.strBeginTime.dwMonth,
                    struRecSpan.strBeginTime.dwDay,
                    struRecSpan.strBeginTime.dwHour);
            log.debug("停止时间：{}-{}-{} {}",
                    struRecSpan.strEndTime.dwYear,
                    struRecSpan.strEndTime.dwMonth,
                    struRecSpan.strEndTime.dwDay,
                    struRecSpan.strEndTime.dwHour);
        }
    }

    // 月历录像查询
    public void getRecMonth(int iUserID) {
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
        if (!b_GetResult) {
            log.debug("月历录像查询失败，错误码：{}", hCNetSDK.NET_DVR_GetLastError());
        } else {
            struMrdSeaResu.read();
            for (int i = 0; i <= 32; i++) {
                int day = i + 1;
                log.debug("{}号是否录像文件{}", day, struMrdSeaResu.byRecordDistribution[i]);
            }
        }
    }

    // 球机GIS信息获取
    public void getGisInfo(int iUserID) {
        NET_DVR_STD_CONFIG struStdCfg = new NET_DVR_STD_CONFIG();
        NET_DVR_GIS_INFO struGisInfo = new NET_DVR_GIS_INFO();
        struStdCfg.read();
        IntByReference lchannel = new IntByReference(1);
        struStdCfg.lpCondBuffer = lchannel.getPointer();
        struStdCfg.dwCondSize = 4;
        struStdCfg.lpOutBuffer = struGisInfo.getPointer();
        struStdCfg.dwOutSize = struGisInfo.size();
        struStdCfg.write();//设置前之前要write()
        boolean getSTDConfig = hCNetSDK.NET_DVR_GetSTDConfig(iUserID, HCNetSDK.NET_DVR_GET_GISINFO, struStdCfg);
        if (!getSTDConfig) {
            log.debug("查询GIS信息失败，错误码：{}", hCNetSDK.NET_DVR_GetLastError());
        } else {
            struGisInfo.read();
            log.debug("查询成功: \n" +
                            "  云台位置: \n" +
                            "    - 水平 (Pan): {}\n" +
                            "    - 垂直 (Tilt): {}\n" +
                            "    - 变焦 (Zoom): {}\n" +
                            "  坐标值: \n" +
                            "    - 水平值 (Horizontal): {}\n" +
                            "    - 垂直值 (Vertical): {}",
                    struGisInfo.struPtzPos.fPanPos,
                    struGisInfo.struPtzPos.fTiltPos,
                    struGisInfo.struPtzPos.fZoomPos,
                    struGisInfo.fHorizontalValue,
                    struGisInfo.fVerticalValue);
        }
    }

    // 球机PTZ参数获取设置
    public void setPTZcfg(int iUserID) {
        HCNetSDK.NET_DVR_PTZPOS struPtTZPos = new HCNetSDK.NET_DVR_PTZPOS();
        IntByReference pUsers = new IntByReference(1);
        boolean b_GetPTZ = hCNetSDK.NET_DVR_GetDVRConfig(iUserID, HCNetSDK.NET_DVR_GET_PTZPOS, 1, struPtTZPos.getPointer(), struPtTZPos.size(), pUsers);
        if (!b_GetPTZ) {
            log.debug("获取PTZ坐标信息失败，错误码：{}", hCNetSDK.NET_DVR_GetLastError());
        } else {
            struPtTZPos.read();
            int wPanPos = Integer.parseInt(Integer.toHexString(struPtTZPos.wPanPos).trim());
            float WPanPos = wPanPos * 0.1f;
            int wTiltPos = Integer.parseInt(Integer.toHexString(struPtTZPos.wTiltPos).trim());
            float WTiltPos = wTiltPos * 0.1f;
            int wZoomPos = Integer.parseInt(Integer.toHexString(struPtTZPos.wZoomPos).trim());
            float WZoomPos = wZoomPos * 0.1f;
            log.debug("PTZ参数：\n  P (Pan)：{}\n  T (Tilt)：{}\n  Z (Zoom)：{}",
                    WPanPos, wTiltPos, wZoomPos);

        }
//        struPtTZPos.wAction = 2;
        //本结构体中的wAction参数是设置时的操作类型，因此获取时该参数无效。实际显示的PTZ值是获取到的十六进制值的十分之一，
        // 如获取的水平参数P的值是0x1750，实际显示的P值为175度；获取到的垂直参数T的值是0x0789，实际显示的T值为78.9度，如果T未负值，获取的值减去360
        // 获取到的变倍参数Z的值是0x1100，实际显示的Z值为110倍。
//        String pHex="13669";
//        int pInter=Integer.parseInt(pHex);
//        short pInter = 13669;
//        log.debug(pInter);
//        struPtTZPos.wPanPos = (short) pInter;
//        struPtTZPos.write();
//        boolean b_SetPTZ = hCNetSDK.NET_DVR_SetDVRConfig(iUserID, HCNetSDK.NET_DVR_SET_PTZPOS, 1, struPtTZPos.getPointer(), struPtTZPos.size());
//        if (b_GetPTZ == false) {
//            log.debug("设置PTZ坐标信息失败，错误码：" + hCNetSDK.NET_DVR_GetLastError());
//        } else {
//
//            log.debug("设置PTZ成功");
//        }
    }

    // 设置云台锁定信息
    public void setPTZLOCKCFG(int iUserID) {
        HCNetSDK.NET_DVR_PTZ_LOCKCFG struPtzLockCfg = new HCNetSDK.NET_DVR_PTZ_LOCKCFG();
        struPtzLockCfg.dwSize = struPtzLockCfg.size();
        Pointer pStrPtzLockCfg = struPtzLockCfg.getPointer();
        NativeLong lChannel = new NativeLong(1);
        IntByReference pInt = new IntByReference(0);
        boolean b_GetPtzLockCfg = hCNetSDK.NET_DVR_GetDVRConfig(iUserID, HCNetSDK.NET_DVR_GET_PTZLOCKCFG, lChannel.intValue(),
                pStrPtzLockCfg, struPtzLockCfg.size(), pInt);
        if (!b_GetPtzLockCfg) {
            log.debug("获取云台锁定信息失败，错误码：{}", hCNetSDK.NET_DVR_GetLastError());
        }
        struPtzLockCfg.read();
        log.debug("通道号：{}", lChannel);
        log.debug("云台锁定控制状态为：{}", struPtzLockCfg.byWorkMode);

        struPtzLockCfg.read();
        struPtzLockCfg.byWorkMode = 1;    //0- 解锁，1- 锁定
        struPtzLockCfg.write();
        boolean b_SetPtzLockCfg = hCNetSDK.NET_DVR_SetDVRConfig(iUserID, HCNetSDK.NET_DVR_SET_PTZLOCKCFG, lChannel.intValue(),
                pStrPtzLockCfg, struPtzLockCfg.size());
        if (!b_SetPtzLockCfg) {
            log.debug("设置云台锁定信息失败，错误码：{}", hCNetSDK.NET_DVR_GetLastError());
        } else {
            log.debug("设置云台锁定信息成功");
            log.debug("云台锁定控制状态当前为：{}", struPtzLockCfg.byWorkMode);
        }
    }

    // 获取(设置)前端参数(扩展)
    public static void getCameraPara(int iUserID) {
        HCNetSDK.NET_DVR_CAMERAPARAMCFG_EX struCameraParam = new HCNetSDK.NET_DVR_CAMERAPARAMCFG_EX();
        Pointer pstruCameraParam = struCameraParam.getPointer();
        IntByReference ibrBytesReturned = new IntByReference(0);
        boolean b_GetCameraParam = hCNetSDK.NET_DVR_GetDVRConfig(iUserID, HCNetSDK.NET_DVR_GET_CCDPARAMCFG_EX, 1, pstruCameraParam, struCameraParam.size(), ibrBytesReturned);
        if (!b_GetCameraParam) {
            log.debug("获取前端参数失败，错误码：{}", hCNetSDK.NET_DVR_GetLastError());
        }
        struCameraParam.read();
        log.debug("是否开启旋转：{}", struCameraParam.struCorridorMode.byEnableCorridorMode);

        struCameraParam.struCorridorMode.byEnableCorridorMode = 1;
        struCameraParam.write();
        boolean b_SetCameraParam = hCNetSDK.NET_DVR_SetDVRConfig(iUserID, HCNetSDK.NET_DVR_SET_CCDPARAMCFG_EX, 1, pstruCameraParam, struCameraParam.size());
        if (!b_SetCameraParam) {
            log.debug("设置前端参数失败，错误码：{}", hCNetSDK.NET_DVR_GetLastError());
        }
        struCameraParam.read();
        log.debug("设置成功");
    }

    // 获取快球聚焦模式信息。
    public static void getFocusMode(int iUserID) {
        HCNetSDK.NET_DVR_FOCUSMODE_CFG struFocusMode = new HCNetSDK.NET_DVR_FOCUSMODE_CFG();
        struFocusMode.read();
        struFocusMode.dwSize = struFocusMode.size();
        struFocusMode.write();
        Pointer pFocusMode = struFocusMode.getPointer();
        IntByReference ibrBytesReturned = new IntByReference(0);
        boolean b_GetCameraParam = hCNetSDK.NET_DVR_GetDVRConfig(iUserID, HCNetSDK.NET_DVR_GET_FOCUSMODECFG, 1, pFocusMode, struFocusMode.size(), ibrBytesReturned);
        if (!b_GetCameraParam) {
            log.debug("获取快球聚焦模式失败，错误码：{}", hCNetSDK.NET_DVR_GetLastError());
        }
        struFocusMode.read();
        log.debug("聚焦模式：{}", struFocusMode.byFocusMode);
        struFocusMode.byFocusMode = 1;
        struFocusMode.byFocusDefinitionDisplay = 1;
        struFocusMode.byFocusSpeedLevel = 3;
        struFocusMode.write();
        boolean b_SetCameraParam = hCNetSDK.NET_DVR_SetDVRConfig(iUserID, HCNetSDK.NET_DVR_SET_FOCUSMODECFG, 1, pFocusMode, struFocusMode.size());
        if (!b_SetCameraParam) {
            log.debug("设置快球聚焦模式失败，错误码：{}", hCNetSDK.NET_DVR_GetLastError());
        }
        struFocusMode.read();
        log.debug("设置成功");
    }

    // 获取IP通道
    public static void getIPChannelInfo(int iUserID) throws UnsupportedEncodingException {
        IntByReference ibrBytesReturned = new IntByReference(0);//获取IP接入配置参数
        HCNetSDK.NET_DVR_IPPARACFG_V40 m_strIpparaCfg = new HCNetSDK.NET_DVR_IPPARACFG_V40();
        m_strIpparaCfg.write();
        //lpIpParaConfig 接收数据的缓冲指针
        Pointer lpIpParaConfig = m_strIpparaCfg.getPointer();
        boolean bRet = hCNetSDK.NET_DVR_GetDVRConfig(iUserID, HCNetSDK.NET_DVR_GET_IPPARACFG_V40, 0, lpIpParaConfig, m_strIpparaCfg.size(), ibrBytesReturned);
        m_strIpparaCfg.read();
        log.debug("起始数字通道号：{}", m_strIpparaCfg.dwStartDChan);

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
//                log.debug("获取图像参数失败，错误码：" + hCNetSDK.NET_DVR_GetLastError());
//            }
            strPicCfg.read();
            m_strIpparaCfg.struStreamMode[iChannum].read();
            if (m_strIpparaCfg.struStreamMode[iChannum].byGetStreamType == 0) {
                m_strIpparaCfg.struStreamMode[iChannum].uGetStream.setType(HCNetSDK.NET_DVR_IPCHANINFO.class);
                m_strIpparaCfg.struStreamMode[iChannum].uGetStream.struChanInfo.read();

                log.debug("--------------第{}个通道------------------", iChannum + 1);
                int channel = m_strIpparaCfg.struStreamMode[iChannum].uGetStream.struChanInfo.byIPID + m_strIpparaCfg.struStreamMode[iChannum].uGetStream.struChanInfo.byIPIDHigh * 256;
                log.debug("channel:{}", channel);
                if (channel > 0) {
                    log.debug("ip： {}", new String(m_strIpparaCfg.struIPDevInfo[channel - 1].struIP.sIpV4).trim());
                }
                log.debug("name： {}", new String(strPicCfg.sChanName, "GBK").trim());
                if (m_strIpparaCfg.struStreamMode[iChannum].uGetStream.struChanInfo.byEnable == 1) {
                    log.debug("IP通道{}在线", channum);
                } else {
                    log.debug("IP通道{}不在线", channum);
                }
            }
        }
    }

    // 获取高精度PTZ绝对位置配置,一般热成像设备支持
    public void getPTZAbsoluteEx() {
        NET_DVR_STD_CONFIG struSTDcfg = new NET_DVR_STD_CONFIG();
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
        boolean bGetPTZ = hCNetSDK.NET_DVR_GetSTDConfig(this.getLoginHandler().intValue(), HCNetSDK.NET_DVR_GET_PTZABSOLUTEEX, struSTDcfg);
        if (!bGetPTZ) {
            log.debug("获取PTZ参数错误,错误码：{}", hCNetSDK.NET_DVR_GetLastError());
            return;
        }
        struPTZ.read();
        log.debug("焦距范围：{}", struPTZ.dwFocalLen);
        log.debug("聚焦参数：{}", struPTZ.struPTZCtrl.dwFocus);
        return;
    }

    // 获取GB28181参数
    public void getGB28181Info() {
        HCNetSDK.NET_DVR_STREAM_INFO streamInfo = new HCNetSDK.NET_DVR_STREAM_INFO();
        streamInfo.read();
        streamInfo.dwSize = streamInfo.size(); //设置结构体大小
        streamInfo.dwChannel = 1; //设置通道
        streamInfo.write();
        Pointer lpInBuffer = streamInfo.getPointer();
        NET_DVR_GBT28181_CHANINFO_CFG gbt28181ChaninfoCfg = new NET_DVR_GBT28181_CHANINFO_CFG();
        gbt28181ChaninfoCfg.read();
        gbt28181ChaninfoCfg.dwSize = gbt28181ChaninfoCfg.size();
        gbt28181ChaninfoCfg.write();
        Pointer lpOutBuffer = gbt28181ChaninfoCfg.getPointer();
        IntByReference lpBytesReturned = new IntByReference(0);
        //3251对应它的宏定义
        boolean bRet = hCNetSDK.NET_DVR_GetDeviceConfig(this.getLoginHandler().intValue(), 3251, 1, lpInBuffer,
                streamInfo.size(), lpBytesReturned.getPointer(), lpOutBuffer, gbt28181ChaninfoCfg.size());
        gbt28181ChaninfoCfg.read();

        if (!bRet) {
            log.debug("获取失败,错误码：{}", hCNetSDK.NET_DVR_GetLastError());
        }
    }

    // 获取码流加密信息
    public void getAESInfo() {
        HCNetSDK.NET_DVR_AES_KEY_INFO net_dvr_aes_key_info = new HCNetSDK.NET_DVR_AES_KEY_INFO();
        net_dvr_aes_key_info.read();
        Pointer pnet_dvr_aes_key_info = net_dvr_aes_key_info.getPointer();
        IntByReference pInt = new IntByReference(0);
        boolean b_GetCfg = hCNetSDK.NET_DVR_GetDVRConfig(this.getLoginHandler().intValue(), HCNetSDK.NET_DVR_GET_AES_KEY,
                0Xffffffff, pnet_dvr_aes_key_info, net_dvr_aes_key_info.size(), pInt);
        if (!b_GetCfg) {
            log.debug("获取码流加密失败  错误码：{}", hCNetSDK.NET_DVR_GetLastError());
        }
        log.debug("获取码流加密信息成功");


    }

    // 设置球机预置点
    public void getCruisePoint() {
        NET_DVR_CRUISEPOINT_COND struCruisepointCond = new NET_DVR_CRUISEPOINT_COND();
        struCruisepointCond.read();
        struCruisepointCond.dwSize = struCruisepointCond.size();
        struCruisepointCond.dwChan = 1;
        struCruisepointCond.wRouteNo = 1;
        struCruisepointCond.write();

        NET_DVR_CRUISEPOINT_V50 struCruisepointV40 = new NET_DVR_CRUISEPOINT_V50();
        struCruisepointV40.read();
        struCruisepointV40.dwSize = struCruisepointV40.size();
        struCruisepointV40.write();

        // 错误信息列表
        IntByReference pInt = new IntByReference(0);
        Pointer lpStatusList = pInt.getPointer();

        boolean flag = hCNetSDK.NET_DVR_GetDeviceConfig(this.getLoginHandler().intValue(), 6714, 1,
                struCruisepointCond.getPointer(), struCruisepointCond.size(), lpStatusList, struCruisepointV40.getPointer(), struCruisepointV40.size());
        if (!flag) {
            int iErr = hCNetSDK.NET_DVR_GetLastError();
            log.debug("NET_DVR_STDXMLConfig失败，错误号：{}", iErr);
            return;
        }
        struCruisepointV40.read();
    }

    // 抓图保存到缓冲区
    public void getPictoPointer(int iUserID) {
        HCNetSDK.NET_DVR_JPEGPARA jpegpara = new HCNetSDK.NET_DVR_JPEGPARA();
        jpegpara.read();
        jpegpara.wPicSize = 255;
        jpegpara.wPicQuality = 0;
        jpegpara.write();
        HCNetSDK.BYTE_ARRAY byte_array = new HCNetSDK.BYTE_ARRAY(10 * 1024 * 1024);
        IntByReference ret = new IntByReference(0);
        boolean b = hCNetSDK.NET_DVR_CaptureJPEGPicture_NEW(iUserID, 1, jpegpara, byte_array.getPointer(), byte_array.size(), ret);
        if (!b) {
            log.debug("抓图失败：{}", hCNetSDK.NET_DVR_GetLastError());
            return;
        }
        byte_array.read();
        log.debug("抓图成功");
    }

    /**
     * 获取报警主机RS485参数
     */
    public void getRs485Cfg() {
        HCNetSDK.NET_DVR_ALARM_RS485CFG rs485CFG = new HCNetSDK.NET_DVR_ALARM_RS485CFG();
        rs485CFG.dwSize = rs485CFG.size();
        Pointer pointer = rs485CFG.getPointer();
        IntByReference pInt1 = new IntByReference(0);
        rs485CFG.write();
        boolean bGetRs485 = hCNetSDK.NET_DVR_GetDVRConfig(this.getLoginHandler().intValue(), HCNetSDK.NET_DVR_GET_ALARM_RS485CFG, 3, pointer, rs485CFG.dwSize, pInt1);
        if (!bGetRs485) {
            log.debug("获取报警主机RS485参数失败！错误号：{}", hCNetSDK.NET_DVR_GetLastError());
            return;
        }
        rs485CFG.read();
    }

}



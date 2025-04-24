package com.dw.hikvision.commom;

import com.dw.hikvision.sdk.HCNetSDK;
import com.dw.hikvision.sdk.structure.NET_DVR_LOCAL_SDK_PATH;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Objects;


/**
 * 初始化 sdk
 *
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/4/22 10:45
 */
@Configuration
public class HikvisionSDKInitializer {

    @PostConstruct
    public void init() {
        // linux系统建议调用以下接口加载组件库
        if (OsSelect.isLinux()) {
            HCNetSDK.BYTE_ARRAY ptrByteArray1 = new HCNetSDK.BYTE_ARRAY(256);
            HCNetSDK.BYTE_ARRAY ptrByteArray2 = new HCNetSDK.BYTE_ARRAY(256);
            String resourcePath = Objects.requireNonNull(this.getClass().getClassLoader().getResource("")).toString();
            if (!resourcePath.endsWith("/")) {
                resourcePath += "/";
            }
            String strPath1 = resourcePath + "libs/hikvision/linux/libcrypto.so.1.1";
            System.arraycopy(strPath1.getBytes(), 0, ptrByteArray1.byValue, 0, strPath1.length());
            ptrByteArray1.write();
            HCNetSDK.HC_NET_SDK.NET_DVR_SetSDKInitCfg(3, ptrByteArray1.getPointer());

            String strPath2 = resourcePath + "libs/hikvision/linux/libssl.so.1.1";
            System.arraycopy(strPath2.getBytes(), 0, ptrByteArray2.byValue, 0, strPath2.length());
            ptrByteArray2.write();
            HCNetSDK.HC_NET_SDK.NET_DVR_SetSDKInitCfg(4, ptrByteArray2.getPointer());

            String strPathCom = resourcePath + "libs/hikvision/linux/";
            NET_DVR_LOCAL_SDK_PATH struComPath = new NET_DVR_LOCAL_SDK_PATH();
            System.arraycopy(strPathCom.getBytes(), 0, struComPath.sPath, 0, strPathCom.length());
            struComPath.write();
            HCNetSDK.HC_NET_SDK.NET_DVR_SetSDKInitCfg(2, struComPath.getPointer());
        }

        // SDK初始化，一个程序进程只需要调用一次
        HCNetSDK.HC_NET_SDK.NET_DVR_Init();

        // 启用SDK写日志
        HCNetSDK.HC_NET_SDK.NET_DVR_SetLogToFile(3, "./sdkLog", false);
    }
}

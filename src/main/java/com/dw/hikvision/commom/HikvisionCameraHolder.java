package com.dw.hikvision.commom;

import com.dw.hikvision.domain.HikvisionCamera;
import com.dw.hikvision.sdk.HCNetSDK;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/4/17 16:29
 */
@Slf4j
@Component
public class HikvisionCameraHolder {

    private static final String DEFAULT_USERNAME = "admin";
    private static final String DEFAULT_PASSWORD = "admin123456";
    private static final int DEFAULT_PORT = 8000;
    /**
     * key：loginHandler；Value：HikvisionCamera
     */
    private static final Map<Long, HikvisionCamera> HASH_MAP = new ConcurrentHashMap<>();

    public static void registerDevice(HikvisionCamera device) {
        HASH_MAP.put(device.getLoginHandler(), device);
        System.out.println(HASH_MAP);
    }

    public static HikvisionCamera getDeviceByLoginHandler(Long longinHandler) {
        return HASH_MAP.get(longinHandler);
    }

    public static void removeByLoginHandler(Long loginHandler) {
        HikvisionCamera hikvisionCamera = HASH_MAP.remove(loginHandler);
    }

    public void remove(Long loginHandler) {
        HikvisionCamera hikvisionCamera = HASH_MAP.remove(loginHandler);
        hikvisionCamera.logout();
    }

    public void removeAll() {
        HASH_MAP.forEach((k, v) -> remove(k));
    }

    @PreDestroy
    public void destroy() {
        for (HikvisionCamera camera : HASH_MAP.values()) {
            camera.logout();
        }
        removeAll();
        // 释放资源
        HCNetSDK.HC_NET_SDK.NET_DVR_Cleanup();
    }
}

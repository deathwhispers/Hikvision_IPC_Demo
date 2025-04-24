package com.dw.hikvision.paramcfg;

import com.dw.hikvision.domain.HikvisionWorkStatus;
import com.dw.hikvision.sdk.HCNetSDK;
import com.dw.hikvision.sdk.callback.DEV_WORK_STATE_CB;
import com.dw.hikvision.sdk.structure.NET_DVR_CHANNELSTATE_V30;
import com.dw.hikvision.sdk.structure.NET_DVR_DISKSTATE;
import com.dw.hikvision.sdk.structure.NET_DVR_WORKSTATE_V40;
import com.sun.jna.Pointer;
import lombok.extern.slf4j.Slf4j;

/**
 * 设备工作状态 callback
 *
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/4/14 17:38
 */
@Slf4j
public class DevWorkStateCallback implements DEV_WORK_STATE_CB {

    private DevWorkStateCallback() {
    }

    private static class DevWorkStateCallbackHolder {
        private static final DevWorkStateCallback INSTANCE = new DevWorkStateCallback();
    }

    public static DevWorkStateCallback getInstance() {
        return DevWorkStateCallbackHolder.INSTANCE;
    }

    @Override
    public boolean invoke(Pointer pUserdata, int iUserID, NET_DVR_WORKSTATE_V40 lpWorkState) {
        System.out.println("callback >> " + lpWorkState);
        return true;
    }

    public HikvisionWorkStatus processDeviceStatus(NET_DVR_WORKSTATE_V40 lpWorkState) {
        HikvisionWorkStatus hikvisionWorkStatus = new HikvisionWorkStatus();
        try {
            int diskStatus = processDiskStatus(lpWorkState.struHardDiskStatic);
            hikvisionWorkStatus.setDiskStatus(diskStatus);
            int audioChanStatus = processAudioChannelStatus(lpWorkState.byAudioInChanStatus);
            hikvisionWorkStatus.setAudioChanStatus(audioChanStatus);
            int channelStatus = processChannelStatus(lpWorkState.struChanStatic);
            hikvisionWorkStatus.setAudioChanStatus(channelStatus);
            int alarmInStatus = processAlarmStatus(lpWorkState.dwHasAlarmInStatic);
            hikvisionWorkStatus.setAlarmInStatus(alarmInStatus);
            int alarmOutStatus = processAlarmStatus(lpWorkState.dwHasAlarmOutStatic);
            hikvisionWorkStatus.setAlarmOutStatus(alarmOutStatus);
            log.debug("hikvisionStatus is {}", hikvisionWorkStatus);
        } catch (Exception e) {
            log.error("处理设备状态时发生异常", e);
        }
        return hikvisionWorkStatus;
    }

    /**
     * 处理硬盘状态
     *
     * @param disks /
     * @return 0:正常, 1:异常
     */
    private int processDiskStatus(NET_DVR_DISKSTATE[] disks) {
        if (disks == null) return 0;
        for (int i = 0; i < Math.min(disks.length, HCNetSDK.MAX_DISKNUM_V30); i++) {
            if (disks[i] == null || disks[i].dwVolume == 0) continue;

            // 如果有任何异常状态则返回 1
            if ((disks[i].dwHardDiskStatic & 0x02) != 0 ||  // 不正常
                    (disks[i].dwHardDiskStatic & 0x03) == 0x03) { // 休眠硬盘出错
                return 1;
            }
        }
        return 0;
    }

    /**
     * 处理语音通道状态
     *
     * @param audioStatus 语音通道状态数组
     * @return 0:所有通道都在使用中, 1:至少有一个通道未使用
     */
    private int processAudioChannelStatus(byte[] audioStatus) {
        if (audioStatus == null) {
            // 无状态信息视为正常
            return 0;
        }
        for (int i = 0; i < HCNetSDK.MAX_AUDIO_V30; i++) {
            int byteIndex = i / 8;
            if (byteIndex >= audioStatus.length) {
                break;
            }

            // 检查当前通道是否未使用
            boolean isUnused = ((audioStatus[byteIndex] >> (i % 8)) & 1) == 0;
            if (isUnused) {
                // 发现未使用的通道
                return 1;
            }
        }
        // 所有通道都在使用中
        return 0;
    }

    /**
     * 处理通道状态
     *
     * @param channels 通道状态数组
     * @return 0:正常, 1:信号丢失, 2:硬件异常
     */
    private int processChannelStatus(NET_DVR_CHANNELSTATE_V30[] channels) {
        if (channels == null) {
            return 0; // 无通道信息视为正常
        }
        int result = 0; // 默认正常
        for (int i = 0; i < Math.min(channels.length, HCNetSDK.MAX_CHANNUM_V40); i++) {
            if (channels[i] == null || channels[i].dwChannelNo == 0xFFFFFFFF) {
                continue;
            }
            // 优先检查硬件异常（更高优先级）
            if (channels[i].byHardwareStatic != 0) {
                result = 2; // 硬件异常
                break;
            }
            // 其次检查信号丢失
            if (channels[i].bySignalStatic != 0 && result < 1) {
                result = 1; // 信号丢失
            }
        }
        return result;
    }

    // 处理报警状态 (1:异常, 0 :正常)
    private int processAlarmStatus(int[] alarmStatus) {
        if (alarmStatus == null) return 0;
        for (int i = 0; i < Math.min(alarmStatus.length, HCNetSDK.MAX_ALARMIN_V40); i++) {
            if (alarmStatus[i] != 0xFFFFFFFF && alarmStatus[i] != 0) {
                return 1; // 发现触发状态
            }
        }
        return 0;
    }
}

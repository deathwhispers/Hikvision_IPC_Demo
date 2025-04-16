package com.dw.hikvision.paramcfg;

import com.dw.hikvision.demo.HCNetSDK;
import com.sun.jna.Pointer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/4/14 17:38
 */
public class dev_work_state_cb implements HCNetSDK.DEV_WORK_STATE_CB {
    public boolean invoke(Pointer pUserdata, int iUserID, HCNetSDK.NET_DVR_WORKSTATE_V40 lpWorkState) {

        lpWorkState.read();

        // 4.1 输出设备全局状态
        System.out.println("\n== 设备工作状态 ==");
        System.out.printf("设备状态: %s%n",
                lpWorkState.dwDeviceStatic == 0 ? "正常" :
                        lpWorkState.dwDeviceStatic == 1 ? "CPU占用过高" : "硬件错误");
        System.out.printf("本地显示状态: %s%n",
                lpWorkState.dwLocalDisplay == 0 ? "正常" : "异常");
        System.out.printf("湿度: %.1f%%，温度: %.1f℃%n",
                lpWorkState.fHumidity, lpWorkState.fTemperature);

        // 4.2 处理硬盘状态
        List<String> normalDisks = new ArrayList<>();
        List<String> abnormalDisks = new ArrayList<>();
        int totalDisks = 0;

        for (int i = 0; i < HCNetSDK.MAX_DISKNUM_V30; i++) {
            HCNetSDK.NET_DVR_DISKSTATE disk = lpWorkState.struHardDiskStatic[i];
            if (disk.dwVolume == 0) {
                continue; // 跳过未检测到的硬盘
            }
            totalDisks++;

            // 解析硬盘状态
            boolean isNormal = true;
            StringBuilder statusDesc = new StringBuilder();
            if ((disk.dwHardDiskStatic & 1) != 0) {
                statusDesc.append("休眠, ");
            }
            if ((disk.dwHardDiskStatic & 2) != 0) {
                statusDesc.append("不正常, ");
                isNormal = false;
            }
            if ((disk.dwHardDiskStatic & 3) != 0) { // 注意：3是二进制 11，可能表示组合状态
                statusDesc.append("休眠硬盘出错");
                isNormal = false;
            }
            if (statusDesc.length() == 0) {
                statusDesc.append("正常");
            } else {
                statusDesc.setLength(statusDesc.length() - 2); // 移除最后的逗号和空格
            }

            // 根据状态分类
            if (isNormal) {
                normalDisks.add(String.format("硬盘 %d: %s", i + 1, statusDesc));
            } else {
                abnormalDisks.add(String.format("硬盘 %d: %s", i + 1, statusDesc));
            }
        }

        // 输出硬盘状态
        System.out.println("\n== 硬盘状态（共 " + totalDisks + " 块硬盘）==");
        if (!normalDisks.isEmpty()) {
            System.out.println("正常硬盘：");
            normalDisks.forEach(System.out::println);
        }
        if (!abnormalDisks.isEmpty()) {
            System.out.println("异常硬盘：");
            abnormalDisks.forEach(System.out::println);
        } else {
            System.out.println("所有硬盘状态正常。");
        }

        // 输出语音通道状态
        System.out.println("\n== 语音通道状态 ==");
        for (int i = 0; i < HCNetSDK.MAX_AUDIO_V30; i++) {
            boolean isUsed = ((lpWorkState.byAudioInChanStatus[i / 8] >> (i % 8)) & 1) == 1;
            System.out.printf("语音通道 %d: %s%n", i + 1, isUsed ? "使用中" : "未使用");
        }

        // 处理并输出通道状态
        System.out.println("\n== 通道状态 ==");
        List<String> abnormalChannels = new ArrayList<>();
        for (int i = 0; i < HCNetSDK.MAX_CHANNUM_V40; i++) {
            HCNetSDK.NET_DVR_CHANNELSTATE_V30 chan = lpWorkState.struChanStatic[i];
            if (chan.dwChannelNo == 0xFFFFFFFF) { // 无效通道
                break;
            }

            StringBuilder statusDesc = new StringBuilder();
            if (chan.byRecordStatic != 0) statusDesc.append("录像中, ");
            if (chan.bySignalStatic != 0) statusDesc.append("信号丢失, ");
            if (chan.byHardwareStatic != 0) statusDesc.append("硬件异常, ");
            if (statusDesc.length() > 0) {
                statusDesc.setLength(statusDesc.length() - 2); // 移除最后的逗号和空格
                abnormalChannels.add(String.format("通道 %d: %s", chan.dwChannelNo, statusDesc));
            } else {
                System.out.printf("通道 %d: 正常%n", chan.dwChannelNo);
            }
        }
        if (!abnormalChannels.isEmpty()) {
            System.out.println("以下通道状态异常：");
            for (String channelInfo : abnormalChannels) {
                System.out.println(channelInfo);
            }
        }

        // 处理并输出报警输入状态
        System.out.println("\n== 报警输入状态 ==");
        List<String> abnormalAlarmIns = new ArrayList<>();
        for (int i = 0; i < HCNetSDK.MAX_ALARMIN_V40; i++) {
            if (lpWorkState.dwHasAlarmInStatic[i] != 0xFFFFFFFF && lpWorkState.dwHasAlarmInStatic[i] != 0) {
                abnormalAlarmIns.add(String.format("报警输入口 %d: 触发", i + 1));
            }
        }
        if (!abnormalAlarmIns.isEmpty()) {
            System.out.println("以下报警输入口状态异常：");
            for (String alarmInfo : abnormalAlarmIns) {
                System.out.println(alarmInfo);
            }
        } else {
            System.out.println("所有报警输入口状态正常。");
        }

        // 处理并输出报警输出状态
        System.out.println("\n== 报警输出状态 ==");
        List<String> abnormalAlarmOuts = new ArrayList<>();
        for (int i = 0; i < HCNetSDK.MAX_ALARMOUT_V40; i++) {
            if (lpWorkState.dwHasAlarmOutStatic[i] != 0xFFFFFFFF && lpWorkState.dwHasAlarmOutStatic[i] != 0) {
                abnormalAlarmOuts.add(String.format("报警输出口 %d: 触发", i + 1));
            }
        }
        if (!abnormalAlarmOuts.isEmpty()) {
            System.out.println("以下报警输出口状态异常：");
            for (String alarmInfo : abnormalAlarmOuts) {
                System.out.println(alarmInfo);
            }
        } else {
            System.out.println("所有报警输出口状态正常。");
        }
        return true;
    }

}

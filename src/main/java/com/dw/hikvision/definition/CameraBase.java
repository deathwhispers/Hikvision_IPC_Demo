package com.dw.hikvision.definition;

import com.sun.jna.Pointer;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 摄像机基础信息
 *
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/4/17 16:34
 */
@Data
@EqualsAndHashCode
public class CameraBase {
    private String peripheralSn;
    private String ip;
    private int port;
    private String username;
    private String password;

    // 设备的 jna 指针，通过 hashcode 生成
    private Pointer devicePointer;
    private Long loginHandler;
}

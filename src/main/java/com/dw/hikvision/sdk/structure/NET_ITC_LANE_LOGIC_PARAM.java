package com.dw.hikvision.sdk.structure;

/**
 * 车道属性参数结构
 *
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/4/21 14:48
 */
public class NET_ITC_LANE_LOGIC_PARAM extends SdkStructure {
    public byte byUseageType;     //车道用途类型，详见ITC_LANE_USEAGE_TYPE
    public byte byDirectionType;  //车道方向类型，详见ITC_LANE_DIRECTION_TYPE
    public byte byCarDriveDirect; //车辆行驶方向，详见ITC_LANE_CAR_DRIVE_DIRECT
    public byte[] byRes = new byte[33];        //保留
}
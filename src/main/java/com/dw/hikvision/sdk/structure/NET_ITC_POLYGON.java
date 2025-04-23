package com.dw.hikvision.sdk.structure;

import static com.dw.hikvision.sdk.HCNetSDK.ITC_MAX_POLYGON_POINT_NUM;

/**
 * 多边型结构体
 *
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/4/21 15:47
 */
public class NET_ITC_POLYGON extends SdkStructure {
    public int dwPointNum; //有效点 大于等于3，若是3点在一条线上认为是无效区域，线交叉认为是无效区域
    public NET_VCA_POINT[] struPos = new NET_VCA_POINT[ITC_MAX_POLYGON_POINT_NUM]; //多边形边界点,最多20个
}
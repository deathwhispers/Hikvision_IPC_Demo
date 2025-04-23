package com.dw.hikvision.sdk.structure;

import static com.dw.hikvision.sdk.HCNetSDK.MAX_CHJC_NUM;

/**
 * 牌识参数
 *
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/4/21 15:52
 */
public class NET_ITC_PLATE_RECOG_PARAM extends SdkStructure {
    public byte[] byDefaultCHN = new byte[MAX_CHJC_NUM]; /*设备运行省份的汉字简写*/
    public byte byEnable; //是否启用该区域牌识，0-否，1-是
    public int dwRecogMode;
    /**
     * 识别的类型，
     * bit0-背向识别：0-正向车牌识别，1-背向识别(尾牌识别) ；
     * bit1-大车牌识别或小车牌识别：0-小车牌识别，1-大车牌识别 ；
     * bit2-车身颜色识别：0-不采用车身颜色识别，在背向识别或小车牌识别时禁止启用，1-车身颜色识别；
     * bit3-农用车识别：0-不采用农用车识别，1-农用车识别；
     * bit4-模糊识别：0-不采用模糊识别，1-模糊识别；
     * bit5-帧定位或场定位：0-帧定位，1-场定位；
     * bit6-帧识别或场识别：0-帧识别，1-场识别；
     * bit7-晚上或白天：0-白天，1-晚上
     * bit8-摩托车识别：0-不采用摩托车识别，1-摩托车识别;
     * bit9-场景模式：0-电警/多帧，1-卡口；
     * bit10-微小车牌：0-不启用，1-启用微小车牌识别(像素60～80)
     * bit11-安全带检测：0-不启用，1-启用安全带检测
     * bit12-民航车牌识别: 0-不启用，1-开启民航车牌识别
     * bit13-车牌过渡倾斜处理: 0-不启用，1-开启过渡倾斜处理（PRS）
     * bit14-超大车牌识别: 0-不启用，1-开启超大车牌识别（PRS）
     * bit15-遮阳板检测：0-不启用，1-启用遮阳板检测
     * bit16-黄标车检测：0-不启用，1-启用黄标车检测
     * bit17-危险品车辆检测：0-不启用，1-启用危险品车辆检测
     * bit18-使馆车牌识别：0-不启用，1-启用使馆车牌识别
     * bit19-车辆子品牌识别：0-不启用，1-启用车辆子品牌识别
     * bit20-打电话识别：0-不启用，1-启用
     * bit21-车窗悬挂物识别：0-不启用，1-启用
     */
    public byte byVehicleLogoRecog;//车标识别 0-不启用，1-启用
    /**
     * 0-保留，1-澳，2-京，3-渝，4-闽，5-甘，6-粤，7-桂，8-贵，9-琼，10-冀，11-豫，
     * 12-黑，13-鄂，14-湘，15-吉，16-苏，17-赣，18-辽，19-蒙，20-宁，21-青，22-鲁，
     * 23-晋，24-陕，25-沪，26-川，27-台，28-津，29-藏，30-港，31-新，32-云，33-浙，
     * 34-皖，0xff-全部
     */
    public byte byProvince;//省份索引值
    public byte byRegion;// 区域索引值 0-保留，1-欧洲，2-俄语区域, 3-欧洲&俄罗斯(EU&CIS),4-中东(Middle East)
    public byte byCountry;//国家索引，参照枚举COUNTRY_INDEX(不支持“COUNTRY_ALL = 0xff,//ALL 全部”)
    public short wPlatePixelWidthMin;//车牌像素识别宽度最小值（单位是像素）当前推荐范围[130,500]
    public short wPlatePixelWidthMax;//车牌像素识别宽度最大值（单位是像素）当前推荐范围[130,500]
    public byte[] byRes = new byte[24];
}

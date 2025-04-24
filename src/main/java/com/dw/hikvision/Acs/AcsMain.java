package com.dw.hikvision.Acs;

import com.dw.hikvision.commom.OsSelect;
import com.dw.hikvision.sdk.HCNetSDK;
import com.dw.hikvision.sdk.structure.NET_DVR_DEVICEINFO_V40;
import com.dw.hikvision.sdk.structure.NET_DVR_LOCAL_SDK_PATH;
import com.dw.hikvision.sdk.structure.NET_DVR_USER_LOGIN_INFO;
import com.sun.jna.Native;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;

/**
 *明眸门禁以卡为中心demo示例
 */
public class AcsMain {
    static HCNetSDK hCNetSDK = null;
    static int lUserID = -1;//用户句柄
    static int iCharEncodeType = 0;  //设备字符集

    /**
     * @param args
     * @throws UnsupportedEncodingException
     * @throws InterruptedException
     */
    public static void main(String[] args) throws UnsupportedEncodingException, InterruptedException, JSONException {

        /*登录*/

        /*远程控门*/
//        DoorManage.controlGateway(lUserID, 1, 0);


        /*门禁主机参数设置（获取）*/
        ACSManage.acsCfg(lUserID);

        /*获取门禁主机工作状态*/
//        ACSManage.getAcsStatus(lUserID);


        /*设置卡计划模块*/
//        CardManage.setCardTemplate(lUserID, 2);

        /*卡下发*/
//        CardManage.setOneCard(lUserID, "123555", 2222, (short) 1);

        /*获取（查询）一张卡信息*/
//        CardManage.getOneCard(lUserID, "123555");

        /*
         * 删除人员
         */
//        UserManage.deleteUserInfo(lUserID);

        /*获取所有卡*/
//        CardManage.getAllCard(lUserID);

        /*批量卡号下发*/
//        String[] CardNo = new String[]{"1111", "2222", "3333", "4444"};
//        int[] EmployeeNo = new int[]{111, 222, 333, 444};
//        CardManage.setMultiCard(lUserID, CardNo, EmployeeNo, 4);

        /*删除单张卡（删除单张卡号之前要先删除这张卡关联的人脸和指纹信息）*/
//        CardManage.delOneCard(lUserID,"123555");

        /*清空设备所有人脸、指纹、卡号信息*/
//        CardManage.cleanCardInfo(lUserID);

        /*下发一张人脸*/
        /*注意：下发人脸之前先下发卡号 */
//        FaceManage.setOneFace(lUserID,"123555");

        /*删除单独人脸信息*/
//        FaceManage.delOneFace(lUserID,"123555");

        /*查询单张卡号关联的人脸信息*/
//        FaceManage.getFaceCfg(lUserID,"4444");

        /*人脸采集（设备采集人脸图片保存到本地）*/
//        FaceManage.captureFaceInfo(lUserID);

        /*门状态计划模板配置*/
//        DoorManage.doorTemplate(lUserID,1);

        /*指纹采集*/
//        FingerManage.captureFinger(lUserID);

        /*指纹下发*/
//        FingerManage.setOneFinger(lUserID,"123555");

        /*指纹删除*/

//        FingerManage.delFinger(lUserID,"123123");


        /*查询设备事件*/
//        EventSearch.searchAllEvent(lUserID);

        /*下发身份证禁止名单*/
//         IDBlockListManage.uploadBlockList(lUserID);

        /*清空身份证禁止名单*/
//        IDBlockListManage.cleanBlockList(lUserID);

        /*
         * 增加sleep时间，保证程序一直运行，
         */
        Thread.sleep(20000);

        /*登出操作*/

        return;
    }

}

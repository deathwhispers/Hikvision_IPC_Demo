package com.dw.hikvision.commom;

import java.io.*;

public class CommonUtil {

    //SDK时间解析
    public static String parseTime(int time) {
        int year = (time >> 26) + 2000;
        int month = (time >> 22) & 15;
        int day = (time >> 17) & 31;
        int hour = (time >> 12) & 31;
        int min = (time >> 6) & 63;
        int second = (time >> 0) & 63;
        String sTime = year + "-" + month + "-" + day + "-" + hour + ":" + min + ":" + second;
//        System.out.println(sTime);
        return sTime;


    }

    //分辨率解析
    public static String parseResolution(int dwResolution) {
        int interlace = (((dwResolution) >> 28) & 0x1);
        int width = ((((dwResolution) >> 19) & 0x1ff) << 3);  //宽
        int hight = ((((dwResolution) >> 8) & 0x7ff) << 1); //高
        int fps = ((dwResolution) & 0xff);  //帧率
        String result = width + "*" + hight + "_" + fps;
        return result;


    }

    /**
     * 读取本地文件到数组中
     *
     * @param filename 本地文件
     * @return 返回读取到的数据到 byte数组
     * @throws IOException
     */
    public static byte[] toByteArray(String filename) throws IOException {
        File file = new File(filename);
        if (!file.exists()) {
            throw new FileNotFoundException(filename);
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream((int) file.length());
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
        try {
            byte[] buffer = new byte[1024];
            int len;
            while (-1 != (len = in.read(buffer, 0, buffer.length))) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            bos.close();
            in.close();
        }
    }


}

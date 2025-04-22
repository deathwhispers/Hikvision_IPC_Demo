package com.dw.hikvision.audio;

import com.dw.hikvision.demo.HCNetSDK;
import com.sun.jna.Pointer;

import java.nio.ByteBuffer;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/4/14 17:22
 */
public class cbVoiceDataCallBack_MR_V30 implements HCNetSDK.FVoiceDataCallBack_MR_V30 {
    public void invoke(int lVoiceComHandle, Pointer pRecvDataBuffer, int dwBufSize, byte byAudioFlag, Pointer pUser) {
        //语音回调函数，实现的是接收设备那边传过来的音频数据（g711编码），如果只需要平台发送音频到设备，不需要接收设备发送的音频，
        // 回调函数里什么都不实现
        //不影响业务功能
        System.out.println("-----=cbVoiceDataCallBack_MR_V30 enter---------");
        if (byAudioFlag == 1) {
            System.out.println("设备发过来的语音");
            //设备发送过来的语音数据
            try {
                //将设备发送过来的语音数据写入文件
                long offset = 0;
                ByteBuffer buffers = pRecvDataBuffer.getByteBuffer(offset, dwBufSize);
                byte[] bytes = new byte[dwBufSize];
                buffers.rewind();
                buffers.get(bytes);
                AudioTest.outputStream.write(bytes);  //这里实现的是将设备发送的g711音频数据写入文件
                //解码
                if (AudioTest.pDecHandle == null) {
                    AudioTest.pDecHandle = AudioTest.hCNetSDK.NET_DVR_InitG711Decoder();
                }
                HCNetSDK.NET_DVR_AUDIODEC_PROCESS_PARAM struAudioParam = new HCNetSDK.NET_DVR_AUDIODEC_PROCESS_PARAM();
                struAudioParam.in_buf = pRecvDataBuffer;
                struAudioParam.in_data_size = dwBufSize;
                HCNetSDK.BYTE_ARRAY ptrVoiceData = new HCNetSDK.BYTE_ARRAY(320);
                ptrVoiceData.write();
                struAudioParam.out_buf = ptrVoiceData.getPointer();
                struAudioParam.out_frame_size = 320;
                struAudioParam.g711_type = 0; //G711编码类型：0- U law，1- A law
                struAudioParam.write();
                if (!AudioTest.hCNetSDK.NET_DVR_DecodeG711Frame(AudioTest.pDecHandle, struAudioParam)) {
                    System.out.println("NET_DVR_DecodeG711Frame failed, error code:" + AudioTest.hCNetSDK.NET_DVR_GetLastError());
                }
                struAudioParam.read();
                //将解码之后PCM音频数据写入文件
                long offsetPcm = 0;
                ByteBuffer buffersPcm = struAudioParam.out_buf.getByteBuffer(offsetPcm, struAudioParam.out_frame_size);
                byte[] bytesPcm = new byte[struAudioParam.out_frame_size];
                buffersPcm.rewind();
                buffersPcm.get(bytesPcm);
                AudioTest.outputStreamPcm.write(bytesPcm);  //这里实现的是将设备发送的pcm音频数据写入文件，（前面的代码实现的就是将g711解码成pcm音频）
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (byAudioFlag == 0) {
            System.out.println("客户端发送音频数据");
        }
    }
}

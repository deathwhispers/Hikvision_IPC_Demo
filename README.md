【Demo内容说明】
---------------------------------------------------
1. 该Demo为报警布防接收报警信息的Demo示例，现有解析报警信息类型：
		COMM_ITS_PLATE_RESULT
		COMM_ALARM_TFS
		OMM_ALARM_AID_V41
		COMM_ALARM_TPS_REAL_TIME
		COMM_ALARM_TPS_STATISTICS
		COMM_ITS_PARK_VEHICLE
		COMM_ALARMHOST_CID_ALARM
		COMM_IPC_AUXALARM_RESULT
		COMM_ISAPI_ALARM
		COMM_VCA_ALARM
		COMM_ALARM_RULE
		COMM_ALARM_ACS
		COMM_ID_INFO_ALARM
		COMM_ALARM_VIDEO_INTERCOM
		COMM_UPLOAD_VIDEO_INTERCOM_EVENT
		COMM_UPLOAD_FACESNAP_RESULT
		COMM_SNAP_MATCH_ALARM
		COMM_ALARM_PDC
		COMM_ALARM_V30
		COMM_ALARM_V40
		COMM_THERMOMETRY_ALARM
		COMM_THERMOMETRY_DIFF_ALARM
		COMM_ALARM_SHIPSDETECTION
		COMM_FIREDETECTION_ALARM
		COMM_UPLOAD_AIOP_VIDEO
		COMM_UPLOAD_AIOP_PICTURE
		COMM_UPLOAD_AIOP_POLLING_SNAP
		COMM_UPLOAD_AIOP_POLLING_VIDEO
2. 该Demo使用Eclipse开发和编译运行。

---

【注意事项】
---------------------------------------------------
1. 请到海康威视官网下载最新版本设备网络SDK：https://open.hikvision.com/download/5cda567cf47ae80dd41a54b3?type=10

2. 请修改程序代码，其中Alarm.java中CreateSDKInstance()接口中指定SDK动态库的路径。此Demo在Win和Linux系统下通用，切换到Linux系统运行，CreateSDKInstance()接口中设置Linux系统SDK库文件路径。

3. Windows开发时需要将“库文件”文件夹中的 .dll 文件拷贝到 lib\win 文件夹下，HCNetSDKCom文件夹（包含里面的功能组件dll库文件）需要和HCNetSDK.dll、HCCore.dll一起加载，放在同一个目录下，且HCNetSDKCom文件夹名不能修改。如果自行开发软件不能正常实现相应功能，而且程序没有指定加载的dll库路径，请在程序运行的情况下尝试删除HCNetSDK.dll。如果可以删除，说明程序可能调用到系统盘Windows->System32目录下的dll文件，建议删除或者更新该目录下的相关dll文件；如果不能删除，dll文件右键选择属性确认SDK库版本。

4. Linux开发时需要将“库文件”文件夹中所有的 .so 文件拷贝到 lib\linux 文件夹下。
HCNetSDKCom文件夹（包含里面的功能组件dll库文件）需要和libhcnetsdk.so、libHCCore.so一起加载，放在同一个目录下，且HCNetSDKCom文件夹名不能修改。如果库文件加载有问题，初始化失败，也可以尝试将SDK所在路径添加到LD_LIBRARY_PATH环境变量中。


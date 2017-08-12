package com.dilusense.custom_camera;

import android.os.Environment;

import java.io.File;

/**
 * 常量类
 * @author KuangCH
 * @version 2015/8/19
 */
public class MyConstants {

    public static String SDCardPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
    public static String ExternalSDCardPath = "/storage/external_storage/sdcard1/";

    public static String MSG_NET_REQUEST_FAILED = "网络请求失败";
    public static String MSG_NET_REQUEST_UNKNOWN = "未知错误";
    public static String MSG_NET_PARAMETER_ERROR = "输入参数错误";
    public static String MSG_NET_SERVER_BUSY = "服务器繁忙";
    public static String MSG_NET_SERVER_INTERNAL_ERROR = "服务器内部错误";

    public static String INTENT_DATA_KEY_IMAGES = "images";
    public static String INTENT_DATA_KEY_TYPE = "pic_source_type";
    public static String INTENT_DATA_KEY_IDENTITY_INFO = "identityInfo";

    public static String CAPTURE_IMG_WH_RATIO = "4/5";

}

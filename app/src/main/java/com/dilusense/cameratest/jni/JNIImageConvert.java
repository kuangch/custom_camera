package com.dilusense.cameratest.jni;

/**
 * Created by Thinkpad on 2017/8/28.
 */

public class JNIImageConvert {

    /**
     * 设置 rgbd 数据存储路径
     * @param path
     */
    public static native void nativeSetRgbdfilePath(String path);

    /**
     * 输入：color流，红外流,转成rgbd数据存储,
     * @param color    color帧数据
     * @param colorWH  数组长度为2， color帧的宽高
     * @param infrared  红外帧数据
     * @param infraredWH  数组长度为2， 红外帧的宽高
     * @return           是否转换保存成功
     */
    public static native boolean nativeStream2Rgbdfile(byte[] color, int[] colorWH, byte[] infrared, int[] infraredWH);
}

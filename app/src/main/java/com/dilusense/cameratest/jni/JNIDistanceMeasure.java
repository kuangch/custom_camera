package com.dilusense.cameratest.jni;

/**
 * Created by Thinkpad on 2017/8/28.
 */

public class JNIDistanceMeasure {

    /**
     * 计算一张图片中两个点之间的空间距离
     * @param image 图像数据
     * @param WH  数组长度为2，图像的宽高
     * @param pointStart 数组长度为2，起始点坐标
     * @param pointEnd  数组长度为2，结束点坐标
     * @return 空间距离（单位 cm）
     */
    public static native long nativeSpaceDistanceMeasure(byte[] image, int[] WH, int[] pointStart, int[] pointEnd);

}

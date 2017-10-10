package com.dilusense.custom_camera.utils;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.util.Log;

/**
 * Created by Thinkpad on 2017/8/15.
 */

public class CameraUtils {

    public static  final  int FRONT = 1;
    public static  final  int BACK = 0;

    // Returns true if the device supports the required hardware level, or better.
    public static boolean isHardwareLevelSupported(CameraCharacteristics c, int requiredLevel) {
        int deviceLevel = c.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
        Log.i("msg", "hardware level: " + String.valueOf(deviceLevel));
        if (deviceLevel == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY) {
            return requiredLevel == deviceLevel;
        }
        // deviceLevel is not LEGACY, can use numerical sort
        return requiredLevel <= deviceLevel;
    }

    private static boolean checkFrontBackCamera(Context ctx, int cameraType){
        try {
            CameraManager cameraManager = (CameraManager) ctx.getSystemService(Context.CAMERA_SERVICE);
            for(String cameraId: cameraManager.getCameraIdList()){
                //获取相机的相关参数
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                // 不使用前置摄像头。
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing != null && facing == cameraType) {
                    return true;
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return false;
    }
    /**
     * 查找前置摄像头
     * @return
     */
    public static boolean findFrontCamera(Context ctx) {
        return checkFrontBackCamera(ctx, CameraCharacteristics.LENS_FACING_BACK);
    }

    /**
     * 查找后置摄像头
     * @return
     */
    public static boolean findBackCamera(Context ctx) {
        return checkFrontBackCamera(ctx, CameraCharacteristics.LENS_FACING_FRONT);
    }

    public static int getCameraId(Context ctx, int firstCamera){

        int back =  CameraCharacteristics.LENS_FACING_FRONT;
        int front =  CameraCharacteristics.LENS_FACING_BACK;
        if (findBackCamera(ctx) && findFrontCamera(ctx)){
            // 前后都有
            return firstCamera == FRONT ? front: back;
        } else if(findBackCamera(ctx) && !findFrontCamera(ctx)){

            // 只有后置
            return  back;
        } else if(!findBackCamera(ctx) && findFrontCamera(ctx)){

            // 只有前置
            return  front;
        }else{

            // 没有前置页面有后置
            return -1;
        }
    }

    public static int returnCameraType(int cameraId){
           return cameraId;
    }

}




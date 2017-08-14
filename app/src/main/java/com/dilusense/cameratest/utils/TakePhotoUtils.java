package com.dilusense.cameratest.utils;

import com.dilusense.cameratest.base.BaseTakePhotoActivity;
import com.dilusense.custom_camera.CustomCameraConstants;
import com.dilusense.custom_camera.utils.StringUtils;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoActivity;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.LubanOptions;
import com.jph.takephoto.model.TakePhotoOptions;

/**
 * Created by Thinkpad on 2017/3/8.
 */
public class TakePhotoUtils {

    public static int IMG_MAX_W = 600;
    public static int IMG_MAX_H
            = (int)
            (IMG_MAX_W
                    * StringUtils.getWHRatio(CustomCameraConstants.CAPTURE_IMG_WH_RATIO,"/").getHeight()
                    / StringUtils.getWHRatio(CustomCameraConstants.CAPTURE_IMG_WH_RATIO,"/").getWidth()
            );
    public static int IMG_MAX_SIZE = 1024 * 100;

    public static TakePhoto getTakePhoto(Object tp){

        TakePhoto takePhoto;
        if(tp instanceof TakePhotoActivity) {
            takePhoto = ((TakePhotoActivity)tp).getTakePhoto();
        }else{
            takePhoto = ((BaseTakePhotoActivity)tp).getTakePhoto();
        }

        TakePhotoUtils.configCompress(takePhoto);
        TakePhotoUtils.configTakePhotoOption(takePhoto);
        return takePhoto;
    }

    public static void configTakePhotoOption(TakePhoto takePhoto) {
        TakePhotoOptions.Builder builder = new TakePhotoOptions.Builder();
        builder.setCorrectImage(true);
        builder.setWithOwnGallery(true);
        takePhoto.setTakePhotoOptions(builder.create());

    }
    public static void configCompress(TakePhoto takePhoto) {
        // compress
        CompressConfig config;
        LubanOptions option = new LubanOptions.Builder().setMaxHeight(IMG_MAX_H).setMaxWidth(IMG_MAX_W).setMaxSize(IMG_MAX_SIZE).create();
        config = CompressConfig.ofLuban(option);
        config.enableReserveRaw(true);
        takePhoto.onEnableCompress(config, true);

    }
    public static CropOptions getCropOptions(){
        CropOptions.Builder builder=new CropOptions.Builder();
        builder.setAspectX(IMG_MAX_W).setAspectY(IMG_MAX_H);
        builder.setOutputX(IMG_MAX_W).setOutputY(IMG_MAX_H);
        builder.setWithOwnCrop(false);
        return builder.create();
    }

}

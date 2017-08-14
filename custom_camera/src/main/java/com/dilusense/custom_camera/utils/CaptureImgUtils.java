package com.dilusense.custom_camera.utils;

import android.content.Context;
import android.net.Uri;

import java.io.File;

/**
 * Created by Thinkpad on 2017/3/10.
 */
public class CaptureImgUtils {
    public static Uri getCaptureImgFileUri(Context ctx){
        File imgCacheDir = DeviceUtils.getDiskCacheDir(ctx,"bitmap");
        if (!imgCacheDir.exists()) {
            imgCacheDir.mkdirs();
        }
        File file=new File(imgCacheDir,"capture.jpg");
        return Uri.fromFile(file);
    }
}

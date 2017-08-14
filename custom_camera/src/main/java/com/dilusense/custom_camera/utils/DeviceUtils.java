package com.dilusense.custom_camera.utils;

import android.content.Context;
import android.os.Environment;
import android.telephony.TelephonyManager;

import java.io.File;

public class DeviceUtils {

    public static String getDeviceID(Context ctx) {

        TelephonyManager telephonyManager = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);

        return telephonyManager.getDeviceId();

    }

    public static File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if ((Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) && context.getExternalCacheDir() != null) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }
}

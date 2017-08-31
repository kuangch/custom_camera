package com.dilusense.a3d_camera.camera;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * Created by Thinkpad on 2017/8/30.
 */

public class ImageUtils {

    public static Bitmap overturnBmp(Bitmap bmp) {
        int w = bmp.getWidth();
        int h = bmp.getHeight();

        Matrix matrix = new Matrix();
        matrix.postScale(-1, 1); // 镜像水平翻转
        return Bitmap.createBitmap(bmp, 0, 0, w, h, matrix, true);

    }
}

package com.dilusense.custom_camera;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.jph.takephoto.model.TException;
import com.jph.takephoto.uitl.TUriParse;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Thinkpad on 2017/3/13.
 */
public class BitmapUtils {
    public static String saveImageFile(Activity act, Uri imgOutputUri, Bitmap bmp) {
        String filePath = null;
        try {
            filePath = TUriParse.getFilePathWithUri(imgOutputUri, act);
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (TException t){
            t.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            if (bmp != null && !bmp.isRecycled()) {
                bmp.recycle();
                bmp = null;
            }
        }
        return filePath;
    }

    public static Bitmap readBitMap(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        //获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }
}

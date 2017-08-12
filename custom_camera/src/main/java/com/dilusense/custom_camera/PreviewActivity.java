package com.dilusense.custom_camera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

public class PreviewActivity extends Activity {

    Bitmap b;
    Uri filePath;
    ImageView id_iv_cancel;
    ImageView id_iv_ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//【旋转问题】首先强制竖屏，手机横过来时候 控件不变
        setContentView(R.layout.activity_preview);


        final ImageView id_iv_preview_photo = (ImageView) this.findViewById(R.id.id_iv_preview_photo);
        final Activity act = this;

        id_iv_cancel = (ImageView) this.findViewById(R.id.id_iv_cancel);
        id_iv_ok = (ImageView) this.findViewById(R.id.id_iv_ok);

        Intent intent = this.getIntent();
        if (intent != null) {
            filePath = getIntent().getExtras().getParcelable(MediaStore.EXTRA_OUTPUT);
            Glide.with(this).load(filePath).asBitmap().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(new SimpleTarget<Bitmap>() {

                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {

                    id_iv_preview_photo.setImageBitmap(resource);
                    b = resource;

                }
            });
        } else {
            Toast.makeText(this, "图片加载错误", Toast.LENGTH_SHORT).show();
        }

        initBigImageShowResource();

        id_iv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                PreviewActivity.this.finish();
            }
        });
        id_iv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (b != null) {
                    int saveW = b.getWidth();
                    int saveH = Math.min(b.getHeight(), (int) (saveW * StringUtils.getWHRatio(MyConstants.CAPTURE_IMG_WH_RATIO, "/").getHeight() / StringUtils.getWHRatio(MyConstants.CAPTURE_IMG_WH_RATIO, "/").getWidth()));
                    Bitmap afterCropBitmap = Bitmap.createBitmap(b, 0, 0, saveW, saveH);

                    BitmapUtils.saveImageFile(act, filePath, afterCropBitmap);

                }

                setResult(RESULT_OK);
                PreviewActivity.this.finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            if (b != null && !b.isRecycled()) {
                b.recycle();
                b = null;
            }
        }catch (Exception e){e.printStackTrace();}
    }

    private void initBigImageShowResource() {
        Context ctx = getApplication();

        // button
        ImgClickBgSwitchUtils.onClick(ctx, id_iv_cancel, R.drawable.camera_back_normal, R.drawable.camera_back_pressed);
        ImgClickBgSwitchUtils.onClick(ctx, id_iv_ok, R.drawable.camera_save_normal, R.drawable.camera_save_pressed);
    }

}

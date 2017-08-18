package com.dilusense.cameratest;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.badoo.mobile.util.WeakHandler;
import com.bumptech.glide.Glide;
import com.dilusense.cameratest.base.BaseTakePhotoActivity;
import com.dilusense.cameratest.utils.TakePhotoUtils;
import com.dilusense.custom_camera.CustomCameraActivity;
import com.dilusense.custom_camera.utils.CaptureImgUtils;
import com.jph.takephoto.model.TResult;

import org.andresoviedo.app.model3D.view.ModelActivity;
import org.andresoviedo.app.util.Utils;
import org.andresoviedo.app.util.content.ContentUtils;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseTakePhotoActivity {

    private static final int REQUEST_CODE_OPEN_FILE = 1000;
    
    @BindView(R.id.btn_load_model)
    Button btn_load_model;
    
    @BindView(R.id.btn_take_photo)
    Button btn_take_photo;

    WeakHandler handler = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    public void init() {
        super.init();

        handler = new WeakHandler();

    }

    @OnClick(R.id.btn_take_photo)
    public void btn_take_photoOnClick(){
        TakePhotoUtils.getTakePhoto(ctx).onPickFromCustomCapture(CaptureImgUtils.getCaptureImgFileUri(ctx),CustomCameraActivity.class);
    }

    @OnClick(R.id.btn_load_model)
    public void btn_load_modelOnClick(){
        Intent target = Utils.createGetContentIntent();
        Intent intent = Intent.createChooser(target, "Select a file");
        try {
            startActivityForResult(intent, REQUEST_CODE_OPEN_FILE);
        } catch (ActivityNotFoundException e) {
            // The reason for the existence of aFileChooser
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_CODE_OPEN_FILE:
                if (resultCode == RESULT_OK) {
                    // The URI of the selected file
                    final Uri uri = data.getData();
                    Log.i("Menu", "Loading '" + uri.toString() + "'");
                    if (uri != null) {
                        final String path = ContentUtils.getPath(getApplicationContext(), uri);
                        if (path != null) {
                            launchModelRendererActivity(path);
                        } else {
                            Toast.makeText(getApplicationContext(), "Problem loading '" + uri.toString() + "'",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Result when loading file was '" + resultCode + "'",
                            Toast.LENGTH_SHORT).show();
                }
             default:
                 super.onActivityResult(requestCode,resultCode,data);
        }
    }
    private void launchModelRendererActivity(String filename) {
        Log.i("Menu", "Launching renderer for '" + filename + "'");
        Intent intent = new Intent(getApplicationContext(), ModelActivity.class);
        Bundle b = new Bundle();
        b.putString("uri", filename);
        b.putString("immersiveMode", "true");
        intent.putExtras(b);
        startActivity(intent);
    }


    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        setTitle("自定义相机");
    }

    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);

        final TResult res = result;
        handler.post(new Runnable() {
            @Override
            public void run() {
                Glide.with(getApplicationContext()).load(
                        new File(res.getImages().get(0).getCompressPath())
                ).into((ImageView)findViewById(R.id.img));
            }
        });
    }
}

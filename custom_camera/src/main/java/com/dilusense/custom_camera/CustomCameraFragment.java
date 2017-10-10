package com.dilusense.custom_camera;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.badoo.mobile.util.WeakHandler;
import com.dilusense.custom_camera.utils.BitmapUtils;
import com.dilusense.custom_camera.utils.CameraUtils;
import com.dilusense.custom_camera.utils.Utils;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Thinkpad on 2017/10/10.
 */

public class CustomCameraFragment extends BaseFragment implements Camera.PictureCallback{

    public static final int RC_IS_CHOOSE_PICTURE = 2001;

    private boolean safeToTakePicture = true;

    private CameraPreview mPreview;
    private Camera mCamera;
    private int cameraId = FindFrontCamera() == -1 ? FindBackCamera() : FindFrontCamera();;

    private WeakHandler handler;

    Uri imgOutputUri;

    private String flashMode =  Camera.Parameters.FLASH_MODE_OFF;

    @BindView(R2.id.id_iv_flash_switch)
    ImageView id_iv_flash_switch;
    @BindView(R2.id.iv_camera_face_frame)
    ImageView iv_camera_face_frame;
    @BindView(R2.id.id_iv_shutter)
    ImageView iv_shutter;

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_custom_camera,container, false);
    }

    @Override
    public void initEvent() {

    }

    @Override
    protected void lazyLoad() {

        initBigImageShowResource();

        imgOutputUri = mAct.imgOutputUri;


        if (!Utils.checkCameraHardware(mAct.getApplicationContext())) {
            Toast.makeText(mAct.getApplicationContext(), "设备没有摄像头", Toast.LENGTH_SHORT).show();
            return;
        }

        handler = new WeakHandler();

        // centerWindowView = findViewById(R2.id.center_window_view);
        Log.d("CameraSurfaceView", "CameraSurfaceView onCreate currentThread : " + Thread.currentThread());
        mPreview = (CameraPreview) mContentView.findViewById(R.id.camera_preview);

    }

    @OnClick(R2.id.id_iv_shutter)
    public void id_iv_shutterOnClick(){
        takePicture(null, null, this);
    }
    @OnClick(R2.id.id_iv_flash_switch)
    public void id_iv_flash_switchOnClick(){
        toggleFlash();
    }
    @OnClick(R2.id.id_iv_change)
    public void id_iv_changeOnClick(){
        changeCameraTwo();
    }
    @OnClick(R2.id.iv_back)
    public void iv_backOnClick(){
        mAct.back();
    }

    @Override
    public void onResume() {
        super.onResume();

        startCamera(cameraId);
        initCameraParameters();
    }

    private void initBigImageShowResource(){
        Context ctx = mAct.getApplication();

        iv_camera_face_frame.setImageBitmap(BitmapUtils.readBitMap(ctx, R.drawable.camera_face_frame));

        // button
        ImgClickBgSwitchUtils.onClick(ctx, iv_shutter, R.drawable.camera_capture_normal, R.drawable.camera_capture_pressed);
    }

    void initFocusParams(Camera.Parameters params) {
        //若支持连续对焦模式，则使用.
        List<String> focusModes = params.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            mCamera.setParameters(params);
        } else if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            //进到这里，说明不支持连续对焦模式，退回到点击屏幕进行一次自动对焦.
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            mCamera.setParameters(params);
            //点击屏幕进行一次自动对焦.
            mPreview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCamera.autoFocus(null);
                }
            });
            //4秒后进行第一次自动对焦，之后每隔8秒进行一次自动对焦.
            Observable.timer(4, TimeUnit.SECONDS).flatMap(new Func1<Long, Observable<?>>() {
                @Override
                public Observable<?> call(Long aLong) {
                    mCamera.autoFocus(null);
                    return Observable.interval(8, TimeUnit.SECONDS);
                }
            }).subscribe(new Action1<Object>() {
                @Override
                public void call(Object aLong) {
                    mCamera.autoFocus(null);
                }
            });
        }
    }

    void changeCameraTwo() {
        //切换前后摄像头
        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.CameraInfo currCameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();//得到摄像头的个数
        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, cameraInfo);//得到每一个摄像头的信息
            Camera.getCameraInfo(cameraId, currCameraInfo); // 当前摄像头信息
            if (currCameraInfo.facing != cameraInfo.facing) {
                cameraId = i;
                break;
            }
        }
        try {
            mCamera.stopPreview();//停掉原来摄像头的预览
            releaseCamera();
            mCamera = Camera.open(cameraId);//打开当前选中的摄像头

            mCamera.setPreviewDisplay(mPreview.getLouisSurfaceHolder());//通过surfaceview显示取景画面
//                mCamera.setDisplayOrientation(90); //
            mPreview.setCamera(mCamera);

            mCamera.startPreview();//开始预览
            initCameraParameters();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void startCamera(int cameraId) {
        // Open the default i.e. the first rear facing camera.
        try {
            if (mCamera == null) {
                mCamera = Camera.open(cameraId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mAct.getApplicationContext(), "启动照相机失败，请检查设备并打开权限", Toast.LENGTH_SHORT).show();
        }
        mPreview.setCamera(mCamera);

    }

    @Override
    public void onPause() {
        super.onPause();
        releaseCamera();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseCamera();
    }

    /**
     * 释放mCamera
     */
    private void releaseCamera() {
        // Because the Camera object is a shared resource, it's very
        // important to release it when the activity is paused.
        if (mCamera != null) {
            mPreview.setCamera(null);
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * 拍照
     *
     * @param shutter
     * @param raw
     * @param jpeg
     */
    public void takePicture(Camera.ShutterCallback shutter, Camera.PictureCallback raw,
                            Camera.PictureCallback jpeg) {
        if (mCamera != null) {
            if (safeToTakePicture) {
                mCamera.takePicture(shutter, raw, jpeg);
                safeToTakePicture = false;
            }
        }
    }

    public void initCameraParameters(){
        if(mCamera != null){
            Camera.Parameters p = mCamera.getParameters();
            p.setFlashMode(flashMode);
            initFocusParams(p);
            mCamera.setParameters(p);

            p = mCamera.getParameters();
            if (Camera.Parameters.FLASH_MODE_OFF.equals(p.getFlashMode())) {
                id_iv_flash_switch.setImageBitmap(BitmapUtils.readBitMap(mAct.getApplicationContext(), R.drawable.camera_flash_off));
            } else if (Camera.Parameters.FLASH_MODE_ON.equals(p.getFlashMode())) {
                id_iv_flash_switch.setImageBitmap(BitmapUtils.readBitMap(mAct.getApplicationContext(), R.drawable.camera_flash_on));
            } else if (Camera.Parameters.FLASH_MODE_AUTO.equals(p.getFlashMode())) {
                id_iv_flash_switch.setImageBitmap(BitmapUtils.readBitMap(mAct.getApplicationContext(), R.drawable.camera_flash_auto));
            } else {
//                Toast.makeText(mAct.getApplicationContext(), "Flash mode setting is not supported.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    /**
     * 开关闪光灯
     * <p/>
     * 持续的亮灯FLASH_MODE_TORCH
     * 闪一下FLASH_MODE_ON
     * 关闭模式FLASH_MODE_OFF
     * 自动感应是否要用闪光灯FLASH_MODE_AUTO
     */
    public void toggleFlash() {
        if (mCamera == null) {
            return;
        }

        Camera.Parameters p = mCamera.getParameters();
        if (Camera.Parameters.FLASH_MODE_OFF.equals(p.getFlashMode())) {
            p.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
            flashMode = Camera.Parameters.FLASH_MODE_ON;
            mCamera.setParameters(p);
            id_iv_flash_switch.setImageBitmap(BitmapUtils.readBitMap(mAct.getApplicationContext(), R.drawable.camera_flash_on));
        } else if (Camera.Parameters.FLASH_MODE_ON.equals(p.getFlashMode())) {
            p.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
            flashMode = Camera.Parameters.FLASH_MODE_AUTO;
            mCamera.setParameters(p);
            id_iv_flash_switch.setImageBitmap(BitmapUtils.readBitMap(mAct.getApplicationContext(), R.drawable.camera_flash_auto));
        } else if (Camera.Parameters.FLASH_MODE_AUTO.equals(p.getFlashMode())) {
            p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            flashMode = Camera.Parameters.FLASH_MODE_OFF;
            mCamera.setParameters(p);
            id_iv_flash_switch.setImageBitmap(BitmapUtils.readBitMap(mAct.getApplicationContext(), R.drawable.camera_flash_off));

//            p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);//持续的亮灯
//            flashMode = Camera.Parameters.FLASH_MODE_TORCH;
//            mCamera.setParameters(p);
//            id_iv_flash_switch.setImageDrawable(getResources().getDrawable(R2.mipmap.camera_flash_light));
        } else {
            Toast.makeText(mAct.getApplicationContext(), "Flash mode setting is not supported.", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onPictureTaken(byte[] data, Camera camera) {

        if (data == null || data.length <= 0) {
            safeToTakePicture = true;
            return;
        }

        Log.d("CameraSurfaceView", "CameraSurfaceView onPictureTaken data.length : " + data.length);

        // 保存图片
        final byte[] b = data;
        new Thread(new Runnable() {
            @Override
            public void run() {
                handleAndSaveBitmap(b);
            }
        }).start();

        safeToTakePicture = true;
    }

    /**
     * 处理拍照图片并保存
     *
     * @param data
     */
    private synchronized void handleAndSaveBitmap(byte[] data) {
        // 保存图片
        //### Bitmap b = BitmapFactory.decodeByteArray(data, 0, data.length);
        Bitmap b = Utils.Bytes2Bitmap(data,720);

        if(cameraId == Camera.CameraInfo.CAMERA_FACING_BACK){
            //后置摄像头
            b = Utils.rotate(b, 90);//摆正位置

        } else if(cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT){
            //前置摄像头
            b = Utils.rotate(b, 270);//摆正位置
            b = overturnBmp(b); // 镜面翻转

        }

        BitmapUtils.saveImageFile(mAct, imgOutputUri, b);

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container,
                        new PreviewFragment(imgOutputUri, PreviewFragment.FILE_TYPE_PHOTO, CameraUtils.returnCameraType(cameraId)),
                        PreviewFragment.TAG)
                .addToBackStack(null)
                .commit();

    }

    public Bitmap overturnBmp(Bitmap bmp) {
        int w = bmp.getWidth();
        int h = bmp.getHeight();

        Matrix matrix = new Matrix();
        matrix.postScale(-1, 1); // 镜像水平翻转
        return Bitmap.createBitmap(bmp, 0, 0, w, h, matrix, true);

    }




    /**
     * 查找前置摄像头
     * @return
     */
    private int FindFrontCamera() {
        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras(); // get cameras number

        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo); // get camerainfo
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                return camIdx;
            }
        }
        return -1;
    }

    /**
     * 查找后置摄像头
     * @return
     */
    private int FindBackCamera() {
        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras(); // get cameras number

        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo); // get camerainfo
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                // 代表摄像头的方位，目前有定义值两个分别为CAMERA_FACING_FRONT前置和CAMERA_FACING_BACK后置
                return camIdx;
            }
        }
        return -1;
    }
}

package com.dilusense.custom_camera;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.badoo.mobile.util.WeakHandler;
import com.dilusense.custom_camera.utils.BitmapUtils;
import com.dilusense.custom_camera.utils.CameraUtils;
import com.dilusense.custom_camera.utils.Utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * 遇到的问题：显示相机SurfaceView尺寸，相机预览尺寸 和 相机保存图片尺寸 三者不一致
 */
public class CustomCameraActivity extends Activity {


    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    ///为了使照片竖直显示
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private String mCameraID;//摄像头Id 0 为后  1 为前
    private ImageReader mImageReader;
    private CameraCaptureSession mCameraCaptureSession;
    private CameraDevice mCameraDevice;
    private CameraManager mCameraManager;
    private Handler childHandler, mainHandler;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;

    private CaptureRequest.Builder previewRequestBuilder;

    public static final int RC_IS_CHOOSE_PICTURE = 2001;

    private boolean safeToTakePicture = true;


    private WeakHandler handler;

    Uri imgOutputUri;


    @BindView(R2.id.id_iv_flash_switch)
    ImageView id_iv_flash_switch;
    @BindView(R2.id.iv_camera_face_frame)
    ImageView iv_camera_face_frame;
    @BindView(R2.id.id_iv_shutter)
    ImageView iv_shutter;

    float x = (float) 0.5;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Hide the window title.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//【旋转问题】首先强制竖屏，手机横过来时候 控件不变

        setContentView(R.layout.activity_custom_camera);

        ButterKnife.bind(this);

        initBigImageShowResource();

        imgOutputUri = getIntent().getExtras().getParcelable(MediaStore.EXTRA_OUTPUT);


        if (!Utils.checkCameraHardware(this)) {
            Toast.makeText(CustomCameraActivity.this, "设备没有摄像头", Toast.LENGTH_SHORT).show();
            return;
        }

        handler = new WeakHandler();

        // centerWindowView = findViewById(R2.id.center_window_view);
        Log.d("CameraSurfaceView", "CameraSurfaceView onCreate currentThread : " + Thread.currentThread());

        mCameraID = String.valueOf(CameraUtils.getCameraId(this,CameraUtils.FRONT));
        init();

    }


    @OnClick(R2.id.id_iv_shutter)
    public void id_iv_shutterOnClick() {
        takePicture();
    }

    @OnClick(R2.id.id_iv_flash_switch)
    public void id_iv_flash_switchOnClick() {
        toggleFlash();
    }

    @OnClick(R2.id.id_iv_change)
    public void id_iv_changeOnClick() {
        changeCameraTwo();
    }

    @OnClick(R2.id.iv_back)
    public void iv_backOnClick() {
        back();
    }

    @Override
    protected void onResume() {
        super.onResume();

//        startCamera(mCameraID);
//        initCameraParameters();
    }


    private void initBigImageShowResource() {
        Context ctx = getApplication();

        iv_camera_face_frame.setImageBitmap(BitmapUtils.readBitMap(getApplicationContext(), R.drawable.camera_face_frame));

        // button
        ImgClickBgSwitchUtils.onClick(ctx, iv_shutter, R.drawable.camera_capture_normal, R.drawable.camera_capture_pressed);
    }

    private void toggleFlash() {
        //TODO
        try {
            boolean x = CameraUtils.isHardwareLevelSupported(mCameraManager.getCameraCharacteristics(mCameraID),CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }


    }

    void changeCameraTwo() {

        if (mCameraDevice == null){
             return;
        }

        switch (mCameraID){
            case "" + CameraCharacteristics.LENS_FACING_BACK:
                mCameraID = "" + CameraCharacteristics.LENS_FACING_FRONT;
                break;
            case "" + CameraCharacteristics.LENS_FACING_FRONT:
                mCameraID = "" + CameraCharacteristics.LENS_FACING_BACK;
                break;
        }

        releaseCamera();
        initCamera2(mCameraID);
    }

    private void startCamera(String cameraId) {
        //TODO

    }

    private void initCameraParameters() {
        //TODO
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseCamera();
    }


    private void init() {
        mSurfaceView = (SurfaceView) findViewById(R.id.camera_preview);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.setKeepScreenOn(true);
        // mSurfaceView添加回调
        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) { //SurfaceView创建
                // 初始化Camera
                initCamera2(mCameraID);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) { //SurfaceView销毁
                // 释放Camera资源
                if (null != mCameraDevice) {
                    mCameraDevice.close();
                    CustomCameraActivity.this.mCameraDevice = null;
                }
            }
        });
    }

    /**
     * 拍照
     */
    private void takePicture() {
        if (mCameraDevice == null) return;
        // 创建拍照需要的CaptureRequest.Builder
        final CaptureRequest.Builder captureRequestBuilder;
        try {
            captureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            // 将imageReader的surface作为CaptureRequest.Builder的目标
            captureRequestBuilder.addTarget(mImageReader.getSurface());
            // 自动对焦
            captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            // 自动曝光
            captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            // 获取手机方向
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            // 根据设备方向计算设置照片的方向
            captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
            //拍照
            CaptureRequest mCaptureRequest = captureRequestBuilder.build();

            mCameraCaptureSession.stopRepeating();
            mCameraCaptureSession.capture(mCaptureRequest, new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                }
            }, childHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_IS_CHOOSE_PICTURE && resultCode == RESULT_OK) {
            setResult(RESULT_OK, data);
            finish();
            Log.d("", "onActivityResult ok cameraActivity : ");
        }

        if (requestCode == RC_IS_CHOOSE_PICTURE && resultCode == RESULT_CANCELED) {
            Log.d("", "onActivityResult canceled  cameraActivity : ");
        }
    }

    public void onPictureTaken(byte[] data, CameraDevice camera) {

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
     * @param data
     */
    private synchronized void handleAndSaveBitmap(byte[] data) {
        // 保存图片
        //### Bitmap b = BitmapFactory.decodeByteArray(data, 0, data.length);
        Bitmap b = Utils.Bytes2Bitmap(data, 720);

        if (mCameraID.equals(String.valueOf(CameraCharacteristics.LENS_FACING_FRONT))) {
            //后置摄像头
            b = Utils.rotate(b, 90);//摆正位置

        } else if (mCameraID.equals(String.valueOf(CameraCharacteristics.LENS_FACING_BACK))) {
            //前置摄像头
            b = Utils.rotate(b, 270);//摆正位置
            b = overturnBmp(b); // 镜面翻转

        }

        String filePath = BitmapUtils.saveImageFile(this, imgOutputUri, b);

        handler.post(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(CustomCameraActivity.this, PreviewActivity.class);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imgOutputUri);
                CustomCameraActivity.this.startActivityForResult(intent, RC_IS_CHOOSE_PICTURE);
            }
        });
    }

    public Bitmap overturnBmp(Bitmap bmp) {
        int w = bmp.getWidth();
        int h = bmp.getHeight();

        Matrix matrix = new Matrix();
        matrix.postScale(-1, 1); // 镜像水平翻转
        return Bitmap.createBitmap(bmp, 0, 0, w, h, matrix, true);

    }


    /**
     * 初始化Camera2
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initCamera2(String cameraId) {
        HandlerThread handlerThread = new HandlerThread("Camera2");
        handlerThread.start();
        childHandler = new Handler(handlerThread.getLooper());
        mainHandler = new Handler(getMainLooper());
        mCameraID = cameraId;
        mImageReader = ImageReader.newInstance(1080, 1920, ImageFormat.JPEG, 1);
        mImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() { //可以在这里处理拍照得到的临时照片 例如，写入本地
            @Override
            public void onImageAvailable(ImageReader reader) {
                mCameraDevice.close();
                // 拿到拍照照片数据
                Image image = reader.acquireNextImage();
                ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                byte[] bytes = new byte[buffer.remaining()];
                buffer.get(bytes);
                onPictureTaken(bytes,mCameraDevice);
            }
        }, mainHandler);
        //获取摄像头管理
        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            //打开摄像头
            mCameraManager.openCamera(mCameraID, stateCallback, mainHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始预览
     */
    private void takePreview() {
        try {

            // 创建预览需要的CaptureRequest.Builder
            previewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            // 将SurfaceView的surface作为CaptureRequest.Builder的目标
            previewRequestBuilder.addTarget(mSurfaceHolder.getSurface());
            // 创建CameraCaptureSession，该对象负责管理处理预览请求和拍照请求
            mCameraDevice.createCaptureSession(Arrays.asList(mSurfaceHolder.getSurface(), mImageReader.getSurface()), new CameraCaptureSession.StateCallback() // ③
            {
                @Override
                public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                    if (null == mCameraDevice) return;
                    // 当摄像头已经准备好时，开始显示预览
                    mCameraCaptureSession = cameraCaptureSession;
                    try {
                        // 自动对焦
                        previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CameraMetadata.CONTROL_AF_MODE_CONTINUOUS_VIDEO);
                        // 打开闪光灯
                        previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);

                        // 显示预览
                        CaptureRequest previewRequest = previewRequestBuilder.build();
                        mCameraCaptureSession.setRepeatingRequest(previewRequest, null, childHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(CustomCameraActivity.this, "配置失败", Toast.LENGTH_SHORT).show();
                }
            }, childHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 摄像头创建监听
     */
    private CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {//打开摄像头
            mCameraDevice = camera;
            //开启预览
            takePreview();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {//关闭摄像头
            if (null != mCameraDevice) {
                mCameraDevice.close();
                CustomCameraActivity.this.mCameraDevice = null;
            }
        }

        @Override
        public void onError(CameraDevice camera, int error) {//发生错误
            Toast.makeText(CustomCameraActivity.this, "摄像头开启失败", Toast.LENGTH_SHORT).show();
        }
    };


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        back();
    }

    private void back() {
        setResult(RESULT_CANCELED);
        finish();
    }

    private void releaseCamera() {
        if(mCameraDevice != null){
            mCameraDevice.close();
        }
    }
}



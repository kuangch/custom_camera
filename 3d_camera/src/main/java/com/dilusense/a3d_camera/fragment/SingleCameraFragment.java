package com.dilusense.a3d_camera.fragment;

import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.badoo.mobile.util.WeakHandler;
import com.dilusense.a3d_camera.R;
import com.dilusense.a3d_camera.R2;
import com.dilusense.a3d_camera.camera.CameraPreview;
import com.dilusense.a3d_camera.camera.CameraUtils;
import com.dilusense.a3d_camera.camera.ImageUtils;
import com.dilusense.a3d_camera.camera.PreviewFragment;
import com.dilusense.custom_camera.utils.BitmapUtils;
import com.dilusense.custom_camera.utils.CaptureImgUtils;
import com.dilusense.custom_camera.utils.Utils;

import butterknife.BindView;

/**
 * Created by Thinkpad on 2017/8/24.
 */

public class SingleCameraFragment extends BaseFragment implements Camera.PictureCallback {

    private boolean safeToTakePicture = true;
    private Camera mCamera;
    private int cameraId = 0;
    private int cameraId_other = 1;

    private WeakHandler handler;

    Uri imgOutputUri;

    ImageView iv_shutter;
    ImageView iv_change;

    @BindView(R2.id.camera_preview)
    CameraPreview mPreview;

    @BindView(R2.id.camera_preview_other)
    CameraPreview mPreview_other;

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.layout_single_camera,container,false);
    }

    @Override
    public void initEvent() {
//        initBigImageShowResource();
        iv_shutter = mAct.iv_shutter;
        iv_change = mAct.iv_change;

        iv_shutter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture(null, null, SingleCameraFragment.this);
            }
        });

        iv_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeCamera();
            }
        });

        mPreview.setOnGetCameraListener(new CameraPreview.IOnCameraGetListener() {
            @Override
            public void onGet(Camera camera) {
                mCamera = camera;
            }
        });
    }

    @Override
    public void lazyLoad() {

        if (!Utils.checkCameraHardware(mAct)) {
            Toast.makeText(mAct.getApplicationContext(), "设备没有摄像头", Toast.LENGTH_SHORT).show();
            return;
        }

        imgOutputUri = CaptureImgUtils.getCaptureImgFileUri(mAct.getApplicationContext());
        handler = new WeakHandler();

        cameraId = CameraUtils.getCameraId(mAct.getApplication(),CameraUtils.FRONT);
        cameraId_other = CameraUtils.getCameraId(mAct.getApplication(),CameraUtils.BACK);

        Log.i(TAG, "init camera: " + cameraId);
        mPreview.init(mAct.getApplicationContext(),cameraId);
        Log.i(TAG, "init camera: " + cameraId_other);
        mPreview_other.init(mAct.getApplicationContext(),cameraId_other,true);

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

    void changeCamera() {
        //切换前后摄像头
        if(mPreview != null){
            if(mPreview_other != null){
                mPreview.stopCamera();
                mPreview_other.changeCamera();
            }
            mPreview.changeCamera();
        }

    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {

        if (data == null || data.length <= 0) {
            safeToTakePicture = true;
            return;
        }

        Log.d("SingleCameraFragment", "CameraSurfaceView onPictureTaken data.length : " + data.length);

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

        if(cameraId == CameraUtils.BACK){
            //后置摄像头
            b = Utils.rotate(b, 90);//摆正位置

        } else if(cameraId == CameraUtils.FRONT){
            //前置摄像头
            b = Utils.rotate(b, 270);//摆正位置
            b = ImageUtils.overturnBmp(b); // 镜面翻转

        }

        String filePath = BitmapUtils.saveImageFile(mAct, imgOutputUri, b);

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container,
                        new PreviewFragment(filePath, PreviewFragment.FILE_TYPE_PHOTO,CameraUtils.returnCameraType(cameraId)),
                        PreviewFragment.TAG)
                .addToBackStack(null)
                .commit();
    }


}

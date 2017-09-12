package com.dilusense.a3d_camera.camera;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;

import com.dilusense.a3d_camera.BuildConfig;
import com.dilusense.a3d_camera.enums.HandleSpeed;

import java.io.IOException;
import java.util.List;

public class MyCameraSurfaceView extends BaseSurface{

	protected static final String TAG = "MyCameraSurfaceView";
	public static int CAMERA_HEIGHT = 720;
	public static int CAMERA_WIDTH = 1280;
	private final int HANDLE_FRAME_INTERVAL = HandleSpeed.MEDIUM.getValue();
	private static int CAMERA_TYPE = 0;
	private int frameCount = 0;
	private long timeFlag = System.currentTimeMillis();
	private boolean isFrontCamera = false;
	private boolean logToggle = BuildConfig.DEBUG;
	private Camera mCamera;
	private int cameraId;

	public List<Camera.Size> mSupportedPreviewSizes;
	public List<Camera.Size> mSupportedPictureSizes;

	public MyCameraSurfaceView(Context paramContext) {
		super(paramContext);

	}

	public MyCameraSurfaceView(Context paramContext,
			AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
	}

	public MyCameraSurfaceView(Context paramContext,
                               AttributeSet paramAttributeSet, int paramInt) {
		super(paramContext, paramAttributeSet);
	}


	public void surfaceChanged(SurfaceHolder paramSurfaceHolder, int paramInt1,
                               int paramInt2, int paramInt3) {

	}

	public void surfaceCreated(SurfaceHolder paramSurfaceHolder) {

		if (onSurfaceCreatedListener != null){
			onSurfaceCreatedListener.onCreated(paramSurfaceHolder);
		}

		if(cameraId == -1){
			return;
		}
		startPlay();
	}

	public void surfaceDestroyed(SurfaceHolder paramSurfaceHolder) {

		isFrontCamera = false;
		stopPlay();
	}


	public void setCameraId(int cameraId){
		this.cameraId = cameraId;
	}

	public void preparePlay(int cameraId){
		try {
            mCamera = null;
            try {
				Log.i(TAG, "open camera: " + cameraId);
                mCamera = Camera.open(cameraId);//打开相机；在低版本里，只有open（）方法；高级版本加入此方法的意义是具有打开多个
                //摄像机的能力，其中输入参数为摄像机的编号
                //在manifest中设定的最小版本会影响这里方法的调用，如果最小版本设定有误（版本过低），在ide里将不允许调用有参的
                //open方法;
                //如果模拟器版本较高的话，无参的open方法将会获得null值!所以尽量使用通用版本的模拟器和API；

				if (onCameraCreatedListener != null)
					onCameraCreatedListener.onCreated(mCamera);
				mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
				mSupportedPictureSizes = mCamera.getParameters().getSupportedPictureSizes();
				for (Camera.Size size : mSupportedPreviewSizes) {
					Log.d(TAG, "Preview for mPreviewSize w - h : " + size.width + " - " + size.height);
				}
				for (Camera.Size size : mSupportedPictureSizes) {
					Log.d(TAG, "Preview for mPictureSize w - h : " + size.width + " - " + size.height);
				}
				mCamera.setDisplayOrientation(90);
			} catch (Exception e) {
                Log.e("============", "摄像头被占用");
                
            }
            if (mCamera == null) {
                Log.e("============", "摄像机为空");
				return;
//                System.exit(0);
            }
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewFormat(ImageFormat.NV21);
            /*这是唯一值，也可以不设置。有些同学可能设置成 PixelFormat 下面的一个值，其实是不对的，具体的可以看官方文档*/
            setPreViewSize(parameters);
            mCamera.setParameters(parameters);
			mCamera.setPreviewDisplay(holder);//设置显示面板控制器

			// 如果不进行decode就不设置每帧回调
			if (isPlay) {
				priviewCallBack pre = new priviewCallBack();//建立预览回调对象
				mCamera.setPreviewCallback(pre); //设置预览回调对象
			}
            //mCamera.getParameters().setPreviewFormat(ImageFormat.JPEG);

		} catch (IOException exception) {
            mCamera.release();
            mCamera = null;
        }
	}

	private void setPreViewSize(Parameters parameters) {
		// TODO Auto-generated method stub
//		Size previewSize = parameters.getPreviewSize();
		Size previewSize = parameters.getPreviewSize();
		CAMERA_WIDTH = previewSize.width;
        CAMERA_HEIGHT = previewSize.height;

		if(CAMERA_WIDTH <= 640){
			List<Size> supportedSizes = parameters.getSupportedPreviewSizes();
	        for(Size size : supportedSizes){
	        	Log.i(TAG, "supportedSizes: w:" + size.width + " h:"+size.height);
	        	if(size.width - CAMERA_WIDTH > 50 && size.width - CAMERA_WIDTH < 300);
	        	CAMERA_WIDTH = previewSize.width;
	            CAMERA_HEIGHT = previewSize.height;
	        }
		}
        if(logToggle)
        	Log.i(TAG, "setPreviewSize: w:" + CAMERA_WIDTH + " h:" + CAMERA_HEIGHT);
        parameters.setPreviewSize(CAMERA_WIDTH, CAMERA_HEIGHT);
	}

	/**
	 * 时间如水2013-7-10
	 *  功能：开始实时预览*/
	public void startPlay() {

		Log.i(TAG, "start play");
		preparePlay(cameraId);
		if(mCamera != null)
			mCamera.startPreview();// 开始预览，这步操作很重要
		playFlag = true;
	}


	int flag = 1;

	// 每次cam采集到新图像时调用的回调方法，前提是必须开启预览
    class priviewCallBack implements Camera.PreviewCallback {


		@Override
        public void onPreviewFrame(byte[] data, Camera camera) {

			// 计算帧率
			frameCount ++ ;
			if(System.currentTimeMillis() - timeFlag > 1000){
				if(logToggle)
					Log.i(TAG, "face------------------frameRate:" + frameCount);
				frameCount = 0;
				timeFlag = System.currentTimeMillis();
			}

			if(onCaptureListener == null){
				return;
			}

        	if(flag > HANDLE_FRAME_INTERVAL){
        		if(logToggle)
//        			LogSaveLocal.i(TAG, "face------------------HANDLE_FRAME_INTERVAL:" + HANDLE_FRAME_INTERVAL);
        		flag = 1;
        	}

        	if(flag % HANDLE_FRAME_INTERVAL== 0){

        		if(logToggle)
        			Log.i(TAG, "onPreviewFrame call!");

        		onCaptureListener.onCapture(data, CAMERA_WIDTH, CAMERA_HEIGHT);
        	}
        	flag ++ ;
        }
    }


	@Override
	public void stopPlay() {
		// TODO Auto-generated method stub

		Log.i(TAG, "stop play");
		playFlag = false;
		if (mCamera != null) {
            try {
                /* 停止预览 */
                mCamera.stopPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }

            //调用release()的前必须设置preview回调为空不然报错
            mCamera.setPreviewCallback(null);
	        mCamera.release();
	        mCamera = null;
        }
	}

	public boolean isFrontCamera(){
		return isFrontCamera;
	}


	/**
	 * 切换摄像头
	 * @return
	 */
	public boolean changeCamera(){

		int cameraCount = 0;
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		Camera.CameraInfo currCameraInfo = new Camera.CameraInfo();
		cameraCount = Camera.getNumberOfCameras(); // get cameras number
		if(cameraCount == 1){
			return false;
		}
		for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
			Camera.getCameraInfo(camIdx, cameraInfo); // get camerainfo
			Camera.getCameraInfo(cameraId, currCameraInfo); // get camerainfo
			if(currCameraInfo.facing != cameraInfo.facing){
				cameraId = camIdx;
				if(cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT){
					isFrontCamera = true;
				}else{
					isFrontCamera = false;
				}
				break;
			}
		}
		
		stopPlay();
		startPlay();
		
		return true;
	}
	
	public int getCameraNum(){
		return Camera.getNumberOfCameras();
	}

}

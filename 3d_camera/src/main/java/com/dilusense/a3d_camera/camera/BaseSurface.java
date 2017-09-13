package com.dilusense.a3d_camera.camera;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import java.util.List;

public abstract class BaseSurface extends SurfaceView implements Callback {

	public static final int PLAY = 0;
	public static final int DECODE = 1;
	protected boolean isPlay = true;
	protected IOnCaptureListener onCaptureListener;
	protected IOnSurfaceCreatedListener onSurfaceCreatedListener;
	protected IOnSurfaceChangeListener onSurfaceChangeListener;
	protected IOnCameraCreatedListener onCameraCreatedListener;
	protected SurfaceHolder holder;
	public boolean playFlag = false;   //播放标志
	public Context ctx;
	
	public BaseSurface(Context paramContext) {
		super(paramContext);
		initSurfaceView(paramContext);

	}

	public BaseSurface(Context paramContext,
			AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		initSurfaceView(paramContext);
	}

	public BaseSurface(Context paramContext,
                       AttributeSet paramAttributeSet, int paramInt) {
		super(paramContext, paramAttributeSet);
		initSurfaceView(paramContext);
	}
	
	protected void initSurfaceView(Context paramContext) {

		this.ctx = paramContext;
		holder = this.getHolder();
		holder.addCallback(this);

	}
	public abstract void startPlay();
	public abstract void stopPlay();
	
	/**
	 * 回调接口
	 */
	public interface IOnCaptureListener{
		void onCapture(byte[] byteBuf, int width, int height);
	}
	
	public void setOnCaptureListener(IOnCaptureListener onCaptureListener){
		this.onCaptureListener = onCaptureListener;
	}

	// surface created
    public interface IOnSurfaceCreatedListener{
        void onCreated(SurfaceHolder paramSurfaceHolder);
    }

    public void setOnSurfaceCreatedListener(IOnSurfaceCreatedListener onCaptureListener){
        this.onSurfaceCreatedListener = onCaptureListener;
    }

	// surface change
	public interface IOnSurfaceChangeListener{
		void onChange(Camera camera);
	}

	public void setOnSurfaceChangeListener(IOnSurfaceChangeListener onCaptureListener){
		this.onSurfaceChangeListener = onCaptureListener;
	}

    // camera created
    public interface IOnCameraCreatedListener{
        void onCreated(Camera camera, List<Camera.Size> mSupportPreview, List<Camera.Size> mPicturePreview);
    }

    public void setOnCameraCreatedListener(IOnCameraCreatedListener onCaptureListener){
        this.onCameraCreatedListener = onCaptureListener;
    }
	
	
	//设置网络数据的使用方式
	public void setDataUsage(int usage){
		this.isPlay = (usage == PLAY);
	}

}

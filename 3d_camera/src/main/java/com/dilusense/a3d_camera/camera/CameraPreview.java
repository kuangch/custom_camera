package com.dilusense.a3d_camera.camera;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * A simple wrapper around a Camera and a SurfaceView that renders a centered preview of the Camera
 * to the surface. We need to center the SurfaceView because not all devices have cameras that
 * support preview sizes at the same aspect ratio as the device's display.
 * <p/>
 */
public class CameraPreview extends ViewGroup{
    private final String TAG = "Preview";
    private IOnCameraGetListener onGetCameraListener;

    /**
     * 图片的偏移
     */
    public int moveX = 0;
    public int moveY = 0;

    public MyCameraSurfaceView mSurfaceView;
    private SurfaceHolder mHolder;
    public Camera.Size mPreviewSize;
    private List<Camera.Size> mSupportedPreviewSizes;

    public Camera.Size mPictureSize;
    private List<Camera.Size> mSupportedPictureSizes;

    CameraPreview(Context context) {
        super(context);
//        init(context);
    }

    public CameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
//        init(context);
    }

    public CameraPreview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
//        init(context);
    }

    public void init(Context context, final int cameraId){
        init(context,cameraId,false);
    }

    public void init(Context context, final int cameraId, boolean isTop) {

        final View previewView = this;

        mSurfaceView = new MyCameraSurfaceView(context);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(mSurfaceView, layoutParams);

        if (isTop){
            mSurfaceView.setZOrderOnTop(true);
        }

        mHolder = mSurfaceView.getHolder();

        mSurfaceView.setOnSurfaceCreatedListener(new BaseSurface.IOnSurfaceCreatedListener() {
            @Override
            public void onCreated(SurfaceHolder paramSurfaceHolder) {
                   mSurfaceView.setCameraId(cameraId);
            }
        });

        mSurfaceView.setOnCameraCreatedListener(new BaseSurface.IOnCameraCreatedListener() {
            @Override
            public void onCreated(Camera camera, List<Camera.Size> mSupportPreview, List<Camera.Size> mPicturePreview) {
                mSupportedPreviewSizes = mSupportPreview;
                mSupportedPictureSizes = mPicturePreview;

                previewView.requestLayout();

                if(onGetCameraListener != null)
                    onGetCameraListener.onGet(camera);
            }

        });

        mSurfaceView.setOnSurfaceChangeListener(new BaseSurface.IOnSurfaceChangeListener() {
            @Override
            public void onChange(Camera camera) {
                // set surfaceview preview size and size of picture
                if (mPreviewSize != null && mPictureSize != null) {
                    Camera.Parameters parameters = camera.getParameters();
                    parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
                    parameters.setPictureSize(mPictureSize.width, mPictureSize.height);
                    camera.setParameters(parameters);
                }
            }
        });

        this.requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // We purposely disregard child measurements because act as a
        // wrapper to a SurfaceView that centers the camera preview instead
        // of stretching it.
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);
        Log.d(TAG, "Preview w - h : " + width + " - " + height);
        if (mSupportedPreviewSizes != null) {
            // 需要宽高切换 因为相机有90度的角度
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, height, width);
//            Log.d(TAG, "Preview mPreviewSize w - h : " + mPreviewSize.width + " - " + mPreviewSize.height);
        }
        if (mSupportedPictureSizes != null) {
            mPictureSize = getOptimalPreviewSize(mSupportedPictureSizes, height, width);
//            Log.d(TAG, "Preview mPictureSize w - h : " + mPictureSize.width + " - " + mPictureSize.height);
        }

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed && getChildCount() > 0) {
            final View child = getChildAt(0);

            final int width = r - l;
            final int height = b - t;

            int previewWidth = width;
            int previewHeight = height;
            if (mPreviewSize != null) {
                previewWidth = mPreviewSize.height;
                previewHeight = mPreviewSize.width;
            }

            // Center the child SurfaceView within the parent.
            if (width * previewHeight > height * previewWidth) {

                final int scaleWidth = width;
                final int scaleHeight = width * previewHeight / previewWidth;
                moveX = 0;
                moveY = (scaleHeight - height) / 2;
                if (moveY < 0) {
                    moveY = 0;
                }
                child.layout(-moveX, -moveY, scaleWidth, scaleHeight);
            } else {

                final int scaleHeight = height;
                final int scaleWidth = height * previewWidth / previewHeight;
                moveX = (scaleWidth - width) / 2;
                moveY = 0;
                if (moveX < 0) {
                    moveX = 0;
                }
                child.layout(-moveX, -moveY, scaleWidth, scaleHeight);
            }

        }
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    public void changeCamera(){
        if (mSurfaceView != null){
            mSurfaceView.changeCamera();
        }
    }

    // camera created
    public interface IOnCameraGetListener{
        void onGet(Camera camera);
    }

    public void setOnGetCameraListener(IOnCameraGetListener listener){
        this.onGetCameraListener = listener;
    }
}

package com.dilusense.custom_camera;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.dilusense.custom_camera.utils.BitmapUtils;
import com.dilusense.custom_camera.utils.StringUtils;

/**
 * 视频播放
 */
public class PreviewFragment extends BaseFragment implements View.OnClickListener {
    public static final String TAG = PreviewFragment.class.getSimpleName();
    public static final int FILE_TYPE_VIDEO=0;
    public static final int FILE_TYPE_PHOTO = 1;
    private VideoView videoView;

    Bitmap b;
    private Uri filePath;
    private int fileType;
    private int direction;
    private ImageView photoPlay,videoUse,videoCancel;

    @SuppressLint("ValidFragment")
    public PreviewFragment(Uri filePath, int type) {
        this.filePath = filePath;
        this.fileType = type;
    }
    @SuppressLint("ValidFragment")
    public PreviewFragment(Uri filePath, int type, int direction) {
        this.filePath = filePath;
        this.fileType = type;
        this.direction = direction;
    }

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_preview, container, false);
    }

    @Override
    public void initEvent() {
        initView(mContentView);
    }

    @Override
    protected void lazyLoad() {

    }

    private void initView(View view) {
        videoView = (VideoView)view.findViewById(R.id.video_play);
        photoPlay = (ImageView)view.findViewById(R.id.photo_play);
        videoCancel = (ImageView)view.findViewById(R.id.video_cancel);
        videoUse = (ImageView)view.findViewById(R.id.video_use);
        videoCancel.setOnClickListener(this);
        videoUse.setOnClickListener(this);
        if(fileType==FILE_TYPE_VIDEO){
            videoView.setVisibility(View.VISIBLE);
            photoPlay.setVisibility(View.GONE);
            videoView.setVideoURI(filePath);
            videoView.start();
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    mp.setLooping(true);
                }
            });

            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    videoView.setVideoPath(filePath.getPath());
                    videoView.start();
                }
            });
        }else if(fileType==FILE_TYPE_PHOTO){
            videoView.setVisibility(View.GONE);
            photoPlay.setVisibility(View.VISIBLE);
            try {
                Glide.with(this).load(filePath).asBitmap().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(new SimpleTarget<Bitmap>() {

                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {

                        photoPlay.setImageBitmap(resource);
                        b = resource;

                    }
                });
            } catch (OutOfMemoryError ex) {
            }
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try{
            if (b != null && !b.isRecycled()) {
                b.recycle();
                b = null;
            }
        }catch (Exception e){e.printStackTrace();}
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.video_use) {
            useVideo();
        } else if (i == R.id.video_cancel) {
            onCancel();
        }
    }

    /**
     * 取消
     */
    public void onCancel(){
        mAct.cancel();
    }

    /**
     * 使用
     */
    public void useVideo(){
        //防止点击过快
        if (mAct != null && !mAct.isFinishing()) {
            if(fileType==FILE_TYPE_VIDEO)
                mAct.returnVideoPath(filePath.getPath());
            else{
                if (b != null) {
                    int saveW = b.getWidth();
                    int saveH = Math.min(b.getHeight(), (int) (saveW * StringUtils.getWHRatio(CustomCameraConstants.CAPTURE_IMG_WH_RATIO, "/").getHeight() / StringUtils.getWHRatio(CustomCameraConstants.CAPTURE_IMG_WH_RATIO, "/").getWidth()));
                    Bitmap afterCropBitmap = Bitmap.createBitmap(b, 0, 0, saveW, saveH);

                    BitmapUtils.saveImageFile(mAct, filePath, afterCropBitmap);

                }

                mAct.returnPhotoPath(filePath.getPath());
            }


        }
    }
}



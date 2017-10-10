package com.dilusense.custom_camera;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Window;

public class CustomCameraActivity extends FragmentActivity {

    public FragmentManager fm;
    public Uri imgOutputUri;
    private CustomCameraFragment customCameraFragment;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Hide the window title.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//【旋转问题】首先强制竖屏，手机横过来时候 控件不变

        setContentView(R.layout.activity_custom_camera);

        imgOutputUri = getIntent().getExtras().getParcelable(MediaStore.EXTRA_OUTPUT);

        fm = getSupportFragmentManager();
        customCameraFragment = new CustomCameraFragment();
        fm.beginTransaction()
                .replace(R.id.fragment_container, customCameraFragment)
                .commit();

    }

    public void popBackStack() {
        fm.popBackStack();
    }

    /**
     * 返回视频路径
     * @param videoPath
     */
    public void returnVideoPath(String videoPath) {

    }

    /**
     * 返回图片路径
     * @param photoPath
     */
    public void returnPhotoPath(String photoPath) {
        ok();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
              back();
    }

    public void ok(){
        setResult(RESULT_OK);
        finish();
    }

    public void cancel(){
        setResult(RESULT_CANCELED);
        fm.beginTransaction()
                .replace(R.id.fragment_container, customCameraFragment)
                .commit();
    }

    public void back(){
        setResult(RESULT_CANCELED);
        finish();
    }
}



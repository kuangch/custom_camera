package com.dilusense.a3d_camera;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import com.dilusense.a3d_camera.databinding.ActCustom3dCameraBinding;
import com.dilusense.a3d_camera.databindings.model.OptionsControl;
import com.dilusense.a3d_camera.enums.Pages;
import com.dilusense.a3d_camera.fragment.DistanceMeasureFragment;
import com.dilusense.a3d_camera.fragment.DoubleCameraFragment;
import com.dilusense.a3d_camera.fragment.SingleCameraFragment;
import com.dilusense.a3d_camera.view.RecordStartView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Thinkpad on 2017/8/24.
 */

public class Custom3DCamera extends AppCompatActivity {

    public Pages currPage = Pages.SCANNER;

    public static final int TAKE_VIDEO_CODE = 1000;
    public static final int TAKE_PHOTO_CODE = 1001;

    public static final String TAKE_VIDEO_PATH = "TAKE_VIDEO_PATH";
    public static final String TAKE_PHOTO_PATH = "TAKE_PHOTO_PATH";

    public List<Fragment> pages = new ArrayList<Fragment>();

    @BindView(R2.id.rsv_record)
    public RecordStartView rsv_record;

    @BindView(R2.id.id_iv_shutter)
    public ImageView iv_shutter;

    @BindView(R2.id.id_iv_change)
    public ImageView iv_change;

    @BindView(R2.id.btn_start_measure)
    public Button btn_start_measure;


    private ActCustom3dCameraBinding d;
    public OptionsControl optionsControl;
    public FragmentManager fm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        d = DataBindingUtil.setContentView(this, R.layout.act_custom_3d_camera);

        ButterKnife.bind(this);

        fm = getSupportFragmentManager();

        optionsControl = new OptionsControl(false,true,false);
        d.setOC(optionsControl);

        fm.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Log.i("test", "onBackStackChanged");
            }
        });

        pages.add(new SingleCameraFragment());
        pages.add(new DoubleCameraFragment());
        pages.add(new DistanceMeasureFragment());

        fm.beginTransaction()
                .replace(R.id.fragment_container, pages.get(currPage.getValue()))
                .commit();
    }

    @OnClick(R2.id.camera)
    public void cameraOnClick(){
        changePage(Pages.CAMERA);
    }

    @OnClick(R2.id.scan)
    public void scanOnClick(){
        changePage(Pages.SCANNER);
    }

    @OnClick(R2.id.measure)
    public void measureOnClick(){
        changePage(Pages.MEASURE);
    }

    private void changePage(Pages newPage){
        if(currPage == newPage){
            return;
        }
        onControlCompatChange(newPage);

        fm.beginTransaction()
                .replace(R.id.fragment_container, pages.get(newPage.getValue()))
                .commit();

        currPage = newPage;
    }


    private void onControlCompatChange(Pages page){

        switch (page){
            case CAMERA:
                optionsControl.setShowCamera(true);
                break;
            case SCANNER:
                optionsControl.setShowScanner(true);
                break;
            case MEASURE:
                optionsControl.setShowMeasure(true);
                break;
        }

    }

    public void popBackStack() {
        fm.popBackStack();
    }

    /**
     * 返回视频路径
     * @param videoPath
     */
    public void returnVideoPath(String videoPath) {
        Intent data = new Intent();
        data.putExtra(TAKE_VIDEO_PATH,videoPath);
        if (getParent() == null) {
            setResult(TAKE_VIDEO_CODE, data);
        } else {
            getParent().setResult(TAKE_VIDEO_CODE, data);
        }
        finish();
    }

    /**
     * 返回图片路径
     * @param photoPath
     */
    public void returnPhotoPath(String photoPath) {
        Intent data = new Intent();
        data.putExtra(TAKE_PHOTO_PATH,photoPath);
        if (getParent() == null) {
            setResult(TAKE_PHOTO_CODE, data);
        } else {
            getParent().setResult(TAKE_PHOTO_CODE, data);
        }
        finish();
    }

}

package com.dilusense.a3d_camera.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dilusense.a3d_camera.R;
import com.dilusense.a3d_camera.R2;
import com.dilusense.a3d_camera.view.RecordStartView;

import butterknife.BindView;

/**
 * Created by Thinkpad on 2017/8/24.
 */

public class DoubleCameraFragment extends BaseFragment {

    RecordStartView rsv_record;

    @Override
    public View initView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_double_camera, container, false);

        return view;
    }

    @Override
    public void initEvent() {
        rsv_record.setOnRecordButtonListener(new RecordStartView.OnRecordButtonListener() {
            @Override
            public void onStartRecord() {

            }

            @Override
            public void onStopRecord() {

            }

            @Override
            public void onTakePhoto() {

            }
        });
    }

    @Override
    public void lazyLoad() {

    }
}

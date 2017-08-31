package com.dilusense.a3d_camera.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
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

public class DistanceMeasureFragment extends BaseFragment {

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_distance_measure, container, false);

        return view;
    }

    @Override
    public void initEvent() {

    }

    @Override
    protected void lazyLoad() {

    }

}

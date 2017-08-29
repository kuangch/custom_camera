package com.dilusense.a3d_camera.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Thinkpad on 2015/12/10.
 */
public abstract class BaseFragment extends Fragment {

    private static final String TAG = "BaseFragment" ;
    public FragmentActivity mAct;
    public View mContentView;

    private Unbinder unbinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = initView(inflater,container,savedInstanceState);
        unbinder = ButterKnife.bind(this, mContentView);
        return mContentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initEvent();
        initData();
        initLogic();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null){
            unbinder.unbind();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"destroy");
    }

    public void init(){
        this.mAct = getActivity();
    };
    public abstract View initView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState);
    public abstract void initEvent();
    public abstract void initData();
    public void initLogic(){};
}

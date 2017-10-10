package com.dilusense.custom_camera;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

    protected boolean isInit = false;
    protected boolean isLoad = false;

    public static String TAG = "BaseFragment" ;
    public CustomCameraActivity mAct;
    public View mContentView;

    private Unbinder unbinder;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        TAG = this.getClass().getName();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = initView(inflater,container,savedInstanceState);

        unbinder = ButterKnife.bind(this, mContentView);

        isInit = true;
        /*初始化的时候去加载数据**/
        isCanLoadData();

        return mContentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initEvent();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        isInit = false;
        isLoad = false;

        if (unbinder != null){
            unbinder.unbind();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, this.getClass().getSimpleName() + " destroy" );
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.i(TAG, this.getClass().getSimpleName() + (isVisibleToUser ? " show" : " hide"));
        isCanLoadData();
    }

    /**
     * 是否可以加载数据
     * 可以加载数据的条件：
     * 1.视图已经初始化
     * 2.视图对用户可见
     */
    private void isCanLoadData() {
        if (!isInit) {
            return;
        }

        if (getUserVisibleHint()) {
            lazyLoad();
            isLoad = true;
        } else {
            if (isLoad) {
                stopLoad();
            }
        }
    }

    private void init(){
        this.mAct = (CustomCameraActivity) getActivity();
    };
    public abstract View initView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState);
    public abstract void initEvent();

    /**
     * 当视图初始化并且对用户可见的时候去真正的加载数据
     */
    protected abstract void lazyLoad();

    /**
     * 当视图已经对用户不可见并且加载过数据，如果需要在切换到其他页面时停止加载数据，可以覆写此方法
     */
    protected void stopLoad() {}

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, this.getClass().getSimpleName() + " onResume");

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, this.getClass().getSimpleName() + " onPause");
    }

}

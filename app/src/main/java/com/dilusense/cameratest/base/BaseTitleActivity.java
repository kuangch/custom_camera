package com.dilusense.cameratest.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;


import com.dilusense.cameratest.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Thinkpad on 2017/3/8.
 */
public class BaseTitleActivity extends Activity {

    public Context ctx = null;

    @BindView(R.id.tv_title_txt)
    TextView tv_title_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        ButterKnife.bind(this);
        ctx = this;
        initBigImageShowResource();
        init();
    }

    public void init() {
    }
    public void initBigImageShowResource(){
    }

    public void setTitle(String title) {

        tv_title_txt.setText(title);
    }

    @OnClick(R.id.title_left)
    public void iv_title_leftOnClick() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
        } catch (Exception e) {
            Log.i("ButterKnife", "unbind failed");
        }

    }
}

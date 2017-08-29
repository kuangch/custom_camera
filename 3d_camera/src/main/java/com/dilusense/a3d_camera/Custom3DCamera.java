package com.dilusense.a3d_camera;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.dilusense.a3d_camera.adapter.MyPagerAdapter;
import com.hy.libary.PagerTabIndicator;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Thinkpad on 2017/8/24.
 */

public class Custom3DCamera extends AppCompatActivity {

    private DisplayMetrics dm; // 获取当前屏幕密度

    @BindView(R2.id.page_tab_indicator)
    PagerTabIndicator page_tab_indicator;

    @BindView(R2.id.pager)
    ViewPager pager;
    private MyPagerAdapter myPagerAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_custom_3d_camera);

        ButterKnife.bind(this);

        myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());

        pager.setAdapter(myPagerAdapter);

        page_tab_indicator.setViewPager(pager);

        setTabsProperty();

    }

    /**
     * 设置标签栏的属性以及状态
     */
    private void setTabsProperty() {
        dm = getResources().getDisplayMetrics();
        // 设置Tab Indicator的颜色
        page_tab_indicator.setIndicatorColor(Color.TRANSPARENT);
        // 设置Tab是自动填充满屏幕的
        page_tab_indicator.setShouldExpand(true);
        // 设置Tab的分割线是透明的
        page_tab_indicator.setDividerColor(Color.TRANSPARENT);
        // 设置Tab Indicator的高度
        page_tab_indicator.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, dm));
        // 设置Tab标题文字的大小
        page_tab_indicator.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 13, dm));
        // 设置Tab标题默认的颜色
        page_tab_indicator.setTextColor(Color.GRAY);
        // 设置选中Tab标题的颜色
        page_tab_indicator.setSelectTextColor(Color.WHITE);
         // 取消点击Tab时的背景色
        page_tab_indicator.setTabBackground(Color.TRANSPARENT);
        //选中的TextSize
        page_tab_indicator.setSelectedTabTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 13, dm));

        page_tab_indicator.setTabPaddingLeftRight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, dm));

    }

}

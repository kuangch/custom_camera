package com.dilusense.a3d_camera.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.dilusense.a3d_camera.fragment.DistanceMeasureFragment;
import com.dilusense.a3d_camera.fragment.DoubleCameraFragment;
import com.dilusense.a3d_camera.fragment.SingleCameraFragment;

public class MyPagerAdapter extends FragmentPagerAdapter {


        private final String[] TITLES = {  "相机","3D扫描", "测距"};

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {

            Fragment fragment = null;

            switch (position) {
                case 0:
                    fragment = new SingleCameraFragment();
                    break;
                case 1:
                    fragment = new DoubleCameraFragment();
                    break;
                case 2:
                    fragment = new DistanceMeasureFragment();
                    break;
            }
            return fragment;
        }

    }
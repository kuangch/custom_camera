package com.dilusense.a3d_camera.databindings.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.Observable;

/**
 * Created by Thinkpad on 2017/8/31.
 */

public class OptionsControl extends BaseObservable{

    private void resetAll(){
        this.showCamera = false;
        this.showMeasure = false;
        this.showScanner = false;
    }

    @Bindable
    public boolean isShowCamera() {
        return showCamera;
    }

    public void setShowCamera(boolean showCamera) {
        resetAll();
        this.showCamera = showCamera;
        notifyChange();
    }

    @Bindable
    public boolean isShowScanner() {
        return showScanner;
    }

    public void setShowScanner(boolean showScanner) {
        resetAll();
        this.showScanner = showScanner;
        notifyChange();
    }

    @Bindable
    public boolean isShowMeasure() {
        return showMeasure;
    }

    public void setShowMeasure(boolean showMeasure) {
        resetAll();
        this.showMeasure = showMeasure;
        notifyChange();
    }

    public OptionsControl(boolean showCamera, boolean showScanner, boolean showMeasure) {
        this.showCamera = showCamera;
        this.showScanner = showScanner;
        this.showMeasure = showMeasure;
    }

    private boolean showCamera;
    private boolean showScanner;
    private boolean showMeasure;

//    public final ObservableField<Boolean>  showCamera  = new ObservableField<>(false);
//    public final ObservableField<Boolean>  showScanner  = new ObservableField<>(true);
//    public final ObservableField<Boolean>  showMeasure  = new ObservableField<>(false);

}

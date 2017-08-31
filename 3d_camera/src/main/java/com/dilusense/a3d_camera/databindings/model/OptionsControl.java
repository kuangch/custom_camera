package com.dilusense.a3d_camera.databindings.model;

/**
 * Created by Thinkpad on 2017/8/31.
 */

public class OptionsControl {
    public boolean getShowCamera() {
        return showCamera;
    }

    public void setShowCamera(boolean showCamera) {
        this.showCamera = showCamera;

        this.showScanner = !showCamera;
        this.showMeasure = !showCamera;
    }

    public boolean getShowScanner() {
        return showScanner;
    }

    public void setShowScanner(boolean showScanner) {
        this.showScanner = showScanner;

        this.showMeasure = !showScanner;
        this.showCamera = !showScanner;
    }

    public boolean getShowMeasure() {
        return showMeasure;
    }

    public void setShowMeasure(boolean showMeasure) {
        this.showMeasure = showMeasure;

        this.showScanner = !showMeasure;
        this.showCamera = !showMeasure;

    }

    public OptionsControl(boolean showCamera, boolean showScanner, boolean showMeasure) {
        this.showCamera = showCamera;
        this.showScanner = showScanner;
        this.showMeasure = showMeasure;
    }

    private boolean showCamera = false;
    private boolean showScanner = true;
    private boolean showMeasure = false;
}

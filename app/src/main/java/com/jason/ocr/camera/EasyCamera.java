package com.jason.ocr.camera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.jason.ocr.CameraActivity;

/**
 * Created by wangz on 2018/8/27.
 */

public class EasyCamera {
    public static final int REQUEST_CAPTURE = 10;
    public static final String EXTRA_VIEW_RATIO = "viewRatio";
    public static final String EXTRA_OUTPUT_URI = "outputUri";
    public static final String EXTRA_OUTPUT_IMAGE_WIDTH = "imageWidth";
    public static final String EXTRA_OUTPUT_IMAGE_HEIGHT = "imageHeight";
    public static final String EXTRA_MARGIN_BY_WIDTH = "marginByWidth";
    public static final String EXTRA_MARGIN_BY_HEIGHT = "marginByHeight";


    private Intent mCameraIntent;
    private Bundle mCameraBundle;

    public static EasyCamera create(@NonNull Uri destination) {
        return new EasyCamera(destination);
    }

    private EasyCamera( @NonNull Uri destination) {
        mCameraIntent = new Intent();
        mCameraBundle = new Bundle();
        mCameraBundle.putParcelable(EXTRA_OUTPUT_URI, destination);
    }

    public EasyCamera withViewRatio(float ratio) {
        mCameraBundle.putFloat(EXTRA_VIEW_RATIO, ratio);
        return this;
    }

    public EasyCamera withMarginCameraEdge(int leftRight,int topBottom){
        mCameraBundle.putInt(EXTRA_MARGIN_BY_WIDTH,leftRight);
        mCameraBundle.putInt(EXTRA_MARGIN_BY_HEIGHT,topBottom);
        return this;
    }

    public void start(@NonNull Activity activity) {
        start(activity, REQUEST_CAPTURE);
    }

    public void start(@NonNull Activity activity, int requestCode) {
        activity.startActivityForResult(getIntent(activity), requestCode);
    }

    public Intent getIntent(@NonNull Context context) {
        mCameraIntent.setClass(context, CameraActivity.class);
        mCameraIntent.putExtras(mCameraBundle);
        return mCameraIntent;
    }

    public static Uri getOutput(@NonNull Intent intent) {
        return intent.getParcelableExtra(EXTRA_OUTPUT_URI);
    }

    public static int getImageWidth(@NonNull Intent intent){
        return intent.getIntExtra(EXTRA_OUTPUT_IMAGE_WIDTH,0);
    }

    public static int getImageHeight(@NonNull Intent intent){
        return intent.getIntExtra(EXTRA_OUTPUT_IMAGE_HEIGHT,0);
    }
}

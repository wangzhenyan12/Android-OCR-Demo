package com.jason.ocr.camera.util;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by wangz on 2018/8/27.
 */

public class DisplayUtils {

    public static Rect createCenterScreenRect(int viewWidth, int viewHeight, int w, int h) {
        int x1 = viewWidth / 2 - w / 2;
        int y1 = viewHeight / 2 - h / 2;
        int x2 = x1 + w;
        int y2 = y1 + h;
        return new Rect(x1, y1, x2, y2);
    }

    public static Point createCenterPictureRect(float ratio, float cameraRatio, int picSizeX, int picSizeY) {
        int wRectPicture;
        int hRectPicture;
        if (ratio > cameraRatio) {
            hRectPicture = picSizeY;
            wRectPicture = (int) (picSizeY / ratio);
        } else {
            wRectPicture = picSizeX;
            hRectPicture = (int) (picSizeX * ratio);
        }
        return new Point(wRectPicture, hRectPicture);
    }

    public static DisplayMetrics getDisplayMetrics(Context context) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(
                Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(
                displaymetrics);
        return displaymetrics;
    }

    public static float getScreenHeight(Context context) {
        return getDisplayMetrics(context).heightPixels;
    }

    public static float getScreenWidth(Context context) {
        return getDisplayMetrics(context).widthPixels;
    }

    public static float px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return pxValue / scale + 0.5f;
    }

    public static float dp2px(Context context, float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return dipValue * scale + 0.5f;
    }

}

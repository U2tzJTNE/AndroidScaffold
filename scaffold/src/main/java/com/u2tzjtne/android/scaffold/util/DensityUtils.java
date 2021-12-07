package com.u2tzjtne.android.scaffold.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * DensityUtils
 * @author u2tzjtne
 */
public class DensityUtils {
    /**
     * Dp 转 Px
     *
     * @param context context
     * @param dpValue dp
     * @return px
     */
    public static int dip2Pixel(Context context, float dpValue) {
        if (context == null) {
            return 0;
        }
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * Px 转 Dp
     *
     * @param context context
     * @param pxValue px
     * @return dp
     */
    public static int pixel2Dip(Context context, float pxValue) {
        if (context == null) {
            return 0;
        }
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获取DisplayMetrics
     *
     * @param context context
     * @return DisplayMetrics
     */
    public static DisplayMetrics getDisplayMetrics(Context context) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        WindowManager wmanager = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE));
        wmanager.getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics;
    }

    /**
     * 获取屏幕宽度
     *
     * @param context
     * @return 屏幕宽度 - PX
     */
    public static int getScreenWidth(Context context) {
        return getDisplayMetrics(context).widthPixels;
    }

    /**
     * 获取屏幕高度
     *
     * @param context context
     * @return 屏幕高度 - PX
     */
    public static int getScreenHeight(Context context) {
        return getDisplayMetrics(context).heightPixels;
    }
}

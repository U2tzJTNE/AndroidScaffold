package com.u2tzjtne.android.scaffold.util;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.u2tzjtne.android.scaffold.base.BaseApp;

/**
 * @author luke
 * @desc 利用单例模式，解决重命名toast重复弹出的问题
 */
public class ToastUtils {
    private static Toast mToast;

    public static void s(@NonNull String mString) {
        if (mToast == null) {
            mToast = Toast.makeText(BaseApp.getInstance(), mString, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(mString);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    public static void s(int messageId) {
        if (mToast == null) {
            mToast = Toast.makeText(BaseApp.getInstance(), messageId, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(messageId);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    public static void l(@NonNull String mString) {
        if (mToast == null) {
            mToast = Toast.makeText(BaseApp.getInstance(), mString, Toast.LENGTH_LONG);
        } else {
            mToast.setText(mString);
            mToast.setDuration(Toast.LENGTH_LONG);
        }
        mToast.show();
    }

    public static void l(int messageId) {
        if (mToast == null) {
            mToast = Toast.makeText(BaseApp.getInstance(), messageId, Toast.LENGTH_LONG);
        } else {
            mToast.setText(messageId);
            mToast.setDuration(Toast.LENGTH_LONG);
        }
        mToast.show();
    }
}

package com.u2tzjtne.android.scaffold.dialog;

import android.content.Context;

/**
 * @author u2tzjtne@gmail.com
 */
public class LoadingDialogUtils {

    private static LoadingDialog mDialog;

    /**
     * 显示
     */
    public static void show(Context context) {
        dismiss();
        mDialog = new LoadingDialog(context);
        // 点击屏幕不隐藏
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(true);
        if (!mDialog.isShowing()) {
            mDialog.show();
        }
    }

    /**
     * 显示
     */
    public static void show(Context context, LoadingDialog.CancelListener cancelListener) {
        dismiss();
        mDialog = new LoadingDialog(context);
        // 点击屏幕不隐藏
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setOnCancelListener(dialog -> cancelListener.onCancel());
        mDialog.setCancelable(true);
        if (!mDialog.isShowing()) {
            mDialog.show();
        }
    }

    /**
     * 隐藏
     */
    public static void dismiss() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }
}

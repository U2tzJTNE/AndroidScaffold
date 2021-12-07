package com.u2tzjtne.android.scaffold.http.handler;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.u2tzjtne.android.scaffold.dialog.LoadingDialog;
import com.u2tzjtne.android.scaffold.dialog.LoadingDialogUtils;


/**
 * @author u2tzjtne@gmail.com
 */
public class LoadingHandler extends Handler {

    public static final int SHOW_DIALOG = 1;
    public static final int DISMISS_DIALOG = 2;

    private final Context mContext;
    private final LoadingDialog.CancelListener mCancelListener;

    public LoadingHandler(Context mContext, LoadingDialog.CancelListener mCancelListener) {
        super();
        this.mCancelListener = mCancelListener;
        this.mContext = mContext;
    }

    private void showDialog() {
        LoadingDialogUtils.show(mContext, mCancelListener);
    }

    private void dismissDialog() {
        LoadingDialogUtils.dismiss();
    }

    @Override
    public void handleMessage(Message msg) {
        if (msg.what == SHOW_DIALOG) {
            showDialog();
        } else if (msg.what == DISMISS_DIALOG) {
            dismissDialog();
        }
    }
}
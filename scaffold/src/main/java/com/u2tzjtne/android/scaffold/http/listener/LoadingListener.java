package com.u2tzjtne.android.scaffold.http.listener;

import android.content.Context;

import com.u2tzjtne.android.scaffold.dialog.LoadingDialog;
import com.u2tzjtne.android.scaffold.http.HttpHelper;
import com.u2tzjtne.android.scaffold.http.handler.LoadingHandler;
import com.yanzhenjie.nohttp.rest.Response;

/**
 * @author: u2tzjtne
 * @email: u2tzjtne@gmail.com
 * @date: 2020/11/17 17:42
 * @desc: 自带加载框的请求监听器
 */
public class LoadingListener<T> extends BaseListener<T> implements LoadingDialog.CancelListener {
    private LoadingHandler mLoadingHandler;

    private Context context;
    private final String sign;

    public LoadingListener(Context context, String sign) {
        this.context = context;
        this.sign = sign;
        mLoadingHandler = new LoadingHandler(context, this);
    }

    private void showLoadingDialog() {
        if (mLoadingHandler != null) {
            mLoadingHandler.obtainMessage(LoadingHandler.SHOW_DIALOG).sendToTarget();
        }
    }

    private void dismissLoadingDialog() {
        if (mLoadingHandler != null) {
            mLoadingHandler.obtainMessage(LoadingHandler.DISMISS_DIALOG).sendToTarget();
            mLoadingHandler = null;
        }
    }

    @Override
    public void onStart(int what) {
        super.onStart(what);
        showLoadingDialog();
    }

    @Override
    public void onSucceed(int what, Response<T> response) {
        super.onSucceed(what, response);
    }

    @Override
    public void onFailed(int what, Response<T> response) {
        super.onFailed(what, response);
    }

    @Override
    public void onFinish(int what) {
        super.onFinish(what);
        dismissLoadingDialog();
        release();
    }

    /**
     * 用户手动取消了加载框
     */
    @Override
    public void onCancel() {
        if (sign != null) {
            HttpHelper.getInstance().cancelRequest(sign);
        }
        release();
    }

    private void release() {
        if (context != null) {
            context = null;
        }
    }
}

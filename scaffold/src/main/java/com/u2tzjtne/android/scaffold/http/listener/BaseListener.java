package com.u2tzjtne.android.scaffold.http.listener;

import com.u2tzjtne.android.scaffold.util.ToastUtils;
import com.yanzhenjie.nohttp.rest.Response;
import com.yanzhenjie.nohttp.rest.SimpleResponseListener;

/**
 * @author: u2tzjtne
 * @email: u2tzjtne@gmail.com
 * @date: 2020/11/17 17:42
 * @desc: 请求监听器基类
 */
public class BaseListener<T> extends SimpleResponseListener<T> {

    @Override
    public void onStart(int what) {
        super.onStart(what);
    }

    @Override
    public void onSucceed(int what, Response<T> response) {
        super.onSucceed(what, response);
    }

    @Override
    public void onFailed(int what, Response<T> response) {
        super.onFailed(what, response);
        response.getException().printStackTrace();
        ToastUtils.s("网络请求异常：code: " + response.responseCode() + " msg: " + response.getException().getMessage());
    }

    @Override
    public void onFinish(int what) {
        super.onFinish(what);
    }
}

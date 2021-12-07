package com.u2tzjtne.android.scaffold.base;

import android.content.Context;

import com.u2tzjtne.android.scaffold.http.HttpHelper;
import com.u2tzjtne.android.scaffold.http.listener.BaseListener;
import com.u2tzjtne.android.scaffold.http.listener.LoadingListener;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.StringRequest;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.net.Proxy;

/**
 * author: luke
 * email: u2tzjtne@gmail.com
 * date: 2021/12/2 14:11
 * desc: 网络请求 逻辑操作等
 */
public class BasePresenter<V extends IContract> {

    private Reference<V> mViewRef;//View接口类型弱引用

    /**
     * 生成一个Request<String>请求
     *
     * @param url    接口完整地址
     * @param method 请求方式
     * @return Request<String>
     */
    public Request<String> createRequest(String url, RequestMethod method) {
        StringRequest request = new StringRequest(url, method);
        request.setProxy(Proxy.NO_PROXY);//禁止代理 防止抓包
        request.setCancelSign(getSignName());
        return request;
    }

    public LoadingListener<String> createLoadListener(Context context) {
        return new LoadingListener<>(context, getSignName());
    }

    public BaseListener<String> createListener() {
        return new BaseListener<>();
    }

    /**
     * 关联Activity或者Fragment
     */
    public void attachView(V view) {
        mViewRef = new SoftReference<>(view); //建立关联
    }

    public V getView() {
        return mViewRef.get();
    }

    public boolean isViewAttached() {//判断是否与View建立了关联
        return mViewRef != null && mViewRef.get() != null;
    }

    public void detachView() {//解除关联
        if (mViewRef != null) {
            //取消网络请求和下载请求
            HttpHelper.getInstance().cancelRequest(getSignName());
            HttpHelper.getInstance().cancelDownload(getSignName());
            mViewRef.clear();
            mViewRef = null;
        }
    }

    /**
     * 生成接口标志信息
     * 用于取消接口
     *
     * @return 标志信息
     */
    private String getSignName() {
        if (mViewRef != null) {
            return Integer.toHexString(System.identityHashCode(mViewRef.get()));
        }
        return null;
    }
}

package com.u2tzjtne.android.scaffold.http;

import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.download.DownloadListener;
import com.yanzhenjie.nohttp.download.DownloadQueue;
import com.yanzhenjie.nohttp.download.DownloadRequest;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;

/**
 * @author: luke
 * @email: u2tzjtne@gmail.com
 * @date: 2021/1/6 9:12
 * @desc: 网络请求帮助类 单例模式
 */
public class HttpHelper {
    /**
     * 在访问HttpHelper时创建单例
     */
    private static class SingletonHolder {
        private static final HttpHelper INSTANCE = new HttpHelper();
    }

    public HttpHelper() {
        requestQueue = NoHttp.newRequestQueue(3);
        downloadQueue = NoHttp.newDownloadQueue(1);
    }

    /**
     * 请求队列
     */
    private final RequestQueue requestQueue;
    /**
     * 下载队列
     */
    private final DownloadQueue downloadQueue;

    /**
     * 获取单例
     *
     * @return 返回单例对象
     */
    public static HttpHelper getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 文件下载
     *
     * @param what     区分下载的标志
     * @param request  请求封装
     * @param listener 监听
     */
    public void download(int what, DownloadRequest request, DownloadListener listener) {
        downloadQueue.add(what, request, listener);
    }

    /**
     * 文件下载
     *
     * @param request  请求封装
     * @param listener 监听
     */
    public void download(DownloadRequest request, DownloadListener listener) {
        download(0, request, listener);
    }

    /**
     * 添加一个请求到请求队列。
     *
     * @param what     用来标志请求, 当多个请求使用同一个Listener时, 在回调方法中会返回这个what。
     * @param request  请求对象。
     * @param listener 结果回调对象。
     */
    public <T> void add(int what, Request<T> request, OnResponseListener<T> listener) {
        requestQueue.add(what, request, listener);
    }

    /**
     * 添加一个请求到请求队列。
     *
     * @param request  请求对象。
     * @param listener 结果回调对象。
     */
    public <T> void add(Request<T> request, OnResponseListener<T> listener) {
        add(0, request, listener);
    }

    /**
     * 取消这个sign标记的所有请求。
     *
     * @param sign 请求的取消标志。
     */
    public void cancelRequest(Object sign) {
        if (sign != null) {
            requestQueue.cancelBySign(sign);
        }
    }

    /**
     * 取消这个sign标记的所有请求
     *
     * @param sign 请求的取消标志。
     */
    public void cancelDownload(Object sign) {
        if (sign != null) {
            downloadQueue.cancelBySign(sign);
        }
    }

    /**
     * 取消队列中所有请求。
     */
    public void cancelAll() {
        requestQueue.cancelAll();
        downloadQueue.cancelAll();
    }

    /**
     * 完全退出app时，调用这个方法释放CPU
     */
    public void stop() {
        requestQueue.stop();
        downloadQueue.stop();
    }
}

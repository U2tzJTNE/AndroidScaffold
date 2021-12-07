package com.u2tzjtne.android.scaffold.media.video;

import com.danikula.videocache.HttpProxyCacheServer;
import com.u2tzjtne.android.scaffold.base.BaseApp;

public class VideoProxyHelper {

    private VideoProxyHelper() {
    }

    public static VideoProxyHelper getInstance() {
        //第一次调用getInstance方法时才加载SingletonHolder并初始化sInstance
        return SingletonHolder.sInstance;
    }

    //静态内部类
    private static class SingletonHolder {
        private static final VideoProxyHelper sInstance = new VideoProxyHelper();
    }

    private HttpProxyCacheServer proxy = null;

    public HttpProxyCacheServer getProxy() {
        if (proxy == null) {
            proxy = newProxy();
        }
        return proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer.Builder(BaseApp.getContext())
                .maxCacheSize(1024 * 1024 * 1024)//1 Gb for cache
                .build();
    }
}

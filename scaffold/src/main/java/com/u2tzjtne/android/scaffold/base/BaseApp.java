package com.u2tzjtne.android.scaffold.base;

import android.app.Application;
import android.content.Context;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.launcher.ARouter;
import com.u2tzjtne.android.scaffold.BuildConfig;
import com.u2tzjtne.android.scaffold.util.LogUtils;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.tencent.mmkv.MMKV;
import com.yanzhenjie.nohttp.InitializationConfig;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.OkHttpNetworkExecutor;

/**
 * @author u2tzjtne@gmail.com
 * @date 2020/6/3
 */
public class BaseApp extends Application {
    private static BaseApp instance;
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        if (instance == null) {
            instance = this;
            context = getApplicationContext();
        }
        init();
    }

    public static BaseApp getInstance() {
        return instance;
    }

    public static Context getContext() {
        return context;
    }

    /**
     * 初始化
     */
    private void init() {
        initLog();
        initMMKV();
        initNet();
        initARouter();
    }

    /**
     * 初始化ARouter
     */
    private void initARouter() {
        // 这两行必须写在init之前，否则这些配置在init过程中将无效
        if (BuildConfig.DEBUG) {
            ARouter.openLog(); // 打印日志
            ARouter.openDebug();// 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        // 尽可能早，推荐在Application中初始化
        ARouter.init(this);
    }

    /**
     * 初始化MMKV
     */
    private void initMMKV() {
        String rootDir = MMKV.initialize(this);
        if (BuildConfig.DEBUG) {
            LogUtils.d("MMKV根路径：" + rootDir);
        }
    }

    /**
     * 初始化日志框架
     */
    private void initLog() {
        Logger.addLogAdapter(new AndroidLogAdapter() {
            @Override
            public boolean isLoggable(int priority, @Nullable String tag) {
                return BuildConfig.DEBUG;
            }
        });
    }

    /**
     * 初始化网络框架
     */
    private void initNet() {
        InitializationConfig config = InitializationConfig.newBuilder(this)
                // 全局连接服务器超时时间，单位毫秒，默认10s。
                .connectionTimeout(10 * 1000)
                // 全局等待服务器响应超时时间，单位毫秒，默认10s。
                .readTimeout(10 * 1000)
                //使用okhttp
                .networkExecutor(new OkHttpNetworkExecutor())
                .build();
        NoHttp.initialize(config);
        // 开启NoHttp的调试模式, 配置后可看到请求过程、日志和错误信息
        com.yanzhenjie.nohttp.Logger.setDebug(BuildConfig.DEBUG);
        // 打印Log的tag
        com.yanzhenjie.nohttp.Logger.setTag("NoHttp");
    }
}

package com.u2tzjtne.android.scaffold.base;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import com.u2tzjtne.android.scaffold.util.AppUtils;
import com.u2tzjtne.android.scaffold.util.KeyboardStatusDetector;
import com.u2tzjtne.android.scaffold.util.LogUtils;
import com.u2tzjtne.android.scaffold.util.SPUtils;
import com.u2tzjtne.android.scaffold.util.ToastUtils;
import com.u2tzjtne.android.scaffold.util.ViewBindingUtils;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

/**
 * @author: u2tzjtne
 * @email: u2tzjtne@gmail.com
 * @date: 2021/1/6 12:43
 * @desc: activity 基类
 */
public abstract class BaseActivity<VB extends ViewBinding,
        V extends IContract, P extends BasePresenter<V>> extends AppCompatActivity {
    private VB binding;
    private P presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configFullscreen();
        setContentView(getBinding().getRoot());
        try {
            getPresenter().attachView((V) this);
        } catch (ClassCastException ignored) {
        }
        initDeviceId();
    }

    public VB getBinding() {
        if (binding == null) {
            binding = ViewBindingUtils.inflateWithGeneric(this, getLayoutInflater());
        }
        return binding;
    }

    public abstract P initPresenter();

    public P getPresenter() {
        if (presenter == null) {
            presenter = initPresenter();
        }
        return presenter;
    }

    private void init() {
        initView();
        initData();
        addKeyboardStatusDetector();
    }

    private void addKeyboardStatusDetector() {
        new KeyboardStatusDetector()
                .registerActivity(this)
                .setVisibilityListener(keyboardVisible -> {
                    if (keyboardVisible) {
                        LogUtils.d("键盘弹出");
                    } else {
                        LogUtils.d("键盘隐藏");
                        configFullscreen();
                    }
                });
    }

    private void configFullscreen() {
        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(option);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            configFullscreen();
        }
    }

    /**
     * 初始化view
     */
    public void initView() {

    }

    /**
     * 初始化数据
     */
    public void initData() {

    }

    /**
     * 初始化设备ID
     */
    private void initDeviceId() {
        String device_id = SPUtils.getString("device_id", "");
        if (TextUtils.isEmpty(device_id)) {
            AndPermission.with(this)
                    .runtime()
                    .permission(Permission.READ_PHONE_STATE,
                            Permission.ACCESS_FINE_LOCATION)
                    .onDenied(data -> {
                        ToastUtils.s("请授予权限后再试！");
                        finish();
                    })
                    .onGranted(data -> {
                        SPUtils.putString("device_id", AppUtils.getDeviceId());
                        init();
                    }).start();
        } else {
            init();
        }
    }

    public void startActivity(Class<?> cls, boolean finish) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
        if (finish) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放Presenter
        getPresenter().detachView();
    }
}

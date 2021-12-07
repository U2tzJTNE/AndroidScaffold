package com.u2tzjtne.android.scaffold.base;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewbinding.ViewBinding;

import com.u2tzjtne.android.scaffold.util.ViewBindingUtils;

/**
 * author: luke
 * email: u2tzjtne@gmail.com
 * date: 2021/12/3 0:12
 * desc: dialog基类 DialogFragment 可以在屏幕旋转后重建
 * 需要确定三个泛型 ViewBinding、Presenter、
 */
public abstract class BaseDialog<VB extends ViewBinding, V extends IContract, P extends BasePresenter<V>> extends DialogFragment {

    private VB binding;
    private P presenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Window window = getDialog().getWindow();
        //去掉dialog默认的padding
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return getBinding().getRoot();
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            getPresenter().attachView((V) this);
        } catch (ClassCastException ignored) {
        }
        if (getDialog() != null) {
            Window window = getDialog().getWindow();
            if (window != null) {
                window.addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                getDialog().setOnShowListener(dialog -> {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                    fullScreen(window);
                });
            }
        }
        initView();
        initData();
    }


    /**
     * view事件、UI元素初始化
     */
    public void initView() {

    }

    /**
     * 获取参数、请求接口数据
     */
    public void initData() {

    }

    public void show(@NonNull FragmentManager manager) {
        show(manager, getClass().getName());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        presenter.detachView();
    }

    private void fullScreen(Window window) {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        window.getDecorView().setSystemUiVisibility(uiOptions);
    }
}

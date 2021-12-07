package com.u2tzjtne.android.scaffold.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

import com.u2tzjtne.android.scaffold.util.ViewBindingUtils;

/**
 * author: luke
 * email: u2tzjtne@gmail.com
 * date: 2021/11/30 10:44
 * desc: fragment基类
 */
public abstract class BaseFragment<VB extends ViewBinding, V extends IContract, P extends BasePresenter<V>> extends Fragment {
    private VB binding;
    private P presenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return getBinding().getRoot();
    }

    public VB getBinding() {
        if (binding==null){
            binding = ViewBindingUtils.inflateWithGeneric(this, getLayoutInflater());
        }
        return binding;
    }

    public abstract P initPresenter();

    public P getPresenter() {
        if (presenter==null){
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
        initView();
        initData();
    }

    public void initView() {

    }

    public void initData() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        getPresenter().detachView();
    }
}

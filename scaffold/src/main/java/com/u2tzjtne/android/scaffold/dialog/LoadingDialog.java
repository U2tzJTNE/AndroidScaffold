package com.u2tzjtne.android.scaffold.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;


import com.u2tzjtne.android.scaffold.R;

import java.util.Objects;

/**
 * @author u2tzjtne@gmail.com
 */
public class LoadingDialog extends Dialog {

    public LoadingDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        Objects.requireNonNull(this.getWindow()).getDecorView().setSystemUiVisibility(uiOptions);
        Objects.requireNonNull(getWindow())
                .setBackgroundDrawableResource(android.R.color.transparent);
        setContentView(R.layout.dialog_loading);
    }

    public interface CancelListener {
        void onCancel();
    }
}

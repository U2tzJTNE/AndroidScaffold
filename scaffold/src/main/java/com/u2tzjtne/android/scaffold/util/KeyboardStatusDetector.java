package com.u2tzjtne.android.scaffold.util;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Rect;
import android.view.View;

/**
 * @author u2tzjtne
 * @desc 监听键盘的弹出和收起
 */
public class KeyboardStatusDetector {
    private static final int SOFT_KEY_BOARD_MIN_HEIGHT = 100;
    private KeyboardVisibilityListener mVisibilityListener;

    boolean keyboardVisible = false;

    public KeyboardStatusDetector registerFragment(Fragment f) {
        return registerView(f.getView());
    }

    public KeyboardStatusDetector registerActivity(Activity a) {
        return registerView(a.getWindow().getDecorView().findViewById(android.R.id.content));
    }

    public KeyboardStatusDetector registerView(final View v) {
        v.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            v.getWindowVisibleDisplayFrame(r);

            int heightDiff = v.getRootView().getHeight() - (r.bottom - r.top);
            if (heightDiff > SOFT_KEY_BOARD_MIN_HEIGHT) { // if more than 100 pixels, its probably a keyboard...
                if (!keyboardVisible) {
                    keyboardVisible = true;
                    if (mVisibilityListener != null) {
                        mVisibilityListener.onVisibilityChanged(true);
                    }
                }
            } else {
                if (keyboardVisible) {
                    keyboardVisible = false;
                    if (mVisibilityListener != null) {
                        mVisibilityListener.onVisibilityChanged(false);
                    }
                }
            }
        });

        return this;
    }

    public KeyboardStatusDetector setVisibilityListener(KeyboardVisibilityListener listener) {
        mVisibilityListener = listener;
        return this;
    }

    public interface KeyboardVisibilityListener {
        void onVisibilityChanged(boolean keyboardVisible);
    }

}

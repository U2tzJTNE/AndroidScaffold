package com.u2tzjtne.android.scaffold.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.u2tzjtne.android.scaffold.R;


/**
 * @author: u2tzjtne
 * @email: u2tzjtne@gmail.com
 * @date: 2021/1/15 16:26
 * @desc: 使用NumberPicker获取数值的对话框
 */
public class NumberPickerDialog extends Dialog
        implements View.OnClickListener,
        NumberPicker.OnValueChangeListener {

    private final String maxValue = "最大值";
    private final String minValue = "最小值";
    private final String currentValue = "当前值";

    private NumberPicker mNumberPicker;
    private TextView mTvPositive;
    private TextView mTvNegative;
    private final NumberPicker.OnValueChangeListener mCallback;

    private int newVal;
    private int oldVal;
    private final int maxValueNumber;
    private final int minValueNumber;
    private final int currentValueNumber;

    /**
     * @param context            上下文
     * @param callBack           回调器
     * @param maxValueNumber     最大值
     * @param minValueNumber     最小值
     * @param currentValueNumber 当前值
     */
    public NumberPickerDialog(Context context, NumberPicker.OnValueChangeListener callBack,
                              int maxValueNumber, int minValueNumber, int currentValueNumber) {
        super(context);
        this.mCallback = callBack;
        this.maxValueNumber = maxValueNumber;
        this.minValueNumber = minValueNumber;
        this.currentValueNumber = currentValueNumber;
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
        getWindow().getDecorView().setSystemUiVisibility(uiOptions);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setContentView(R.layout.dialog_number_picker);
        initView();
    }

    private void initView() {
        mNumberPicker = findViewById(R.id.numberPicker);
        mTvNegative = findViewById(R.id.tv_negative);
        mTvPositive = findViewById(R.id.tv_positive);
        mNumberPicker.setMaxValue(maxValueNumber);
        mNumberPicker.setMinValue(minValueNumber);
        mNumberPicker.setValue(currentValueNumber);
        mNumberPicker.setOnValueChangedListener(this);
        mTvPositive.setOnClickListener(this);
        mTvNegative.setOnClickListener(this);
    }

    @Override
    public Bundle onSaveInstanceState() {
        Bundle state = super.onSaveInstanceState();
        state.putInt(maxValue, mNumberPicker.getMaxValue());
        state.putInt(minValue, mNumberPicker.getMinValue());
        state.putInt(currentValue, mNumberPicker.getValue());
        return state;
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int max = savedInstanceState.getInt(maxValue);
        int min = savedInstanceState.getInt(minValue);
        int cur = savedInstanceState.getInt(currentValue);
        mNumberPicker.setMaxValue(max);
        mNumberPicker.setMinValue(min);
        mNumberPicker.setValue(cur);
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        this.oldVal = oldVal;
        this.newVal = newVal;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_positive) {
            mCallback.onValueChange(mNumberPicker, oldVal, newVal);
            dismiss();
        } else if (v.getId() == R.id.tv_negative) {
            dismiss();
        }
    }
}
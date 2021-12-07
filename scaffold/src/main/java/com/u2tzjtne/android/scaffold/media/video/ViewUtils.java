package com.u2tzjtne.android.scaffold.media.video;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.view.View;
import android.view.ViewGroup;

import com.u2tzjtne.android.scaffold.base.BaseApp;
import com.u2tzjtne.android.scaffold.util.LogUtils;

/**
 * @author: u2tzjtne
 * @email: u2tzjtne@gmail.com
 * @date: 2021/1/19 14:48
 * @desc:
 */
public class ViewUtils {
    /**
     * 修改预览View的大小,以用来适配屏幕
     */
    public static void fitViewToScreen(Context context, View view, int dataWidth, int dataHeight) {
        int videoWidth = dataWidth;
        int videoHeight = dataHeight;
        LogUtils.d("视频高度：" + videoHeight + "  视频宽度：" + videoWidth);
        int deviceWidth = context.getResources().getDisplayMetrics().widthPixels;
        int deviceHeight = context.getResources().getDisplayMetrics().heightPixels;
        LogUtils.d("屏幕高度：" + deviceHeight + "  屏幕宽度：" + deviceWidth);
        float devicePercent;
        //下面进行求屏幕比例,因为横竖屏会改变屏幕宽度值,所以为了保持更小的值除更大的值.
        if (BaseApp.getInstance().getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) { //竖屏
            devicePercent = (float) deviceWidth / (float) deviceHeight; //竖屏状态下宽度小与高度,求比
        } else { //横屏
            devicePercent = (float) deviceHeight / (float) deviceWidth; //横屏状态下高度小与宽度,求比
        }
        if (videoWidth > videoHeight) { //判断视频的宽大于高,那么我们就优先满足视频的宽度铺满屏幕的宽度,然后在按比例求出合适比例的高度
            videoWidth = deviceWidth;//将视频宽度等于设备宽度,让视频的宽铺满屏幕
            videoHeight = (int) (deviceWidth * devicePercent);//设置了视频宽度后,在按比例算出视频高度
        } else {  //判断视频的高大于宽,那么我们就优先满足视频的高度铺满屏幕的高度,然后在按比例求出合适比例的宽度
            if (context.getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {//竖屏
                videoHeight = deviceHeight;
                //接受在宽度的轻微拉伸来满足视频铺满屏幕的优化
                float videoPercent = (float) videoWidth / (float) videoHeight;//求视频比例 注意是宽除高 与 上面的devicePercent 保持一致
                float differenceValue = Math.abs(videoPercent - devicePercent);//相减求绝对值
                LogUtils.d("devicePercent=" + devicePercent);
                LogUtils.d("videoPercent=" + videoPercent);
                LogUtils.d("differenceValue=" + differenceValue);
                if (differenceValue < 0.3) { //如果小于0.3比例,那么就放弃按比例计算宽度直接使用屏幕宽度
                    videoWidth = deviceWidth;
                } else {
                    videoWidth = (int) (videoWidth / devicePercent);//注意这里是用视频宽度来除
                }
            } else { //横屏
                videoHeight = deviceHeight;
                videoWidth = (int) (deviceHeight * devicePercent);
            }
        }
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = videoWidth;
        layoutParams.height = videoHeight;
        view.setLayoutParams(layoutParams);
    }
}

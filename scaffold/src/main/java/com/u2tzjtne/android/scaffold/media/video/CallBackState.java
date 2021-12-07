package com.u2tzjtne.android.scaffold.media.video;

import androidx.annotation.NonNull;

/**
 * @author: u2tzjtne
 * @email: u2tzjtne@gmail.com
 * @date: 2021/1/5 11:25
 * @desc: 播放状态
 */
public enum CallBackState {
    PREPARE("MediaPlayer--准备完毕"),
    COMPLETE("MediaPlayer--播放结束"),
    ERROR("MediaPlayer--播放错误"),
    EXCEPTION("MediaPlayer--播放异常"),
    INFO("MediaPlayer--播放开始"),
    PROGRESS("MediaPlayer--播放进度回调"),
    SEEK_COMPLETE("MediaPlayer--拖动到尾端"),
    VIDEO_SIZE_CHANGE("MediaPlayer--读取视频大小"),
    BUFFER_UPDATE("MediaPlayer--更新流媒体缓存状态"),
    FORMATE_NOT_SURPORT("MediaPlayer--音视频格式可能不支持"),
    SURFACEVIEW_NULL("SurfaceView--还没初始化"),
    SURFACEVIEW_NOT_ARREADY("SurfaceView--还没准备好"),
    SURFACEVIEW_CHANGE("SurfaceView--Holder改变"),
    SURFACEVIEW_CREATE("SurfaceView--Holder创建"),
    SURFACEVIEW_DESTROY("SurfaceView--Holder销毁");

    private final String state;

    CallBackState(String var3) {
        this.state = var3;
    }

    @Override
    @NonNull
    public String toString() {
        return this.state;
    }
}

package com.u2tzjtne.android.scaffold.media.video;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaDataSource;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.RequiresApi;

import com.u2tzjtne.android.scaffold.base.BaseApp;
import com.u2tzjtne.android.scaffold.util.LogUtils;

/**
 * 多媒体播放 本地视频 音频 流媒体
 *
 * @author u2tzjtne
 */
public class MediaPlayerHelper {
    private final String[] ext = {".3gp", ".3GP", ".mp4", ".MP4", ".mp3", ".ogg", ".OGG", ".MP3", ".wav", ".WAV"};//定义支持的文件格式
    private final Holder uiHolder;//UI的容器
    private static MediaPlayerHelper instance;
    private int delaySecondTime = 1000;//进度回调间隔
    private boolean isHolderCreate = false;//SurfaceHolder是否准备好了
    private boolean fitScreenWidth = false;//匹配屏幕宽度 会根据视频长宽比自动计算视频高度
    private boolean enableCache = false;//是否开启缓存

    /**
     * 获得静态类
     *
     * @return 类对象
     */
    public static synchronized MediaPlayerHelper getInstance() {
        if (instance == null) {
            instance = new MediaPlayerHelper();
        }
        return instance;
    }

    /**
     * 获得流媒体对象
     *
     * @return 对象
     */
    public MediaPlayer getMediaPlayer() {
        return uiHolder.player;
    }

    /**
     * 设置播放进度时间间隔
     *
     * @param time 时间
     * @return 类对象
     */
    public MediaPlayerHelper setProgressInterval(int time) {
        delaySecondTime = time;
        return instance;
    }

    /**
     * 设置是否匹配屏幕宽度
     *
     * @param fitScreenWidth 是否匹配屏幕宽度
     * @return 类对象
     */
    public MediaPlayerHelper setFitScreenWidth(boolean fitScreenWidth) {
        this.fitScreenWidth = fitScreenWidth;
        return this;
    }

    /**
     * 设置是否开启缓存
     *
     * @param enableCache 是否开启缓存
     * @return 类对象
     */
    public MediaPlayerHelper setEnableCache(boolean enableCache) {
        this.enableCache = enableCache;
        return this;
    }


    /**
     * 通过Assets文件名播放Assets目录下的音频
     *
     * @param context   引用
     * @param assetName 名字,带后缀，比如:text.mp3
     */
    public void playAsset(Context context, String assetName, boolean isVideo) {
        if (!checkSupport(assetName)) {
            onStatusCallbackNext(CallBackState.FORMATE_NOT_SURPORT, assetName);
            return;
        }
        if (isVideo) {
            if (isHolderCreate) {
                beginPlayAsset(context, assetName);
            } else {
                setOnHolderCreateListener(() -> beginPlayAsset(context, assetName));
            }
        } else {
            beginPlayAsset(context, assetName);
        }
    }

    /**
     * 通过文件路径播放音视频
     *
     * @param path 路径
     */
    public void playUrl(Context context, final String path, boolean isVideo) {
        if (isVideo) {
            if (isHolderCreate) {
                beginPlayUrl(context, path);
            } else {
                setOnHolderCreateListener(() -> beginPlayUrl(context, path));
            }
        } else {
            beginPlayUrl(context, path);
        }
    }

    /**
     * 播放流视频
     *
     * @param videoBuffer videoBuffer
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void playByte(byte[] videoBuffer, boolean isVideo) {
        if (isVideo) {
            if (isHolderCreate) {
                beginPlayDataSource(new ByteMediaDataSource(videoBuffer));
            } else {
                setOnHolderCreateListener(() -> beginPlayDataSource(new ByteMediaDataSource(videoBuffer)));
            }
        } else {
            beginPlayDataSource(new ByteMediaDataSource(videoBuffer));
        }
    }

    /**
     * 停止资源
     */
    public void pause() {
        if (uiHolder.player != null) {
            uiHolder.player.pause();
        }
    }

    /**
     * 播放
     */
    public void start() {
        if (uiHolder.player != null) {
            uiHolder.player.start();
        }
    }

    /**
     * 停止资源
     */
    public void stop() {
        if (uiHolder.player != null) {
            if (uiHolder.player.isPlaying()) {
                uiHolder.player.stop();
            }
        }
    }

    /**
     * 释放资源
     */
    public void release() {
        if (uiHolder.player != null) {
            uiHolder.player.release();
            uiHolder.player = null;
        }
        refresh_time_handler.removeCallbacks(refresh_time_Thread);
    }

    /**
     * 重新创建MediaPlayer
     */
    public void reCreateMediaPlayer() {
        if (uiHolder.player != null) {
            if (uiHolder.player.isPlaying()) {
                uiHolder.player.stop();
            }
            uiHolder.player.release();
        }
        uiHolder.player = new MediaPlayer();
        initPlayerListener();
    }

    /**
     * 设置SurfaceView
     *
     * @param videoView 控件
     * @return 类对象
     */
    public MediaPlayerHelper setVideoView(VideoView videoView) {
        if (videoView == null) {
            onStatusCallbackNext(CallBackState.SURFACEVIEW_NULL, uiHolder.player);
        } else {
            uiHolder.videoView = videoView;
            uiHolder.surfaceHolder = uiHolder.videoView.getHolder();
            uiHolder.surfaceHolder.addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    isHolderCreate = true;
                    if (uiHolder.player != null && holder != null) {
                        //解决部分机型/电视播放的时候有声音没画面的情况
                        if (uiHolder.videoView != null) {
                            uiHolder.videoView.post(() -> {
                                holder.setFixedSize(uiHolder.videoView.getWidth(), uiHolder.videoView.getHeight());
                                try {
                                    uiHolder.player.setDisplay(holder);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                    }
                    onStatusCallbackNext(CallBackState.SURFACEVIEW_CREATE, holder);
                    onHolderCreateNext();
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                    onStatusCallbackNext(CallBackState.SURFACEVIEW_CHANGE, format, width, height);
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    isHolderCreate = false;
                    onStatusCallbackNext(CallBackState.SURFACEVIEW_DESTROY, holder);
                }
            });
        }
        return instance;
    }

    /**
     * 设置媒体控制器
     *
     * @param mediaController 媒体控制器
     * @return 类对象
     */
    public MediaPlayerHelper setMediaController(MediaController mediaController) {
        uiHolder.mediaController = mediaController;
        if (uiHolder.videoView != null) {
            uiHolder.videoView.setMediaController(uiHolder.mediaController);
        }
        return instance;
    }

    /**
     * 构造函数
     */
    public MediaPlayerHelper() {
        if (instance == null) {
            instance = this;
        }
        this.uiHolder = new Holder();
        uiHolder.player = new MediaPlayer();
        initPlayerListener();
    }

    /**
     * 时间监听
     */
    private void initPlayerListener() {
        uiHolder.player.setOnCompletionListener(mp -> {
            onStatusCallbackNext(CallBackState.PROGRESS, 100);
            onStatusCallbackNext(CallBackState.COMPLETE, mp);
        });
        uiHolder.player.setOnErrorListener((mp, what, extra) -> {
            String errorString = "what:" + what + " extra:" + extra;
            onStatusCallbackNext(CallBackState.ERROR, errorString);
            return false;
        });
        uiHolder.player.setOnInfoListener((mp, what, extra) -> {
            onStatusCallbackNext(CallBackState.INFO, mp, what, extra);
            return false;
        });
        uiHolder.player.setOnPreparedListener(mp -> {
            try {
                if (uiHolder.videoView != null) {
                    //解决部分机型/电视播放的时候有声音没画面的情况
                    uiHolder.videoView.post(() -> {
                        uiHolder.surfaceHolder.setFixedSize(uiHolder.videoView.getWidth(), uiHolder.videoView.getHeight());
                        //设置预览区域
                        uiHolder.player.setDisplay(uiHolder.surfaceHolder);
                        uiHolder.player.start();
                        refresh_time_handler.postDelayed(refresh_time_Thread, delaySecondTime);
                    });
                }
            } catch (Exception e) {
                onStatusCallbackNext(CallBackState.EXCEPTION, e.toString());
            }
            String holderMsg = "holder -";
            if (uiHolder.surfaceHolder != null) {
                holderMsg = holderMsg + " height：" + uiHolder.surfaceHolder.getSurfaceFrame().height();
                holderMsg = holderMsg + " width：" + uiHolder.surfaceHolder.getSurfaceFrame().width();
            }
            onStatusCallbackNext(CallBackState.PREPARE, holderMsg);
        });
        uiHolder.player.setOnSeekCompleteListener(mp ->
                onStatusCallbackNext(CallBackState.SEEK_COMPLETE, mp));
        uiHolder.player.setOnVideoSizeChangedListener((mp, width, height) -> {
            LogUtils.e("onVideoSizeChanged: 触发 width=" + width + "height=" + height);
            if (fitScreenWidth) {
                ViewUtils.fitViewToScreen(BaseApp.getInstance(),
                        uiHolder.videoView, uiHolder.player.getVideoWidth(),
                        uiHolder.player.getVideoHeight());
            }
            onStatusCallbackNext(CallBackState.VIDEO_SIZE_CHANGE, width, height);
        });
        uiHolder.player.setOnBufferingUpdateListener((mp, percent) ->
                onStatusCallbackNext(CallBackState.BUFFER_UPDATE, mp, percent));
    }

    /**
     * 播放
     *
     * @param path 参数
     */
    private void beginPlayUrl(Context context, String path) {
        /*
         * 其实仔细观察优酷app切换播放网络视频时的确像是这样做的：先暂停当前视频，
         * 让mediaplayer与先前的surfaceHolder脱离“绑定”,当mediaplayer再次准备好要start时，
         * 再次让mediaplayer与surfaceHolder“绑定”在一起，显示下一个要播放的视频。
         * 注：MediaPlayer.setDisplay()的作用： 设置SurfaceHolder用于显示的视频部分媒体。
         */
        try {
            uiHolder.player.setDisplay(null);
            uiHolder.player.reset();
            String newPath = "";
            if (enableCache) {
                newPath = VideoProxyHelper.getInstance().getProxy().getProxyUrl(path);
            }
            if (TextUtils.isEmpty(newPath)) {
                LogUtils.e("开启缓存失败: " + path);
                newPath = path;
            }
            uiHolder.player.setDataSource(newPath);
            //异步
            uiHolder.player.prepareAsync();
        } catch (Exception e) {
            onStatusCallbackNext(CallBackState.ERROR, e.toString());
        }
    }

    /**
     * 显示控制栏
     */
    public void showController() {
        if (uiHolder.videoView != null && uiHolder.mediaController != null) {
            uiHolder.mediaController.show();
        }
    }

    /**
     * 隐藏控制栏
     */
    public void hideController() {
        if (uiHolder.videoView != null && uiHolder.mediaController != null) {
            uiHolder.mediaController.hide();
        }
    }

    /**
     * 播放
     *
     * @param assetName 参数
     */
    private void beginPlayAsset(Context context, String assetName) {
        /*
         * 其实仔细观察优酷app切换播放网络视频时的确像是这样做的：先暂停当前视频，
         * 让mediaplayer与先前的surfaceHolder脱离“绑定”,当mediaplayer再次准备好要start时，
         * 再次让mediaplayer与surfaceHolder“绑定”在一起，显示下一个要播放的视频。
         * 注：MediaPlayer.setDisplay()的作用： 设置SurfaceHolder用于显示的视频部分媒体。
         */
        AssetManager assetMg = context.getAssets();
        try {
            uiHolder.assetDescriptor = assetMg.openFd(assetName);
            uiHolder.player.setDisplay(null);
            uiHolder.player.reset();
            uiHolder.player.setDataSource(uiHolder.assetDescriptor.getFileDescriptor(), uiHolder.assetDescriptor.getStartOffset(), uiHolder.assetDescriptor.getLength());
            uiHolder.player.prepareAsync();
        } catch (Exception e) {
            onStatusCallbackNext(CallBackState.ERROR, e.toString());
        }
    }

    /**
     * 播放
     *
     * @param mediaDataSource 参数
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void beginPlayDataSource(MediaDataSource mediaDataSource) {
        /*
         * 其实仔细观察优酷app切换播放网络视频时的确像是这样做的：先暂停当前视频，
         * 让mediaplayer与先前的surfaceHolder脱离“绑定”,当mediaplayer再次准备好要start时，
         * 再次让mediaplayer与surfaceHolder“绑定”在一起，显示下一个要播放的视频。
         * 注：MediaPlayer.setDisplay()的作用： 设置SurfaceHolder用于显示的视频部分媒体。
         */
        try {
            uiHolder.player.setDisplay(null);
            uiHolder.player.reset();
            uiHolder.player.setDataSource(mediaDataSource);
            uiHolder.player.prepareAsync();
        } catch (Exception e) {
            onStatusCallbackNext(CallBackState.ERROR, e.toString());
        }
    }

    /**
     * 检查是否可以播放
     *
     * @param path 参数
     * @return 结果
     */
    private boolean checkSupport(String path) {
        boolean support = false;
        for (String s : ext) {
            if (path.endsWith(s)) {
                support = true;
                break;
            }
        }
        if (!support) {
            onStatusCallbackNext(CallBackState.FORMATE_NOT_SURPORT, uiHolder.player);
            return false;
        }
        return true;
    }

    /**
     * 播放进度定时器
     */
    private final Handler refresh_time_handler = new Handler();
    private final Runnable refresh_time_Thread = new Runnable() {
        @Override
        public void run() {
            refresh_time_handler.removeCallbacks(refresh_time_Thread);
            try {
                if (uiHolder.player != null && uiHolder.player.isPlaying()) {
                    int duraction = uiHolder.player.getDuration();
                    if (duraction > 0) {
                        onStatusCallbackNext(CallBackState.PROGRESS, 100 * uiHolder.player.getCurrentPosition() / duraction);
                    }
                }
            } catch (IllegalStateException e) {
                onStatusCallbackNext(CallBackState.EXCEPTION, e.toString());
            }
            refresh_time_handler.postDelayed(refresh_time_Thread, delaySecondTime);
        }
    };

    /* ***************************** Holder封装UI ***************************** */

    private static final class Holder {
        private SurfaceHolder surfaceHolder;
        private MediaPlayer player;
        private VideoView videoView;
        private MediaController mediaController;
        private AssetFileDescriptor assetDescriptor;
    }

    /* ***************************** StatusCallback ***************************** */

    private OnStatusCallbackListener onStatusCallbackListener;

    /**
     * 接口类 -> OnStatusCallbackListener
     */
    public interface OnStatusCallbackListener {
        void onNext(CallBackState status, Object... args);
    }

    /**
     * 对外暴露接口 -> setOnStatusCallbackListener
     *
     * @param onStatusCallbackListener
     * @return
     */
    public MediaPlayerHelper setOnStatusCallbackListener(OnStatusCallbackListener onStatusCallbackListener) {
        this.onStatusCallbackListener = onStatusCallbackListener;
        return instance;
    }

    /**
     * 内部使用方法 -> StatusCallbackNext
     *
     * @param status
     * @param args
     */
    private void onStatusCallbackNext(CallBackState status, Object... args) {
        if (onStatusCallbackListener != null) {
            onStatusCallbackListener.onNext(status, args);
        }
    }

    /* ***************************** HolderCreate(内部使用) ***************************** */

    private OnHolderCreateListener onHolderCreateListener;

    /**
     * 接口类 -> OnHolderCreateListener
     */
    private interface OnHolderCreateListener {
        void onHolderCreate();
    }

    /**
     * 内部露接口 -> setOnHolderCreateListener
     *
     * @param onHolderCreateListener
     */
    private void setOnHolderCreateListener(OnHolderCreateListener onHolderCreateListener) {
        this.onHolderCreateListener = onHolderCreateListener;
    }

    /**
     * 内部使用方法 -> HolderCreateNext
     */
    private void onHolderCreateNext() {
        if (onHolderCreateListener != null) {
            onHolderCreateListener.onHolderCreate();
        }
    }
}
package com.u2tzjtne.android.scaffold.util;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.u2tzjtne.android.scaffold.thread.ThreadManager;

import java.util.EnumMap;
import java.util.Map;

/**
 * @author: u2tzjtne
 * @email: u2tzjtne@gmail.com
 * @date: 2021/1/13 9:34
 * @desc: 二维码工具类
 */
public final class QRCodeUtils {

    /**
     * 编码类型
     */
    private static final Map<EncodeHintType, Object> ENCODE_HINTS = new EnumMap<>(EncodeHintType.class);

    /**
     * 解析配置
     */
    private static final Map<DecodeHintType, Object> DECODE_HINTS = new EnumMap<>(DecodeHintType.class);

    static {
        // 编码类型
        ENCODE_HINTS.put(EncodeHintType.CHARACTER_SET, "utf-8");
        // 指定纠错等级, 纠错级别 ( L 7%、M 15%、Q 25%、H 30% )
        ENCODE_HINTS.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        // 设置二维码边的空度, 非负数
        ENCODE_HINTS.put(EncodeHintType.MARGIN, 0);
    }

    private QRCodeUtils() {
    }

    /**
     * detail: 生成二维码结果回调
     */
    public interface QRResultCallback {

        /**
         * 生成二维码结果回调
         *
         * @param success 是否成功
         * @param bitmap  成功图片
         * @param e       异常信息
         */
        void onResult(
                boolean success,
                Bitmap bitmap,
                Exception e
        );
    }

    /**
     * 生成二维码图片
     *
     * @param content  生成内容
     * @param size     图片宽高大小 ( 正方形 px )
     * @param callback 生成结果回调
     */
    public static void createQRCodeImage(
            final String content,
            final int size,
            final QRResultCallback callback
    ) {
        createQRCodeImage(content, size, null, callback);
    }

    /**
     * 生成二维码图片
     *
     * @param content  生成内容
     * @param size     图片宽高大小 ( 正方形 px )
     * @param logo     中间 Logo
     * @param callback 生成结果回调
     */
    public static void createQRCodeImage(
            final String content,
            final int size,
            final Bitmap logo,
            final QRResultCallback callback
    ) {
        ThreadManager.getInstance(10).execute(() -> {
            try {
                // 生成二维码图片
                Bitmap qrCodeBitmap = syncEncodeQRCode(content, size);
                if (logo != null) { // 中间 Logo
                    qrCodeBitmap = addLogoToQRCode(qrCodeBitmap, logo);
                }
                // 触发回调
                if (callback != null) {
                    callback.onResult(true, qrCodeBitmap, null);
                }
            } catch (Exception e) {
                LogUtils.e("createQRCodeImage error: " + e.getMessage());
                // 触发回调
                if (callback != null) {
                    callback.onResult(false, null, e);
                }
            }
        });
    }

    public static void createQRCodeImage(String content, ImageView imageView) {
        //判断合法性
        if (content == null || "".equals(content) || content.length() < 1) {
            return;
        }
        int width = 300;
        if (imageView.getWidth() > 0) {
            width = imageView.getWidth();
        }
        //生成二维码图片的格式，使用ARGB_8888
        createQRCodeImage(content, width, (success, bitmap, e) -> {
            if (imageView != null) {
                imageView.post(() -> {
                    if (imageView != null) {
                        imageView.setImageBitmap(bitmap);
                    }
                });
            }
        });
    }

    /**
     * 解析二维码图片
     *
     * @param bitmap   待解析的二维码图片
     * @param callback 解析结果回调
     */
    public static void decodeQRCode(
            final Bitmap bitmap,
            final QRScanCallback callback
    ) {
        if (bitmap == null) {
            if (callback != null) {
                callback.onResult(false, null, new Exception("bitmap is null"));
            }
            return;
        }
        // 开始解析
        ThreadManager.getInstance(5).execute(() -> {
            try {
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                int[] pixels = new int[width * height];
                bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
                RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
                Result result = new MultiFormatReader()
                        .decode(new BinaryBitmap(new HybridBinarizer(source)), DECODE_HINTS);
                // 触发回调
                if (callback != null) {
                    callback.onResult((result != null), result, null);
                }
            } catch (Exception e) {
                LogUtils.e("decodeQRCode error: " + e.getMessage());
                // 触发回调
                if (callback != null) {
                    callback.onResult(false, null, e);
                }
            }
        });
    }

    /**
     * 获取扫描结果数据
     *
     * @param result 识别数据 {@link Result}
     * @return 扫描结果数据
     */
    public static String getResultData(final Result result) {
        return (result != null) ? result.getText() : null;
    }

    /**
     * 同步创建黑色前景色、白色背景色的二维码图片
     *
     * @param content 生成内容
     * @param size    图片宽高大小 ( 正方形 px )
     * @return 二维码图片
     */
    public static Bitmap syncEncodeQRCode(
            final String content,
            final int size
    ) {
        return syncEncodeQRCode(content, size, Color.BLACK, Color.WHITE);
    }

    /**
     * 同步创建指定前景色、指定背景色二维码图片
     * <pre>
     *     该方法是耗时操作, 请在子线程中调用
     * </pre>
     *
     * @param content         生成内容
     * @param size            图片宽高大小 ( 正方形 px )
     * @param foregroundColor 二维码图片的前景色
     * @param backgroundColor 二维码图片的背景色
     * @return 二维码图片
     */
    public static Bitmap syncEncodeQRCode(
            final String content,
            final int size,
            final int foregroundColor,
            final int backgroundColor
    ) {
        try {
            BitMatrix matrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, size, size, ENCODE_HINTS);
            int[] pixels = new int[size * size];
            for (int y = 0; y < size; y++) {
                for (int x = 0; x < size; x++) {
                    if (matrix.get(x, y)) {
                        pixels[y * size + x] = foregroundColor;
                    } else {
                        pixels[y * size + x] = backgroundColor;
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, size, 0, 0, size, size);
            return bitmap;
        } catch (Exception e) {
            LogUtils.e("syncEncodeQRCode error: " + e.getMessage());
            return null;
        }
    }

    /**
     * 添加 Logo 到二维码图片上
     * <pre>
     *     非最优方法, 该方式是直接把 Logo 覆盖在图片上会遮挡部分内容
     * </pre>
     *
     * @param src  二维码图片
     * @param logo Logo
     * @return 含 Logo 二维码图片
     */
    public static Bitmap addLogoToQRCode(
            final Bitmap src,
            final Bitmap logo
    ) {
        if (src == null || logo == null) {
            return src;
        }
        // 获取图片宽度高度
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();
        int logoWidth = logo.getWidth();
        int logoHeight = logo.getHeight();
        // 缩放图片  这里的 4 决定 Logo 在图片的比例 四分之一
        float scaleFactor = srcWidth * 1.0f / 4 / logoWidth;
        Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(src, 0, 0, null);
            canvas.scale(scaleFactor, scaleFactor, srcWidth / 2, srcHeight / 2);
            canvas.drawBitmap(logo, (srcWidth - logoWidth) / 2, (srcHeight - logoHeight) / 2, null);
            canvas.save();
            canvas.restore();
        } catch (Exception e) {
            LogUtils.e("addLogoToQRCode error: " + e.getMessage());
            bitmap = null;
        }
        return bitmap;
    }

    /**
     * detail: 二维码扫描结果回调
     */
    public interface QRScanCallback {

        /**
         * 二维码扫描结果回调
         *
         * @param success 是否成功
         * @param result  识别数据 {@link Result}
         * @param e       异常信息
         */
        void onResult(
                boolean success,
                Result result,
                Exception e
        );
    }
}

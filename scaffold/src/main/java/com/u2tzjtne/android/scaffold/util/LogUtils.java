package com.u2tzjtne.android.scaffold.util;

import com.orhanobut.logger.Logger;

/**
 * @author u2tzjtne@gmail.com
 * @date 2020/04/27
 * @desc 系统log类 使用Logger封装实现
 */
public class LogUtils {

    /**
     * 以级别为 d 的形式输出LOG
     */
    public static void v(String msg) {
        Logger.v(msg);
    }

    /**
     * 以级别为 d 的形式输出LOG
     */
    public static void d(String msg) {
        Logger.d(msg);
    }

    /**
     * 以级别为 i 的形式输出LOG
     */
    public static void i(String msg) {
        Logger.i(msg);
    }

    /**
     * 以级别为 w 的形式输出LOG
     */
    public static void w(String msg) {
        Logger.w(msg);
    }

    /**
     * 以级别为 e 的形式输出LOG
     */
    public static void e(String msg) {
        Logger.e(msg);
    }

    /**
     * 以级别为 e 的形式输出LOG
     */
    public static void json(String msg) {
        Logger.json(msg);
    }
}

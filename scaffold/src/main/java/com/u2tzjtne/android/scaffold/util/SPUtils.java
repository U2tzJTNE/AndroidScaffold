package com.u2tzjtne.android.scaffold.util;

import com.tencent.mmkv.MMKV;

/**
 * @author u2tzjtne@gmail.com
 * @date 2017/10/9
 * @desc SharedPreferences工具类 使用mmkv实现
 */
public class SPUtils {

    private static final MMKV kv = MMKV.defaultMMKV();

    public static void putBoolean(String key, boolean value) {
        kv.encode(key, value);
    }

    public static boolean getBoolean(String key, boolean defValue) {
        return kv.decodeBool(key, defValue);
    }

    public static void putInt(String key, int value) {
        kv.encode(key, value);
    }

    public static int getInt(String key, int defValue) {
        return kv.decodeInt(key, defValue);
    }

    public static void putString(String key, String value) {
        kv.encode(key, value);
    }

    public static String getString(String key, String defValue) {
        return kv.decodeString(key, defValue);
    }
}

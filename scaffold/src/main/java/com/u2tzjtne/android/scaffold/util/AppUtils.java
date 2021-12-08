package com.u2tzjtne.android.scaffold.util;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.u2tzjtne.android.scaffold.base.BaseApp;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static android.content.Context.TELEPHONY_SERVICE;

import androidx.core.content.FileProvider;

/**
 * @author u2tzjtne@gmail.com
 * @date 2020/4/16
 */
public class AppUtils {
    /**
     * 重启应用
     */
    public static void restartApp(Context context) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setClassName(context.getPackageName(),
                getLauncherActivity(context, context.getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    /**
     * 获取启动页
     */
    public static String getLauncherActivity(Context context, String pkg) {
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setPackage(pkg);
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> info = pm.queryIntentActivities(intent, 0);
        return info.get(0).activityInfo.name;
    }

    /**
     * 生成设备唯一标识：IMEI、AndroidId、macAddress 三者拼接再 MD5
     */
    @SuppressLint("HardwareIds")
    public static String getDeviceId() {

        Context context = BaseApp.getContext();
        String imei = "";
        String androidId = "";
        String macAddress = "";
        if (AndPermission.hasPermissions(context, Permission.READ_PHONE_STATE, Permission.CALL_PHONE)) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
            if (telephonyManager != null) {
                imei = telephonyManager.getDeviceId();
            }
        }
        ContentResolver contentResolver = context.getContentResolver();
        if (contentResolver != null) {
            androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID);
        }
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (AndPermission.hasPermissions(context, Permission.ACCESS_FINE_LOCATION)) {
            if (wifiManager != null) {
                macAddress = wifiManager.getConnectionInfo().getMacAddress();
            }
        }
        StringBuilder longIdBuilder = new StringBuilder();
        if (imei != null) {
            longIdBuilder.append(imei);
        }
        if (androidId != null) {
            longIdBuilder.append(androidId);
        }
        if (macAddress != null) {
            longIdBuilder.append(macAddress);
        }
        return md5(longIdBuilder.toString());
    }

    /**
     * 将指定字符串md5
     */
    public static String md5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            StringBuilder result = new StringBuilder();
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result.append(temp);
            }
            return result.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取App版本号
     */
    public static int getVersionCode(Context context) {
        try {
            return context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 得到SD卡根目录
     */
    public static File getRootPath(Context context) {
        if (sdCardIsAvailable()) {
            // 取得sdcard文件路径
            return Environment.getExternalStorageDirectory();
        } else {
            return context.getFilesDir();
        }
    }

    /**
     * 判断SD卡是否可用
     */
    public static boolean sdCardIsAvailable() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File sd = new File(Environment.getExternalStorageDirectory().getPath());
            return sd.canWrite();
        } else {
            return false;
        }
    }

    /**
     * 判断指定app是否安装
     */
    public static boolean checkAppInstalled(Context context, String pkgName) {
        if (pkgName == null || pkgName.isEmpty()) {
            return false;
        }
        final PackageManager packageManager = context.getPackageManager();
        // 获取所有已安装程序的包信息
        List<PackageInfo> info = packageManager.getInstalledPackages(0);
        if (info == null || info.isEmpty()) {
            return false;
        }
        for (int i = 0; i < info.size(); i++) {
            if (pkgName.equals(info.get(i).packageName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 安装apk
     */
    public static void installApk(Context context, File file) {
        Intent intent = new Intent();
        // 添加这一句表示对目标应用临时授权该Uri所代表的文件
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        // 必须新任务栈
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            String processName = context.getApplicationInfo().processName;
            LogUtils.d("applicationId: " + processName);
            uri = FileProvider.getUriForFile(
                    context, processName + ".fileProvider", file);
        } else {
            // Android N-FC
            uri = Uri.fromFile(file);
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }
}

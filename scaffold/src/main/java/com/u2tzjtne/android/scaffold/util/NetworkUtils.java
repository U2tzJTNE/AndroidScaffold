package com.u2tzjtne.android.scaffold.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * @author u2tzjtne
 */
public class NetworkUtils {

    private final Context context;

    public NetworkUtils(Context context) {
        this.context = context;
    }

    public boolean isConnected() {

        try {
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {

                NetworkInfo info = connectivity.getActiveNetworkInfo();

                if (info != null && info.isConnected()) {
                    if (info.getState() == NetworkInfo.State.CONNECTED) {

                        return true;
                    }
                }
            }
        } catch (Exception e) {
            Log.e("error", e.toString());
        }
        return false;

    }

    public boolean isWifiConnected() {
        if (context != null) {
            ConnectivityManager manager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWifiNetworkInfo = null;
            if (manager != null) {
                mWifiNetworkInfo = manager
                        .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            }
            if (mWifiNetworkInfo != null) {
                return mWifiNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public boolean isMobileConnected() {
        if (context != null) {
            ConnectivityManager manager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mobileNetworkInfo = null;
            if (manager != null) {
                mobileNetworkInfo = manager
                        .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            }
            if (mobileNetworkInfo != null) {
                return mobileNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public int getConnectedType() {
        if (context != null) {
            ConnectivityManager manager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = null;
            if (manager != null) {
                mNetworkInfo = manager.getActiveNetworkInfo();
            }
            if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
                LogUtils.d("Connect Type = " + mNetworkInfo.getType());
                return mNetworkInfo.getType();
            }
        }
        return -1;
    }
}


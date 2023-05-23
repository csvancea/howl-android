package com.smd.cv.howl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkChangeBroadcastReceiver extends BroadcastReceiver {
    private final WiFiConnectCallback callback;
    private boolean isConnectedToWiFi;

    public static NetworkChangeBroadcastReceiver registerNewInstance(Context context, WiFiConnectCallback callback) {
        NetworkChangeBroadcastReceiver broadcastReceiver = new NetworkChangeBroadcastReceiver(context, callback);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(broadcastReceiver, filter);
        return broadcastReceiver;
    }

    public void unregister(Context context) {
        context.unregisterReceiver(this);
    }

    private NetworkChangeBroadcastReceiver(Context context, WiFiConnectCallback callback) {
        this.callback = callback;
        this.isConnectedToWiFi = isConnectedToWiFiNetwork(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean currentlyConnected = isConnectedToWiFiNetwork(context);

        if (currentlyConnected != isConnectedToWiFi) {
            if (currentlyConnected) {
                callback.onWiFiConnected();
            }

            isConnectedToWiFi = currentlyConnected;
        }
    }

    private boolean isConnectedToWiFiNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return (activeNetwork != null && activeNetwork.getType() == ConnectivityManager.TYPE_WIFI);
    }
}
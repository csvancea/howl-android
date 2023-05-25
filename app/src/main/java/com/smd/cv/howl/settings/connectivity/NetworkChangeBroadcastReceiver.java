package com.smd.cv.howl.settings.connectivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkChangeBroadcastReceiver extends BroadcastReceiver {
    private final Context context;
    private final NetworkConnectCallback callback;
    private boolean isConnected;

    public static NetworkChangeBroadcastReceiver registerNewInstance(Context context, NetworkConnectCallback callback) {
        NetworkChangeBroadcastReceiver broadcastReceiver = new NetworkChangeBroadcastReceiver(context, callback);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(broadcastReceiver, filter);
        return broadcastReceiver;
    }

    public void unregister() {
        context.unregisterReceiver(this);
    }

    private NetworkChangeBroadcastReceiver(Context context, NetworkConnectCallback callback) {
        this.context = context;
        this.callback = callback;
        this.isConnected = isConnected(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean currentlyConnected = isConnected(context);

        if (currentlyConnected != isConnected) {
            if (currentlyConnected) {
                callback.onNetworkConnected();
            }

            isConnected = currentlyConnected;
        }
    }

    private boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return (activeNetwork != null);
    }
}
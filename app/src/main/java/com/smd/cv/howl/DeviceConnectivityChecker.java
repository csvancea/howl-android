package com.smd.cv.howl;

import android.os.Handler;
import android.os.Looper;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

interface DeviceConnectivityCallback {
    void onDeviceUp();
    void onDeviceDown();
}

class DeviceConnectivityChecker {
    private static final String DEVICE_ECHO_URL = "http://192.168.4.1/api/v1/ping";
    private static final String DEVICE_ECHO_MSG = "howl-echo-msg";
    private static final Integer DELAY_BETWEEN_CHECKS = 5000; // in ms


    private final DeviceConnectivityCallback callback;
    private final Handler handler;
    private boolean isChecking;

    public DeviceConnectivityChecker(DeviceConnectivityCallback callback) {
        this.callback = callback;
        this.handler = new Handler(Looper.getMainLooper());
        this.isChecking = false;
    }

    public void startChecking() {
        if (isChecking) {
            return;
        }

        isChecking = true;
        new Thread(
            () -> {
                while (isChecking) {
                    boolean isUp = isDeviceUp();

                    handler.post(() -> {
                        if (isUp) {
                            callback.onDeviceUp();
                        } else {
                            callback.onDeviceDown();
                        }
                    });

                    try {
                        Thread.sleep(DELAY_BETWEEN_CHECKS);
                    } catch (InterruptedException ignored) {
                    }
                }
            }
        ).start();
    }

    public void stopChecking() {
        this.isChecking = false;
    }

    private boolean isDeviceUp() {
        try {
            byte[] postData       = DEVICE_ECHO_MSG.getBytes(StandardCharsets.UTF_8);
            int    postDataLength = postData.length;

            URL url = new URL(DEVICE_ECHO_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
            conn.setRequestProperty("Content-Type", "text/plain");
            conn.setRequestProperty("Accept", "text/plain");
            conn.setRequestProperty("Charset", "utf-8");
            conn.setDoOutput(true);
            conn.setUseCaches(false);

            try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                wr.write(postData);
            }

            // conn.connect();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return false;
            }

            byte[] readData = new byte[postDataLength];
            try (DataInputStream rd = new DataInputStream(conn.getInputStream())) {
                rd.readFully(readData);
            }

            return Arrays.equals(readData, postData);
        } catch (IOException ignored) {
        }
        return false;
    }
}

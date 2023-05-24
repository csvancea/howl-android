package com.smd.cv.howl.settings.api;

public class FireAlarmAccessPoint {
    public final String ssid;
    public final String rssi;
    public final boolean secure;


    public FireAlarmAccessPoint(String ssid, String rssi, boolean secure) {
        this.ssid = ssid;
        this.rssi = rssi;
        this.secure = secure;
    }
}

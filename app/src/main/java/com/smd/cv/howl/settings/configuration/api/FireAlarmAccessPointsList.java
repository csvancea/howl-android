package com.smd.cv.howl.settings.configuration.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FireAlarmAccessPointsList {
    static public class FireAlarmAccessPoint {
        public final String ssid;
        public final int rssi;
        public final boolean secure;

        public FireAlarmAccessPoint(String ssid, int rssi, boolean secure) {
            this.ssid = ssid;
            this.rssi = rssi;
            this.secure = secure;
        }
    }

    @SerializedName("access-points")
    public final List<FireAlarmAccessPoint> accessPoints;

    public FireAlarmAccessPointsList(List<FireAlarmAccessPoint> accessPoints) {
        this.accessPoints = accessPoints;
    }
}

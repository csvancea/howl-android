package com.smd.cv.howl.settings.api;

import com.google.gson.annotations.SerializedName;

import java.net.URL;

public class FireAlarmSettings {
    public static class WiFi {
        public final String ssid;
        public final String pass;

        public WiFi(String ssid, String pass) {
            this.ssid = ssid;
            this.pass = pass;
        }
    }

    public static class Server {
        public final URL url;
        public final String guid;

        @SerializedName("root_certificate")
        public final String rootCertificate;

        public Server(URL url, String guid, String rootCertificate) {
            this.url = url;
            this.guid = guid;
            this.rootCertificate = rootCertificate;
        }
    }

    public final WiFi wifi;
    public final Server server;

    public FireAlarmSettings(WiFi wifi, Server server) {
        this.wifi = wifi;
        this.server = server;
    }
}

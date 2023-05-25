package com.smd.cv.howl.api;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Measurement {
    public final int id;

    @SerializedName("gas_value")
    public final int gasValue;

    @SerializedName("gas_detected")
    public final boolean gasDetected;

    @SerializedName("flame_detected")
    public final boolean flameDetected;

    @SerializedName("created")
    public final Date createdAt;

    public Measurement(int id, int gasValue, boolean gasDetected, boolean flameDetected, Date createdAt) {
        this.id = id;
        this.gasValue = gasValue;
        this.gasDetected = gasDetected;
        this.flameDetected = flameDetected;
        this.createdAt = createdAt;
    }
}
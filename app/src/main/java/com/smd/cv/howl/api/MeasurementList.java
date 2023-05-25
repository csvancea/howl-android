package com.smd.cv.howl.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MeasurementList {
    @SerializedName("items")
    public final List<Measurement> measurements;

    @SerializedName("total_count")
    public final int totalNumberOfMeasurements;

    public MeasurementList(List<Measurement> measurements, int totalNumberOfMeasurements) {
        this.measurements = measurements;
        this.totalNumberOfMeasurements = totalNumberOfMeasurements;
    }
}

package com.smd.cv.howl.api;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MeasurementService {
    @GET("/api/v1/measurements")
    Call<MeasurementList> getLatestMeasurements(@Query("guid") String sensorGuid, @Query("page") int page, @Query("per_page") int perPage);

    @GET("/api/v1/measurements")
    Call<MeasurementList> getLatestMeasurements(@Query("guid") String sensorGuid, @Query("page") int page);

    @GET("/api/v1/measurements")
    Call<MeasurementList> getLatestMeasurements(@Query("guid") String sensorGuid);

    @GET("/api/v1/latest-detection")
    Call<Measurement> getLatestDetectionMeasurement(@Query("guid") String sensorGuid);

    static MeasurementService newInstance(String baseUrl) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(MeasurementService.class);
    }
}

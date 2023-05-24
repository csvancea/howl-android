package com.smd.cv.howl.settings.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface FireAlarmConfigurationService {
    @GET("/api/v1/access-points")
    Call<List<FireAlarmAccessPoint>> getAccessPoints();

    @GET("/api/v1/settings")
    Call<FireAlarmSettings> getSettings();

    @POST("/api/v1/fireAlarmSettings")
    void postSettings(@Body FireAlarmSettings fireAlarmSettings);

    @POST("/api/v1/switch-mode")
    void postSwitchMode();

    static FireAlarmConfigurationService newInstance() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.4.1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(FireAlarmConfigurationService.class);
    }
}

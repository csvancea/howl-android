package com.smd.cv.howl.settings.api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface FireAlarmConfigurationService {
    @GET("/api/v1/access-points")
    Call<FireAlarmAccessPointsList> getAccessPoints();

    @GET("/api/v1/settings")
    Call<FireAlarmSettings> getSettings();

    @POST("/api/v1/settings")
    Call<Void> postSettings(@Body FireAlarmSettings fireAlarmSettings);

    @POST("/api/v1/switch-mode")
    Call<Void> postSwitchMode();

    static FireAlarmConfigurationService newInstance() {
        // Needed for access-points. That operation is slow.
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.4.1/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(FireAlarmConfigurationService.class);
    }
}

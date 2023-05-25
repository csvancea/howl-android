package com.smd.cv.howl.tableview;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.smd.cv.howl.api.MeasurementList;
import com.smd.cv.howl.api.MeasurementService;

import retrofit2.Call;
import retrofit2.Response;

public class TableViewDataFetcher {
    private static final int PAGE = 1;
    public static final int PER_PAGE = 1000000;

    private final MeasurementService mMeasurementService;
    private final String mSensorGuid;

    public TableViewDataFetcher(MeasurementService measurementService, String sensorGuid) {
        mMeasurementService = measurementService;
        mSensorGuid = sensorGuid;
    }

    public void fetchMeasurements(Callback callback) {
        mMeasurementService.getLatestMeasurements(mSensorGuid, PAGE, PER_PAGE).enqueue(new retrofit2.Callback<MeasurementList>() {
            @Override
            public void onResponse(@NonNull Call<MeasurementList> call, @NonNull Response<MeasurementList> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    callback.onDataFetched(null);
                    return;
                }

                callback.onDataFetched(new TableViewModel(response.body().measurements));
            }

            @Override
            public void onFailure(@NonNull Call<MeasurementList> call, @NonNull Throwable t) {
                callback.onDataFetched(null);
            }
        });
    }

    public interface Callback {
        void onDataFetched(@Nullable TableViewModel tableViewModel);
    }
}

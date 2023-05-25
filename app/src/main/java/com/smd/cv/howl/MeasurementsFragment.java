package com.smd.cv.howl;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.smd.cv.howl.api.MeasurementList;
import com.smd.cv.howl.api.MeasurementService;
import com.smd.cv.howl.databinding.FragmentMeasurementsBinding;
import com.smd.cv.howl.settings.configuration.Preferences;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class MeasurementsFragment extends Fragment {
    private static final String TAG = MeasurementsFragment.class.getSimpleName();
    private FragmentMeasurementsBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentMeasurementsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String apiServer = Preferences.getApiServer(getContext());
        String sensorGuid = Preferences.getSensorGuid(getContext());
        MeasurementService service = MeasurementService.newInstance(apiServer);

        service.getLatestMeasurements(sensorGuid).enqueue(new Callback<MeasurementList>() {
            @Override @EverythingIsNonNull
            public void onResponse(Call<MeasurementList> call, Response<MeasurementList> response) {
                Log.i(TAG, "request succeeded");
                if (!response.isSuccessful() || response.body() == null) {
                    Log.i(TAG, "... but it was not successful??");
                }
            }

            @Override @EverythingIsNonNull
            public void onFailure(Call<MeasurementList> call, Throwable t) {
                Log.i(TAG, "request failed");
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
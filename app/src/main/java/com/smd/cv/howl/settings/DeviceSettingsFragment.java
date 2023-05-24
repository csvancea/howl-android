package com.smd.cv.howl.settings;

import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;

import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;

import com.smd.cv.howl.R;
import com.smd.cv.howl.settings.api.FireAlarmAccessPointsList;
import com.smd.cv.howl.settings.api.FireAlarmConfigurationService;
import com.smd.cv.howl.settings.api.FireAlarmSettings;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class DeviceSettingsFragment extends PreferenceFragmentCompat {
    private static final String TAG = DeviceSettingsFragment.class.getSimpleName();

    private FireAlarmConfigurationService deviceConfigurationService;
    private FireAlarmSettings deviceSettings;
    private FireAlarmAccessPointsList deviceAccessPointsList;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        EditTextPreference etpPassword = getPreferenceManager().findPreference("password");
        if (etpPassword != null) {
            etpPassword.setOnBindEditTextListener(editText -> {
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            });
        }

        deviceConfigurationService = FireAlarmConfigurationService.newInstance();
        requestDeviceConfiguration();
    }

    private void requestDeviceConfiguration() {
        deviceConfigurationService.getSettings().enqueue(new Callback<FireAlarmSettings>() {
            @Override @EverythingIsNonNull
            public void onResponse(Call<FireAlarmSettings> call, Response<FireAlarmSettings> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    reportFetchErrorAndGoBack();
                    return;
                }

                deviceSettings = response.body();
                requestDeviceAccessPoints();
            }

            @Override @EverythingIsNonNull
            public void onFailure(Call<FireAlarmSettings> call, Throwable t) {
                reportFetchErrorAndGoBack();
            }
        });
    }

    private void requestDeviceAccessPoints() {
        deviceConfigurationService.getAccessPoints().enqueue(new Callback<FireAlarmAccessPointsList>() {
            @Override @EverythingIsNonNull
            public void onResponse(Call<FireAlarmAccessPointsList> call, Response<FireAlarmAccessPointsList> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    reportFetchErrorAndGoBack();
                    return;
                }

                deviceAccessPointsList = response.body();
                populatePreferences();
            }

            @Override @EverythingIsNonNull
            public void onFailure(Call<FireAlarmAccessPointsList> call, Throwable t) {
                reportFetchErrorAndGoBack();
            }
        });
    }

    private void populatePreferences() {
        Log.i(TAG, "Populate preferences");
    }

    private void reportFetchErrorAndGoBack() {
        Log.e(TAG, "Fetch error");
    }
}
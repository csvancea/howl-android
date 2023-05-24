package com.smd.cv.howl.settings;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.widget.Toast;

import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.smd.cv.howl.R;
import com.smd.cv.howl.settings.api.FireAlarmAccessPointsList;
import com.smd.cv.howl.settings.api.FireAlarmConfigurationService;
import com.smd.cv.howl.settings.api.FireAlarmSettings;

import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class DeviceSettingsFragment extends PreferenceFragmentCompat implements SaveActionCallback {
    private static final String TAG = DeviceSettingsFragment.class.getSimpleName();

    private ListPreference prefSSID;
    private EditTextPreference prefPassword;
    private EditTextPreference prefApiEndpoint;
    private Preference prefGuid;
    private EditTextPreference prefRootCertificate;

    private ProgressDialog progressDialog;

    private FireAlarmConfigurationService deviceConfigurationService;
    private FireAlarmSettings deviceSettings;
    private FireAlarmAccessPointsList deviceAccessPointsList;

    boolean settingsFetched;


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        prefSSID = findPreference("ssid");
        prefPassword = findPreference("password");
        prefApiEndpoint = findPreference("api_endpoint");
        prefGuid = findPreference("sensor_guid");
        prefRootCertificate = findPreference("root_certificate");

        prefPassword.setOnBindEditTextListener(editText -> {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        });

        prefGuid.setOnPreferenceClickListener(preference -> {
            String sensorGuid = UUID.randomUUID().toString();

            prefGuid.setSummary(sensorGuid);
            prefGuid.setDefaultValue(sensorGuid);
            return true;
        });

        ((SettingsActivity)requireActivity()).registerSaveCallback(this);

        progressDialog = new ProgressDialog(this.requireActivity());
        progressDialog.setTitle(R.string.device_title_loading_settings);
        progressDialog.setMessage(getString(R.string.device_fetching_in_progress));
        progressDialog.setCancelable(false);

        settingsFetched = false;
        deviceConfigurationService = FireAlarmConfigurationService.newInstance();
        requestDeviceConfiguration();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        ((SettingsActivity)requireActivity()).registerSaveCallback(null);
    }

    private void requestDeviceConfiguration() {
        progressDialog.show();

        deviceConfigurationService.getSettings().enqueue(new Callback<FireAlarmSettings>() {
            @Override @EverythingIsNonNull
            public void onResponse(Call<FireAlarmSettings> call, Response<FireAlarmSettings> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    reportCommunicationError(true);
                    return;
                }

                deviceSettings = response.body();
                requestDeviceAccessPoints();
            }

            @Override @EverythingIsNonNull
            public void onFailure(Call<FireAlarmSettings> call, Throwable t) {
                reportCommunicationError(true);
            }
        });
    }

    private void requestDeviceAccessPoints() {
        deviceConfigurationService.getAccessPoints().enqueue(new Callback<FireAlarmAccessPointsList>() {
            @Override @EverythingIsNonNull
            public void onResponse(Call<FireAlarmAccessPointsList> call, Response<FireAlarmAccessPointsList> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    reportCommunicationError(true);
                    return;
                }

                deviceAccessPointsList = response.body();
                populatePreferences();
            }

            @Override @EverythingIsNonNull
            public void onFailure(Call<FireAlarmAccessPointsList> call, Throwable t) {
                reportCommunicationError(true);
            }
        });
    }

    @Override
    public void onSave() {
        if (!settingsFetched) {
            Toast.makeText(requireActivity(), getString(R.string.device_fetching_in_progress), Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        if (prefSSID.getValue() == null || prefSSID.getValue().isEmpty()) {
            Toast.makeText(requireActivity(), getString(R.string.device_wifi_not_configured), Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        pushConfigurationAndSwitchToNormalMode();
    }

    private void populatePreferences() {
        Stream<String> networkStream = deviceAccessPointsList.accessPoints.stream().map((e) -> e.ssid);

        // If the device is configured to connect to a network, add that to the stream too
        if (!deviceSettings.wifi.ssid.isEmpty()) {
            networkStream = Stream.concat(Stream.of(deviceSettings.wifi.ssid), networkStream);
        }

        String[] availableNetworks = networkStream.distinct().toArray(String[]::new);
        prefSSID.setEntries(availableNetworks);
        prefSSID.setEntryValues(availableNetworks);
        if (!deviceSettings.wifi.ssid.isEmpty()) {
            prefSSID.setValue(deviceSettings.wifi.ssid);
        }
        prefPassword.setText(deviceSettings.wifi.pass);

        // If the device is not set-up (ie: server settings are empty), then keep the default values
        if (!deviceSettings.server.url.isEmpty()) {
            prefApiEndpoint.setText(deviceSettings.server.url);
        }
        if (!deviceSettings.server.rootCertificate.isEmpty()) {
            prefRootCertificate.setText(deviceSettings.server.rootCertificate);
        }

        String sensorGuid = deviceSettings.server.guid.isEmpty() ? UUID.randomUUID().toString() : deviceSettings.server.guid;
        prefGuid.setSummary(sensorGuid);
        prefGuid.setDefaultValue(sensorGuid);

        if (progressDialog != null) {
            progressDialog.dismiss();
        }

        settingsFetched = true;
    }

    private void pushConfigurationAndSwitchToNormalMode() {
        FireAlarmSettings settings = new FireAlarmSettings(
                new FireAlarmSettings.WiFi(prefSSID.getValue(), prefPassword.getText()),
                new FireAlarmSettings.Server(prefApiEndpoint.getText(), Objects.requireNonNull(prefGuid.getSummary()).toString(), prefRootCertificate.getText())
        );

        deviceConfigurationService.postSettings(settings).enqueue(new Callback<Void>() {
            @Override @EverythingIsNonNull
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    reportCommunicationError(false);
                    return;
                }

                switchDeviceToNormalMode();
            }

            @Override @EverythingIsNonNull
            public void onFailure(Call<Void> call, Throwable t) {
                reportCommunicationError(false);
            }
        });
    }

    private void switchDeviceToNormalMode() {
        deviceConfigurationService.postSwitchMode().enqueue(new Callback<Void>() {
            @Override @EverythingIsNonNull
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) {
                    reportCommunicationError(false);
                    return;
                }

                if (getActivity() != null) {
                    Toast.makeText(getActivity(), R.string.device_setup_finished, Toast.LENGTH_SHORT)
                                    .show();
                    getActivity().finish();
                }
            }

            @Override @EverythingIsNonNull
            public void onFailure(Call<Void> call, Throwable t) {
                reportCommunicationError(false);
            }
        });
    }

    private void reportCommunicationError(boolean goBack) {
        Log.e(TAG, "Comm error");

        if (getActivity() != null) {
            Toast.makeText(getActivity(), R.string.device_communication_failed, Toast.LENGTH_SHORT)
                    .show();

            if (goBack) {
                getActivity().finish();
            }
        }
    }
}
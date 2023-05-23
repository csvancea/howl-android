package com.smd.cv.howl;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;
import com.smd.cv.howl.databinding.FragmentDeviceConnectivityBinding;

public class DeviceConnectivityFragment extends Fragment implements DeviceConnectivityCallback {
    private static final String TAG = DeviceConnectivityFragment.class.getSimpleName();
    private FragmentDeviceConnectivityBinding binding;
    private DeviceConnectivityChecker deviceConnChecker;

    public static DeviceConnectivityFragment newInstance() {
        return new DeviceConnectivityFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentDeviceConnectivityBinding.inflate(inflater, container, false);
        deviceConnChecker = new DeviceConnectivityChecker(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.networkSettingsButton.setOnClickListener(
                btnView -> startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS))
        );

        enterScanningMode();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        deviceConnChecker = null;
    }

    private void enterScanningMode() {
        deviceConnChecker.start();

        binding.deviceScanningView.setText(R.string.device_scanning_in_progress);
        binding.networkSettingsButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onDeviceScanEnd(boolean isDeviceConnected) {
        if (isDeviceConnected) {
            // TODO: request WiFi and go to the next fragment
        } else {
            binding.deviceScanningView.setText(R.string.device_scanning_failed);
            binding.networkSettingsButton.setVisibility(View.VISIBLE);
        }

        Snackbar.make(binding.getRoot(), "Device is " + ((isDeviceConnected) ? "up" : "down"), Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
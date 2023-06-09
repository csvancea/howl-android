package com.smd.cv.howl.settings.connectivity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;
import com.smd.cv.howl.R;
import com.smd.cv.howl.databinding.FragmentDeviceConnectivityBinding;

public class DeviceConnectivityFragment extends Fragment implements DeviceConnectivityCallback, NetworkConnectCallback {
    private static final String TAG = DeviceConnectivityFragment.class.getSimpleName();
    private FragmentDeviceConnectivityBinding binding;
    private DeviceConnectivityChecker deviceConnChecker;
    private NetworkChangeBroadcastReceiver networkChangeBroadcastReceiver;

    private boolean isInDeviceScanningMode;

    public static DeviceConnectivityFragment newInstance() {
        return new DeviceConnectivityFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        deviceConnChecker = new DeviceConnectivityChecker(this);
        networkChangeBroadcastReceiver = NetworkChangeBroadcastReceiver.registerNewInstance(this.requireActivity(), this);
        isInDeviceScanningMode = false;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentDeviceConnectivityBinding.inflate(inflater, container, false);
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
        isInDeviceScanningMode = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        networkChangeBroadcastReceiver.unregister();
        networkChangeBroadcastReceiver = null;
        deviceConnChecker = null;
    }

    private void enterScanningMode() {
        deviceConnChecker.start();

        binding.deviceScanningView.setText(R.string.device_scanning_in_progress);
        binding.networkSettingsButton.setVisibility(View.INVISIBLE);

        isInDeviceScanningMode = true;
    }

    @Override
    public void onDeviceScanEnd(boolean isDeviceConnected) {
        if (isDeviceConnected) {
            NavHostFragment.findNavController(DeviceConnectivityFragment.this)
                    .navigate(R.id.action_deviceConnectivityFragment_to_deviceSettingsFragment);
        } else {
            binding.deviceScanningView.setText(R.string.device_communication_failed);
            binding.networkSettingsButton.setVisibility(View.VISIBLE);
        }

        isInDeviceScanningMode = false;

        Snackbar.make(binding.getRoot(), "Device is " + ((isDeviceConnected) ? "up" : "down"), Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Override
    public void onNetworkConnected() {
        if (!isInDeviceScanningMode) {
            enterScanningMode();
        }

        Snackbar.make(binding.getRoot(), "Connected to a Wi-Fi network", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
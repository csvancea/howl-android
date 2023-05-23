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

import com.smd.cv.howl.databinding.FragmentDeviceConnectivityBinding;

public class DeviceConnectivityFragment extends Fragment {
    private FragmentDeviceConnectivityBinding binding;

    public static DeviceConnectivityFragment newInstance() {
        return new DeviceConnectivityFragment();
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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
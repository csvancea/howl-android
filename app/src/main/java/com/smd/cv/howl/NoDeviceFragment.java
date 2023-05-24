package com.smd.cv.howl;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.smd.cv.howl.databinding.FragmentNoDeviceBinding;

public class NoDeviceFragment extends Fragment {

    private FragmentNoDeviceBinding binding;
    private final SettingsInvoker settingsInvoker;

    public static NoDeviceFragment newInstance(SettingsInvoker settingsInvoker) {
        return new NoDeviceFragment(settingsInvoker);
    }

    private NoDeviceFragment(SettingsInvoker settingsInvoker) {
        this.settingsInvoker = settingsInvoker;
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentNoDeviceBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonSettings.setOnClickListener(view1 -> settingsInvoker.invokeSettings());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
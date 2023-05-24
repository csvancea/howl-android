package com.smd.cv.howl.settings;

import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;

import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;

import com.smd.cv.howl.R;

public class DeviceSettingsFragment extends PreferenceFragmentCompat {

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
    }
}
package com.smd.cv.howl.settings.configuration;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.StringRes;

import com.smd.cv.howl.R;
import com.smd.cv.howl.settings.configuration.api.FireAlarmSettings;

import java.net.MalformedURLException;
import java.net.URL;

public class Preferences {
    private Preferences() { }

    public static boolean isDeviceConfigured(Context context) {
        return !getSensorGuid(context).isEmpty();
    }

    public static String getApiServer(Context context) {
        return getStringSharedPreference(context, R.string.preference_api_server);
    }

    public static String getSensorGuid(Context context) {
        return getStringSharedPreference(context, R.string.preference_sensor_guid);
    }

    public static void update(Context context, FireAlarmSettings settings) {
        SharedPreferences sharedPref = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString(context.getString(R.string.preference_api_server), extractServerFromFullUrl(settings.server.url));
        editor.putString(context.getString(R.string.preference_sensor_guid), settings.server.guid);
        editor.apply();
    }

    public static void clear(Context context) {
        SharedPreferences sharedPref = getSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.remove(context.getString(R.string.preference_api_server));
        editor.remove(context.getString(R.string.preference_sensor_guid));
        editor.apply();
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
    }

    private static String getStringSharedPreference(Context context, @StringRes int key) {
        return getSharedPreferences(context).getString(context.getString(key), "");
    }

    private static String extractServerFromFullUrl(String fullUrl) {
        try {
            URL url = new URL(fullUrl);
            return url.getProtocol() + "://" + url.getAuthority();
        } catch (MalformedURLException ignored) {
            return "";
        }
    }
}

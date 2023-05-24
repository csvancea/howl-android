package com.smd.cv.howl;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.smd.cv.howl.databinding.ActivityMainBinding;
import com.smd.cv.howl.settings.SettingsActivity;
import com.smd.cv.howl.settings.configuration.Preferences;

import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements SettingsInvoker {
    private static final int REQUEST_SETTINGS = 1;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        if (savedInstanceState == null) {
            Fragment initialFragment = Preferences.isDeviceConfigured(this)
                    ? new SecondFragment()
                    : NoDeviceFragment.newInstance(this);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_frame, initialFragment)
                    .commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        binding = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            invokeSettings();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SETTINGS && resultCode == RESULT_OK) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_frame, new SecondFragment())
                    .commit();
        }
    }

    public void invokeSettings() {
        startActivityForResult(
                new Intent(this, SettingsActivity.class),
                REQUEST_SETTINGS
        );
    }
}
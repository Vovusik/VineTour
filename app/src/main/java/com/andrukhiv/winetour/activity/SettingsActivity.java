package com.andrukhiv.winetour.activity;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import com.andrukhiv.winetour.R;
import com.andrukhiv.winetour.fragment.SettingsFragment;


public class SettingsActivity extends BaseActivity {

    @Override
    public int getContentViewId() {
        return R.layout.settings_activity;
    }

    @Override
    public int getNavigationMenuItemId() {
        return R.id.navigation_settings;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       // setContentView(R.layout.settings_activity);

        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                   }

        if (savedInstanceState == null) {
            showFragment();
        }
    }

    private void showFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_layout, new SettingsFragment())
                .commit();
    }
}

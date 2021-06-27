package com.andrukhiv.winetour.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.andrukhiv.winetour.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public abstract class BaseActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    protected BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());

        animationStartActivity();

        navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        // Встановлюю анімацію екрана при переході на інші актівіті
        // при нажатии нижних элементов навигации экрана
        animationStartActivity();
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateNavigationBarState();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_home: {
                startActivity(new Intent(this, HomeMainActivity.class));
                break;
            }
            case R.id.navigation_maps: {
                startActivity(new Intent(this, MapsActivity.class));
                break;
            }
            case R.id.navigation_festival: {
                startActivity(new Intent(this, FestivalMainActivity.class));
                break;
            }
            case R.id.navigation_news: {
                startActivity(new Intent(this, NewsActivity.class));
                break;
            }
//            case R.id.navigation_shop: {
//                startActivity(new Intent(this, ShopActivity.class));
//                break;
//            }
            case R.id.navigation_settings: {
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            }
        }
        finish();
        return true;
    }

    private void updateNavigationBarState() {
        int actionId = getNavigationMenuItemId();
        selectBottomNavigationBarItem(actionId);
    }

    void selectBottomNavigationBarItem(int itemId) {
        MenuItem item = navigationView.getMenu().findItem(itemId);
        item.setChecked(true);
    }

    protected abstract int getContentViewId();

    public abstract int getNavigationMenuItemId();

    private void animationStartActivity() {
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}

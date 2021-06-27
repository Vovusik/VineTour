package com.andrukhiv.winetour.activity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.andrukhiv.winetour.service.GalleryApi;
import com.andrukhiv.winetour.model.GalleryModel.FlickrPhoto;
import com.andrukhiv.winetour.adapter.GalleryPagerAdapter;
import com.andrukhiv.winetour.R;
import com.andrukhiv.winetour.helper.ParallaxTransformer;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GalleryDetailPagerActivity extends AppCompatActivity {

    public ViewPager viewPager;
    GalleryPagerAdapter viewpagerAdapter;
    public int positionChosen; // Получить положение изображения кликнув на главной стороне
    private FlickrPhoto list;
    private TextView lblCount;
    Timer swipeTimer;
    TextView titleToolbar;
    int onStartCount = 0;
    private Object savedInstanceState;
    public SlidrConfig mConfig;
   // private Intent shareIntent;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_detail_activity);

        Intent intent = this.getIntent();
        positionChosen = intent.getIntExtra("position", 0);

        progressBar = findViewById(R.id.progressBar);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setSupportActionBar(findViewById(R.id.gallery_detail_toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.toolbar_ic_bottom_arrow);
        }

        titleToolbar = findViewById(R.id.textTitle);
        lblCount = findViewById(R.id.gallery_count);

        viewPager = findViewById(R.id.view_pager);
        viewPager.setPageTransformer(true, new ParallaxTransformer());

        getData();

        autoStartViewPager(); // працює NORM

        animationStartActivity();

        sliderConfig();
    }

    private void getData() {
        progressBar = new ProgressBar(this);
        progressBar = findViewById(R.id.progressBar);

        // RequestQueue: место для хранения запроса перед отправкой создаем RequestQueue по команде
        Call<FlickrPhoto> imageList = GalleryApi.getImageService().searchTitle();
        imageList.enqueue(new Callback<FlickrPhoto>() {

            @Override
            public void onResponse(@NonNull Call<FlickrPhoto> call, @NonNull Response<FlickrPhoto> response) {
                list = response.body();
                assert list != null;
                viewpagerAdapter = new GalleryPagerAdapter(GalleryDetailPagerActivity.this, list.getPhotos().getPhoto()); // прикрепить данные к адаптеру
                viewPager.setAdapter(viewpagerAdapter);
                viewPager.setCurrentItem(positionChosen, true);
                displayMetaInfo(positionChosen);

                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                        // Позвоните, когда начнется событие прокрутки, и перейдите на эту страницу
                    }

                    @Override
                    public void onPageSelected(int position) {
                        // называется, когда выбрана страница
                        displayMetaInfo(position);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                        // называется, когда меняется статус Scoll
                    }
                });
                viewpagerAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<FlickrPhoto> call, @NonNull Throwable throwable) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(GalleryDetailPagerActivity.this, R.string.toast_failure, Toast.LENGTH_SHORT).show();

//                Intent intent = new Intent(GalleryDetailPagerActivity.this, SplashScreenActivity.class);
//                startActivity(intent);
//                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            finish();
        }
        return (super.onOptionsItemSelected(item));
    }

    private void autoStartViewPager() {

        final Handler handler = new Handler();
        final Runnable Update = () -> {
            if (viewPager.getCurrentItem() < Objects.requireNonNull(viewPager.getAdapter()).getCount() - 1) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            } else {
                viewPager.setCurrentItem(0);
            }
        };

        swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 5000, 5000);
    }

    @SuppressLint("SetTextI18n")
    private void displayMetaInfo(int position) {
        int newPosition = position % list.getPhotos().getPhoto().size();

        lblCount.setText((newPosition + 1) + "/" + list.getPhotos().getPhoto().size());

//        Toast.makeText(Main2Activity.this,
//                "Картинка : " +
//                        list.getPhotos().getPhoto().get(position).getTitle(), Toast.LENGTH_LONG).show();

        titleToolbar.setText(list.getPhotos().getPhoto().get(position).getTitle());
        // Objects.requireNonNull(getSupportActionBar()).setTitle(list.getPhotos().getPhoto().get(newPosition).getTitle());
        //getSupportActionBar().setSubtitle(grapes.get(newPosition).getShortDescriptionFestival());
    }

    private void animationStartActivity() {
        onStartCount = 1;
        if (savedInstanceState == null) {  // 1st time
            this.overridePendingTransition(R.anim.slide_in_bottom,
                    R.anim.slide_in_bottom);
        } else { // уже создана, поэтому обратная анимация
            onStartCount = 2;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        //KitchenDetailsActivity.this.overridePendingTransition(R.anim.nothing,R.anim.nothing);
        overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_out_bottom);
    }

    private void sliderConfig() {
        mConfig = new SlidrConfig.Builder()
                .position(SlidrPosition.TOP)
                .sensitivity(0.5f)
                .build();
        Slidr.attach(this, mConfig);
    }
}

package com.andrukhiv.winetour.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;

import com.andrukhiv.winetour.model.GalleryModel.Photo;
import com.andrukhiv.winetour.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;


public class GalleryDetailSearchActivity extends AppCompatActivity {

    Photo photo;
    public SlidrConfig mConfig;
    ImageView searchImage;
    ImageButton imageButton;
    TextView titleSearchToolbar;
    public static final String GALLERY_ID = "galleryId";
    private Object savedInstanceState;
    int onStartCount = 0;
    private ShareActionProvider miShareAction;
    private Intent shareIntent;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_item_detail);

        photo = (Photo) getIntent().getSerializableExtra(GALLERY_ID);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setSupportActionBar(findViewById(R.id.gallery_search_toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.toolbar_ic_bottom_arrow);
        }

        progressBar = findViewById(R.id.progressBar);

        titleSearchToolbar = findViewById(R.id.titleSearchTitle);
        searchImage = findViewById(R.id.image_slide);

        titleSearchToolbar.setText(photo.getTitle());
        try {
            // Загрузите изображение асинхронно с удаленного URL, настройте общий доступ после завершения
            Glide.with(this)
                    .load(photo.createURL())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(searchImage);

        } catch (Exception ignored) {

//            Intent intent = new Intent(GalleryDetailSearchActivity.this, SplashScreenActivity.class);
//            startActivity(intent);
//            finish();

        }

        sliderConfig();

        animationStartActivity();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sliderConfig() {
        mConfig = new SlidrConfig.Builder()
                .position(SlidrPosition.TOP)
                .sensitivity(0.5f)
                .build();
        Slidr.attach(this, mConfig);
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
        overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_out_bottom);
    }
}

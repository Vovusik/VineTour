package com.andrukhiv.winetour.activity;

import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.andrukhiv.winetour.R;
import com.andrukhiv.winetour.adapter.MapsAdapterTrans;
import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.request.transition.ViewPropertyTransition;
import com.google.vr.sdk.widgets.pano.VrPanoramaEventListener;
import com.google.vr.sdk.widgets.pano.VrPanoramaView;


public class MapVirtualImage extends AppCompatActivity implements View.OnClickListener {

    private VrPanoramaView mVrPanoramaView;
    private VrPanoramaView.Options paNormalOptions;

    private Object savedInstanceState;
    int onStartCount = 0;
    private ProgressBar progressBar;
    private int index;
    public static final String INDEX = "INDEX";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_virtual_activity);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ImageButton imageButton = findViewById(R.id.map_virtual_back_button);
        imageButton.setOnClickListener(this);

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        index = bundle.getInt("INDEX", 0);

        animationStartActivity();

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(ProgressBar.VISIBLE);

        initVrPaNormalView();
    }

    // инициализировать картинку VR
    private void initVrPaNormalView() {
        mVrPanoramaView = findViewById(R.id.map_panorama_view);
        paNormalOptions = new VrPanoramaView.Options();

        paNormalOptions.inputType = VrPanoramaView.Options.TYPE_MONO;

        mVrPanoramaView.setFullscreenButtonEnabled(false); // скрыть кнопку полноэкранного режима
        mVrPanoramaView.setInfoButtonEnabled(false); // установить кнопку, чтобы скрыть самую левую информацию
        mVrPanoramaView.setStereoModeButtonEnabled(false); // установить кнопку, чтобы скрыть трехмерную модель
        // Fixme блокування/розблокування автообертання
        mVrPanoramaView.setPureTouchTracking(false);

        // відразу відкриваю на повний екран, але не бачить інших кнопок
        //mVrPanoramaView.setDisplayMode(VrPanoramaView.DisplayMode.FULLSCREEN_MONO);
        //mVrPanoramaView.setEventListener();
        mVrPanoramaView.setEventListener(new ActivityEventListener()); //настроить мониторинг
        // загрузить локальный источник изображения
        //mVrPanoramaView.loadImageFromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.andes), paNormalOptions);

        Glide
                .with(this)
                .asBitmap()
                .load("https://" + MapsAdapterTrans.getVirtualPhoto(index))
                .placeholder(R.drawable.placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(GenericTransitionOptions.with(animationObject))
                .into(new CustomTarget<Bitmap>() {
                          @Override
                          public void onResourceReady(@NonNull Bitmap resource, Transition<? super Bitmap>
                                  transition) {
                              mVrPanoramaView.loadImageFromBitmap(resource, paNormalOptions);
                              progressBar.setVisibility(View.GONE);
                          }

                          @Override
                          public void onLoadCleared(@Nullable Drawable placeholder) {
                              progressBar.setVisibility(View.GONE);
                          }
                      }
                );
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.map_virtual_back_button) {
            onBackPressed();
            finish();
        }
    }

    private static class ActivityEventListener extends VrPanoramaEventListener {
        @Override
        public void onLoadSuccess() {// Картинка успешно загружена
            //progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onLoadError(String errorMessage) {// Не удалось загрузить изображение
        }

        @Override
        public void onClick() { // Super.onClick () срабатывает, когда мы нажимаем на VrPanoramaView

//            Context context = getApplicationContext();
//            CharSequence text = "Hello toast!";
//            int duration = Toast.LENGTH_SHORT;
//
//            Toast toast = Toast.makeText(context,
//                    text + MapsAdapterTrans.getVirtualPhoto(index),
//                    duration);
//            toast.show();
        }

        @Override
        public void onDisplayModeChanged(int newDisplayMode) {
            super.onDisplayModeChanged(newDisplayMode);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVrPanoramaView.resumeRendering();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVrPanoramaView.pauseRendering();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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

    private void animationStartActivity() {
        onStartCount = 1;
        if (savedInstanceState == null) {  // 1st time
            this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } else { // уже создана, поэтому обратная анимация
            onStartCount = 2;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    // Анімація завантаження картинки
    public ViewPropertyTransition.Animator animationObject = view -> {
        view.setAlpha(0f);

        ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        fadeAnim.setDuration(2500);
        fadeAnim.start();
    };
}
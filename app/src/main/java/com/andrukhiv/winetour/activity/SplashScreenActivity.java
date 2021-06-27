package com.andrukhiv.winetour.activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.andrukhiv.winetour.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.testing.FakeReviewManager;
import com.google.android.play.core.tasks.Task;


public class SplashScreenActivity extends AppCompatActivity implements InstallStateUpdatedListener {

    private long ms = 0;
    private final long splashTime = 1200;
    private final boolean splashActive = true;
    private final boolean paused = false;
    ConstraintLayout constraintLayout;
    private ReviewManager reviewManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        setStatusBarColor();

        constraintLayout = findViewById(R.id.cl);

        Thread thread = new Thread() {
            public void run() {
                try {
                    while (splashActive && ms < splashTime) {
                        if (!paused)
                            ms = ms + 100;
                        SystemClock.sleep(100);
                    }
                } catch (Exception ignored) {

                } finally {

                    if (!isOnline()) {
                        errorSnackbar();
                    } else {
                        goHomeMainActivity();
                    }
                }
            }
        };
        thread.start();

        // TODO не забути знати з теста
        //reviewManager = ReviewManagerFactory.create(this);
        reviewManager = new FakeReviewManager(this); // Протестируйте с помощью FakeReviewManager

        // Оцінка додатку
        showRateApp();
    }

    private void errorSnackbar() {
        Snackbar snackbar = Snackbar.make(constraintLayout, "Відсутній інтернет", Snackbar.LENGTH_INDEFINITE)
                .setAction("ПІД’ЄДНАТИ", v -> recreate());
        snackbar.setActionTextColor(ContextCompat.getColor(SplashScreenActivity.this, R.color.colorError));
        View snackbarLayout = snackbar.getView();
        snackbar.setBackgroundTint(Color.BLACK);
        TextView textView = snackbarLayout.findViewById(R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.laboratory_ic_error, 0, 0, 0);
        textView.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen.snackbar_icon_padding));
        snackbar.show();

//        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)
//                snackbar.getView().getLayoutParams();
//        params.setMargins(0, 0, 0, 0);
//        snackbar.getView().setLayoutParams(params);

    }

    private void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int color = ContextCompat.getColor(this, R.color.colorPrimary);
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }

    private void goHomeMainActivity() {
        Intent intent = new Intent(this, HomeMainActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    /**
     * Показывает нижний лист приложения для оценки с помощью API просмотра в приложении
     * Нижний лист может отображаться или не отображаться в зависимости от квот и ограничений
     * https://developer.android.com/guide/playcore/in-app-review#quotas
     * Мы показать диалоговое окно отката, если есть ошибка
     */
    public void showRateApp() {
        Task<ReviewInfo> request = reviewManager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Мы можем получить объект ReviewInfo
                ReviewInfo reviewInfo = task.getResult();

                Task<Void> flow = reviewManager.launchReviewFlow(this, reviewInfo);
                flow.addOnCompleteListener(task1 -> {
                    // Поток закончился. API не указывает, просматривал ли пользователь
                    // обзор или нет, и даже не отображается ли диалоговое окно обзора. Таким образом,
                    // независимо от результата, мы продолжаем работу над приложением.
                });
            } else {
                // Возникла какая-то проблема, продолжайте независимо от результата.
                // показывать диалог приложения нативной ставки при ошибке
                showRateAppFallbackDialog();
            }
        });
    }

    /**
     * Отображение собственного диалогового окна с тремя кнопками для просмотра приложения
     * Перенаправьте пользователя в PlayStore для просмотра приложения
     */
    private void showRateAppFallbackDialog() {
        new MaterialAlertDialogBuilder(this, R.style.NetworkAlertDialog)
                .setTitle(R.string.rate_app_title)
                .setMessage(R.string.rate_app_message)
                .setPositiveButton(R.string.rate_btn_pos, (dialog, which) -> redirectToPlayStore())
                .setNegativeButton(R.string.rate_btn_neg,
                        (dialog, which) -> {
                            // примите меры при нажатии не сейчас
                        })
                .setNeutralButton(R.string.rate_btn_nut,
                        (dialog, which) -> {
                            // принять меры при нажатии напомнить мне позже
                        })
                .setOnDismissListener(dialog -> {
                })
                .show();
    }

    // перенаправление пользователя в PlayStore
    public void redirectToPlayStore() {
        final String appPackageName = getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (ActivityNotFoundException exception) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    @Override
    public void onStateUpdate(InstallState state) {

    }
}

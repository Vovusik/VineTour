package com.andrukhiv.winetour.app;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import com.andrukhiv.winetour.R;
import com.andrukhiv.winetour.helper.ThemeHelper;


public class AppController extends Application {

    private static final String TAG = AppController.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();

        // Створюю власну тему для додатка
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        String themePref = sharedPreferences.getString("theme", ThemeHelper.DEFAULT_MODE);
        ThemeHelper.applyTheme(themePref);

        createChannel(); // Створюю канал для Notification


    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Создать канал для показа уведомлений.
            String channelId = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);

            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(new NotificationChannel(
                        channelId,
                        channelName,
                        NotificationManager.IMPORTANCE_LOW));
            }
        }
    }
}
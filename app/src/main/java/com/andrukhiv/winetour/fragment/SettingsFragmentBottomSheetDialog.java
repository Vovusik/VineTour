package com.andrukhiv.winetour.fragment;

import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.andrukhiv.winetour.R;
import com.andrukhiv.winetour.activity.HomeMainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.concurrent.Executor;


public class SettingsFragmentBottomSheetDialog extends BottomSheetDialogFragment {

    String mString;
    public static final String SETTINGS_KEY = "settings_key";
    String TAG = "SettingsFragmentBottomSheetDialog";

    public static SettingsFragmentBottomSheetDialog newInstance(String string) {
        SettingsFragmentBottomSheetDialog f = new SettingsFragmentBottomSheetDialog();
        Bundle args = new Bundle();
        args.putString("string", string);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(STYLE_NORMAL, R.style.SheetDialog);

        assert getArguments() != null;
        mString = getArguments().getString("string");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_dialog_bottom_sheet, container, false);
        TextView tv = view.findViewById(R.id.text);

        TextView textView = view.findViewById(R.id.dismiss);
        textView.setOnClickListener(v -> {
            SettingsFragment.myBottomSheet.dismiss();
        });

        // ініціалізувати віддалене налаштування
        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600)//3600
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);


//        HashMap<String, Object> firebaseDefaultMap = new HashMap<>();
//        firebaseDefaultMap.put(DESCRIPTION_KEY, expandableTextView);
//        mFirebaseRemoteConfig.setDefaultsAsync(firebaseDefaultMap);


        mFirebaseRemoteConfig.setDefaultsAsync (R.xml.remote_config_defaults);

        // потрібно отримати значення
        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()) {
                            boolean updated = task.getResult();
                            Log.e(TAG, "Config params updated: " + updated);
                        } else {
                            task.getException().printStackTrace();
                            Toast.makeText(getContext(), "Fetch failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        // використовувати наші ключі для URL
        //mFirebaseRemoteConfig.getString(DESCRIPTION_KEY);
       // tv.setText(mFirebaseRemoteConfig.getString(SETTINGS_KEY));

        Spanned htmlAsSpanned = Html.fromHtml(mFirebaseRemoteConfig.getString(SETTINGS_KEY)); // used by TextView
        tv.setText(htmlAsSpanned);

        return view;
    }
}

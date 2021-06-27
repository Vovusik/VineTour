package com.andrukhiv.winetour.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.andrukhiv.winetour.R;
import com.andrukhiv.winetour.adapter.MapsAdapterOdesa;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;


public class MapBottomSheetDialogFragmentOdesa extends BottomSheetDialogFragment
        implements AppBarLayout.OnOffsetChangedListener , View.OnClickListener, EasyPermissions.PermissionCallbacks {

    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION = 200;
    private boolean mIsTheTitleContainerVisible = true;
    private LinearLayoutCompat mTitleContainer;
    private FrameLayout mFrameParallax;
    private FrameLayout frameViewPager;
    private int position;
    private static final String TAG = "MapsFragmentZakarpattya";

    private static final int RC_CALL = 101;
    private static final int RC_LOCATION = 102;

    private static final String[] CALL = {Manifest.permission.CALL_PHONE};
    private static final String[] LOCATION = {Manifest.permission.ACCESS_FINE_LOCATION};


    static MapBottomSheetDialogFragmentOdesa newInstance(int i) {

        MapBottomSheetDialogFragmentOdesa fragment = new MapBottomSheetDialogFragmentOdesa();

        Bundle args = new Bundle();
        args.putInt("INDEX", i);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(STYLE_NORMAL, R.style.SheetDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.maps_dialog_bottom_sheet, container, false);

        Bundle args = getArguments();
        if (args != null) position = args.getInt("INDEX", 0);

        TextView bottomAddress = view.findViewById(R.id.bottom_address);
        TextView bottomCall = view.findViewById(R.id.bottom_call);
        TextView bottomWeb = view.findViewById(R.id.bottom_web);
        //TextView locationDescription = view.findViewById(R.id.location_description);

        bottomAddress.setText(MapsAdapterOdesa.getAddress(position));
        bottomCall.setText(MapsAdapterOdesa.getTextPhone(position));
        bottomWeb.setText(MapsAdapterOdesa.getTextWeb(position));
       // locationDescription.setText(MapsAdapterOdesa.getDescripion(position));

        ImageButton closeImageButton = view.findViewById(R.id.bottom_sheet_close);
        closeImageButton.setOnClickListener(v -> MapsFragmentOdesa.myBottomSheet.dismiss());

        mTitleContainer = view.findViewById(R.id.main_linearlayout_title);
        //TextView mTitle = view.findViewById(R.id.main_textview_title);
        AppBarLayout mAppBarLayout = view.findViewById(R.id.main_appbar);
        ImageView mImageparallax = view.findViewById(R.id.main_imageview_placeholder);
        mFrameParallax = view.findViewById(R.id.main_framelayout_title);
        frameViewPager = view.findViewById(R.id.frame_view_pager);
       // mTitle.setText(MapsAdapterOdesa.TITLES.get(index));

        mAppBarLayout.addOnOffsetChangedListener(this);
        initParallaxValues();

        Glide
                .with((getContext()))
                .load(MapsAdapterOdesa.getImage(position))
                .apply(new RequestOptions()
                                .transform(new BlurTransformation(1, 1))
                )
                .thumbnail(0.5f)
                .into(mImageparallax);

        Button mLink = view.findViewById(R.id.web);
        mLink.setOnClickListener(this);

        Button mDirections = view.findViewById(R.id.directions);
        mDirections.setOnClickListener(this);

        Button mCall = view.findViewById(R.id.call);
        mCall.setOnClickListener(this);

        return view;
    }

    // Чтобы начать использовать EasyPermissions, попросите Activity(или Fragment)
    // переопределить onRequestPermissionsResult
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // EasyPermissions обрабатывает результат запроса.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.web:
                Uri.parse("http://" + MapsAdapterOdesa.getTextWeb(position));
                openCustomTab();
                break;
            case R.id.call:
                callTask();
                break;
            case R.id.directions:
                locationTask();
                break;

        }
    }

    // Используется для проверки наличия у приложения необходимых разрешений.
    // Этот метод может принимать любое количество разрешений в качестве последнего аргумента.
    private boolean hasCallPermissions() {
        return EasyPermissions.hasPermissions((getContext()), CALL);
    }


    private boolean hasLocationPermissions() {
        return EasyPermissions.hasPermissions((getContext()), LOCATION);
    }

    // Использование AfterPermissionGrantedаннотации. Это необязательно,
    // но предусмотрено для удобства.
    // Если все разрешения в данном запросе предоставлены, будут выполнены все методы,
    // отмеченные соответствующим кодом запроса (убедитесь, что у вас есть уникальный код запроса).
    // Аннотированный метод должен быть пустым и без входных параметров (вместо этого вы можете
    // использовать onSaveInstanceState , чтобы сохранить состояние ваших подавленных параметров).
    // Это должно упростить общий поток необходимости запуска запрашивающего метода после того,
    // как все его разрешения были предоставлены. Это также может быть достигнуто путем добавления
    // логики onPermissionsGrantedобратного вызова.
    @AfterPermissionGranted(RC_CALL)
    private void callTask() {
        if (hasCallPermissions()) {
            Toast.makeText(getContext(), R.string.hasPermissions, Toast.LENGTH_SHORT).show();
            callPhone();
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_call), RC_CALL, CALL);
        }
    }

    @AfterPermissionGranted(RC_LOCATION)
    private void locationTask() {
        if (hasLocationPermissions()) {
            Toast.makeText(getContext(), R.string.hasPermissions, Toast.LENGTH_LONG).show();
            locationDirections();
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_location), RC_LOCATION, LOCATION);
        }
    }


    private void callPhone() {
        Uri call = Uri.parse("tel:" + MapsAdapterOdesa.getTextPhone(position));
        Intent callIntent = new Intent(Intent.ACTION_DIAL, call);
        if (ActivityCompat.checkSelfPermission((getContext()),
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(callIntent);
    }

    private void locationDirections() {
        Uri location = Uri.parse("google.navigation:q=" + MapsAdapterOdesa.getNavigationPosition(position));
        Intent locationIntent = new Intent(Intent.ACTION_VIEW, location);
        locationIntent.setPackage("com.google.android.apps.maps");
        if (ActivityCompat.checkSelfPermission((getContext()),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(locationIntent);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Log.d(TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size());
    }


    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());
        // (Необязательно) Проверьте, запретил ли пользователь какие-либо разрешения и
        // отметит «НИКОГДА НЕ СМОЖАТЬ СНОВА».
        // Это отобразит диалоговое окно, в котором они будут отображаться,
        // чтобы включить разрешение в настройках приложения.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }


    @SuppressLint("StringFormatMatches")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            String yes = getString(R.string.yes);
            String no = getString(R.string.no);

            // Do something after user returned from app settings screen, like showing a Toast.
            Toast.makeText(
                    getContext(),
                    getString(R.string.returned_from_app_settings_to_activity,
                            hasCallPermissions() ? yes : no,
                            hasLocationPermissions() ? yes : no),
                    Toast.LENGTH_LONG)
                    .show();
        }
    }


    private void openCustomTab() {

        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();

        // установить цвета панели инструментов
        builder.setToolbarColor(ContextCompat.getColor((getContext()), R.color.colorPrimary));
        builder.setSecondaryToolbarColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
        // поширити додаток
        builder.addDefaultShareMenuItem();
        // показувати заголовок
        builder.setShowTitle(true);
        // змінюю хрестик на стрілочку повернення додому
        //builder.setCloseButtonIcon(BitmapFactory.decodeResource(
        //getResources(), R.drawable.toolbar_ic_up_arrow));

        // настроить анимацию начала и выхода
        builder.setStartAnimations(getContext(), R.anim.slide_in_right, R.anim.slide_out_left);
        builder.setExitAnimations(getContext(), android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(getContext(), Uri.parse("http://" + MapsAdapterOdesa.getTextWeb(position)));
    }











    private void initParallaxValues() {

        CollapsingToolbarLayout.LayoutParams petDetailsLp =
                (CollapsingToolbarLayout.LayoutParams) frameViewPager.getLayoutParams();

        CollapsingToolbarLayout.LayoutParams petBackgroundLp =
                (CollapsingToolbarLayout.LayoutParams) mFrameParallax.getLayoutParams();

        petDetailsLp.setParallaxMultiplier(0.9f);
        petBackgroundLp.setParallaxMultiplier(0.3f);

        frameViewPager.setLayoutParams(petDetailsLp);
        mFrameParallax.setLayoutParams(petBackgroundLp);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {

        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        handleAlphaOnTitle(percentage);
    }

    private void handleAlphaOnTitle(float percentage) {

        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {

            if (mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitleContainer, View.INVISIBLE);
                mIsTheTitleContainerVisible = false;
            }

        } else {

            if (!mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitleContainer, View.VISIBLE);
                mIsTheTitleContainerVisible = true;
            }
        }
    }

    private static void startAlphaAnimation(View v, int visibility) {

        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(MapBottomSheetDialogFragmentOdesa.ALPHA_ANIMATIONS_DURATION);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }
}

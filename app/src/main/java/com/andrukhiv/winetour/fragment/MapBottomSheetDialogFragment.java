package com.andrukhiv.winetour.fragment;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.andrukhiv.winetour.activity.MapVirtualImage;
import com.andrukhiv.winetour.R;
import com.andrukhiv.winetour.model.WineCardModel;
import com.andrukhiv.winetour.adapter.MapsAdapterTrans;
import com.andrukhiv.winetour.adapter.WineCardRecyclerAdapter;
import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.ViewPropertyTransition;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

import at.blogc.android.views.ExpandableTextView;
import jp.wasabeef.glide.transformations.BlurTransformation;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static android.view.ViewGroup.*;


public class MapBottomSheetDialogFragment extends BottomSheetDialogFragment implements EasyPermissions.PermissionCallbacks, View.OnClickListener, AppBarLayout.OnOffsetChangedListener {

    private static final String TAG = "MapsFragmentZakarpattya";

    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.8f;
    private static final int ALPHA_ANIMATIONS_DURATION = 200;

    private boolean mIsTheTitleContainerVisible = true;
    private LinearLayout mTitleContainer;
    private FrameLayout mFrameParallax;
    private FrameLayout frameViewPager;
    private int index;
    private RecyclerView mRecyclerView;
    private ProgressBar progressBar;
    TextView bottomWeb;

    private static final int RC_CALL = 101;
    private static final int RC_LOCATION = 102;

    private static final String[] CALL = {Manifest.permission.CALL_PHONE};
    private static final String[] LOCATION = {Manifest.permission.ACCESS_FINE_LOCATION};

    static MapBottomSheetDialogFragment newInstance(int i) {

        MapBottomSheetDialogFragment fragment = new MapBottomSheetDialogFragment();

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
        if (args != null) index = args.getInt("INDEX", 0);

        AppBarLayout mAppBarLayout = view.findViewById(R.id.main_appbar);
        mAppBarLayout.addOnOffsetChangedListener(this);

        final ExpandableTextView expandableTextView = view.findViewById(R.id.expandable_desc_text);
        final Button buttonToggle = view.findViewById(R.id.button_toggle);
        expandableTextView.setAnimationDuration(500L);
        expandableTextView.setText(MapsAdapterTrans.getDescription(index));

        buttonToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                expandableTextView.toggle();
                buttonToggle.setText(expandableTextView.isExpanded() ? R.string.expand : R.string.collapse);
            }
        });

        Toolbar toolbar = view.findViewById(R.id.main_toolbar);
        ((AppCompatActivity) (getActivity())).setSupportActionBar(toolbar);
        toolbar.setTitle(MapsAdapterTrans.TITLES.get(index));
        setHasOptionsMenu(true);

        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(ProgressBar.VISIBLE);

        //reset the menu at top
//        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
//        actionBar.setHomeAsUpIndicator(R.drawable.toolbar_ic_bottom_arrow);

        TextView bottomAddress = view.findViewById(R.id.bottom_address);
        TextView bottomCall = view.findViewById(R.id.bottom_call);
        bottomWeb = view.findViewById(R.id.bottom_web);
//        TextView locationDescription = view.findViewById(R.id.location_description);

        bottomAddress.setText(MapsAdapterTrans.getAddress(index));
        bottomCall.setText(MapsAdapterTrans.getTextPhone(index));

        //locationDescription.setText(MapsAdapterTrans.getDescription(index));

        //  linearWineCard = view.findViewById(R.id.wine_card);

        ImageButton closeImageButton = view.findViewById(R.id.bottom_sheet_close);
        closeImageButton.setOnClickListener(v -> MapsFragmentTrans.myBottomSheet.dismiss());

        mTitleContainer = view.findViewById(R.id.main_linearlayout_title);
        TextView mTitle = view.findViewById(R.id.main_textview_title);
        ImageView mImageparallax = view.findViewById(R.id.main_imageview_placeholder);
        mFrameParallax = view.findViewById(R.id.main_framelayout_title);
        frameViewPager = view.findViewById(R.id.frame_view_pager);
        mTitle.setText(MapsAdapterTrans.TITLES.get(index));

        mRecyclerView = view.findViewById(R.id.recyclerview);

        startAlphaAnimation(mTitle, 0, View.VISIBLE);

        initParallaxValues();

        Glide
                .with((getContext()))
                .load("https://" + MapsAdapterTrans.getImage(index))
                .placeholder(R.drawable.placeholder)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .apply(new RequestOptions()
                        .transform(new BlurTransformation(1, 1))
                )
                .thumbnail(0.5f)
                .transition(GenericTransitionOptions.with(animationObject))
                .into(mImageparallax);

        Button mLink = view.findViewById(R.id.web);
        mLink.setOnClickListener(this);

        Button mDirections = view.findViewById(R.id.directions);
        mDirections.setOnClickListener(this);

        Button mCall = view.findViewById(R.id.call);
        mCall.setOnClickListener(this);

        initRecyclerWineCard();

        // при значенні getShowDetails(index) == false приховую інформацію винної карти
        if (MapsAdapterTrans.getShowDetails(index) == false) {
            LinearLayout layout = view.findViewById(R.id.wine_card);
            LayoutParams params = layout.getLayoutParams();
            params.height = 0;
            params.width = 0;
            layout.setLayoutParams(params);
        }

        // при відсутності сайту, заповняється поле "інформація про веб-сайт" і виводиться тост
        if (MapsAdapterTrans.getTextWeb(index) == null) {
            bottomWeb.setText("Інформація відсутня");
        } else {
            bottomWeb.setText(MapsAdapterTrans.getTextWeb(index));
        }

        return view;
    }

    private void initRecyclerWineCard() {
        List<WineCardModel> dabListItem = WineCardModel.getData();

        LinearLayoutManager manager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setHasFixedSize(true);

        WineCardRecyclerAdapter mAdapter = new WineCardRecyclerAdapter(getActivity(), dabListItem);
        mRecyclerView.setAdapter(mAdapter);
        // mRecyclerView.addItemDecoration(new FooterItemDecoration());
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
                startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleContainerVisible = false;
            }

        } else {

            if (!mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleContainerVisible = true;
            }
        }
    }

    public static void startAlphaAnimation(View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.maps, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.virtual_photo) {
            openVirtualImage();
        }
        return (super.onOptionsItemSelected(item));
    }

    private void openVirtualImage() {
        Intent intent;

        Context context = getContext();
        intent = new Intent(getContext(), MapVirtualImage.class);
        Bundle bundle = new Bundle();
        bundle.putInt(MapVirtualImage.INDEX, index);
        intent.putExtras(bundle);
        assert context != null;
        context.startActivity(intent);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        // при значенні ShowDetails true, або ж відсуності 3D фото приховую чи показую меню
        MenuItem someMenuItem = menu.findItem(R.id.virtual_photo);
        //MapsAdapterTrans.getShowDetails(index) == true &&
        if (MapsAdapterTrans.getVirtualPhoto(index) != null) {
            someMenuItem.setVisible(true);
        } else {
            someMenuItem.setVisible(false);
        }
    }

    public void onClick(View v) {
        Context context = getContext();
        int duration = Toast.LENGTH_LONG;
        Toast toast;

        switch (v.getId()) {
            case R.id.web:
                if (MapsAdapterTrans.getTextWeb(index) == null) {
                    toast = Toast.makeText(context,
                            "Інформація відсутня", duration);
                    toast.show();
                } else {
                    Uri.parse("https://" + MapsAdapterTrans.getTextWeb(index));
                    openCustomTab();
                }
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
        return EasyPermissions.hasPermissions(getContext(), LOCATION);
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
        Uri call = Uri.parse("tel:" + MapsAdapterTrans.getTextPhone(index));
        Intent callIntent = new Intent(Intent.ACTION_DIAL, call);
        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(callIntent);
    }

    private void locationDirections() {
        Uri location = Uri.parse("google.navigation:q=" + MapsAdapterTrans.getNavigationPosition(index));
        Intent locationIntent = new Intent(Intent.ACTION_VIEW, location);
        locationIntent.setPackage("com.google.android.apps.maps");
        if (ActivityCompat.checkSelfPermission(getContext(),
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
        builder.setToolbarColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        builder.setSecondaryToolbarColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
        // поширити додаток
        builder.addDefaultShareMenuItem();
        // показувати заголовок
        builder.setShowTitle(true);
        // змінюю хрестик на стрілочку повернення додому
        builder.setCloseButtonIcon(BitmapFactory.decodeResource(
                getResources(), R.drawable.toolbar_ic_up_arrow));

        // настроить анимацию начала и выхода
        builder.setStartAnimations(getContext(), R.anim.slide_in_right, R.anim.slide_out_left);
        builder.setExitAnimations(getContext(), android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(getContext(), Uri.parse("http://" + MapsAdapterTrans.getTextWeb(index)));
    }

    // Анімація завантаження картинки
    public ViewPropertyTransition.Animator animationObject = view -> {
        view.setAlpha(0f);

        ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        fadeAnim.setDuration(2500);
        fadeAnim.start();
    };
}

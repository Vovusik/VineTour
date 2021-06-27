package com.andrukhiv.winetour.activity;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;

import com.andrukhiv.winetour.R;
import com.andrukhiv.winetour.model.WineCardModel;
import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.ViewPropertyTransition;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;

import java.util.Objects;

import jp.wasabeef.glide.transformations.BlurTransformation;


public class WineCardDetailsActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

    public WineCardModel wineCardModel;
    public SlidrConfig mConfig;
    int onStartCount = 0;
    private Object savedInstanceState;
    private ImageView imageBottleDetails, imageBackgroundDetails, imageSale;
    public static final String EXTRA_ID = "ID";
    //
    private Toolbar toolbar;
    private AppBarLayout appBarLayout;
    private LinearLayout linearLayout;
    private TextView nameToolbar, priceSaleSDetails, priceCard, nameTitle;

    private Menu collapsedMenu;
    private boolean appBarExpanded = true;

    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.2f;
    private static final float PERCENTAGE_TO_SHOW_TITLE_TOOLBAR = 0.8f;
    private static final float PERCENTAGE_TO_HIDE_IMAGE_TOOLBAR = 0.8f;

    // тривалість анімацї елементів
    private static final int ALPHA_ANIMATIONS_DURATION = 200;

    private boolean toolbarContainerVisible = false;
    private boolean imageContainerVisible = true;
    private boolean layoutContainerVisible = true;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wine_card_detail_activity);

        wineCardModel = (WineCardModel) Objects.requireNonNull(getIntent().getExtras()).getSerializable("ID");

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // заборона видимості елементів при переході між фрагментами
        startAlphaAnimation(toolbar, 0, View.INVISIBLE);

        imageBottleDetails = findViewById(R.id.image_bottle_details);
        imageBackgroundDetails = findViewById(R.id.image_background_details);
        imageSale = findViewById(R.id.image_sale);
        linearLayout = findViewById(R.id.layout_title);
        nameTitle = findViewById(R.id.name_title);
        appBarLayout = findViewById(R.id.appbar);
        priceSaleSDetails = findViewById(R.id.price_sale_details);
        nameToolbar = findViewById(R.id.name_toolbar);
        priceCard = findViewById(R.id.price_card);

        nameToolbar.setText(wineCardModel.getName());
        nameTitle.setText(wineCardModel.getName());
        priceSaleSDetails.setText(wineCardModel.getPriceSale());
        priceCard.setText(wineCardModel.getPrice());

        Glide
                .with(this)
                .load("https://live.staticflickr.com/65535/" + wineCardModel.getImageBottle())
                .apply(new RequestOptions()
//                        .diskCacheStrategy(DiskCacheStrategy.ALL)
//                        .skipMemoryCache(true)
                                .transform(new BlurTransformation(1, 1))
                )
                .thumbnail(0.5f)
                //.signature(new ObjectKey(System.currentTimeMillis() / (10 * 60 * 1000)))
                .transition(GenericTransitionOptions.with(animationObject))
                .into(imageBottleDetails);


        Glide
                .with(this)
                .load("https://live.staticflickr.com/65535/" + wineCardModel.getBackgroundImage())
                .apply(new RequestOptions()
//                        .diskCacheStrategy(DiskCacheStrategy.ALL)
//                        .skipMemoryCache(true)
                                .transform(new BlurTransformation(1, 1))
                )
                .thumbnail(0.5f)
                //.signature(new ObjectKey(System.currentTimeMillis() / (10 * 60 * 1000)))
                .transition(GenericTransitionOptions.with(animationObject))
                .into(imageBackgroundDetails);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCustomTab();
            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.toolbar_ic_bottom_arrow);
        }

        appBarLayout.addOnOffsetChangedListener(this);

        sliderConfig();

        animationStartActivity();

        visibilityImageSale();
    }

    private void visibilityImageSale() {
        if (wineCardModel.getPriceSale() == null) {
            imageSale.setVisibility(View.INVISIBLE);
            priceCard.setTextColor(ContextCompat.getColor(this, R.color.textColorPrimary));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_wine_card, menu);
        collapsedMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();

                return true;
        }
        if (item.getTitle() == "Add") {
            openCustomTab();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (collapsedMenu != null
                && (!appBarExpanded || collapsedMenu.size() != 1)) {
            //collapsed
            collapsedMenu.add("Add")
                    .setIcon(R.drawable.navigation_shop)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        } else {
            //expanded
        }
        return super.onPrepareOptionsMenu(collapsedMenu);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        handleAlphaOnTitle(percentage);
        handleTitle(percentage);
        handleImageToolbar(percentage);
    }

    private void handleAlphaOnTitle(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_TOOLBAR) {
            if (toolbarContainerVisible) {
                startAlphaAnimation(toolbar, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                toolbarContainerVisible = false;
            }
        } else {
            if (!toolbarContainerVisible) {
                startAlphaAnimation(toolbar, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                toolbarContainerVisible = true;
            }
        }
    }

    private void handleTitle(float percentage) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if (layoutContainerVisible) {
                startAlphaAnimation(linearLayout, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                layoutContainerVisible = false;
            }
        } else {
            if (!layoutContainerVisible) {
                startAlphaAnimation(linearLayout, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                layoutContainerVisible = true;
            }
        }
    }

    private void handleImageToolbar(float percentage) {
        if (percentage >= PERCENTAGE_TO_HIDE_IMAGE_TOOLBAR) {
            if (imageContainerVisible) {
                startAlphaAnimation(imageBottleDetails, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                imageContainerVisible = false;
            }
        } else {
            if (!imageContainerVisible) {
                startAlphaAnimation(imageBottleDetails, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                imageContainerVisible = true;
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
            this.overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
        } else { // уже создана, поэтому обратная анимация
            onStartCount = 2;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
    }

    // Анімація завантаження картинки
    public ViewPropertyTransition.Animator animationObject = view -> {
        view.setAlpha(0f);

        ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        fadeAnim.setDuration(2500);
        fadeAnim.start();
    };


    private void openCustomTab() {

        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();

        // установить цвета панели инструментов
        builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        builder.setSecondaryToolbarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        // поширити додаток
        builder.addDefaultShareMenuItem();
        // показувати заголовок
        builder.setShowTitle(true);
        // змінюю хрестик на стрілочку повернення додому
        builder.setCloseButtonIcon(BitmapFactory.decodeResource(
                getResources(), R.drawable.toolbar_ic_bottom_arrow));

        // настроить анимацию начала и выхода
        builder.setStartAnimations(this, R.anim.slide_in_bottom, R.anim.slide_out_top);
        builder.setExitAnimations(this, R.anim.slide_in_top, R.anim.slide_out_bottom);

        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse("http://" + wineCardModel.getLinkShop()));
    }
}

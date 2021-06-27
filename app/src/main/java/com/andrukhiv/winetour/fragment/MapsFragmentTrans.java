package com.andrukhiv.winetour.fragment;


import android.animation.ObjectAnimator;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.andrukhiv.winetour.R;
import com.andrukhiv.winetour.adapter.MapsAdapterTrans;
import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.ViewPropertyTransition;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import jp.wasabeef.glide.transformations.BlurTransformation;


public class MapsFragmentTrans extends Fragment implements View.OnClickListener {

    private TextView mTitle;
    private ImageView mImage;
    private int index;
    private ProgressBar progressBar;

    static BottomSheetDialogFragment myBottomSheet;

    public static final String PLACEHOLDER_URI = "https://live.staticflickr.com/65535/50170538523_941ae74b51_c.jpg";

    public MapsFragmentTrans() {
    }

    public static MapsFragmentTrans newInstance(int i) {

        MapsFragmentTrans fragment = new MapsFragmentTrans();
        Bundle args = new Bundle();
        args.putInt("INDEX", i);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.map_pager_card, container, false);

        progressBar = rootView.findViewById(R.id.progressBar);
        progressBar.setVisibility(ProgressBar.VISIBLE);

        CardView mCardView = rootView.findViewById(R.id.map_pager_card);
        mCardView.setOnClickListener(this);

        mTitle = rootView.findViewById(R.id.map_title_card);
        mImage = rootView.findViewById(R.id.map_image_card);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) index = args.getInt("INDEX", 0);

        mTitle.setText(MapsAdapterTrans.TITLES.get(index));
        // mDescription.setText(MapsAdapterTrans.getDescripion(index));
        // mAddress.setText(MapsAdapterTrans.getAddress(index));

        Glide
                .with(getContext())
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
//                        .diskCacheStrategy(DiskCacheStrategy.ALL)
//                        .skipMemoryCache(true)
                                .transform(new BlurTransformation(1, 1))
                )
                .thumbnail(0.5f)
                //.signature(new ObjectKey(System.currentTimeMillis() / (10 * 60 * 1000)))
                .transition(GenericTransitionOptions.with(animationObject))
                .into(mImage);
    }

    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.map_pager_card:
                openBottomSheetDialog();

                getResources().getConfiguration();
                break;
        }
    }

    private void openBottomSheetDialog() {
        FragmentManager fm = getChildFragmentManager();
        myBottomSheet = MapBottomSheetDialogFragment.newInstance(index);
        myBottomSheet.show(fm, myBottomSheet.getTag());
    }

    // Анімація завантаження картинки
    public ViewPropertyTransition.Animator animationObject = view -> {
        view.setAlpha(0f);

        ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        fadeAnim.setDuration(2500);
        fadeAnim.start();
    };
}

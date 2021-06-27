package com.andrukhiv.winetour.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.andrukhiv.winetour.model.GalleryModel.Photo;
import com.andrukhiv.winetour.R;
import com.andrukhiv.winetour.activity.GalleryDetailSearchActivity;
import com.bumptech.glide.Glide;

import java.util.List;

public class HomePagerAdapter extends PagerAdapter {

    private List<Photo> homeModels;
    private LayoutInflater inflater;

    public HomePagerAdapter(Context context, List<Photo> photo) {
        this.homeModels = photo;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        int limitItems = 6;
        return Math.min(homeModels.size(), limitItems);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View view = inflater.inflate(R.layout.home_pager_card, container, false);

        ImageView imageCard = view.findViewById(R.id.image);
        TextView titleCard = view.findViewById(R.id.title);

        titleCard.setText(homeModels.get(position).getTitle());

        Glide
                .with(imageCard)
                .load(homeModels.get(position).createURL())
                .placeholder(R.drawable.placeholder)
                .into(imageCard);

        view.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //Toast.makeText(container.getContext(), homeModels.get(position).getTitle(), Toast.LENGTH_SHORT).show();

                Context context = container.getContext();
                Intent intent = new Intent(v.getContext(), GalleryDetailSearchActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(GalleryDetailSearchActivity.GALLERY_ID, homeModels.get(position));
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });


        container.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}

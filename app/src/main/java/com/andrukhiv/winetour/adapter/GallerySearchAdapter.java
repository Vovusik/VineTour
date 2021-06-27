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
import androidx.recyclerview.widget.RecyclerView;

import com.andrukhiv.winetour.model.GalleryModel.Photo;
import com.andrukhiv.winetour.R;
import com.andrukhiv.winetour.activity.GalleryDetailSearchActivity;
import com.bumptech.glide.Glide;

import java.util.List;


public class GallerySearchAdapter extends RecyclerView.Adapter<GallerySearchAdapter.ImageViewHolder> {

    private final Context context;
    private final List<Photo> mItemList;

    public GallerySearchAdapter(Context context, List<Photo> mItemList) {
        this.context = context;
        this.mItemList = mItemList;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.gallery_item_card, viewGroup, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageViewHolder holder, final int position) {

        holder.title.setText(mItemList.get(position).getTitle());
        Glide.with(context)
                .load(mItemList.get(position).createURL())
                .placeholder(R.drawable.placeholder)
                .into(holder.image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = holder.itemView.getContext();
                Intent intent = new Intent(v.getContext(), GalleryDetailSearchActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(GalleryDetailSearchActivity.GALLERY_ID, mItemList.get(position));
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        private final ImageView image;
        private final TextView title;

        ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.idImage);
        }
    }
}


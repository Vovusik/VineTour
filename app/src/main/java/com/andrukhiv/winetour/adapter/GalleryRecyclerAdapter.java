package com.andrukhiv.winetour.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.andrukhiv.winetour.model.GalleryModel.Photo;
import com.andrukhiv.winetour.R;
import com.andrukhiv.winetour.activity.GalleryDetailPagerActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;

import java.util.ArrayList;
import java.util.List;


import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;


public class GalleryRecyclerAdapter extends RecyclerView.Adapter<GalleryRecyclerAdapter.ImageViewHolder> {

    private Context context;
    private List<Photo> mItemList;

    public GalleryRecyclerAdapter(Context context, List<Photo> mItemList) {
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
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            holder.image.setTransitionName(
//                    holder.image.getContext().getString(R.string.transition_name, position)); // в качестве примера ресурсу R.string.transition_name задано значение name%1$d

        DrawableCrossFadeFactory factory =
                new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build();
        holder.title.setText(mItemList.get(position).getTitle());

        Glide.with(context)
                .load(mItemList.get(position).createURL())
                .placeholder(R.drawable.placeholder)
                .transition(withCrossFade(factory))
                .into(holder.image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), GalleryDetailPagerActivity.class);
                intent.putExtra("position", position);
                context.startActivity(intent);

//                Toast.makeText(context,
//                        "Картинка : " +
//                                mItemList.get(position).getTitle(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView title;

        ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.idImage);
        }
    }

    public void clear() {
        mItemList.clear();
        notifyDataSetChanged();
    }

    // Добавить список предметов - изменить на используемый тип
    public void addAll(List<Photo> list) {
        mItemList.addAll(list);
        notifyDataSetChanged();
    }

    public void setItems(List<Photo> datas) {
        mItemList = new ArrayList<>(datas);
        notifyDataSetChanged();
    }
}


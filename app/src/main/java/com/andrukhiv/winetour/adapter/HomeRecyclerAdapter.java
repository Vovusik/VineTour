package com.andrukhiv.winetour.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.andrukhiv.winetour.R;
import com.andrukhiv.winetour.model.VideoModel;
import com.andrukhiv.winetour.activity.VideoDetailsActivity;
import com.bumptech.glide.Glide;

import java.util.ArrayList;


public class HomeRecyclerAdapter extends RecyclerView.Adapter<HomeRecyclerAdapter.YoutubePostHolder> {

    private final LayoutInflater mInflater;
    private ArrayList<VideoModel> dataSet;
    private int Previusposition = 0;
    private final int limitItems = 5;

    public static class YoutubePostHolder extends RecyclerView.ViewHolder {
        CardView cardView;

        public YoutubePostHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.item_card);
        }
    }

    public HomeRecyclerAdapter(Context mContext, ArrayList<VideoModel> dataSet) {
        this.dataSet = dataSet;
        this.mInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public YoutubePostHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = mInflater.inflate(R.layout.home_recycler_card, parent, false);

        return new YoutubePostHolder(view);
    }

    @Override
    public void onBindViewHolder(final YoutubePostHolder holder, final int position) {
        final VideoModel object = dataSet.get(position);
        CardView cardView = holder.cardView;

        TextView textViewTitle = cardView.findViewById(R.id.textViewTitle);
        ImageView ImageThumb = cardView.findViewById(R.id.ImageThumb);

        textViewTitle.setText(object.getTitle());
        Glide.with(holder.itemView.getContext())
                .load(object.getThumbnail())
                .placeholder(R.drawable.placeholder)
                .into(ImageThumb);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = holder.itemView.getContext();
                Intent intent = new Intent(v.getContext(), VideoDetailsActivity.class);
                intent.putExtra(VideoModel.class.toString(), dataSet.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(dataSet.size() > limitItems){
            return limitItems;
        }
        else {
            return dataSet.size();
        }
    }
}

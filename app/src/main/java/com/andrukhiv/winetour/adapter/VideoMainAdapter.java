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
import com.andrukhiv.winetour.helper.VideoAnimationUtil;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;


public class VideoMainAdapter extends RecyclerView.Adapter<VideoMainAdapter.YoutubePostHolder> {

    private final LayoutInflater mInflater;
    private ArrayList<VideoModel> videoDataModels;
    private int Previusposition = 0;

    public static class YoutubePostHolder extends RecyclerView.ViewHolder {
        CardView cardView;

        public YoutubePostHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.item_card);
        }
    }

    public VideoMainAdapter(Context mContext, ArrayList<VideoModel> videoDataModels) {
        this.videoDataModels = videoDataModels;
        this.mInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public YoutubePostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = mInflater.inflate(R.layout.video_item_card, parent, false);

        return new YoutubePostHolder(view);
    }

    @Override
    public void onBindViewHolder(final YoutubePostHolder holder, final int position) {
        final VideoModel object = videoDataModels.get(position);
        CardView cardView = holder.cardView;

        TextView textViewTitle = cardView.findViewById(R.id.textViewTitle);
        ImageView ImageThumb = cardView.findViewById(R.id.ImageThumb);

        textViewTitle.setText(object.getTitle());
        Glide
                .with(holder.itemView.getContext())
                .load(object.getThumbnail())
                .placeholder(R.drawable.placeholder)
                .into(ImageThumb);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = holder.itemView.getContext();
                Intent intent = new Intent(v.getContext(), VideoDetailsActivity.class);
                intent.putExtra(VideoModel.class.toString(), videoDataModels.get(position));
                context.startActivity(intent);
            }
        });

        if(position > Previusposition)
        {
            VideoAnimationUtil.animate(holder,true);
        } else {
            VideoAnimationUtil.animate(holder,false);
        }
        Previusposition = position;
    }

    @Override
    public int getItemCount() {
        return videoDataModels.size();
    }

    public void setItems(List<VideoModel> datas){
        videoDataModels = new ArrayList<>(datas);
        notifyDataSetChanged();
    }
}

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
import com.andrukhiv.winetour.activity.FestivalDetailsActivity;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;


public class FestivalMainAdapter extends RecyclerView.Adapter<FestivalMainAdapter.YoutubePostHolder> {

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

    public FestivalMainAdapter(Context mContext, ArrayList<VideoModel> videoDataModels) {
        this.videoDataModels = videoDataModels;
        this.mInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public YoutubePostHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = mInflater.inflate(R.layout.video_item_card, parent, false);
        //View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item_card, parent, false);

        return new YoutubePostHolder(view);
    }

    @Override
    public void onBindViewHolder(final YoutubePostHolder postHolder, final int position) {
        final VideoModel object = videoDataModels.get(position);
        CardView cardView = postHolder.cardView;

        TextView textViewTitle = cardView.findViewById(R.id.textViewTitle);
        ImageView ImageThumb = cardView.findViewById(R.id.ImageThumb);

        textViewTitle.setText(object.getTitle());
        Glide.with(postHolder.itemView.getContext())
                .load(object.getThumbnail())
                .placeholder(R.drawable.placeholder)
                .into(ImageThumb);

        postHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = postHolder.itemView.getContext();
                Intent intent = new Intent(v.getContext(), FestivalDetailsActivity.class);
                intent.putExtra(VideoModel.class.toString(), videoDataModels.get(position));
                context.startActivity(intent);
            }
        });

//        if(position > Previusposition)
//        {
//            VideoAnimationUtil.animate(postHolder,true);
//        }else {
//            VideoAnimationUtil.animate(postHolder,false);
//        }
//        Previusposition = position;
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

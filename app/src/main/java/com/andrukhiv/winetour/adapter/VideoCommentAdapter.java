package com.andrukhiv.winetour.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.andrukhiv.winetour.R;
import com.andrukhiv.winetour.model.VideoCommentModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;


public class VideoCommentAdapter extends RecyclerView.Adapter<VideoCommentAdapter.YoutubeCommentHolder> {

    private ArrayList<VideoCommentModel> dataSet;
    private static Context mContext = null;

    public VideoCommentAdapter(Context mContext, ArrayList<VideoCommentModel> data) {
        this.dataSet = data;
        this.mContext = mContext;
    }

    @Override
    public YoutubeCommentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.video_comment_layout, parent, false);
        YoutubeCommentHolder postHolder = new YoutubeCommentHolder(view);
        return postHolder;
    }

    @Override
    public void onBindViewHolder(YoutubeCommentHolder holder, int position) {
        TextView textViewName = holder.textViewName;
        TextView feedback = holder.feedback;
        ImageView imageView = holder.imageViewIcon;
        VideoCommentModel object = dataSet.get(position);
        textViewName.setText(object.getTitle());
        feedback.setText(object.getComment());
        try {
            if (object.getThumbnail() != null) {
                if (object.getThumbnail().startsWith("http")) {
//                    Picasso
//                            .get()
//                            .load(object.getThumbnail())
//                            .into(imageView);

                    Glide
                            .with(imageView.getContext())
                            .load(object.getThumbnail())
                            .apply(RequestOptions.circleCropTransform())
                            .into(imageView);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class YoutubeCommentHolder extends RecyclerView.ViewHolder {

        TextView textViewName;
        TextView feedback;
        ImageView imageViewIcon;

        public YoutubeCommentHolder(View itemView) {
            super(itemView);
            this.textViewName = itemView.findViewById(R.id.textViewName);
            this.imageViewIcon = itemView.findViewById(R.id.profile_image);
            this.feedback = itemView.findViewById(R.id.feedback);
        }
    }
}



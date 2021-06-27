package com.andrukhiv.winetour.model;

import android.os.Parcel;
import android.os.Parcelable;

public class VideoCommentModel implements Parcelable {

    private String title = "";
    private String comment = "";
    private String publishedAt = "";
    private String thumbnail = "";
    private String video_id = "";

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getVideo_id() {
        return video_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(comment);
        dest.writeString(publishedAt);
        dest.writeString(thumbnail);
        dest.writeString(video_id);
    }

    public VideoCommentModel() {
        super();
    }

    protected VideoCommentModel(Parcel in) {
        this();
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        this.title = in.readString();
        this.comment = in.readString();
        this.publishedAt = in.readString();
        this.thumbnail = in.readString();
        this.video_id = in.readString();
    }

    public static final Creator<VideoCommentModel> CREATOR = new Creator<VideoCommentModel>() {
        @Override
        public VideoCommentModel createFromParcel(Parcel in) {
            return new VideoCommentModel(in);
        }

        @Override
        public VideoCommentModel[] newArray(int size) {
            return new VideoCommentModel[size];
        }
    };
}

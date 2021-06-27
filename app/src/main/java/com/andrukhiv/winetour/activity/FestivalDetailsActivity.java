package com.andrukhiv.winetour.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.andrukhiv.winetour.constant.Constants;
import com.andrukhiv.winetour.FestivalNavIconClickListener;
import com.andrukhiv.winetour.R;
import com.andrukhiv.winetour.adapter.VideoCommentAdapter;
import com.andrukhiv.winetour.model.VideoCommentModel;
import com.andrukhiv.winetour.model.VideoModel;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;


public class FestivalDetailsActivity extends AppCompatActivity
        implements YouTubePlayer.OnInitializedListener, View.OnClickListener {

    public static VideoModel youtubeDataModel = null;
    TextView textViewName, textViewDes;
    int onStartCount = 0;
    public SlidrConfig mConfig;
    private Object savedInstanceState;
    private RecyclerView mList_videos = null;
    private Toolbar toolbar;
    public Button buttonGalleryImage, buttonGalleryVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.festival_details_activity);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        youtubeDataModel = getIntent().getParcelableExtra(VideoModel.class.toString());

        toolbar = findViewById(R.id.toolbar);

        buttonGalleryImage = findViewById(R.id.btn_gallery_image);
        buttonGalleryVideo = findViewById(R.id.btn_gallery_video);

        YouTubePlayerFragment mYoutubePlayerView = (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.youtube_player);
        mYoutubePlayerView.initialize(Constants.GOOGLE_YOUTUBE_API_KEY, this);

        textViewName = findViewById(R.id.textViewTitle);
        textViewDes = findViewById(R.id.textViewDes);

        textViewName.setText(youtubeDataModel.getTitle());
        textViewDes.setText(youtubeDataModel.getDescription());

        mList_videos = findViewById(R.id.mList_videos);
        new RequestYoutubeCommentAPI().execute();

        buttonGalleryImage.setOnClickListener(this);
        buttonGalleryVideo.setOnClickListener(this);

        sliderConfig();

        animationStartActivity();

        setUpToolbar();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setUpToolbar() {
        toolbar.setNavigationOnClickListener(new FestivalNavIconClickListener(
                this,
                findViewById(R.id.product_grid),
                new AccelerateDecelerateInterpolator(),
                this.getResources().getDrawable(R.drawable.festival_hamburger_detail), // Menu open icon
                this.getResources().getDrawable(R.drawable.festival_hamburger_menu))); // Menu close icon

        // Set cut corner background for API 23+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            findViewById(R.id.product_grid)
                    .setBackgroundResource(R.drawable.festival_card_component);
        }

    }

    private void sliderConfig() {
        mConfig = new SlidrConfig.Builder()
                .position(SlidrPosition.TOP)
                .sensitivity(0.5f)
                .build();
        Slidr.attach(this, mConfig);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        youTubePlayer.setPlayerStateChangeListener(playerStateChangeListener);
        youTubePlayer.setPlaybackEventListener(playbackEventListener);
        youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
        youTubePlayer.setShowFullscreenButton(false); // заблокував кнопку FullScreen

        if (!wasRestored) {
            youTubePlayer.cueVideo(youtubeDataModel.getVideo_id());
            //player.loadVideo(VIDEO_ID); // відео відразу програється
        }
    }

    private YouTubePlayer.PlaybackEventListener playbackEventListener = new YouTubePlayer.PlaybackEventListener() {
        @Override
        public void onPlaying() {
        }

        @Override
        public void onPaused() {
        }

        @Override
        public void onStopped() {
        }

        @Override
        public void onBuffering(boolean b) {
        }

        @Override
        public void onSeekTo(int i) {
        }
    };

    private YouTubePlayer.PlayerStateChangeListener playerStateChangeListener = new YouTubePlayer.PlayerStateChangeListener() {
        @Override
        public void onLoading() {
        }

        @Override
        public void onLoaded(String s) {
        }

        @Override
        public void onAdStarted() {
        }

        @Override
        public void onVideoStarted() {
        }

        @Override
        public void onVideoEnded() {
        }

        @Override
        public void onError(YouTubePlayer.ErrorReason errorReason) {
        }
    };

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
    }

    private class RequestYoutubeCommentAPI extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            HttpClient httpClient = new DefaultHttpClient();
            String VIDEO_COMMENT_URL =
                    "https://www.googleapis.com/youtube/v3/commentThreads?part=snippet&maxResults=100&videoId=" +
                            youtubeDataModel.getVideo_id() + "&key=" + Constants.GOOGLE_YOUTUBE_API_KEY;
            HttpGet httpGet = new HttpGet(VIDEO_COMMENT_URL);
            Log.e("url: ", VIDEO_COMMENT_URL);
            try {
                HttpResponse response = httpClient.execute(httpGet);
                HttpEntity httpEntity = response.getEntity();
                String json = EntityUtils.toString(httpEntity);
                return json;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

//        @Override
//        protected void onPostExecute(String response) {
//            super.onPostExecute(response);
//            if (response != null) {
//                try {
//                    JSONObject jsonObject = new JSONObject(response);
//                    Log.e("response", jsonObject.toString());
//                    mListData = parseJson(jsonObject);
//                    initVideoList(mListData);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
    }

    public void initVideoList(ArrayList<VideoCommentModel> mListData) {
        mList_videos.setLayoutManager(new LinearLayoutManager(this));
        VideoCommentAdapter mAdapter = new VideoCommentAdapter(this, mListData);
        mList_videos.setAdapter(mAdapter);
    }

    public ArrayList<VideoCommentModel> parseJson(JSONObject jsonObject) {
        ArrayList<VideoCommentModel> mList = new ArrayList<>();

        if (jsonObject.has("items")) {
            try {
                JSONArray jsonArray = jsonObject.getJSONArray("items");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject json = jsonArray.getJSONObject(i);

                    VideoCommentModel youtubeObject = new VideoCommentModel();
                    JSONObject jsonTopLevelComment = json.getJSONObject("snippet").getJSONObject("topLevelComment");
                    JSONObject jsonSnippet = jsonTopLevelComment.getJSONObject("snippet");

                    String title = jsonSnippet.getString("authorDisplayName");
                    String thumbnail = jsonSnippet.getString("authorProfileImageUrl");
                    String comment = jsonSnippet.getString("textDisplay");

                    youtubeObject.setTitle(title);
                    youtubeObject.setComment(comment);
                    youtubeObject.setThumbnail(thumbnail);
                    mList.add(youtubeObject);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return mList;
    }

    @Override
    public void onClick(View v) {
        Intent intent;

        switch (v.getId()) {
            case R.id.btn_gallery_image:
                intent = new Intent(FestivalDetailsActivity.this, GalleryMainActivity.class);
                startActivity(intent);
                break;

            case R.id.btn_gallery_video:
                intent = new Intent(FestivalDetailsActivity.this, FestivalPlayListActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void animationStartActivity() {
        onStartCount = 1;
        if (savedInstanceState == null) {  // 1st time
            this.overridePendingTransition(R.anim.slide_in_bottom,
                    R.anim.slide_in_bottom);
        } else { // уже создана, поэтому обратная анимация
            onStartCount = 2;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        //overridePendingTransition(0, 0);
        overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_out_bottom);
    }

}

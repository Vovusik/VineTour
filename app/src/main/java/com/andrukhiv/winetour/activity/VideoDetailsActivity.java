package com.andrukhiv.winetour.activity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.andrukhiv.winetour.constant.Constants;
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

import at.blogc.android.views.ExpandableTextView;


public class VideoDetailsActivity extends AppCompatActivity
        implements YouTubePlayer.OnInitializedListener {

    public static VideoModel youtubeDataModel = null;

    TextView textViewName;
    int onStartCount = 0;
    public SlidrConfig mConfig;
    private Object savedInstanceState;
    private ArrayList<VideoCommentModel> mListData = new ArrayList<>();
    private VideoCommentAdapter mAdapter = null;
    private RecyclerView mList_videos = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_details_activity);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        youtubeDataModel = getIntent().getParcelableExtra(VideoModel.class.toString());
        //Log.e("", youtubeDataModel.getDescription());

        YouTubePlayerFragment mYoutubePlayerView = (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.youtube_player);

        mYoutubePlayerView.initialize(Constants.GOOGLE_YOUTUBE_API_KEY, this);

        final ExpandableTextView expandableTextView = this.findViewById(R.id.expandableTextView);
        final Button buttonToggle = this.findViewById(R.id.button_toggle);
        expandableTextView.setAnimationDuration(500L);
        expandableTextView.setText(youtubeDataModel.getDescription());

        buttonToggle.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void onClick(final View v) {
                buttonToggle.setText(expandableTextView.isExpanded() ? R.string.expand : R.string.collapse);
                expandableTextView.toggle();
            }
        });

        textViewName = findViewById(R.id.textViewName);
        textViewName.setText(youtubeDataModel.getTitle());

        mList_videos = findViewById(R.id.mList_videos);
        new RequestYoutubeCommentAPI().execute();

        sliderConfig();

        animationStartActivity();
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

    @SuppressLint("StaticFieldLeak")
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

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if (response != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.e("response", jsonObject.toString());
                    mListData = parseJson(jsonObject);
                    initVideoList(mListData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void initVideoList(ArrayList<VideoCommentModel> mListData) {
        mList_videos.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new VideoCommentAdapter(this, mListData);
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

    private void animationStartActivity() {
        onStartCount = 1;
        if (savedInstanceState == null) {  // 1st time
            overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
        } else { // уже создана, поэтому обратная анимация
            onStartCount = 2;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        //overridePendingTransition(0, 0);
        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
    }
}

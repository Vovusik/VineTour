package com.andrukhiv.winetour.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.andrukhiv.winetour.constant.Constants;
import com.andrukhiv.winetour.service.GalleryApi;
import com.andrukhiv.winetour.model.GalleryModel.FlickrPhoto;
import com.andrukhiv.winetour.adapter.HomePagerAdapter;
import com.andrukhiv.winetour.adapter.HomeRecyclerAdapter;
import com.andrukhiv.winetour.R;
import com.andrukhiv.winetour.model.VideoModel;
import com.andrukhiv.winetour.helper.HomePagerTransformer;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

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
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import at.blogc.android.views.ExpandableTextView;
import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeMainActivity extends BaseActivity implements View.OnClickListener {

    private ViewPager homeViewPager;
    HomePagerAdapter adapter;
    private CircleIndicator indicator;
    private ProgressBar progressBar;
    private FlickrPhoto list;
    //private Spinner mSpinner;
    //Article currentArticle;

    // Ключи удаленной настройки
    public static final String DESCRIPTION_KEY = "description_key";

    // public static final String PLAYLIST_ID = "PLrpFeCjRTp_QIehwevh5PzNPSRZTAHZjg"; //POV Wedge Playlist

    public static final String PLAYLIST_GET_URL =
            "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&playlistId=" +
                    Constants.PLAYLIST_ID +
                    "&maxResults=50&key=" +
                    Constants.GOOGLE_YOUTUBE_API_KEY +
                    "";

    private RecyclerView mList_videos;
    private HomeRecyclerAdapter recyclerAdapter;
    private ArrayList<VideoModel> mListData;

    String TAG = "HomeActivity";

    @Override
    public int getContentViewId() {
        return R.layout.home_activity_main;
    }

    @Override
    public int getNavigationMenuItemId() {
        return R.id.navigation_home;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //setContentView(R.layout.home_activity_main);

        //     homeModelArrayList = GalleryModel.getGrapesInternet();

        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.app_name);

        //mSpinner = findViewById(R.id.spinner);

        //   adapter = new HomePagerAdapter(homeModelArrayList, this);
        homeViewPager = findViewById(R.id.home_view_pager);
        // homeViewPager.setAdapter(adapter);
        homeViewPager.setPadding(50, 0, 50, 0);
        homeViewPager.setPageTransformer(false, new HomePagerTransformer());

        getData();

        final ExpandableTextView expandableTextView = this.findViewById(R.id.expandableTextView);
        final Button buttonToggle = this.findViewById(R.id.button_toggle);
        expandableTextView.setAnimationDuration(500L);
        //expandableTextView.setText(getString(R.string.fullscreen_tutorial));

        buttonToggle.setOnClickListener(v -> {
            buttonToggle.setText(expandableTextView.isExpanded() ? R.string.expand : R.string.collapse);
            expandableTextView.toggle();
        });

        TextView textMoreVideo = findViewById(R.id.click_more_video);
        TextView textMorePhoto = findViewById(R.id.click_more_photo);

        textMoreVideo.setOnClickListener(this);
        textMorePhoto.setOnClickListener(this);

        indicator = findViewById(R.id.indicator);

        autoStartViewPager();

        mListData = new ArrayList<>();
        recyclerAdapter = new HomeRecyclerAdapter(this, mListData);
        mList_videos = findViewById(R.id.mList_videos);

        new RequestYoutubeAPI().execute();

        initList(mListData);

        //spinnerRegionToolbar();


        // ініціалізувати віддалене налаштування
        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0)//3600
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);


//        HashMap<String, Object> firebaseDefaultMap = new HashMap<>();
//        firebaseDefaultMap.put(DESCRIPTION_KEY, expandableTextView);
//        mFirebaseRemoteConfig.setDefaultsAsync(firebaseDefaultMap);


        mFirebaseRemoteConfig.setDefaultsAsync (R.xml.remote_config_defaults);

        // потрібно отримати значення
        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        boolean updated = task.getResult();
                        Log.e(TAG, "Config params updated: " + updated);
                    } else {
                        Objects.requireNonNull(task.getException()).printStackTrace();
                        Toast.makeText(HomeMainActivity.this, "Fetch failed",
                                Toast.LENGTH_SHORT).show();
                    }
                });
        // використовувати наші ключі для URL
        //mFirebaseRemoteConfig.getString(DESCRIPTION_KEY);
        //expandableTextView.setText(mFirebaseRemoteConfig.getString(DESCRIPTION_KEY));

        Spanned htmlAsSpanned = Html.fromHtml(mFirebaseRemoteConfig.getString(DESCRIPTION_KEY)); // used by TextView
        expandableTextView.setText(htmlAsSpanned);

       // expandableTextView.setText(mFirebaseRemoteConfig.getString(DESCRIPTION_KEY));




    }

    private void initList(ArrayList<VideoModel> mListData) {

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

//        SnapHelper snapHelper = new PagerSnapHelper();
//        snapHelper.attachToRecyclerView(mList_videos);

//        LinearSnapHelper mLinearSnapHelper = new LinearSnapHelper();
//        mLinearSnapHelper.attachToRecyclerView(mRecyclerView);

        mList_videos.setLayoutManager(layoutManager);
        recyclerAdapter = new HomeRecyclerAdapter(this, mListData);
        mList_videos.setAdapter(recyclerAdapter);
    }

    // создаем асинхронную задачу для получения всех данных с YouTube
    @SuppressLint("StaticFieldLeak")
    private class RequestYoutubeAPI extends AsyncTask<Void, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(PLAYLIST_GET_URL);
            Log.e("URL uu", PLAYLIST_GET_URL);
            try {
                HttpResponse response = httpClient.execute(httpGet);
                HttpEntity httpEntity = response.getEntity();
                return EntityUtils.toString(httpEntity);
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
                    mListData = parseVideoListFromResponse(jsonObject);
                    initList(mListData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public ArrayList<VideoModel> parseVideoListFromResponse(JSONObject jsonObject) {
        ArrayList<VideoModel> mList = new ArrayList<>();

        if (jsonObject.has("items")) {
            try {
                JSONArray jsonArray = jsonObject.getJSONArray("items");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject json = jsonArray.getJSONObject(i);
                    if (json.has("kind")) {
                        if (json.getString("kind").equals("youtube#playlistItem")) {
                            VideoModel youtubeObject = new VideoModel();
                            JSONObject jsonSnippet = json.getJSONObject("snippet");
                            String vedio_id = "";
                            if (jsonSnippet.has("resourceId")) {
                                JSONObject jsonResource = jsonSnippet.getJSONObject("resourceId");
                                vedio_id = jsonResource.getString("videoId");

                            }
                            String title = jsonSnippet.getString("title");
                            String description = jsonSnippet.getString("description");
                            String publishedAt = jsonSnippet.getString("publishedAt");
                            String thumbnail = jsonSnippet.getJSONObject("thumbnails").getJSONObject("high").getString("url");

                            youtubeObject.setTitle(title);
                            youtubeObject.setDescription(description);
                            youtubeObject.setPublishedAt(publishedAt);
                            youtubeObject.setThumbnail(thumbnail);
                            youtubeObject.setVideo_id(vedio_id);
                            mList.add(youtubeObject);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return mList;
    }

    private void autoStartViewPager() {

        final Handler handler = new Handler();
        final Runnable Update = () -> {
            if (homeViewPager.getCurrentItem() < Objects.requireNonNull(homeViewPager.getAdapter()).getCount() - 1) {
                homeViewPager.setCurrentItem(homeViewPager.getCurrentItem() + 1);
            } else {
                homeViewPager.setCurrentItem(0);
            }
        };

        //  ArrayList<GalleryModel> homeModelArrayList;
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 10000, 5000);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        Intent intent;

        switch (v.getId()) {
            case R.id.click_more_video:
                intent = new Intent(HomeMainActivity.this, VideoPlayListActivity.class);
                startActivity(intent);
                break;
            case R.id.click_more_photo:
                intent = new Intent(HomeMainActivity.this, GalleryMainActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void getData() {
        progressBar = new ProgressBar(this);
        progressBar = findViewById(R.id.progressBar);

        // RequestQueue: место для хранения запроса перед отправкой создаем RequestQueue по команде
        Call<FlickrPhoto> imageList = GalleryApi.getImageService().historyPhoto();
        imageList.enqueue(new Callback<FlickrPhoto>() {

            @Override
            public void onResponse(@NonNull Call<FlickrPhoto> call, @NonNull Response<FlickrPhoto> response) {
                list = response.body();
                assert list != null;
                adapter = new HomePagerAdapter(HomeMainActivity.this, list.getPhotos().getPhoto()); // прикрепить данные к адаптеру
                homeViewPager.setAdapter(adapter);
                indicator.setViewPager(homeViewPager);

                homeViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    }

                    @Override
                    public void onPageSelected(int position) {
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                    }
                });
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<FlickrPhoto> call, @NonNull Throwable throwable) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(HomeMainActivity.this, R.string.toast_failure, Toast.LENGTH_SHORT).show();
            }
        });
    }




}


// https://stackoverflow.com/questions/49341948/firebase-retriving-single-text-data-and-setting-it-in-a-textview
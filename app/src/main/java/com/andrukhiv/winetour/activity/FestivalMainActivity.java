package com.andrukhiv.winetour.activity;


import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.andrukhiv.winetour.constant.Constants;
import com.andrukhiv.winetour.adapter.FestivalMainAdapter;
import com.andrukhiv.winetour.R;
import com.andrukhiv.winetour.model.VideoModel;

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

public class FestivalMainActivity extends BaseActivity {

    @Override
    public int getContentViewId() {
        return R.layout.festival_activity;
    }

    @Override
    public int getNavigationMenuItemId() {
        return R.id.navigation_festival;
    }

    public static final String PLAYLIST_GET_URL =
            "https://www.googleapis.com/youtube/v3/" +
                    "playlistItems?part=" +
                    "snippet" +
                    "&playlistId=" +
                    Constants.FESTIVAL_PLAYLIST_ID +
                    "&maxResults=" +
                    "50" +
                    "&key=" +
                    Constants.GOOGLE_YOUTUBE_API_KEY +
                    "";

    private RecyclerView mList_videos = null;
    private FestivalMainAdapter festivalMainAdapter = null;
    private ArrayList<VideoModel> mListData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mListData = new ArrayList<>();
        festivalMainAdapter = new FestivalMainAdapter(this, mListData);
        mList_videos = findViewById(R.id.recyclerview);

        new RequestYoutubeAPI().execute();
    }

    private void initList(ArrayList<VideoModel> mListData) {

        int numColumns = getResources().getInteger(R.integer.video_columns);

        mList_videos.setLayoutManager(new GridLayoutManager(this, numColumns));
        festivalMainAdapter = new FestivalMainAdapter(this, mListData);
        mList_videos.setAdapter(festivalMainAdapter);
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
}

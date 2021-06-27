package com.andrukhiv.winetour.activity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.andrukhiv.winetour.constant.Constants;
import com.andrukhiv.winetour.R;
import com.andrukhiv.winetour.adapter.VideoMainAdapter;
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
import java.util.List;
import java.util.Objects;


public class VideoPlayListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
// https://developers.google.com/youtube/v3/docs/search/list?apix_params=%7B%22part%22%3A%22snippet%22%2C%22channelId%22%3A%22UCMCgOm8GZkHp8zJ6l7_hIuA%22%2C%22maxResults%22%3A50%2C%22order%22%3A%22date%22%7D#try-it

    private Object savedInstanceState;
    int onStartCount = 0;

    public static final String PLAYLIST_GET_URL =
            "https://www.googleapis.com/youtube/v3/" +
                    "playlistItems?part=" +
                    "snippet" +
                    "&playlistId=" +
                    Constants.PLAYLIST_ID +
                    "&maxResults=" +
                    "50" +
                    "&key=" +
                    Constants.GOOGLE_YOUTUBE_API_KEY +
                    "";

    private RecyclerView mList_videos = null;
    private VideoMainAdapter adapter = null;
    private ArrayList<VideoModel> mListData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_main_activity);

        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.toolbar_ic_up_arrow);// замените своим пользовательским значком
        }

        mListData = new ArrayList<>();
        adapter = new VideoMainAdapter(this, mListData);
        mList_videos = findViewById(R.id.mList_videos);

        new RequestYoutubeAPI().execute();

        initList(mListData);

        animationStartActivity();
    }

    private void initList(ArrayList<VideoModel> mListData) {

        int numColumns = getResources().getInteger(R.integer.video_columns);

        mList_videos.setLayoutManager(new GridLayoutManager(this, numColumns));
        adapter = new VideoMainAdapter(this, mListData);
        mList_videos.setAdapter(adapter);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        final MenuItem item = menu.findItem(R.id.action_search);

        final SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(this);

        searchView.setQueryHint("Пошук …");

        item.setOnActionExpandListener( new MenuItem.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {

                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                adapter.setItems(mListData);
                return true;

            }
        });

        ImageView searchIcon = searchView.findViewById(R.id.search_button);
        searchIcon.setImageDrawable(ContextCompat.getDrawable(Objects.requireNonNull(this),
                R.drawable.menu_ic_search));

        ImageView closeIcon = searchView.findViewById(R.id.search_close_btn);
        closeIcon.setImageDrawable(ContextCompat.getDrawable(this,
                R.drawable.toolbar_ic_close));

        // встановлюю ширину вікна пошуку на весь екран
        searchView.setMaxWidth(Integer.MAX_VALUE);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        final List<VideoModel> filteredModelList = filter(mListData, query);
        adapter.setItems(filteredModelList);
        adapter.notifyDataSetChanged();
        mList_videos.scrollToPosition(0);

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final List<VideoModel> filteredModelList = filter(mListData, newText);
        adapter.setItems(filteredModelList);
        return true;
    }

    private List<VideoModel> filter(List<VideoModel> datas, String newText) {
        newText = newText.toLowerCase().trim();

        final List<VideoModel> filteredModelList = new ArrayList<>();
        for (VideoModel data : datas) {

            final String text = data.getTitle().toLowerCase().trim();
            if (text.contains(newText)) {
                filteredModelList.add(data);
            }
        }

        return filteredModelList;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            finish();
        }
        return (super.onOptionsItemSelected(item));
    }

//    @Override
//    public void onBackPressed() {
//        finish();
//        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
//    }
//


//    @Override
//    public void startActivity(Intent intent) {
//        super.startActivity(intent);
//        overridePendingTransition(
//                R.anim.slide_in_right, R.anim.slide_out_left);
//
//        // как упомянуто в q http://stackoverflow.com/a/13578979/2288146
//        // Первый аргумент определяет анимацию перехода,
//        // используемую вторым действием. Также две анимации можно более правильно назвать
//        // as slide_out_to_right and slide_in_from_left, since in fact they
//        // all slide to the right.
//    }

    private void animationStartActivity() {
        onStartCount = 1;
        if (savedInstanceState == null) {  // 1st time
            this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else { // уже создана, поэтому обратная анимация
            onStartCount = 2;
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}















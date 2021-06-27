package com.andrukhiv.winetour.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.andrukhiv.winetour.service.GalleryApi;
import com.andrukhiv.winetour.model.GalleryModel.FlickrPhoto;
import com.andrukhiv.winetour.model.GalleryModel.Photo;
import com.andrukhiv.winetour.adapter.GalleryRecyclerAdapter;
import com.andrukhiv.winetour.adapter.GallerySearchAdapter;
import com.andrukhiv.winetour.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GalleryMainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private Context context;
    private RecyclerView recyclerView;
    private FlickrPhoto list;
    private List<Photo> newList;
    private GalleryRecyclerAdapter adapter;
    private GallerySearchAdapter searchAdapter;
    private String userInput;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private Object savedInstanceState;
    int onStartCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_main_activity);

        context = GalleryMainActivity.this;

        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.toolbar_ic_up_arrow);
        }

        int numColumns = getResources().getInteger(R.integer.gallery_columns);
        recyclerView = findViewById(R.id.imageList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, numColumns));

//        if (recyclerView != null) {
//            StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(numColumns, OrientationHelper.VERTICAL);
//            layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
//            recyclerView.setLayoutManager(layoutManager);
//            recyclerView.setItemAnimator(new DefaultItemAnimator());
//
//        }
//      recyclerView.setLayoutManager(new StaggeredGridLayoutManager(numColumns,
//                StaggeredGridLayoutManager.VERTICAL));

        swipeRefreshLayout = findViewById(R.id.swipeContainer);
        // Настройте прослушиватель обновления, который запускает загрузку новых данных
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Не забудьте очистить старые элементы, прежде чем добавлять в новые
                adapter.clear();
                // ...данные вернулись, добавьте новые элементы в ваш адаптер...
                adapter.addAll(list.getPhotos().getPhoto());
                // Теперь мы вызываем setRefreshing (false), чтобы завершить обновление сигнала
                swipeRefreshLayout.setRefreshing(false);

                getData();
            }
        });
        // Настройте освежающие цвета
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        getData();

        animationStartActivity();
    }

    private void getData() {
        progressBar = new ProgressBar(this);
        progressBar = findViewById(R.id.progressBar);

        Call<FlickrPhoto> imageList = GalleryApi.getImageService().searchTitle();
        imageList.enqueue(new Callback<FlickrPhoto>() {

            @Override
            public void onResponse(Call<FlickrPhoto> call, Response<FlickrPhoto> response) {
                if (response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    //Toast.makeText(context, "Fetching Images..", Toast.LENGTH_SHORT).show();
                    list = response.body();
                    assert list != null;
                    adapter = new GalleryRecyclerAdapter(context, list.getPhotos().getPhoto());
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(context, R.string.toast_error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FlickrPhoto> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(GalleryMainActivity.this, R.string.toast_failure, Toast.LENGTH_SHORT).show();

//                Intent intent = new Intent(GalleryMainActivity.this, SplashScreenActivity.class);
//                startActivity(intent);
//                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(this);

        searchView.setQueryHint("Пошук фестивалю …");

        item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                adapter.setItems(newList);
                return true;
            }
        });

        ImageView searchIcon = searchView.findViewById(R.id.search_button);
        searchIcon.setImageDrawable(ContextCompat.getDrawable(Objects.requireNonNull(this),
                R.drawable.menu_ic_search));

        ImageView closeIcon = searchView.findViewById(R.id.search_close_btn);
        closeIcon.setImageDrawable(ContextCompat.getDrawable(this,
                R.drawable.toolbar_ic_close));

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                onRestart();
                //getData();
                return false;
            }
        });

        // встановлюю ширину вікна пошуку на весь екран
        searchView.setMaxWidth(Integer.MAX_VALUE);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        userInput = newText.toLowerCase();
        newList = new ArrayList<>();

        Call<FlickrPhoto> imageList = GalleryApi.getImageService().searchTitle();
        imageList.enqueue(new Callback<FlickrPhoto>() {

            @Override
            public void onResponse(@NonNull Call<FlickrPhoto> call, @NonNull Response<FlickrPhoto> response) {
                if (response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    list = response.body();
                    assert list != null;
                    for (Photo p : list.getPhotos().getPhoto()) {
                        if (p.getTitle().toLowerCase().contains(userInput)) {
                            newList.add(p);
                        }
                    }
//                    adapter = new GalleryRecyclerAdapter(context, newList);
//                    recyclerView.setAdapter(adapter);
                    searchAdapter = new GallerySearchAdapter(context, newList);
                    recyclerView.setAdapter(searchAdapter);
                    //Toast.makeText(context, "Search Results..", Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(context, R.string.toast_error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<FlickrPhoto> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(GalleryMainActivity.this, R.string.toast_failure, Toast.LENGTH_SHORT).show();
            }
        });
        return true;
    }

    @Override
    protected void onRestart() {
        getData();
        super.onRestart();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            finish();
        }
        return (super.onOptionsItemSelected(item));
    }

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













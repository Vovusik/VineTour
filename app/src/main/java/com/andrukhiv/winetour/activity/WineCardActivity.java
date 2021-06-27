package com.andrukhiv.winetour.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.andrukhiv.winetour.R;
import com.andrukhiv.winetour.model.WineCardModel;
import com.andrukhiv.winetour.adapter.WineCardRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;


public class WineCardActivity extends AppCompatActivity {

//    public static WineCardActivity newInstance() {
//        return new WineCardActivity();
//    }

    RecyclerView mRecyclerView;
    private List<WineCardModel> dabListItem = new ArrayList<>();
    private WineCardRecyclerAdapter mAdapter;

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wine_card_recycler_fragment);

        dabListItem = WineCardModel.getData();

        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new WineCardRecyclerAdapter(WineCardActivity.this, dabListItem);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView = findViewById(R.id.recyclerview);
    }
}

package com.andrukhiv.winetour.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import com.andrukhiv.winetour.adapter.MapsAdapterTrans;
import com.andrukhiv.winetour.model.MapsModel;
import com.andrukhiv.winetour.R;
import com.andrukhiv.winetour.helper.HomePagerTransformer;
import com.andrukhiv.winetour.helper.MapsUtils;
import com.github.nitrico.mapviewpager.MapViewPager;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;


public class MapsActivity extends BaseActivity implements
        MapViewPager.Callback,
        OnMapReadyCallback {

    private static final String TAG = MapsActivity.class.getSimpleName();

    public Toolbar mToolbarActivity;
    private ViewPager viewPager;
    private MapViewPager mvp;
    public Spinner mSpinner;
    private SupportMapFragment mapFragment;
    DatabaseReference databaseReference;
    private Query query, query2;
    private GoogleMap mMap;
    List<MapsModel> mapsModels, mapsModelsOdesa;

    @Override
    public int getContentViewId() {
        return R.layout.maps_activity;
    }

    @Override
    public int getNavigationMenuItemId() {
        return R.id.navigation_maps;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //databaseReference = FirebaseDatabase.getInstance().getReference().child("festival");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("festival").child("uz");
        databaseReference.addListenerForSingleValueEvent(valueEventListener);
        databaseReference.keepSynced(true);

//        query = FirebaseDatabase
//                .getInstance()
//                .getReference("festival")
//                .child("uz")
//                .orderByChild("terruarId")
//                .equalTo(1);
//        query.addListenerForSingleValueEvent(valueEventListener);

        mToolbarActivity = findViewById(R.id.toolbar_spinner_maps);
        setSupportActionBar(mToolbarActivity);
        // TODO: Видимість спінера
        mToolbarActivity.setVisibility(View.INVISIBLE);
        // Прибрав назву заголовку з тулбара
        Objects.requireNonNull(getSupportActionBar()).setTitle(null);

//        mapsModels = MapsModel.getMaps();
//        mapsModelsOdesa = MapsModel.getMaps();

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        viewPager = findViewById(R.id.viewPager);
        viewPager.setPageMargin(MapsUtils.dp(this, 16));
        //MapsUtils.setMargins(viewPager);
        viewPager.setPadding(80, 0, 100, 32);
        viewPager.setPageTransformer(false, new HomePagerTransformer());
        viewPager.setHorizontalFadingEdgeEnabled(true);
        viewPager.setFadingEdgeLength(0);

        // Добавляю спіннер у тулбар
        mSpinner = findViewById(R.id.spinner);

        //  spinnerRegionToolbar();

        // loadMovie();


    }

    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            mapsModels = new ArrayList<>();
            mapsModelsOdesa = new ArrayList<>();
            if (dataSnapshot.exists()) {
                for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                    MapsModel pidval = productSnapshot.getValue(MapsModel.class);
                    mapsModels.add(pidval);
                    mapsModelsOdesa.add(pidval);

                    // Сортую за Id
                    Collections.sort(mapsModels, new Comparator<MapsModel>() {
                        @Override
                        public int compare(MapsModel o1, MapsModel o2) {
                            return Integer.compare(o1.getId(), o2.getId());
                        }
                    });

                    Collections.sort(mapsModelsOdesa, new Comparator<MapsModel>() {
                        @Override
                        public int compare(MapsModel o1, MapsModel o2) {
                            return Integer.compare(o1.getId(), o2.getId());
                        }
                    });
                }
                if (getBaseContext() != null) {
                    spinnerRegionToolbar();
                    // transcarpathianMapViewPager();
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Toast.makeText(MapsActivity.this, "Opsss.... Щось не так", Toast.LENGTH_SHORT).show();
        }
    };

    private void spinnerRegionToolbar() {

        ArrayAdapter<String> mAdapter = new ArrayAdapter<>(MapsActivity.this,
                R.layout.maps_item_spinner,
                getResources().getStringArray(R.array.region));
        mAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        mSpinner.setAdapter(mAdapter);
        //transcarpathianMapViewPager();

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

                switch (position) {
                    case 0:
//                 query = FirebaseDatabase
//                .getInstance()
//                .getReference("festival")
//                .child("uz")
//                .orderByChild("terruarId")
//                .equalTo(1);
//        query.addListenerForSingleValueEvent(valueEventListener);
                        transcarpathianMapViewPager();
                        break;
                    case 1:
//                      query = FirebaseDatabase
//                                .getInstance()
//                                .getReference("festival")
//                                .child("uk")
//                                .orderByChild("sort")
//                                .equalTo("столовий");
//                        query.addListenerForSingleValueEvent(valueEventListener);
                        odesaMapViewPager();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void transcarpathianMapViewPager() {
        mvp = new MapViewPager.Builder(MapsActivity.this)
                .mapFragment(mapFragment)
                .viewPager(viewPager)
                .position(0)
                .adapter(new MapsAdapterTrans(this.getSupportFragmentManager(), mapsModels))
                .callback(MapsActivity.this)
                .build();
    }

    private void odesaMapViewPager() {
        mvp = new MapViewPager.Builder(MapsActivity.this)
                .mapFragment(mapFragment)
                .viewPager(viewPager)
                .position(2)
                .adapter(new MapsAdapterTrans(this.getSupportFragmentManager(), mapsModelsOdesa))
                .callback(MapsActivity.this)
                .build();
    }

    @Override
    public void onMapViewPagerReady() {
        mvp.getMap().setPadding(
                MapsUtils.getNavigationBarWidth(this),
                MapsUtils.getNavigationBarWidth(this),
                MapsUtils.getNavigationBarWidth(this),
                viewPager.getHeight());
        mvp.getMap().setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Встановлення маркера власного кольору
//        mvp.getMarker(1)
//                .setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
//        //.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.toolbar_ic_up_arrow));
//        mvp.getMarker(0)
//                .setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));

        // Зміна відтінку маркера, який не перебуває у фокусі
        mvp.getViewPager().addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < mvp.getMarkers().size(); i++) {
                    Marker marker = mvp.getMarker(i);
                    if (i == position) marker.setAlpha(1f);
                    else marker.setAlpha(0.3f);
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        int uiMode = getResources().getConfiguration().uiMode;
        int dayNightUiMode = uiMode & Configuration.UI_MODE_NIGHT_MASK;

        try {
            if (dayNightUiMode == Configuration.UI_MODE_NIGHT_YES) {
                boolean success = googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(this, R.raw.style_night));
                if (!success) {
                    Log.e(TAG, "Разбор стиля не удался.");
                }
            } else {
                boolean success = googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(this, R.raw.style_day));
                if (!success) {
                    Log.e(TAG, "Разбор стиля не удался.");
                }
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Не могу найти стиль. Ошибка:", e);
        }

        // Присобачив кнопку власного місцезнаходження
//        mMap = googleMap;
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//
//        mMap.setMyLocationEnabled(true);
//
//        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
//            @Override
//            public void onMyLocationChange(Location location) {
//
//                CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
//                CameraUpdate zoom = CameraUpdateFactory.zoomTo(11);
//                mMap.clear();
//
//                MarkerOptions mp = new MarkerOptions();
//
//                mp.position(new LatLng(location.getLatitude(), location.getLongitude()));
//
//                mp.title("my position");
//
//                mMap.addMarker(mp);
//                mMap.moveCamera(center);
//                mMap.animateCamera(zoom);
//            }
//        });


    }

}

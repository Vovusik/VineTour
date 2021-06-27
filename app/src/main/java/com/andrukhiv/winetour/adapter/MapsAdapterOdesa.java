package com.andrukhiv.winetour.adapter;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.andrukhiv.winetour.fragment.MapsFragmentOdesa;
import com.andrukhiv.winetour.model.MapsModel;
import com.github.nitrico.mapviewpager.MapViewPager;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MapsAdapterOdesa extends MapViewPager.Adapter {

    private List<MapsModel> mapsModelsOdesa;
    public static ArrayList<String> TITLES = new ArrayList<>();
    public static ArrayList<String> DESCRIPTION = new ArrayList<>();
    public static ArrayList<String> ADDRESS = new ArrayList<>();
    public static ArrayList<String> TEXT_WEB = new ArrayList<>();
    public static ArrayList<String> TEXT_PHONE = new ArrayList<>();
    public static ArrayList<String> NAVIGATION_POSITION = new ArrayList<>();
    public static ArrayList<String> IMAGES = new ArrayList<>();

    public ArrayList<CameraPosition> POSITIONS = new ArrayList<>();


    public MapsAdapterOdesa(FragmentManager fm, List<MapsModel> mapsModelsOdesa) {
        super(fm);
        this.mapsModelsOdesa = mapsModelsOdesa;

        for (int i=0; i<mapsModelsOdesa.size(); i++){

            TITLES.add(mapsModelsOdesa.get(i).getTitle());
            DESCRIPTION.add(mapsModelsOdesa.get(i).getDescription());
            ADDRESS.add(mapsModelsOdesa.get(i).getAddress());
            TEXT_WEB.add(mapsModelsOdesa.get(i).getWeb());
            TEXT_PHONE.add(mapsModelsOdesa.get(i).getPhone());
            NAVIGATION_POSITION.add(mapsModelsOdesa.get(i).getNavPosition());
            IMAGES.add(mapsModelsOdesa.get(i).getPhoto());

            POSITIONS.add(CameraPosition.fromLatLngZoom(new LatLng(mapsModelsOdesa.get(i).getLat(),
                    mapsModelsOdesa.get(i).getLng()), 14f));
        }
        // Event name iterator
        for (String element : TITLES) {
            Log.i("iterator", element);
        }
    }


    @Override
    public CameraPosition getCameraPosition(int position) {
        return POSITIONS.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES.get(position);
    }

    static String getDescripion(int position) {
        return DESCRIPTION.get(position);
    }

    public static String getAddress(int position) {
        return ADDRESS.get(position);
    }

    public static String getTextWeb(int position) {
        return TEXT_WEB.get(position);
    }

    public static String getTextPhone(int position) {
        return TEXT_PHONE.get(position);
    }

    public static String getNavigationPosition(int position) {
        return NAVIGATION_POSITION.get(position);
    }

    public static String getImage(int position){
        return IMAGES.get(position);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return MapsFragmentOdesa.newInstance(position);
    }

    @Override
    public int getCount() {
        return mapsModelsOdesa.size();
    }
}

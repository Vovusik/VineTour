package com.andrukhiv.winetour.adapter;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.andrukhiv.winetour.fragment.MapsFragmentTrans;
import com.andrukhiv.winetour.model.MapsModel;
import com.github.nitrico.mapviewpager.MapViewPager;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MapsAdapterTrans extends MapViewPager.Adapter {

    private List<MapsModel> mapsModels;
    public static ArrayList<String> TITLES = new ArrayList<>();
    public static ArrayList<String> DESCRIPTION = new ArrayList<>();
    public static ArrayList<String> ADDRESS = new ArrayList<>();
    public static ArrayList<String> TEXT_WEB = new ArrayList<>();
    public static ArrayList<String> TEXT_PHONE = new ArrayList<>();
    public static ArrayList<String> NAVIGATION_POSITION = new ArrayList<>();
    public static ArrayList<String> IMAGES = new ArrayList<>();
    public static ArrayList<String> VIRTUAL_PHOTO = new ArrayList<>();
    public static ArrayList<Boolean> SHOW_DETAILS_PHOTO = new ArrayList<Boolean>();

    public ArrayList<CameraPosition> POSITIONS = new ArrayList<>();


    public MapsAdapterTrans(FragmentManager fm, List<MapsModel> mapsModels) {
        super(fm);
        this.mapsModels = mapsModels;

        for (int i=0; i<mapsModels.size(); i++){

            TITLES.add(mapsModels.get(i).getTitle());
            DESCRIPTION.add(mapsModels.get(i).getDescription());
            ADDRESS.add(mapsModels.get(i).getAddress());
            TEXT_WEB.add(mapsModels.get(i).getWeb());
            TEXT_PHONE.add(mapsModels.get(i).getPhone());
            NAVIGATION_POSITION.add(mapsModels.get(i).getNavPosition());
            IMAGES.add(mapsModels.get(i).getPhoto());
            VIRTUAL_PHOTO.add(mapsModels.get(i).getVirtPhoto());
            SHOW_DETAILS_PHOTO.add(mapsModels.get(i).getShowDetails());

            POSITIONS.add(CameraPosition.fromLatLngZoom(new LatLng(mapsModels.get(i).getLat(),
                    mapsModels.get(i).getLng()), 12f));
        }

        // Итератор имени события
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

    public static String getDescription(int position) {
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
        return NAVIGATION_POSITION.get(position);  }

    public static String getImage(int position){
        return IMAGES.get(position);
    }

    public static String getVirtualPhoto(int position){
        return VIRTUAL_PHOTO.get(position);
    }
    public static Boolean getShowDetails(int position){
        return SHOW_DETAILS_PHOTO.get(position);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return MapsFragmentTrans.newInstance(position);
    }

    @Override
    public int getCount() {
        return mapsModels.size();
    }
}

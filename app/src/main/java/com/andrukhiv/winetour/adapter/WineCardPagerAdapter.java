package com.andrukhiv.winetour.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class WineCardPagerAdapter extends FragmentPagerAdapter {

    private int allPagers;

    public WineCardPagerAdapter(FragmentManager fm, int allPagers) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.allPagers = allPagers;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
//            case 0:
//                return new WineCardRecyclerFragment();
//            case 0:
//                return new WineCardDescriptionFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return allPagers;
    }
}

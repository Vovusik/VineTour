package com.andrukhiv.winetour.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.andrukhiv.winetour.R;
import com.andrukhiv.winetour.model.WineCardModel;


public class WineCardDescriptionFragment extends Fragment {

    public WineCardDescriptionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.wine_card_description_fragment, container, false);

        TextView textView = rootView.findViewById(R.id.textView_1);
        textView.setText(WineCardModel.Desc_data);
        return rootView;

    }


}

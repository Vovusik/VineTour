package com.andrukhiv.winetour.helper;

import android.view.View;

import androidx.viewpager.widget.ViewPager;

import com.andrukhiv.winetour.R;

public class ParallaxTransformer implements ViewPager.PageTransformer {


	@Override
	public void transformPage(View page, float position) {

		if (position > -1 && position < 1) {
			page.setAlpha(1);

			float width = (float)page.getWidth();
            // page.setTranslationX(position);
            // Parallax - використати у фомуванні
            //page.findViewById(R.id.image_slide).setTranslationX(-position * (float)(width / 2));
            page.findViewById(R.id.image_slide).setTranslationX(-position * width);
            //page.findViewById(R.id.layout_tools).setTranslationX(-position * width);

		} else {
			page.setAlpha(0);
		}
	}
}

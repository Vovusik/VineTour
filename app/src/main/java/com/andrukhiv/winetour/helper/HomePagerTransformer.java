package com.andrukhiv.winetour.helper;


import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;


public class HomePagerTransformer implements ViewPager.PageTransformer {

    // Коэффициент масштабирования, когда страница находится вне экрана.
    private float finalScale;
    private float finalAlpha;

    public HomePagerTransformer() {
        finalScale = 0.8f;
        finalAlpha = 0.5f;
    }

    public HomePagerTransformer (float finalScale) {
        setFinalScale(finalScale);
    }

    @Override
    public void transformPage(@NonNull View page, float position) {
        float scale;
        if (position <= -1 || position >= 1) {
            scale = finalScale;
        } else {
            scale = 1 - (1 - finalScale) * Math.abs(position);
        }

        page.setPivotX(position < 0 ? page.getWidth() : 0);
        page.setPivotY(page.getHeight() / 2f);

        page.setScaleX(scale);
        page.setScaleY(scale);


        page.setAlpha(finalAlpha +
                (scale - finalScale) /
                        (1 - finalScale) * (1 - finalAlpha));

    }

    public float getFinalScale() {
        return finalScale;
    }

    private void setFinalScale(float finalScale) {
        this.finalScale = finalScale;
    }
}

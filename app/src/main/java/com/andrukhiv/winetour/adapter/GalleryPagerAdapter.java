package com.andrukhiv.winetour.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.andrukhiv.winetour.model.GalleryModel.Photo;
import com.andrukhiv.winetour.R;
import com.bumptech.glide.Glide;

import java.util.List;


public class GalleryPagerAdapter extends PagerAdapter {

    private Context context;
    private List<Photo> listPhotos;
    private LayoutInflater inflater;

    public GalleryPagerAdapter(Context context, List<Photo> listPhotos) {
        this.context = context;
        this.listPhotos = listPhotos;
        this.inflater = LayoutInflater.from(context);
    }

    // Переход на другую страницу уничтожит элемент на старой странице
    @Override
    public void destroyItem(ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    // возвращает количество отображаемых элементов
    @Override
    public int getCount() {
        return listPhotos.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view1 = inflater.inflate(R.layout.gallery_item_slide, container, false);

        ImageView photoView = view1.findViewById(R.id.image_slide);

        Glide.with(context)
                .load(listPhotos.get(position).createURL())
                .placeholder(R.drawable.placeholder)
                .into(photoView);

        container.addView(view1, 0);
        return view1;
    }

    // Метод, который проверяет, связаны ли объекты, возвращаемые
    // instantiateItem (), с предоставленным представлением
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }
}

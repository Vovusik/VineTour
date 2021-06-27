package com.andrukhiv.winetour.service;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class GalleryApi {

    private static final String BASE_URL = "https://api.flickr.com/services/rest/";

    public static GalleryService imageService = null;

    public static GalleryService getImageService() {
        if (imageService == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            imageService = retrofit.create(GalleryService.class);
        }
        return imageService;
    }
}
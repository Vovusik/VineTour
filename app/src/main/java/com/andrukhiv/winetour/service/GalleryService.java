package com.andrukhiv.winetour.service;


import com.andrukhiv.winetour.model.GalleryModel.FlickrPhoto;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GalleryService {

//    @GET("services/rest/?method=flickr.photos.getRecent&page=1&format=json&nojsoncallback=1")
//    Call<ImageList> getImageList(@Query("api_key") String apiKey,
//                                 @Query("per_page") Integer perPage);


//    @GET("?method=flickr.photos.getRecent&api_key=" + GALLERY_PATH)
//    Call<ImageList> getImageList();

    @GET("?method=flickr.photos.search&api_key=" + SEARCH_PATH)
    Call<FlickrPhoto> searchTitle();

    @GET("?method=flickr.photos.search&api_key=" + HOME_PATH)
    Call<FlickrPhoto> historyPhoto();

    String BASE_URL = "https://api.flickr.com/services/rest/";
    String API_KEY = "7f02d04d7829190892d9f4aee1ca28b1";
    String USER_ID = "188203272%40N05";
    String TAGS = "шлях";
    String TEXT = "";
    String FORMAT = "json";
    String SORT = "date-posted-asc";
    String PAGE = "500";

    //private static final String SEARCH_PATH = "&api_key=7f02d04d7829190892d9f4aee1ca28b1&user_id=188203272%40N05&tags=шлях&format=json&nojsoncallback=1&extras=url_s&sort=date-posted-desc&per_page=500";

    String SEARCH_PATH =
            "&api_key=" +
                    "7f02d04d7829190892d9f4aee1ca28b1" +
                    "&user_id=" +
                    "188203272%40N05" +
                    "&tags=" +
                    "tourism" +
                    "&format=" +
                    "json" +
                    "&nojsoncallback=" +
                    "1" +
                    "&extras=" +
                    "url_s" +
                    "&sort=" +
                    "date-posted-desc" +
                    "&per_page=" +
                    "500";


    String HOME_PATH =
            "&api_key=" +
                    "7f02d04d7829190892d9f4aee1ca28b1" +
                    "&user_id=" +
                    "188203272%40N05" +
                    "&tags=" +
                    "history" +
                    "&format=" +
                    "json" +
                    "&nojsoncallback=" +
                    "1" +
                    "&extras=" +
                    "url_s" +
                    "&sort=" +
                    "date-posted-desc" +
                    "&per_page=" +
                    "500";




    //https://api.flickr.com/services/rest/?method=flickr.photos.getRecent&api_key=?&method=flickr.people.getPublicPhotos&api_key=7f02d04d7829190892d9f4aee1ca28b1&user_id=188203272%40N05&format=json&nojsoncallback=1&extras=url_s
    //"services/rest/?method=flickr.photos.getRecent&page=1&format=json&nojsoncallback=1"
    String GALLERY_PATH =
            "?&method=" +
                    "flickr.people.getPublicPhotos" +
                    "&api_key=" +
                    "7f02d04d7829190892d9f4aee1ca28b1" +
                    "&user_id=" +
                    "188203272%40N05" +
                    "&format=" +
                    "json" +
                    "&nojsoncallback=" +
                    "1" +
                    "&extras=" +
                    "url_s" +
                    "&per_page=" +
                    "500";
}

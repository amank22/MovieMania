package com.aman.moviemania.helper;


import com.aman.moviemania.interfaces.RestService;
import com.aman.moviemania.parcel.MovieParcel;
import com.aman.moviemania.parcel.ReviewsParcel;
import com.aman.moviemania.parcel.VideoParcel;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Aman Kapoor on 12-10-2016.
 */

public class RestHelper {

    private static <T> T createRetrofitService(final Class<T> clazz) {


        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        // add logging as last interceptor
        httpClient.addInterceptor(logging);  // <-- this is the important line!

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.getMovieBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(httpClient.build())
                .build();

        return retrofit.create(clazz);
    }

    public static Observable<MovieParcel> getPopularMovies() {
        RestService service = RestHelper.createRetrofitService(RestService.class);
        return service.getPopularMovies(Constants.getMovieApiKey());
    }

    public static Observable<MovieParcel> getTopRatedMovies() {
        RestService service = RestHelper.createRetrofitService(RestService.class);
        return service.getTopRatedMovies(Constants.getMovieApiKey());
    }

    public static Observable<VideoParcel> getTrailersForMovie(int id) {
        RestService service = RestHelper.createRetrofitService(RestService.class);
        return service.getTrailersForMovie(id, Constants.getMovieApiKey())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<ReviewsParcel> getReviewsForMovie(int id) {
        RestService service = RestHelper.createRetrofitService(RestService.class);
        return service.getReviewsForMovie(id, Constants.getMovieApiKey())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}

package com.aman.moviemania.helper;



import com.aman.moviemania.interfaces.RestService;
import com.aman.moviemania.parcel.MovieParcel;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

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
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread());
    }

}

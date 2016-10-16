package com.aman.moviemania.interfaces;


import com.aman.moviemania.parcel.MovieParcel;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Aman Kapoor on 12-10-2016.
 */
public interface RestService {


    @GET("movie/popular")
    Observable<MovieParcel> getPopularMovies(@Query("api_key") String apiKey);

    @GET("movie/top_rated")
    Observable<MovieParcel> getTopRatedMovies(@Query("api_key") String apiKey);

}

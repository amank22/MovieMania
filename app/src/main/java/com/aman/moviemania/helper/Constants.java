package com.aman.moviemania.helper;

/**
 * Created by Aman Kapoor on 12-10-2016.
 */

public final class Constants {

    private static final String MOVIE_API_KEY = "ENTER_YOUR_API_KEY";
    private static final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/";
    private static final String MOVIE_BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";
    private static final String YOUTUBE_VIDEO_BASE_IMAGE_URL_1 = "http://img.youtube.com/vi/";
    private static final String YOUTUBE_VIDEO_BASE_IMAGE_URL_2 = "/hqdefault.jpg";
    private static final String MOVIE_BL_POPULAR_COMPLETE = "com.aman.udacityportfolio.popular.complete";
    private static final String MOVIE_BL_POPULAR_ERROR = "com.aman.udacityportfolio.popular.error";
    private static final String MOVIE_BL_TOP_COMPLETE = "com.aman.udacityportfolio.top.complete";
    private static final String MOVIE_BL_TOP_ERROR = "com.aman.udacityportfolio.top.error";

    public static String getMovieApiKey() {
        try {
            Class.forName("com.aman.moviemania.helper.ApiKey");
            return ApiKey.MOVIE_API_KEY;
        } catch (ClassNotFoundException e) {
            return MOVIE_API_KEY;
        }
    }

    public static String getMovieBaseUrl() {
        return MOVIE_BASE_URL;
    }

    public static String getMovieBaseImageUrl(boolean thumb) {
        return thumb ? MOVIE_BASE_IMAGE_URL + "w342" : MOVIE_BASE_IMAGE_URL + "w780";
    }

    public static String getYoutubeThumbLink(String youtube_id) {
        return YOUTUBE_VIDEO_BASE_IMAGE_URL_1 + youtube_id + YOUTUBE_VIDEO_BASE_IMAGE_URL_2;
    }

    public static String getMovieBlPopularComplete() {
        return MOVIE_BL_POPULAR_COMPLETE;
    }

    public static String getMovieBlPopularError() {
        return MOVIE_BL_POPULAR_ERROR;
    }

    public static String getMovieBlTopComplete() {
        return MOVIE_BL_TOP_COMPLETE;
    }

    public static String getMovieBlTopError() {
        return MOVIE_BL_TOP_ERROR;
    }
}

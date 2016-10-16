package com.aman.moviemania.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.aman.moviemania.helper.Constants;
import com.aman.moviemania.helper.RestHelper;
import com.aman.moviemania.parcel.MovieParcel;

import rx.Observer;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 */
public class MovieIntentService extends IntentService {

    private static final String ACTION_POPULAR = "com.aman.udacityportfolio.service.action.POPULAR";
    private static final String ACTION_TOP_RATED = "com.aman.udacityportfolio.service.action.TOP.RATED";

    public MovieIntentService() {
        super("MovieIntentService");
    }

    /**
     * Starts this service to perform action Popular Movies with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionPopular(Context context) {
        Intent intent = new Intent(context, MovieIntentService.class);
        intent.setAction(ACTION_POPULAR);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Top rated movies with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionTop(Context context) {
        Intent intent = new Intent(context, MovieIntentService.class);
        intent.setAction(ACTION_TOP_RATED);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_POPULAR.equals(action)) {
                handleActionPopular();
            } else if (ACTION_TOP_RATED.equals(action)) {
                handleActionTop();
            }
        }
    }

    private void handleActionPopular() {
        RestHelper.getPopularMovies().subscribe(new Observer<MovieParcel>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Intent intent = new Intent(Constants.getMovieBlPopularError());
                intent.putExtra(Constants.getMovieBlPopularError(), e.getLocalizedMessage());
                LocalBroadcastManager.getInstance(MovieIntentService.this).sendBroadcast(intent);
            }

            @Override
            public void onNext(MovieParcel movieParcel) {
                Intent intent = new Intent(Constants.getMovieBlPopularComplete());
                intent.putExtra(Constants.getMovieBlPopularComplete(), movieParcel);
                LocalBroadcastManager.getInstance(MovieIntentService.this).sendBroadcast(intent);
            }
        });
    }

    private void handleActionTop() {
        RestHelper.getTopRatedMovies().subscribe(new Observer<MovieParcel>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Intent intent = new Intent(Constants.getMovieBlTopError());
                intent.putExtra(Constants.getMovieBlTopError(), e.getLocalizedMessage());
                LocalBroadcastManager.getInstance(MovieIntentService.this).sendBroadcast(intent);
            }

            @Override
            public void onNext(MovieParcel movieParcel) {
                Intent intent = new Intent(Constants.getMovieBlTopComplete());
                intent.putExtra(Constants.getMovieBlTopComplete(), movieParcel);
                LocalBroadcastManager.getInstance(MovieIntentService.this).sendBroadcast(intent);
            }
        });
    }
}

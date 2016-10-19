package com.aman.moviemania;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aman.moviemania.adapter.MovieAdapter;
import com.aman.moviemania.helper.Constants;
import com.aman.moviemania.helper.GridAutofitLayoutManager;
import com.aman.moviemania.helper.ItemClickSupport;
import com.aman.moviemania.helper.MovieDbHelper;
import com.aman.moviemania.helper.Utils;
import com.aman.moviemania.parcel.MovieData;
import com.aman.moviemania.parcel.MovieParcel;
import com.aman.moviemania.service.MovieIntentService;

import java.util.ArrayList;
import java.util.List;


public class MoviesActivity extends AppCompatActivity {

    private static final String PARCEL_KEY = "parcel_key";
    private static final String PARCEL_TYPE_KEY = "parcel_type_key";
    private static final String LIST_POSITION_KEY = "list_position_key";
    private static final int PARCEL_TYPE_POPULAR = 0;
    private static final int PARCEL_TYPE_TOP = 1;

    private int currentType;
    private GridLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private MovieParcel parcel;
    private TextView errorText;
    private ProgressBar progressBar;
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.getMovieBlPopularComplete())) {
                parcel = intent.getParcelableExtra(Constants.getMovieBlPopularComplete());
                complete(parcel);
            } else if (action.equals(Constants.getMovieBlPopularError())) {
                error(intent.getStringExtra(Constants.getMovieBlPopularError()));
            } else if (action.equals(Constants.getMovieBlTopComplete())) {
                parcel = intent.getParcelableExtra(Constants.getMovieBlTopComplete());
                complete(parcel);
            } else if (action.equals(Constants.getMovieBlTopError())) {
                error(intent.getStringExtra(Constants.getMovieBlTopError()));
            }
        }
    };
    private List<Integer> favIds;
    private boolean isTablet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        chooseTitle();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_movies);
        errorText = (TextView) findViewById(R.id.movie_error_text);
        progressBar = (ProgressBar) findViewById(R.id.movie_progress);
        fabOperations();
        int oldPosition = 0;
        NestedScrollView bottomSheet = (NestedScrollView) findViewById(R.id.bottom_sheet);
        isTablet = bottomSheet != null;
        if (savedInstanceState != null) {
            currentType = savedInstanceState.getInt(PARCEL_TYPE_KEY);
            parcel = savedInstanceState.getParcelable(PARCEL_KEY);
            oldPosition = savedInstanceState.getInt(LIST_POSITION_KEY);
        } else {
            currentType = 0;
        }
        if (parcel == null) {
            setLayoutManager(0, false);
            if (currentType == PARCEL_TYPE_POPULAR)
                MovieIntentService.startActionPopular(this);
            else if (currentType == PARCEL_TYPE_TOP)
                MovieIntentService.startActionTop(this);
        } else {
            setLayoutManager(oldPosition, true);
        }
        addClickSupport();
    }

    private void fabOperations() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.dialog_title_choose_movies);
            String[] items = {getString(R.string.movie_cat_popular), getString(R.string.movie_cat_top), getString(R.string.movie_cat_fav)};
            builder.setItems(items, (dialog, pos) -> {
                if (!Utils.checkNetworkConnected(MoviesActivity.this) && pos != 2) {
                    Toast.makeText(MoviesActivity.this, R.string.error_no_internet, Toast.LENGTH_LONG).show();
                    return;
                }

                if (pos == 0 && currentType != pos) {
                    MovieIntentService.startActionPopular(this);
                    errorText.setText(R.string.loading_movies);
                    errorText.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                } else if (pos == 1 && currentType != pos) {
                    MovieIntentService.startActionTop(this);
                    errorText.setText(R.string.loading_movies);
                    errorText.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                } else if (pos == 2 && currentType != pos) {
                    try {
                        parcel = MovieDbHelper.getInstance(MoviesActivity.this).getFavMovieParcel();
                        setAdapterToRecycleView(parcel);
                    } catch (Exception e) {
                        recyclerView.setVisibility(View.GONE);
                        errorText.setText(e.getMessage());
                        errorText.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }
                currentType = pos;
            });
            AlertDialog alert = builder.create();
            alert.show();

        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (parcel != null) {
            outState.putParcelable(PARCEL_KEY, parcel);
        }
        outState.putInt(PARCEL_TYPE_KEY, currentType);
        outState.putInt(LIST_POSITION_KEY, layoutManager.findFirstVisibleItemPosition());
    }

    private void setLayoutManager(int oldPosition, boolean isOldParcel) {
        errorText.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        CoordinatorLayout root = (CoordinatorLayout) findViewById(R.id.activity_movies);
        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (isTablet) {
                    int columnWidth = (int) getResources().getDimension(R.dimen.movies_grid_width);
//                    Log.d("ColumnWidth", columnWidth + "");
                    layoutManager = new GridAutofitLayoutManager(MoviesActivity.this, columnWidth);
                } else {
                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        layoutManager = new GridLayoutManager(MoviesActivity.this, 2);
                    } else {
                        layoutManager = new GridLayoutManager(MoviesActivity.this, 3);
                    }
                }
                recyclerView.setLayoutManager(layoutManager);
                if (isOldParcel) {
                    setAdapterToRecycleView(parcel, oldPosition);
                }
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
                    root.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                else
                    root.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private void setAdapterToRecycleView(MovieParcel parcel) {
        movieAdapter = new MovieAdapter(MoviesActivity.this, parcel);
        recyclerView.setAdapter(movieAdapter);
        chooseTitle();
        recyclerView.setVisibility(View.VISIBLE);
        errorText.setVisibility(View.GONE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void setAdapterToRecycleView(MovieParcel oldParcel, int oldPosition) {
        movieAdapter = new MovieAdapter(MoviesActivity.this, oldParcel);
        recyclerView.setAdapter(movieAdapter);
        layoutManager.scrollToPosition(oldPosition);
        chooseTitle();
        recyclerView.setVisibility(View.VISIBLE);
        errorText.setVisibility(View.GONE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void chooseTitle() {
        if (getSupportActionBar() == null) {
            return;
        }
        if (currentType == PARCEL_TYPE_TOP) {
            getSupportActionBar().setTitle(R.string.movie_cat_top);
        } else if (currentType == PARCEL_TYPE_POPULAR) {
            getSupportActionBar().setTitle(R.string.movie_cat_popular);
        }
    }

    private void addClickSupport() {
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(
                (recyclerView, position, v) -> {
                    MovieData clickedParcel = movieAdapter.getMovieAtPosition(position);
                    boolean fav = false;
                    if (favIds.contains(clickedParcel.getId())) {
                        fav = true;
                    }
                    Intent i = new Intent(MoviesActivity.this, MovieDetailsActivity.class);
                    i.putExtra(MovieDetailsActivity.KEY_PARCEL_MOVIE, clickedParcel);
                    i.putExtra(MovieDetailsActivity.KEY_IS_FAV, fav);
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(MoviesActivity.this, v, "poster");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        startActivity(i, options.toBundle());
                    } else {
                        startActivity(i);
                    }
                }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(Constants.getMovieBlPopularComplete());
        intentFilter.addAction(Constants.getMovieBlTopComplete());
        intentFilter.addAction(Constants.getMovieBlPopularError());
        intentFilter.addAction(Constants.getMovieBlTopError());
        LocalBroadcastManager.getInstance(MoviesActivity.this).registerReceiver(receiver, intentFilter);
        try {
            favIds = MovieDbHelper.getInstance(this).getFavMoviesIds();
        } catch (Exception e) {
            favIds = new ArrayList<>();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(MoviesActivity.this).unregisterReceiver(receiver);
    }

    private void complete(MovieParcel parcel) {
        setAdapterToRecycleView(parcel);
    }

    private void error(String error) {
        //TODO: better error handling
        if (!Utils.checkNetworkConnected(this)) {
            errorText.setText(R.string.error_no_internet);
        } else {
            errorText.setText(R.string.error_unknown);
        }
        errorText.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

}

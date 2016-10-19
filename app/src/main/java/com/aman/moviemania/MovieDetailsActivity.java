package com.aman.moviemania;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.aman.moviemania.adapter.ReviewsAdapter;
import com.aman.moviemania.adapter.TrailerAdapter;
import com.aman.moviemania.helper.Constants;
import com.aman.moviemania.helper.ItemClickSupport;
import com.aman.moviemania.helper.MovieDbHelper;
import com.aman.moviemania.helper.RestHelper;
import com.aman.moviemania.parcel.MovieData;
import com.aman.moviemania.parcel.ReviewsParcel;
import com.aman.moviemania.parcel.ReviewsParcelResults;
import com.aman.moviemania.parcel.VideoParcel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import rx.Observer;

public class MovieDetailsActivity extends AppCompatActivity {

    public static final String KEY_PARCEL_MOVIE = "movie_parcel_key";
    private static final String TRAILER_PARCEL_KEY = "trailer_parcel_key";
    private static final String REVIEWS_PARCEL_KEY = "reviews_parcel_key";
    private VideoParcel trailerParcel;
    private ReviewsParcel reviewsParcel;
    private TextView title, date, content;
    private ImageView heroImage;
    private TextView ratings;
    private RecyclerView trailers, reviews;
    private MovieData parcel;

    private static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(null);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            try {
                MovieDbHelper.getInstance(MovieDetailsActivity.this).addMovieToFav(parcel);
                Snackbar.make(view, "Added to favorite", Snackbar.LENGTH_LONG)
                        .show();
            } catch (Exception e) {
                Snackbar.make(view, e.getMessage(), Snackbar.LENGTH_LONG)
                        .show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        parcel = getIntent().getParcelableExtra(KEY_PARCEL_MOVIE);
        initViews();
        if (parcel != null) {
            addDatas(parcel);
            bottomSheetInteractions();
            if (savedInstanceState == null) {
                loadVideosData(parcel.getId());
                loadReviewsData(parcel.getId());
            } else {
                trailerParcel = savedInstanceState.getParcelable(TRAILER_PARCEL_KEY);
                reviewsParcel = savedInstanceState.getParcelable(REVIEWS_PARCEL_KEY);
                if (trailerParcel != null) {
                    TrailerAdapter adapter = new TrailerAdapter(MovieDetailsActivity.this, trailerParcel);
                    trailers.setAdapter(adapter);
                } else {
                    trailers.setVisibility(View.GONE);
                }
                if (reviewsParcel != null) {
                    for (ReviewsParcelResults data : reviewsParcel.getResults()) {
                        Log.d("Comment B", data.getContent().length() + "");
                        data.setContent(data.getContent().substring(0, 200) + "...");
                        Log.d("Comment A", data.getContent().length() + "");
                    }
                    ReviewsAdapter adapter = new ReviewsAdapter(MovieDetailsActivity.this, reviewsParcel);
                    reviews.setAdapter(adapter);
                } else {
                    reviews.setVisibility(View.GONE);
                }
            }
        }

    }

    private void initViews() {
        title = (TextView) findViewById(R.id.movie_detail_title);
        date = (TextView) findViewById(R.id.movie_detail_date);
        content = (TextView) findViewById(R.id.movie_detail_content);
        heroImage = (ImageView) findViewById(R.id.hero_image_movie_detail);
        ratings = (TextView) findViewById(R.id.movie_detail_rating);
        trailers = (RecyclerView) findViewById(R.id.recyclerView_trailers);
        trailers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        reviews = (RecyclerView) findViewById(R.id.recyclerView_reviews);
        reviews.setLayoutManager(new LinearLayoutManager(this));
        trailers.setHasFixedSize(true);
//        reviews.setHasFixedSize(true);
        reviews.getLayoutManager().setAutoMeasureEnabled(true);
        addClickSupport();
    }

    private void addClickSupport() {
        ItemClickSupport.addTo(trailers).setOnItemClickListener(
                (recyclerView, position, v) -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" +
                        trailerParcel.getResults().get(position).getKey())))
        );
    }

    private void bottomSheetInteractions() {
        View bottomSheet = findViewById(R.id.bottom_sheet);
        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        final ColorMatrix matrix = new ColorMatrix();
        ValueAnimator animation = ValueAnimator.ofFloat(0f, 1f);
        animation.setDuration(500);
        animation.setInterpolator(new FastOutLinearInInterpolator());
        animation.addUpdateListener(animation1 -> {
            matrix.setSaturation(animation1.getAnimatedFraction());
            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
            heroImage.setColorFilter(filter);
        });
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                animation.setCurrentPlayTime((long) (500 * (1 - slideOffset)));
            }
        });
    }

    private void addDatas(MovieData parcel) {
        title.setText(parcel.getTitle());
        date.setText(parcel.getReleaseDate());
        content.setText(parcel.getOverview());
        String rate = String.format("%s/10", parcel.getVoteAverage());
        Spannable span = new SpannableString(rate);
        span.setSpan(new RelativeSizeSpan(1.5f), 0, (parcel.getVoteAverage() + "").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ratings.setText(span);
        String url = Constants.getMovieBaseImageUrl(false) + parcel.getPosterPath();
        Glide.with(this).load(url).diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        generateColor(resource);
                        return false;
                    }
                })
                .into(heroImage);
    }

    private void loadVideosData(int id) {
        RestHelper.getTrailersForMovie(id).subscribe(new Observer<VideoParcel>() {
            @Override
            public void onCompleted() {
                if (trailerParcel != null) {
                    TrailerAdapter adapter = new TrailerAdapter(MovieDetailsActivity.this, trailerParcel);
                    trailers.setAdapter(adapter);
                } else {
                    trailers.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(Throwable e) {
                trailers.setVisibility(View.GONE);
            }

            @Override
            public void onNext(VideoParcel videoParcel) {
                if (videoParcel.getResults().isEmpty()) {
                    trailers.setVisibility(View.GONE);
                    return;
                }
                trailerParcel = videoParcel;
            }
        });
    }

    private void loadReviewsData(int id) {
        RestHelper.getReviewsForMovie(id).subscribe(new Observer<ReviewsParcel>() {
            @Override
            public void onCompleted() {
                if (reviewsParcel != null) {
                    for (ReviewsParcelResults data : reviewsParcel.getResults()) {
                        Log.d("Comment B", data.getContent().length() + "");
                        data.setContent(data.getContent().substring(0, Math.min(200, data.getContent().length())) + "...");
                        Log.d("Comment A", data.getContent().length() + "");
                    }
                    ReviewsAdapter adapter = new ReviewsAdapter(MovieDetailsActivity.this, reviewsParcel);
                    reviews.setAdapter(adapter);
                } else {
                    reviews.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ReviewsParcel reviewsParcel) {
                if (reviewsParcel.getResults().length == 0) {
                    reviews.setVisibility(View.GONE);
                    return;
                }
                MovieDetailsActivity.this.reviewsParcel = reviewsParcel;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (trailerParcel != null) {
            outState.putParcelable(TRAILER_PARCEL_KEY, trailerParcel);
        }
        if (reviewsParcel != null) {
            outState.putParcelable(REVIEWS_PARCEL_KEY, reviewsParcel);
        }
    }

    private void generateColor(Drawable resource) {
        Palette.PaletteAsyncListener paletteListener = palette -> {
            // access palette colors here
            int vibrant = getBackgroundColor(palette);
            changeBackgroundColor(vibrant);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                changeStatusBarColor(vibrant);
            }
        };

        Bitmap myBitmap = MovieDetailsActivity.drawableToBitmap(resource);
        if (myBitmap != null && !myBitmap.isRecycled()) {
            Palette.from(myBitmap).generate(paletteListener);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void changeStatusBarColor(int vibrant) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(vibrant);
    }

    private int getBackgroundColor(Palette palette) {
        int defaultC = ContextCompat.getColor(MovieDetailsActivity.this, R.color.primary_text);
        int vibrant = palette.getVibrantColor(defaultC);
        if (palette.getVibrantSwatch() != null) {
            int textColor = palette.getVibrantSwatch().getTitleTextColor();
            title.setTextColor(textColor);
            ratings.setTextColor(textColor);
            date.setTextColor(palette.getVibrantSwatch().getBodyTextColor());
        } else {
            title.setTextColor(Color.WHITE);
            ratings.setTextColor(Color.WHITE);
            date.setTextColor(Color.LTGRAY);
        }
        return vibrant;
    }

    private void changeBackgroundColor(int vibrant) {
        int colorFrom = ContextCompat.getColor(MovieDetailsActivity.this, R.color.white);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, vibrant);
        colorAnimation.setInterpolator(new DecelerateInterpolator());
        colorAnimation.setDuration(1000); // milliseconds
        colorAnimation.addUpdateListener(animator -> findViewById(R.id.movie_detail_header_layout).setBackgroundColor((int) animator.getAnimatedValue()));
        colorAnimation.start();
    }
}

package com.aman.moviemania;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.aman.moviemania.helper.Constants;
import com.aman.moviemania.parcel.MovieData;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class MovieDetailsActivity extends AppCompatActivity {

    public static final String KEY_PARCEL_MOVIE = "movie_parcel_key";
    private TextView title, date, content;
    private ImageView heroImage;
    private TextView ratings;

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
        fab.setOnClickListener(view -> Snackbar.make(view, "Favourite icon.Coming Soon", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        MovieData parcel = getIntent().getParcelableExtra(KEY_PARCEL_MOVIE);
        title = (TextView) findViewById(R.id.movie_detail_title);
        date = (TextView) findViewById(R.id.movie_detail_date);
        content = (TextView) findViewById(R.id.movie_detail_content);
        heroImage = (ImageView) findViewById(R.id.hero_image_movie_detail);
        ratings = (TextView) findViewById(R.id.movie_detail_rating);
        if (parcel != null) {
            addDatas(parcel);
            bottomSheetInteractions();
        }

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
        int colorChange = ContextCompat.getColor(MovieDetailsActivity.this, R.color.accent);
        span.setSpan(new RelativeSizeSpan(1.5f), 0, (parcel.getVoteAverage() + "").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        span.setSpan(new ForegroundColorSpan(colorChange), 0, (parcel.getVoteAverage() + "").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
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
            date.setTextColor(palette.getVibrantSwatch().getBodyTextColor());
        } else {
            title.setTextColor(Color.WHITE);
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

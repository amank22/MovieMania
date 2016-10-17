package com.aman.moviemania.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.aman.moviemania.R;
import com.aman.moviemania.helper.Constants;
import com.aman.moviemania.parcel.MovieData;
import com.aman.moviemania.parcel.MovieParcel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

/**
 * Created by Aman Kapoor on 12-10-2016.
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private final MovieParcel parcel;
    private final Context context;

    public MovieAdapter(Context context, MovieParcel parcel) {
        this.parcel = parcel;
        this.context = context;
    }

    public MovieData getMovieAtPosition(int position) {
        return this.parcel.getMovieDatas().get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.movie_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MovieData mData = parcel.getMovieDatas().get(position);
        String url = Constants.getMovieBaseImageUrl(true) + mData.getPosterPath();
        Glide.with(context).load(url)
                .thumbnail(0.5f)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.mThumbnail);
    }

    @Override
    public int getItemCount() {
        return parcel == null ? 0 : parcel.getMovieDatas().size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        final ImageView mThumbnail;

        ViewHolder(View v) {
            super(v);
            mThumbnail = (ImageView) v;
        }
    }

}

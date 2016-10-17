package com.aman.moviemania.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aman.moviemania.R;
import com.aman.moviemania.helper.Constants;
import com.aman.moviemania.parcel.VideoData;
import com.aman.moviemania.parcel.VideoParcel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

/**
 * Created by Aman Kapoor on 12-10-2016.
 */
public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder> {

    private final VideoParcel parcel;
    private final Context context;

    public TrailerAdapter(Context context, VideoParcel parcel) {
        this.parcel = parcel;
        this.context = context;
    }

    public VideoData getTrailerAtPosition(int position) {
        return this.parcel.getResults().get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.trailers_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        VideoData mData = parcel.getResults().get(position);
        holder.mName.setText(mData.getName());
        holder.mSite.setText(mData.getSite());
        String url = Constants.getYoutubeThumbLink(mData.getKey());
        Glide.with(context).load(url)
                .thumbnail(0.5f)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.mThumbnail);
    }

    @Override
    public int getItemCount() {
        return parcel == null ? 0 : parcel.getResults().size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        final ImageView mThumbnail;
        final TextView mName;
        final TextView mSite;

        ViewHolder(View v) {
            super(v);
            mThumbnail = (ImageView) v.findViewById(R.id.trailers_image);
            mName = (TextView) v.findViewById(R.id.trailers_name);
            mSite = (TextView) v.findViewById(R.id.trailers_site);
        }
    }

}

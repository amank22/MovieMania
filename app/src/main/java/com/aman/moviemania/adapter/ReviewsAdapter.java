package com.aman.moviemania.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aman.moviemania.R;
import com.aman.moviemania.parcel.ReviewsParcel;
import com.aman.moviemania.parcel.ReviewsParcelResults;

/**
 * Created by Aman Kapoor on 12-10-2016.
 */
public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {

    private final ReviewsParcel parcel;
    private final Context context;

    public ReviewsAdapter(Context context, ReviewsParcel parcel) {
        this.parcel = parcel;
        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.reviews_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ReviewsParcelResults mData = parcel.getResults()[position];
        holder.mUser.setText(mData.getAuthor());
        holder.mComment.setText(mData.getContent());
        Log.d("Comment In", mData.getContent().length() + "");
    }

    @Override
    public int getItemCount() {
        return parcel == null ? 0 : parcel.getResults().length;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        final TextView mUser;
        final TextView mComment;

        ViewHolder(View v) {
            super(v);
            mUser = (TextView) v.findViewById(R.id.reviews_user);
            mComment = (TextView) v.findViewById(R.id.reviews_comment);
        }
    }

}

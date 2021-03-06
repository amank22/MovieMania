package com.aman.moviemania.parcel;

import android.os.Parcel;
import android.os.Parcelable;

public class ReviewsParcel implements Parcelable {
    public static final Creator<ReviewsParcel> CREATOR = new Creator<ReviewsParcel>() {
        @Override
        public ReviewsParcel createFromParcel(Parcel source) {
            ReviewsParcel var = new ReviewsParcel();
            var.id = source.readInt();
            var.page = source.readInt();
            var.total_pages = source.readInt();
            var.results = source.createTypedArray(ReviewsParcelResults.CREATOR);
            var.total_results = source.readInt();
            return var;
        }

        @Override
        public ReviewsParcel[] newArray(int size) {
            return new ReviewsParcel[size];
        }
    };
    private int id;
    private int page;
    private int total_pages;
    private ReviewsParcelResults[] results;
    private int total_results;

    public int getId() {
        return this.id;
    }

    public int getPage() {
        return this.page;
    }

    public int getTotal_pages() {
        return this.total_pages;
    }

    public ReviewsParcelResults[] getResults() {
        return this.results;
    }

    public int getTotal_results() {
        return this.total_results;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.page);
        dest.writeInt(this.total_pages);
        dest.writeTypedArray(this.results, flags);
        dest.writeInt(this.total_results);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}

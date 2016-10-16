package com.aman.moviemania.parcel;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aman Kapoor on 12-10-2016.
 */

public class MovieParcel implements Parcelable {


    @SuppressWarnings("unused")
    public static final Creator<MovieParcel> CREATOR = new Creator<MovieParcel>() {
        @Override
        public MovieParcel createFromParcel(Parcel in) {
            return new MovieParcel(in);
        }

        @Override
        public MovieParcel[] newArray(int size) {
            return new MovieParcel[size];
        }
    };
    @SerializedName("page")
    @Expose
    private int page;
    @SerializedName("results")
    @Expose
    private List<MovieData> movieDatas = new ArrayList<>();
    @SerializedName("total_results")
    @Expose
    private int totalResults;
    @SerializedName("total_pages")
    @Expose
    private int totalPages;

    /**
     * No args constructor for use in serialization
     */
    public MovieParcel() {
    }


    /**
     * @param movieDatas
     * @param totalResults
     * @param page
     * @param totalPages
     */
    public MovieParcel(int page, List<MovieData> movieDatas, int totalResults, int totalPages) {
        this.page = page;
        this.movieDatas = movieDatas;
        this.totalResults = totalResults;
        this.totalPages = totalPages;
    }

    public MovieParcel(Parcel in) {
        page = in.readInt();
        if (in.readByte() == 0x01) {
            movieDatas = new ArrayList<>();
            in.readList(movieDatas, MovieData.class.getClassLoader());
        } else {
            movieDatas = null;
        }
        totalResults = in.readInt();
        totalPages = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(page);
        if (movieDatas == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(movieDatas);
        }
        dest.writeInt(totalResults);
        dest.writeInt(totalPages);
    }

    public int getPage() {
        return page;
    }

    public List<MovieData> getMovieDatas() {
        return movieDatas;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public int getTotalPages() {
        return totalPages;
    }
}
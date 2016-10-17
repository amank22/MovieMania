package com.aman.moviemania.parcel;

/**
 * Created by Aman Kapoor on 17-10-2016.
 */

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class VideoParcel implements Parcelable {

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<VideoParcel> CREATOR = new Parcelable.Creator<VideoParcel>() {
        @Override
        public VideoParcel createFromParcel(Parcel in) {
            return new VideoParcel(in);
        }

        @Override
        public VideoParcel[] newArray(int size) {
            return new VideoParcel[size];
        }
    };
    @SerializedName("id")
    @Expose
    public int id;
    @SerializedName("results")
    @Expose
    private List<VideoData> results = new ArrayList<>();

    /**
     * No args constructor for use in serialization
     */
    public VideoParcel() {
    }


    /**
     * @param id
     * @param results
     */
    public VideoParcel(int id, List<VideoData> results) {
        this.id = id;
        this.results = results;
    }

    protected VideoParcel(Parcel in) {
        id = in.readInt();
        if (in.readByte() == 0x01) {
            results = new ArrayList<VideoData>();
            in.readList(results, VideoData.class.getClassLoader());
        } else {
            results = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        if (results == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(results);
        }
    }

    public int getId() {
        return id;
    }

    public List<VideoData> getResults() {
        return results;
    }
}
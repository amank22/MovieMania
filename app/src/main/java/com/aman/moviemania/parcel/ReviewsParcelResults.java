package com.aman.moviemania.parcel;

import android.os.Parcel;
import android.os.Parcelable;

public class ReviewsParcelResults implements Parcelable {
    public static final Creator<ReviewsParcelResults> CREATOR = new Creator<ReviewsParcelResults>() {
        @Override
        public ReviewsParcelResults createFromParcel(Parcel source) {
            ReviewsParcelResults var = new ReviewsParcelResults();
            var.author = source.readString();
            var.id = source.readString();
            var.content = source.readString();
            var.url = source.readString();
            return var;
        }

        @Override
        public ReviewsParcelResults[] newArray(int size) {
            return new ReviewsParcelResults[size];
        }
    };
    private String author;
    private String id;
    private String content;
    private String url;

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.author);
        dest.writeString(this.id);
        dest.writeString(this.content);
        dest.writeString(this.url);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}

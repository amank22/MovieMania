package com.aman.moviemania.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by aman.kapoor on 10/18/2016.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    private static MovieDbHelper sInstance;

    public static synchronized MovieDbHelper getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new MovieDbHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private MovieDbHelper(Context context) {
        super(context, MovieContract.DATABASE_NAME, null, MovieContract.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MovieContract.MOVIE.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(MovieContract.MOVIE.DELETE_TABLE);
    }


}

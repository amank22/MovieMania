package com.aman.moviemania.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.aman.moviemania.parcel.MovieData;
import com.aman.moviemania.parcel.MovieParcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aman.kapoor on 10/18/2016.
 * Not using content provider as the saving operations are very minimal and is limited to this application only.
 * Handling manually use-cases.
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

    public MovieParcel getFavMovieParcel() throws Exception {
        List<MovieData> results = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(MovieContract.MOVIE.TABLE_NAME, null, null, null, null, null, null, null);
        try {
            cursor.moveToFirst();
            while (cursor.isLast()) {
                MovieData data = new MovieData(cursor.getInt(0), cursor.getString(MovieContract.MOVIE.COLUMN_INDEX_TITLE),
                        cursor.getString(MovieContract.MOVIE.COLUMN_INDEX_RELEASE_DATE)
                        , cursor.getString(MovieContract.MOVIE.COLUMN_INDEX_OVERVIEW),
                        cursor.getFloat(MovieContract.MOVIE.COLUMN_INDEX_VOTE_AVERAGE),
                        cursor.getString(MovieContract.MOVIE.COLUMN_INDEX_POSTER_PATH));
                results.add(data);
            }
        } catch (Exception e){
            throw e;
        }finally {
            cursor.close();
        }
        if (results.isEmpty()){
            throw new Exception("Try click on the heart icon on movie description");
        }
        return new MovieParcel(1, results, results.size(), 1);
    }


    public void addMovieToFav(MovieData data) throws Exception {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MovieContract.MOVIE._ID, data.getId());
        values.put(MovieContract.MOVIE.COLUMN_TITLE, data.getTitle());
        values.put(MovieContract.MOVIE.COLUMN_RELEASE_DATE, data.getReleaseDate());
        values.put(MovieContract.MOVIE.COLUMN_OVERVIEW, data.getOverview());
        values.put(MovieContract.MOVIE.COLUMN_VOTE_AVERAGE, data.getVoteAverage());
        values.put(MovieContract.MOVIE.COLUMN_POSTER_PATH, data.getPosterPath());

        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert(MovieContract.MOVIE.TABLE_NAME, null, values);
        if (newRowId == -1) {
            throw new Exception("Error adding movie to favorite");
        }
    }

    public void removeMovieFromFav(int id) throws Exception {
        SQLiteDatabase db = getWritableDatabase();
        int affected = db.delete(MovieContract.MOVIE.TABLE_NAME, MovieContract.MOVIE._ID + "=?",
                new String[]{String.valueOf(id)});
        if (affected == 0) {
            throw new Exception("Unable to remove favorite. Please try again.");
        }
    }
}

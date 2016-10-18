package com.aman.moviemania.helper;

import android.provider.BaseColumns;

/**
 * Created by aman.kapoor on 10/18/2016.
 */
public class MovieContract {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "movies.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";

    public MovieContract() {
    }

    public static abstract class MOVIE implements BaseColumns {
        public static final String TABLE_NAME = "movies_table";

        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_OVERVIEW + TEXT_TYPE + COMMA_SEP +
                COLUMN_RELEASE_DATE + TEXT_TYPE + COMMA_SEP +
                COLUMN_TITLE + TEXT_TYPE + COMMA_SEP +
                COLUMN_VOTE_AVERAGE + INTEGER_TYPE + " )";
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

}

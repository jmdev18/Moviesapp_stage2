package com.example.santiago.moviesapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.santiago.moviesapp.data.MoviesContract.MoviesEntry;

/**
 * Created by Santiago on 22/12/2017.
 */

public class MoviesDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "waitlist.db";
    private static final int DATABASE_VERSION = 1;
    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
         final String TEXT_TYPE = " TEXT";
         final String COMMA_SEP = ",";
         final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + MoviesEntry.TABLE_NAME + " (" +
                        MoviesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        MoviesEntry.MOVIE_ID + TEXT_TYPE + COMMA_SEP +
                        MoviesEntry.MOVIE_TITLE + TEXT_TYPE + COMMA_SEP +
                        MoviesEntry.MOVIE_IMAGE_URL + TEXT_TYPE + COMMA_SEP +
                        MoviesEntry.MOVIE_VOTE_AVERAGE + TEXT_TYPE + COMMA_SEP +
                        MoviesEntry.MOVIE_RELEASE_DATE + TEXT_TYPE + COMMA_SEP +
                        MoviesEntry.MOVIE_SIPNOSIS + TEXT_TYPE + COMMA_SEP+
                        MoviesEntry.MOVIE_ADDED_FAVORITES + " NUMERIC"+" )";
         sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // For now simply drop the table and create a new one. This means if you change the
        // DATABASE_VERSION the table will be dropped.
        // In a production app, this method might be modified to ALTER the table
        // instead of dropping it, so that existing data is not deleted.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}

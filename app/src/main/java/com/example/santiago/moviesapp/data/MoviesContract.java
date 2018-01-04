package com.example.santiago.moviesapp.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Santiago on 20/12/2017.
 */

public class MoviesContract {
    public static final String AUTHORITY = "com.example.santiago.moviesapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+AUTHORITY);
    public static final String PATH_MOVIES ="movies";
    public static class MoviesEntry implements BaseColumns {

        public final static Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String TABLE_NAME = "movies";
        public static final String _ID = BaseColumns._ID;
        public static final String MOVIE_ID = "movie_id";
        public static final String MOVIE_TITLE = "title";
        public static final String MOVIE_IMAGE_URL = "image_url";
        public static final String MOVIE_VOTE_AVERAGE = "vote_average";
        public static final String MOVIE_RELEASE_DATE = "release_date";
        public static final String MOVIE_SIPNOSIS = "sipnosis";
        public static final String MOVIE_ADDED_FAVORITES ="is_checked";

    }
}

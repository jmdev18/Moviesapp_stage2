package com.example.santiago.moviesapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.santiago.moviesapp.Models.Review;
import com.example.santiago.moviesapp.Utilities.NetworkUtils;
import com.example.santiago.moviesapp.adapters.RecyclerReviewsAdapter;
import com.example.santiago.moviesapp.adapters.RecyclerTrailersAdapter;
import com.example.santiago.moviesapp.data.MoviesContract;
import com.example.santiago.moviesapp.data.MoviesDbHelper;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.util.List;

import static android.content.ContentValues.TAG;
import static com.example.santiago.moviesapp.data.MoviesContract.MoviesEntry.CONTENT_URI;
import static com.example.santiago.moviesapp.data.MoviesContract.MoviesEntry.MOVIE_ADDED_FAVORITES;
import static com.example.santiago.moviesapp.data.MoviesContract.MoviesEntry.TABLE_NAME;


public class DetailActivity extends AppCompatActivity implements
        RecyclerTrailersAdapter.TrailersAdapterOnClickHandler, LoaderManager.LoaderCallbacks<String[]> {
    private static final String TAG = "DetailActivity";
    private static final int TRAILERS_LOADER = 1;
    private static final int REVIEWS_LOADER = 2;

    private RecyclerView recyclerViewTrailers;
    private RecyclerTrailersAdapter recyclerTrailersAdapter;
    private final String KEY_ID_TRAILERS = "id";
    private TextView noTrailersTextView;

    private RecyclerView recyclerViewReviews;
    private RecyclerReviewsAdapter recyclerReviewsAdapter;
    private final String KEY_ID_REVIEWS = "id";
    private TextView noReviewsTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        TextView movieTitle = findViewById(R.id.movieTitle);
        ImageView imageMovie = findViewById(R.id.imageDetailMovie);
        TextView releaseDate = findViewById(R.id.releaseDateMovie);
        RatingBar voteAverage = findViewById(R.id.voteAverageMovie);
        TextView sipnosis = findViewById(R.id.sipnosisMovie);
        final CheckBox addToFavorite = findViewById(R.id.addToFavorite);
        recyclerViewTrailers = findViewById(R.id.recyclerTrailers);
        noTrailersTextView = findViewById(R.id.noTrailers);
        recyclerViewReviews = findViewById(R.id.recyclerReviews);
        noReviewsTextView = findViewById(R.id.noReviews);

        final Intent intent = getIntent();

        if (intent.hasExtra("movieTitle")) {
            movieTitle.setText(intent.getStringExtra("movieTitle"));
        }
        if (intent.hasExtra("movieUrlImage")) {
            Picasso.with(this).load(intent.getStringExtra("movieUrlImage")).into(imageMovie);
        }
        if (intent.hasExtra("movieReleaseDate")) {
            releaseDate.setText(intent.getStringExtra("movieReleaseDate"));
        }
        if (intent.hasExtra("movieVoteAverage")) {
            voteAverage.setRating(Float.parseFloat(intent.getStringExtra("movieVoteAverage")) / 2);
        }
        if (intent.hasExtra("movieSynopsis")) {
            sipnosis.setText(intent.getStringExtra("movieSynopsis"));
        }
        Cursor cursor = getContentResolver().query(CONTENT_URI, null, "movie_id=?", new String[]{intent.getStringExtra("movieId")}, null);
        if (cursor!=null){
            if (cursor.getCount()>0){
                int isAdded = 0;
                while (cursor.moveToNext()){
                    isAdded = cursor.getInt(cursor.getColumnIndex(MoviesContract.MoviesEntry.MOVIE_ADDED_FAVORITES));
                }
                if (isAdded==1){
                    addToFavorite.setChecked(true);
                }else{
                    addToFavorite.setChecked(false);
                }
            }
            cursor.close();
        }

        if (intent.hasExtra("movieId")) {
            addToFavorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                    if (isChecked) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(MoviesContract.MoviesEntry.MOVIE_ID, intent.getStringExtra("movieId"));
                        contentValues.put(MoviesContract.MoviesEntry.MOVIE_TITLE, intent.getStringExtra("movieTitle"));
                        contentValues.put(MoviesContract.MoviesEntry.MOVIE_IMAGE_URL, intent.getStringExtra("movieUrlImage"));
                        contentValues.put(MoviesContract.MoviesEntry.MOVIE_VOTE_AVERAGE, intent.getStringExtra("movieVoteAverage"));
                        contentValues.put(MoviesContract.MoviesEntry.MOVIE_RELEASE_DATE, intent.getStringExtra("movieReleaseDate"));
                        contentValues.put(MoviesContract.MoviesEntry.MOVIE_SIPNOSIS, intent.getStringExtra("movieSynopsis"));
                        contentValues.put(MoviesContract.MoviesEntry.MOVIE_ADDED_FAVORITES, 1);
                        Uri uri = getContentResolver().insert(CONTENT_URI, contentValues);
                        Log.d(TAG, "onCheckedChanged: operaation = " + uri);
                        Toast.makeText(getApplicationContext(), "Added to favorites", Toast.LENGTH_SHORT).show();
                    } else {
                        Uri uri = CONTENT_URI.buildUpon().appendPath(intent.getStringExtra("movieId")).build();
                        int rowsDeleted = getContentResolver().delete(uri, null, null);
                        Toast.makeText(getApplicationContext(), "Deleted from favorites " + rowsDeleted, Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
        loadTrailers(intent.getStringExtra("movieId"));
        loadReviews(intent.getStringExtra("movieId"));
    }

    @Override
    public void onClickTrailers(final String url) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(appIntent);
    }

    public void showDataTrailers() {
        recyclerViewTrailers.setVisibility(View.VISIBLE);
        noTrailersTextView.setVisibility(View.GONE);
    }

    public void showNoTrialers() {
        recyclerViewTrailers.setVisibility(View.GONE);
        noTrailersTextView.setVisibility(View.VISIBLE);
    }

    public void showDataReviews() {
        recyclerViewReviews.setVisibility(View.VISIBLE);
        noReviewsTextView.setVisibility(View.GONE);
    }

    public void showNoReviews() {
        recyclerViewReviews.setVisibility(View.GONE);
        noReviewsTextView.setVisibility(View.VISIBLE);
    }

    private void loadReviews(String id) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_ID_REVIEWS, id);
        LoaderManager loaderManager = getSupportLoaderManager();

        Loader<List<Review>> reviewsLoader = loaderManager.getLoader(REVIEWS_LOADER);

        if (reviewsLoader == null) {
            loaderManager.initLoader(REVIEWS_LOADER, bundle, loaderReview);
        } else {
            loaderManager.restartLoader(REVIEWS_LOADER, bundle, loaderReview);
        }
    }

    private void loadTrailers(String id) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_ID_TRAILERS, id);
        LoaderManager loaderManager = getSupportLoaderManager();

        Loader<String[]> trailersLoader = loaderManager.getLoader(TRAILERS_LOADER);

        if (trailersLoader == null) {
            loaderManager.initLoader(TRAILERS_LOADER, bundle, this);
        } else {
            loaderManager.restartLoader(TRAILERS_LOADER, bundle, this);
        }
    }

    private LoaderManager.LoaderCallbacks<List<Review>> loaderReview =
            new LoaderManager.LoaderCallbacks<List<Review>>() {
                @Override
                public Loader<List<Review>> onCreateLoader(int id, final Bundle args) {
                    return new AsyncTaskLoader<List<Review>>(getApplicationContext()) {
                        @Override
                        protected void onStartLoading() {
                            if (args == null) {
                                return;
                            }
                            showDataReviews();
                            forceLoad();
                        }

                        @Override
                        public List<Review> loadInBackground() {
                            try {
                                String id = args.getString(KEY_ID_REVIEWS);
                                List<Review> reviewList = NetworkUtils.getMovieReviews(id);
                                return reviewList;
                            } catch (Exception e) {
                                return null;
                            }
                        }
                    };
                }

                @Override
                public void onLoadFinished(Loader<List<Review>> loader, List<Review> data) {
                    if (data == null || data.size() < 1) {
                        showNoReviews();
                    } else {
                        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                        recyclerViewReviews.setLayoutManager(layoutManager);
                        recyclerViewReviews.setHasFixedSize(true);
                        recyclerReviewsAdapter = new RecyclerReviewsAdapter(getApplicationContext());
                        recyclerViewReviews.setAdapter(recyclerReviewsAdapter);
                        recyclerReviewsAdapter.setData(data);
                    }
                }

                @Override
                public void onLoaderReset(Loader<List<Review>> loader) {

                }
            };

    @Override
    public Loader<String[]> onCreateLoader(final int id, final Bundle args) {
        return new AsyncTaskLoader<String[]>(this) {
            @Override
            protected void onStartLoading() {
                if (args == null) {
                    return;
                }
                showDataTrailers();
                forceLoad();
            }

            @Override
            public String[] loadInBackground() {

                String idTrailers = args.getString(KEY_ID_TRAILERS);
                if (idTrailers == null || TextUtils.isEmpty(idTrailers)) {
                    return null;
                }
                try {
                    String[] videos = NetworkUtils.getMovieVideoUrls(idTrailers);
                    return videos;
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String[]> loader, String[] data) {
        if (data == null || data.length < 1) {
            showNoTrialers();
        } else {
            LinearLayoutManager layoutManager
                    = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
            recyclerViewTrailers.setLayoutManager(layoutManager);
            recyclerViewTrailers.setHasFixedSize(true);
            recyclerTrailersAdapter = new RecyclerTrailersAdapter(this, this);
            recyclerViewTrailers.setAdapter(recyclerTrailersAdapter);
            recyclerTrailersAdapter.setData(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<String[]> loader) {
        //not implemented yet
    }

}




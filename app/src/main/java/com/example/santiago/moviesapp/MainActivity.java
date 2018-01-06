package com.example.santiago.moviesapp;

import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.santiago.moviesapp.Models.Movie;
import com.example.santiago.moviesapp.Utilities.NetworkUtils;
import com.example.santiago.moviesapp.adapters.RecyclerAdapter;
import com.example.santiago.moviesapp.data.MoviesContract;
import com.example.santiago.moviesapp.data.MoviesDbHelper;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RecyclerAdapter.ListItemClickListener,
        LoaderManager.LoaderCallbacks<List<Movie>> {
    private static final String TAG = "MainActivity";

    //Reccycler
    private GridLayoutManager gridLayoutManager;
    private RecyclerView mRecyclerView;
    private RecyclerAdapter mRecyclerApdater;
    //Reccycler

    private TextView error;
    private String orderBy;
    private SwipeRefreshLayout swipeRefreshLayout;

    private static final int LOADER_ID = 1;

    final String ORDER_BY_KEY = "order_by";

    private static final int LOADER_FAVORITES = 2;

    private Parcelable scrollListState;
    private static final String SCROLL_PARCELABLE_KEY = "parcelable_scroll_position";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        error = findViewById(R.id.error);
        error.setVisibility(View.INVISIBLE);
        //handling refresh gesture
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i(TAG, "onRefresh called from SwipeRefreshLayout");
                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        loadData(orderBy);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
        );

        //recycler config
        mRecyclerView = findViewById(R.id.recycler);

        int orientation = getResources().getConfiguration().orientation;
        int smallestScreenWidthDp = getResources().getConfiguration().smallestScreenWidthDp;
        if (orientation == 1) {
            gridLayoutManager = new GridLayoutManager(this, 2);
        } else {
            gridLayoutManager = new GridLayoutManager(this, 3);
        }
        if (smallestScreenWidthDp == 600) {
            if (orientation == 1) {
                gridLayoutManager = new GridLayoutManager(this, 3);
            } else {
                gridLayoutManager = new GridLayoutManager(this, 4);
            }
        }

        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerApdater = new RecyclerAdapter(getApplicationContext(), this);
        mRecyclerView.setAdapter(mRecyclerApdater);
        //initialize the loader
        //recycler config
        orderBy = "popular";
        if (savedInstanceState != null) {
            orderBy = savedInstanceState.getString(ORDER_BY_KEY);
            loadData(orderBy);
        }
        loadData(orderBy);
    }

    private void showError() {
        //if we have an error
        //this method is for make invisible the movies and show a textview with a error
        error.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    private void showData() {
        //if all its correct
        //this method let us make visibe the movies and invisible the textview with the error
        mRecyclerView.setVisibility(View.VISIBLE);
        error.setVisibility(View.INVISIBLE);
    }

    private void loadData(String orderBy) {
        //here we call the showdata method for make visibles the movies
        //then we set the data of the movies to null
        //and finally we sync and
        showData();
        mRecyclerApdater.setData(null);
        scrollListState = null;
        if (orderBy.equals("favorites")) {
            LoaderManager loaderManager = getSupportLoaderManager();
            Loader<List<Movie>> favoritesLoader = loaderManager.getLoader(LOADER_ID);

            if (favoritesLoader == null) {
                loaderManager.initLoader(LOADER_FAVORITES, null, loaderFavorites);
            } else {
                loaderManager.restartLoader(LOADER_FAVORITES, null, loaderFavorites);
            }
            return;
        }
        Bundle queryBundle = new Bundle();
        queryBundle.putString(ORDER_BY_KEY, orderBy);
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<List<Movie>> moviesAppLoader = loaderManager.getLoader(LOADER_ID);

        if (moviesAppLoader == null) {
            loaderManager.initLoader(LOADER_ID, queryBundle, this);
        } else {
            loaderManager.restartLoader(LOADER_ID, queryBundle, this);
        }
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void onClick(Movie movieClicked) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra("movieId", movieClicked.getId());
        intent.putExtra("movieTitle", movieClicked.getTitle());
        intent.putExtra("movieUrlImage", movieClicked.getUrlImage());
        intent.putExtra("movieReleaseDate", movieClicked.getReleaseDate());
        intent.putExtra("movieVoteAverage", movieClicked.getVoteAverage());
        intent.putExtra("movieSynopsis", movieClicked.getSipnosis());
        startActivity(intent);
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<List<Movie>>(this) {
            List<Movie> movieList;

            @Override
            protected void onStartLoading() {
                if (args == null) {
                    return;
                }
                showData();
                if (movieList != null) {
                    deliverResult(movieList);
                } else {
                    forceLoad();
                }
            }

            @Override
            public List<Movie> loadInBackground() {
                String orderBy = args.getString(ORDER_BY_KEY);
                URL url = NetworkUtils.createUrl(orderBy);
                if (orderBy == null || TextUtils.isEmpty(orderBy)) {
                    return null;
                }
                try {
                    String responseRequest = NetworkUtils.makeHttpRequest(url);
                    List<Movie> newJsonMovieList = NetworkUtils.getJson(getApplicationContext(), responseRequest);
                    Log.d(TAG, "doInBackground: " + newJsonMovieList);
                    return newJsonMovieList;

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(List<Movie> data) {
                movieList = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {
        swipeRefreshLayout.setRefreshing(false);
        if (data != null) {
            showData();
            mRecyclerApdater.setData(data);
            mRecyclerView.getLayoutManager().onRestoreInstanceState(scrollListState);
        } else {
            showError();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {

    }

    private LoaderManager.LoaderCallbacks<List<Movie>> loaderFavorites =
            new LoaderManager.LoaderCallbacks<List<Movie>>() {
                @Override
                public Loader<List<Movie>> onCreateLoader(int id, final Bundle args) {
                    return new AsyncTaskLoader<List<Movie>>(getApplicationContext()) {
                        @Override
                        protected void onStartLoading() {
                            forceLoad();
                        }

                        @Override
                        public List<Movie> loadInBackground() {
                            List<Movie> movies = new ArrayList<>();
                            Cursor cursor = getContentResolver().query(MoviesContract.MoviesEntry.CONTENT_URI, null, null, null, null);
                            if (cursor != null) {
                                while (cursor.moveToNext()) {
                                    String movieId = cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.MOVIE_ID));
                                    String movieTitle = cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.MOVIE_TITLE));
                                    String imageUrl = cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.MOVIE_IMAGE_URL));
                                    String voteAverage = cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.MOVIE_VOTE_AVERAGE));
                                    String releaseDate = cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.MOVIE_RELEASE_DATE));
                                    String sipnosis = cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.MOVIE_SIPNOSIS));
                                    Log.d(TAG, "onOptionsItemSelected: " + imageUrl);
                                    Movie movie = new Movie(movieId, movieTitle, imageUrl, voteAverage, releaseDate, sipnosis);
                                    movies.add(movie);
                                }
                                cursor.close();
                            }
                            return movies;
                        }
                    };
                }

                @Override
                public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> data) {
                    if (orderBy.equals("favorites")) {
                        if (data != null) {
                            showData();
                            mRecyclerApdater.setData(data);
                            mRecyclerView.getLayoutManager().onRestoreInstanceState(scrollListState);
                        } else {
                            showError();
                        }
                    }
                }

                @Override
                public void onLoaderReset(Loader<List<Movie>> loader) {

                }
            };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                loadData(orderBy);
                return true;
            case R.id.popular:
                orderBy = "popular";
                loadData(orderBy);
                return true;
            case R.id.top_rated:
                orderBy = "top_rated";
                loadData(orderBy);
                return true;
            case R.id.favorites:
                orderBy = "favorites";
                loadData(orderBy);
                return true;
            case R.id.reset:
                MoviesDbHelper moviesDbHelper1 = new MoviesDbHelper(this);
                SQLiteDatabase sqLiteDatabase1 = moviesDbHelper1.getWritableDatabase();
                sqLiteDatabase1.delete(MoviesContract.MoviesEntry.TABLE_NAME, null, null);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ORDER_BY_KEY, orderBy);
        Parcelable scrollParcelable =mRecyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(SCROLL_PARCELABLE_KEY,scrollParcelable);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        scrollListState = savedInstanceState.getParcelable(SCROLL_PARCELABLE_KEY);
    }
}

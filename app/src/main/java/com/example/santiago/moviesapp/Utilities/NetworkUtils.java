package com.example.santiago.moviesapp.Utilities;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.santiago.moviesapp.Models.Movie;
import com.example.santiago.moviesapp.Models.Review;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Santiago on 15/11/2017.
 */

public final class NetworkUtils {

    private static final String BASE_URL = "https://api.themoviedb.org/3/movie/";
    private static final String API_KEY_KEY = "api_key";
    private static final String API_KEY_VALUE = "5b457fd223b36746a494b0f20be527db";
    private static final String TAG = "NetworkUtils";


    public static URL createUrl(String orderBy) {
        Uri uri = Uri.parse(BASE_URL).buildUpon().
                appendPath(orderBy).
                appendQueryParameter(API_KEY_KEY, API_KEY_VALUE).build();
        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (Exception e) {
            Log.d(TAG, "createUrl: " + e.getMessage());
        }
        return url;
    }

    public static URL createUrlTrailers(String id) {
        Uri uri = Uri.parse(BASE_URL).buildUpon().
                appendPath(id).
                appendPath("videos").
                appendQueryParameter(API_KEY_KEY, API_KEY_VALUE).build();
        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (Exception e) {
            Log.d(TAG, "createUrl: " + e.getMessage());
        }
        return url;
    }

    public static URL createUrlReviews(String id) {
        Uri uri = Uri.parse(BASE_URL).buildUpon().
                appendPath(id).
                appendPath("reviews").
                appendQueryParameter(API_KEY_KEY, API_KEY_VALUE).build();
        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (Exception e) {
            Log.d(TAG, "createUrl: " + e.getMessage());
        }
        return url;
    }

    public static String makeHttpRequest(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            InputStream inputStream = connection.getInputStream();
            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");
            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            connection.disconnect();
        }
    }

    public static List<Movie> getJson(Context context, String json) throws JSONException {
        List<Movie> jsonMovieList = new ArrayList<>();
        JSONObject jsonBase = new JSONObject(json);

        JSONArray jsonArray = jsonBase.getJSONArray("results");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject movie = jsonArray.getJSONObject(i);
            String id = String.valueOf(movie.getInt("id"));
            String title = movie.getString("title");
            String imageJson = movie.getString("poster_path");
            String image = "http://image.tmdb.org/t/p/w185/" + imageJson;
            String voteAverage = String.valueOf(movie.getDouble("vote_average"));
            //release date getting by parts
            String releaseDate = movie.getString("release_date");//example = 2017-12-01
            //String[] parts = releaseDate.split("-");//2017, 12, 01
            //String releaseDateYear = parts[0];
            String sipnosis = movie.getString("overview");
            //Review review = listReviews.get(0);
            Movie newMovie = new Movie(id, title, image, voteAverage, releaseDate, sipnosis);
            jsonMovieList.add(newMovie);
            //release date getting by parts
            //getting the trailers
        }
        return jsonMovieList;
    }

    public static String[] getMovieVideoUrls(String id) throws JSONException {
        URL videoJsonUrl = createUrlTrailers(id);
        String[] videoUrl = null;
        try {
            String responseRequest = makeHttpRequest(videoJsonUrl);
            videoUrl = getUrlVideoByJson(responseRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return videoUrl;
    }

    private static String[] getUrlVideoByJson(String json) throws JSONException {
        JSONObject jsonBase = new JSONObject(json);
        JSONArray jsonArray = jsonBase.getJSONArray("results");
        String[] urlVideos = new String[jsonArray.length()];
        String youtubePath = "https://www.youtube.com/watch?v=";
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject videoUrl = jsonArray.getJSONObject(i);
            String videoUrlKey = videoUrl.getString("key");
            urlVideos[i] = youtubePath + videoUrlKey;
        }
        return urlVideos;
    }

    public static List<Review> getMovieReviews(String id) throws JSONException {
        URL videoJsonUrl = createUrlReviews(id);
        List<Review> reviews = null;
        try {
            String responseRequest = makeHttpRequest(videoJsonUrl);
            reviews = getReviewsByJson(responseRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    private static List<Review> getReviewsByJson(String json) throws JSONException {
        List<Review> reviews = new ArrayList<>();

        JSONObject jsonBase = new JSONObject(json);
        JSONArray jsonArray = jsonBase.getJSONArray("results");

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject reviewReceived = jsonArray.getJSONObject(i);
            String authorReview = reviewReceived.getString("author");
            String review = reviewReceived.getString("content");
            Review actualReview = new Review(authorReview, review);
            reviews.add(actualReview);
        }
        return reviews;
    }
}
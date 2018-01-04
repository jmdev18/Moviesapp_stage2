package com.example.santiago.moviesapp.Models;

/**
 * Created by Santiago on 27/12/2017.
 */

public class Review {
    private String mAuthor;
    private String mReview;

    public Review(String author,String review){
        mAuthor =author;
        mReview = review;
    }
    public String getAuthor() {
        return mAuthor;
    }

    public String getReview() {
        return mReview;
    }
}

package com.example.santiago.moviesapp.Models;

import java.util.List;

/**
 * Created by Santiago on 18/11/2017.
 */

public class Movie {
    private String mId;
    private String mTitle;
    private String mUrlImage;
    private String mVoteAverage;
    private String mReleaseDate;
    private String mSipnosis;

    public Movie(String id ,String title,String urlImage, String voteAverage,
                 String releaseDate,String sipnosis){
        mId= id;
        mTitle = title;
        mUrlImage = urlImage;
        mVoteAverage = voteAverage;
        mReleaseDate = releaseDate;
        mSipnosis = sipnosis;
    }


    public String getId(){
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getUrlImage() {
        return mUrlImage;
    }

    public String getVoteAverage() {
        return mVoteAverage;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public String getSipnosis() {
        return mSipnosis;
    }

}

package com.example.android.popular_movies;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by abs on 2/24/2016.
 */
public class MovieItem implements Parcelable{       //Parcel
    @Override
    public int describeContents(){      //to describe the kinds of special objects contained
        return 0;
    }
    @Override
    public void writeToParcel(final Parcel dest, final int flag){      //write to parcel
        dest.writeString(mPosterPath);
        dest.writeString(mOverview);
        dest.writeString(mReleaseDate);
        dest.writeString(mId);
        dest.writeString(mOriginalTitle);
        dest.writeString(mVoteAverage);
    }
    //static CREATOR class creates your object from a Parcel via the createFromParcel
    public static final Parcelable.Creator<MovieItem> CREATOR = new Parcelable.Creator<MovieItem>(){
        @Override
        public MovieItem createFromParcel(final Parcel in) {      //takes in a parcel and passes it to a constructor
            return new MovieItem(in);
        }

        @Override
        public MovieItem[] newArray(final int size){       //allows an array of your objects to be parcelled
            return new MovieItem[size];
        }
    };

    private static final String BaseUrlImage = "http://image.tmdb.org/t/p/";
    private static final String UrlImageSize = "w500/";
    private final String mPosterPath ;
    private final String mOverview ;
    private final String mReleaseDate ;
    private final String mId;
    private final String mOriginalTitle ;
    private final String mVoteAverage ;

    public MovieItem(final String posterPath, final String overview,final String releaseDate, final String id, final String originalTitle,final String voteAverage){
        mPosterPath = BaseUrlImage + UrlImageSize + posterPath;
        mOverview = overview;
        mReleaseDate = releaseDate;
        mId = id;
        mOriginalTitle = originalTitle ;
        mVoteAverage = voteAverage;
    }

    private MovieItem( final Parcel in){
        mPosterPath = in.readString();
        mOverview = in.readString();
        mReleaseDate = in.readString();
        mId = in.readString();
        mOriginalTitle = in.readString();
        mVoteAverage = in.readString();
    }
    public String getId(){return mId;
    }
    public String getOriginalTitle(){ return mOriginalTitle;
    }
    public String getPosterPath(){ return mPosterPath;
    }
    public String getOverview(){ return mOverview;
    }
    public String getVoteAverage(){ return mVoteAverage;
    }
    public String getReleaseDate(final boolean year){
        return year ? mReleaseDate.substring(0,4) : mReleaseDate;
    }

}


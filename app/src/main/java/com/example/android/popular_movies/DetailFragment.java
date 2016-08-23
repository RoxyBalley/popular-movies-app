package com.example.android.popular_movies;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Switch;

import com.squareup.picasso.Picasso;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment {
    private static final String ARG_MOVIE = DetailFragment.class.getSimpleName() + "movie";

    private Switch mFavoriteSwitch;
    private MovieItem movie;

    public static DetailFragment newInstance(final MovieItem movie) {
        final DetailFragment fragment = new DetailFragment();
        final Bundle args = new Bundle();
        args.putParcelable(ARG_MOVIE, movie);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Empty constructor for system creation.
     */
    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle args = getArguments();
        movie = args.getParcelable(ARG_MOVIE);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            ((TextView)rootView.findViewById(R.id.movie_title)).setText(movie.getOriginalTitle());
            ((TextView)rootView.findViewById(R.id.movie_vote_avg)).setText(movie.getVoteAverage());
            ((TextView)rootView.findViewById(R.id.movie_overview)).setText(movie.getOverview());
            ((TextView)rootView.findViewById(R.id.movie_release_date)).setText(movie.getReleaseDate(true));
            String url = movie.getPosterPath();
            ImageView imageView = (ImageView)rootView.findViewById(R.id.movie_image);
            Picasso.with(getActivity()).load(url).into(imageView);

        return rootView;
    }
}

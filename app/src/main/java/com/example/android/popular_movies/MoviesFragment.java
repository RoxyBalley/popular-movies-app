package com.example.android.popular_movies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * A placeholder fragment containing a simple view.
 */
public class MoviesFragment extends Fragment {

    public MoviesFragment() {
    }
  /*  public interface Callback{
        //called when item selected in gridView
        void onItemSelected(MovieItem movieItem);
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    public final String LOGCAT = MoviesFragment.class.getSimpleName();
    private MoviesAdapter moviesAdapter;
    public int MAX_PAGES = 10;
    private int mPage = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        moviesAdapter = new MoviesAdapter(getActivity(), R.layout.movie_item, new ArrayList<MovieItem>());
        GridView gridView = (GridView) rootView.findViewById(R.id.grid_layout);
        gridView.setAdapter(moviesAdapter);
        fetchMoviesTask fetchMovie = new fetchMoviesTask();
        fetchMovie.execute("81acee672bd444827fa5abca3f5055da");
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                MovieItem movieDetails = moviesAdapter.getItem(position);
               // Toast.makeText(getActivity(),null, Toast.LENGTH_SHORT).show();
               // Intent intent = new Intent (getActivity(), DetailActivity.class).putExtra(MovieItem.EXTRA_MOVIE, movieDetails.toBundle() );
               // startActivity(intent);
                ((Callback) getActivity()).onItemSelected(movieDetails);
            }
        });
        if (savedInstanceState == null) {
            Log.v(LOGCAT,"No saved instance stage--------------");
        }
        else{Log.v(LOGCAT,"Has saved instance stage---------------");}
        return rootView;
    }
    /**
     * DetailFragmentCallback for when an item has been selected.
     */
    public interface Callback {

        /**
         * Called when an item is selected in the gridview
         *
         * @param movie the selected movie
         */
        void onItemSelected(MovieItem movie);
    }

    public class fetchMoviesTask extends AsyncTask<String, Void, MovieItem[]> {
        public final String LOG_TAG = fetchMoviesTask.class.getSimpleName();

        @Override
        protected MovieItem[] doInBackground(final String... params) {
            // If there's no zip code, there's nothing to look up.  Verify size of params.
            if (params.length == 0) {
                return null;
            }
            if(mPage <= MAX_PAGES) {
                mPage += 3;
            }
            int page = mPage;
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            // Will contain the raw JSON response as a string.
            String MoviesJSONStr = null;

            /*String format = "json";
            String units = "metric";
            int numDays = 7;
*/
            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // https://api.themoviedb.org/3/movie/550?include_image_language=en&api_key=81acee672bd444827fa5abca3f5055da
                final String FORECAST_BASE_URL = "https://api.themoviedb.org/3/discover/movie?";
                final String APPID_PARAM = "api_key";
                final String PAGE = "page";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon().appendQueryParameter(PAGE,String.valueOf(page)).appendQueryParameter(APPID_PARAM, BuildConfig.MOVIE_DB_API_KEY).build();
                URL url = new URL(builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                MoviesJSONStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }

            }
            try {
                return getMoviesFromJson(MoviesJSONStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        public MovieItem[] getMoviesFromJson(String MoviesJSONStr ) throws JSONException{
            final String OWN_RESULTS = "results";
            final String OWN_ID = "id";
            final String OWN_POSTER = "poster_path";
            final String OWN_TITLE = "original_title";
            final String OWM_OVERVIEW = "overview";
            final String OWM_VOTE_AVERAGE = "vote_average";
            final String OWM_RELEASE_DATE = "release_date";

            JSONObject moviesJson = new JSONObject(MoviesJSONStr);
            JSONArray moviesArray = moviesJson.getJSONArray(OWN_RESULTS);

            final MovieItem[] resultStrs = new MovieItem[moviesArray.length()];
            for(int i = 0; i < moviesArray.length(); i++){
                String title, posterPath;
                // Get the JSON object representing one movie
                JSONObject movieObject = moviesArray.getJSONObject(i);
                resultStrs[i] = new MovieItem( movieObject.getString(OWN_POSTER), movieObject.getString(OWM_OVERVIEW), movieObject.getString(OWM_RELEASE_DATE),
                        movieObject.getString(OWN_ID), movieObject.getString(OWN_TITLE), movieObject.getString(OWM_VOTE_AVERAGE));
                Log.v(LOG_TAG, "Popular movies string array: " + resultStrs);
            }
            return resultStrs;
        }
        @Override
        protected void onPostExecute(MovieItem[] result) {
            if (result != null) {
                moviesAdapter.clear();
                for (MovieItem allMoviesStr : result) {
                    moviesAdapter.add(allMoviesStr);
                }
                // New data is back from the server.  Hooray!
            }
        }
    }
}

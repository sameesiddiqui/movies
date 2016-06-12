package com.mosthype;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;

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


public class MainActivityFragment extends Fragment {

    public static int width;
    private static String title = "Now Playing";
    private static String sortParam;

    private static ArrayList<String> posters;
    private static GridView gridView;

    private static ArrayList<String> overviews;
    private static ArrayList<String> titles;
    private static ArrayList<String> dates;
    private static ArrayList<String> ratings;
    private static String youtube1;
    private static String youtube2;
    private static ArrayList<String> ids;
    private static ArrayList<Boolean> favorties;
    private static ArrayList<ArrayList<String>> comments;


    // replace with your own api key. Create account at themoviedb.org
    public static final String API_KEY = "";

    public MainActivityFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main_activity, container, false);

        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        if (MainActivity.isTab) {
            width = size.x/6;
        } else {
            width = size.x/3;
        }

        if (getActivity()!=null) {
            ArrayList<String> array = new ArrayList<>();
            ImageAdapter adapter = new ImageAdapter(getActivity(), array, width);
            gridView = (GridView) rootView.findViewById(R.id.movie_grid);

            gridView.setColumnWidth(width);
            gridView.setAdapter(adapter);
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                new YoutubeLoadTask().execute(position);

                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra("overview", overviews.get(position))
                        .putExtra("poster", posters.get(position))
                        .putExtra("title", titles.get(position))
                        .putExtra("comments", comments.get(position))
                        .putExtra("date", dates.get(position))
                        .putExtra("youtube1", youtube1)
                        .putExtra("youtube2", youtube2)
                        .putExtra("rating", ratings.get(position));
                startActivity(intent);
            }
        });

        return rootView;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.main, menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.action_top_rated:
                sortParam = "top_rated";
                title = "Top Rated";
                break;
            case R.id.action_popular:
                sortParam = "popular";
                title = "Popular";
                break;
            case R.id.action_now_playing:
                sortParam = "now_playing";
                title = "Now Playing";
                break;
            case R.id.action_upcoming:
                sortParam = "upcoming";
                title = "Upcoming";
                break;
            default:
                sortParam = "now_playing";
                break;
        }
        gridView.setAdapter(null);
        new ImageLoadTask().execute();

        return super.onOptionsItemSelected(item);
    }


    public void onStart() {
        super.onStart();
        getActivity().setTitle(title);

        if (isNetworkAvailable()) {
            gridView.setVisibility(GridView.VISIBLE);
            new ImageLoadTask().execute();
        } else gridView.setVisibility(GridView.GONE);
    }

    // helper to check network status
    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = cm.getActiveNetworkInfo();
        return network!=null && network.isConnected();
    }


    // imageload task on worker thread
    public class ImageLoadTask extends AsyncTask<Void, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            while (true) {

                try {
                    posters = new ArrayList(Arrays.asList(getPathsFromAPI(sortParam)));
                    return posters;
                } catch (Exception e) {
                    continue;
                }
            }
        }

        protected void onPostExecute(ArrayList<String> moviePosters) {
            if (moviePosters != null && getActivity() != null) {
                getActivity().setTitle(title);
                ImageAdapter imageAdapter = new ImageAdapter(getContext(), moviePosters, width);
                gridView.setAdapter(imageAdapter);
                Log.v("butter", "set");
            }
        }

    }

    public class YoutubeLoadTask extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... params) {
            youtube1 = getYoutubeFromIds(ids,0, params[0]);
            youtube2 = getYoutubeFromIds(ids,1, params[0]);
            return null;
        }
    }


    // helper to get movie poster paths from json
    private String[] getPathsFromAPI(String sort) {
        while (true) {
            HttpURLConnection urlConnection = null;
            BufferedReader bufferedReader = null;
            String JSONresult;

            try {
                String urlString;
                if (sort!=null) {
                    urlString = "http://api.themoviedb.org/3/movie/" + sort + "?api_key=" + API_KEY;
                } else {
                    urlString = "http://api.themoviedb.org/3/movie/now_playing?api_key=" + API_KEY;
                }
                URL url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // input to string
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer stringBuffer = new StringBuffer();

                if (inputStream == null) {
                    return null;
                }

                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line + "\n");
                }
                if (stringBuffer.length() == 0) {
                    return null;
                }
                JSONresult = stringBuffer.toString();

                try {

                    overviews = new ArrayList<String>(Arrays.asList(getStringsFromJSON(JSONresult, "overview")));
                    titles = new ArrayList<String>(Arrays.asList(getStringsFromJSON(JSONresult, "original_title")));
                    ratings = new ArrayList<String>(Arrays.asList(getStringsFromJSON(JSONresult, "vote_average")));
                    dates = new ArrayList<String>(Arrays.asList(getStringsFromJSON(JSONresult, "release_date")));
                    ids = new ArrayList<String>(Arrays.asList(getStringsFromJSON(JSONresult, "id")));

                    /*
                    while (true) {

                        youtube1 = new ArrayList<String>(Arrays.asList(getYoutubeFromIds(ids, 0)));
                        youtube2 = new ArrayList<String>(Arrays.asList(getYoutubeFromIds(ids, 1)));
                        int nullCount = 0;

                        for (int i=0; i<youtube1.size(); i++) {
                            if (youtube1.get(i) == null) {
                                nullCount++;
                                youtube1.set(i, "no video found");
                            }
                        }

                        for (int i=0; i<youtube2.size(); i++) {
                            if (youtube2.get(i) == null) {
                                nullCount++;
                                youtube2.set(i, "no video found");
                            }
                        }

                        if (nullCount>2) continue;
                        break;
                    }
*/
                    comments = getReviewsFromIds(ids);
                    return getPathsFromJSON(JSONresult);
                } catch (JSONException e) {
                    return null;
                }


            } catch (Exception e) {
                continue;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }

                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private ArrayList<ArrayList<String>> getReviewsFromIds(ArrayList<String> ids) {
        outerloop:
        while (true) {

            ArrayList<ArrayList<String>> results = new ArrayList<>();
            for (int i=0; i< ids.size(); i++) {
                HttpURLConnection urlConnection = null;
                BufferedReader bufferedReader = null;
                String JSONresult;

                try {
                    String urlString;
                    urlString = "http://api.themoviedb.org/3/movie/" + ids.get(i) + "/reviews?api_key=" + API_KEY;

                    URL url = new URL(urlString);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    // input to string
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer stringBuffer = new StringBuffer();

                    if (inputStream == null) {
                        return null;
                    }

                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuffer.append(line + "\n");
                    }
                    if (stringBuffer.length() == 0) {
                        return null;
                    }
                    JSONresult = stringBuffer.toString();

                    try {
                        results.add(getCommentsFromJSON(JSONresult));
                    } catch (JSONException e) {
                        return null;
                    }


                } catch (Exception e) {
                    continue outerloop;
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }

                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        } catch (final IOException e) {

                        }
                    }
                }
            }
            return results;
        }
    }

    private ArrayList<String> getCommentsFromJSON(String jsonString) throws JSONException{
        JSONObject jsonObject = new JSONObject(jsonString);
        JSONArray reviewsArray = jsonObject.getJSONArray("results");
        ArrayList<String> results = new ArrayList<>();

        if (reviewsArray.length() == 0) {
            results.add("No reviews found for this movie");
            return results;
        }
        for (int i=0; i<reviewsArray.length(); i++) {
            results.add(reviewsArray.getJSONObject(i).getString("content"));
        }
        return results;
    }

    //fetch youtube links with movie id
    private String getYoutubeFromIds(ArrayList<String> ids, int position, int clickPosition) {

        String result = null;
        //String[] result = new String[ids.size()];
        //for (int i=0; i< ids.size(); i++) {
        //while (true) {
        HttpURLConnection urlConnection = null;
        BufferedReader bufferedReader = null;
        String JSONresult;

        try {
            String urlString;
            urlString = "http://api.themoviedb.org/3/movie/" + ids.get(clickPosition) + "/videos?api_key=" + API_KEY;
            Log.d("id", ids.get(clickPosition));
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // input to string
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer stringBuffer = new StringBuffer();

            if (inputStream == null) {
                return null;
            }

            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line + "\n");
            }
            if (stringBuffer.length() == 0) {
                return null;
            }
            JSONresult = stringBuffer.toString();

            try {
                result = getYoutubeFromJSON(JSONresult, position);
                //break;
            } catch (JSONException e) {
                //continue;
                Log.d("EXCEPTON", e.getStackTrace().toString());
            }


        } catch (Exception e) {

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        }
    //}
        return result;
    }


    private String getYoutubeFromJSON(String jsonString, int position) throws JSONException{
        JSONObject jsonObject = new JSONObject(jsonString);
        JSONArray youtubeArray = jsonObject.getJSONArray("results");
        String result = "no videos found";
        if (position == 0) {
            JSONObject youtube = youtubeArray.getJSONObject(position);
            result = youtube.getString("key");
            Log.d("RESULT", result);
        } else if (position == 1) {
            if (youtubeArray.length() > 1) {
                JSONObject youtube = youtubeArray.getJSONObject(position);
                result = youtube.getString("key");
            } else {
                JSONObject youtube = youtubeArray.getJSONObject(0);
                result = youtube.getString("key");
            }
            Log.d("RESULT", result);
        }
        Log.d("RESULT", result);
        return result;
    }

        // fetch movie details
        private String[] getStringsFromJSON(String jsonString, String param) throws JSONException{
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray movieArray = jsonObject.getJSONArray("results");
            String[] result = new String[movieArray.length()];

            for (int i=0; i<movieArray.length(); i++) {
                JSONObject movieData = movieArray.getJSONObject(i);

                if(param.equals("vote_average")) {
                    Double rating = movieData.getDouble(param);
                    String ratingString = rating + "/10";
                    result[i] = ratingString;
                } else result[i] = movieData.getString(param);
            }
            return result;
        }

        // create string array of movie paths
        private String[] getPathsFromJSON(String jsonString) throws JSONException {

            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray moviesArray = jsonObject.getJSONArray("results");
            String[] result = new String[moviesArray.length()];

            for (int i = 0; i < moviesArray.length(); i++) {
                JSONObject movie = moviesArray.getJSONObject(i);
                result[i] = movie.getString("poster_path");
            }
            return result;
        }
    }


package com.mosthype;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.support.v7.widget.ShareActionProvider;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailActivityFragment extends Fragment {

    public static String youtube1;
    public static String youtube2;
    private static String overview;
    private static String rating;
    private static String date;
    private static String review;
    private static String title;
    private static String poster;
    private static ArrayList<String> comments;
    private static Button b;
    private static ShareActionProvider mShareActionProvider;

    public DetailActivityFragment() {
        setHasOptionsMenu(true);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu_detail, menu);
        MenuItem share = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(share);

        if (mShareActionProvider!= null) {
            mShareActionProvider.setShareIntent(createShareIntent());
        }
    }

    private Intent createShareIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "Check out this trailer for " + title +": " + "https://www.youtube.com/watch?v=" + youtube1);
        return intent;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_detail_activity, container, false);
        Intent intent = getActivity().getIntent();
        getActivity().setTitle("Movie Details");

        review = null;
        if (intent!=null && intent.hasExtra("overview")) {
            overview = intent.getStringExtra("overview");
            TextView textView = (TextView) rootView.findViewById(R.id.overview_of_movie);
            textView.setText(overview);
        }
        if (intent!=null && intent.hasExtra("title")) {
            title = intent.getStringExtra("title");
            TextView textView = (TextView) rootView.findViewById(R.id.title_of_movie);
            textView.setText(title);
        }
        if (intent!=null && intent.hasExtra("rating")) {
            rating = intent.getStringExtra("rating");
            TextView textView = (TextView) rootView.findViewById(R.id.rating_of_movie);
            textView.setText(rating);
        }
        if (intent!=null && intent.hasExtra("date")) {
            date = intent.getStringExtra("date");
            TextView textView = (TextView) rootView.findViewById(R.id.date_of_movie);
            textView.setText(date);
        }
        if (intent!=null && intent.hasExtra("poster")) {
            poster = intent.getStringExtra("poster");
            ImageView imageView = (ImageView) rootView.findViewById(R.id.movie_poster);
            Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w185/" + poster).resize(MainActivityFragment.width, (int) (MainActivityFragment.width *1.5)).into(imageView);
        }
        if (intent!=null && intent.hasExtra("youtube1")) {
            youtube1 = intent.getStringExtra("youtube1");
        }
        if (intent!=null && intent.hasExtra("youtube2")) {
            youtube2 = intent.getStringExtra("youtube2");
        }
        if (intent!=null && intent.hasExtra("comments")) {
            comments = intent.getStringArrayListExtra("comments");
            for (int i=0; i<comments.size(); i++) {
                LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.linear_layout);
                View divider = new View(getActivity());
                TextView textView = new TextView(getActivity());

                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                textView.setLayoutParams(layoutParams);

                int paddingPixel = 10;
                float density = getActivity().getResources().getDisplayMetrics().density;
                int paddingDp = (int) (paddingPixel * density);
                textView.setPadding(0, paddingDp, 0, paddingDp);

                RelativeLayout.LayoutParams dividerP = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
                divider.setLayoutParams(dividerP);
                divider.setBackgroundColor(Color.BLACK);

                textView.setText(comments.get(i));
                layout.addView(divider);
                layout.addView(textView);

                if (review == null) {
                    review = comments.get(i);
                } else {
                    review = "DIVIDER_TEXT" + comments.get(i);
                }
            }
        }

        /*
              b = (Button)rootView.findViewById(R.id.favorite);
        if(intent !=null && intent.hasExtra("favorite"))
        {
            favorite = intent.getBooleanExtra("favorite", false);
            if(!favorite)
            {
                b.setText("FAVORITE");
                b.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
            }
            else
            {
                b.setText("UNFAVORITE");
                b.getBackground().setColorFilter(Color.CYAN, PorterDuff.Mode.MULTIPLY);
            }
        }
        */
        return rootView;
    }

}

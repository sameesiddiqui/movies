package com.mosthype;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail, new DetailActivityFragment())
                    .commit();
        }
    }

    public void onClickTrailer1(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);//, Uri.parse("vnd.youtube:" + DetailActivityFragment.youtube1));
        intent.setData(Uri.parse("vnd.youtube:" + DetailActivityFragment.youtube1));
        startActivity(intent);

    }

    public void onClickTrailer2(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + DetailActivityFragment.youtube2));
        startActivity(intent);
    }
    /*
            public void onClickFavorite(View v)
            {
                Button b = (Button)findViewById(R.id.favorite);
                if(b.getText().equals("FAVORITE"))
                {
                    //code to store movie data in database
                    b.setText("UNFAVORITE");
                    b.getBackground().setColorFilter(Color.CYAN, PorterDuff.Mode.MULTIPLY);

                }
                else
                {
                    b.setText("FAVORITE");
                    b.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                }
            }
            */

}

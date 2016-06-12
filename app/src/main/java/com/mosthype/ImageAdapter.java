package com.mosthype;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by glock on 6/5/16.
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> movieArray;
    private int width;

    public ImageAdapter(Context context, ArrayList<String> paths, int x) {
        mContext = context;
        movieArray = paths;
        width =x;
    }

    @Override
    public int getCount() {
        return movieArray.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
        } else {
            imageView = (ImageView) convertView;
        }

        Drawable d = resizeDrawable(mContext.getResources().getDrawable(R.drawable.samee));
        Picasso.with(mContext).load("http://image.tmdb.org/t/p/w185/" + movieArray.get(position)).resize(width, (int) (width*1.5)).into(imageView);

        return imageView;
    }

    private Drawable resizeDrawable(Drawable img){
        Bitmap b = ((BitmapDrawable) img).getBitmap();
        Bitmap bitmap = Bitmap.createScaledBitmap(b, width, (int) (width*1.5), false);
        return new BitmapDrawable(mContext.getResources(), bitmap);
    }
}
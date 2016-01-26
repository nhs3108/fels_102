package com.example.nhs3108.fels102.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.example.nhs3108.fels102.R;

import java.io.InputStream;

/**
 * Created by nhs3108 on 1/26/16.
 */
public class DrawImageViewSrcTask extends AsyncTask<String, Void, Bitmap> {
    ImageView mImageView;
    boolean hasError = false;

    public DrawImageViewSrcTask(ImageView imageView) {
        this.mImageView = imageView;
    }

    protected Bitmap doInBackground(String... urls) {
        String url = urls[0];
        Bitmap bitmap = null;
        try {
            InputStream in = new java.net.URL(url).openStream();
            bitmap = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            hasError = true;
            e.printStackTrace();
        }
        return bitmap;
    }

    protected void onPostExecute(Bitmap result) {
        if (hasError) {
            mImageView.setImageResource(R.mipmap.ic_framgia);
        } else {
            mImageView.setImageBitmap(result);
        }
    }
}

package com.example.nhs3108.fels102.adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nhs3108.fels102.R;
import com.example.nhs3108.fels102.utils.Category;

import java.io.InputStream;
import java.util.ArrayList;

public class CategoryAdapter extends ArrayAdapter<Category> {
    private Activity mActivity;
    private int mIdLayout;
    private ArrayList<Category> mList;
    private LayoutInflater mInflater;

    public CategoryAdapter(Activity activity, int idLayout, ArrayList<Category> list) {
        super(activity, idLayout, list);
        this.mActivity = activity;
        this.mIdLayout = idLayout;
        this.mList = list;
        this.mInflater = LayoutInflater.from(activity);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        Category category = mList.get(position);
        if (convertView == null) {
            convertView = mInflater.inflate(mIdLayout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.categoryPhoto = (ImageView) convertView.findViewById(R.id.img_category_photo);
            viewHolder.categoryName = (TextView) convertView.findViewById(R.id.text_category_name);
            viewHolder.categoryStatus = (TextView) convertView.findViewById(R.id.text_category_status);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        new SetImageViewSrcTask(viewHolder.categoryPhoto).execute(category.getPhotoUrl());
        viewHolder.categoryName.setText(category.getName());
        viewHolder.categoryStatus.setText(String.format("%s : %s",
                mActivity.getString(R.string.num_of_words_learned),
                category.getSumOfLearnedWords()));
        return convertView;
    }

    static class ViewHolder {
        ImageView categoryPhoto;
        TextView categoryName;
        TextView categoryStatus;
    }

    private class SetImageViewSrcTask extends AsyncTask<String, Void, Bitmap> {
        ImageView mImageView;
        boolean hasError = false;

        public SetImageViewSrcTask(ImageView imageView) {
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

}

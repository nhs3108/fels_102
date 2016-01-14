package com.example.nhs3108.fels102.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nhs3108.fels102.R;
import com.example.nhs3108.fels102.activities.LessonActivity;
import com.example.nhs3108.fels102.constants.CommonConsts;
import com.example.nhs3108.fels102.utils.Category;

import java.io.InputStream;
import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private static Activity sActivity;
    private static ArrayList<Category> sList;
    private LayoutInflater mInflater;

    public CategoryAdapter(Activity activity, ArrayList<Category> list) {
        this.sActivity = activity;
        this.sList = list;
        this.mInflater = LayoutInflater.from(activity);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.item_category, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Category category = sList.get(position);
        new SetImageViewSrcTask(viewHolder.categoryPhoto).execute(category.getPhotoUrl());
        viewHolder.categoryName.setText(category.getName());
        viewHolder.categoryStatus.setText(String.format("%s : %s",
                sActivity.getString(R.string.num_of_words_learned),
                category.getSumOfLearnedWords()));
        viewHolder.categoryId = sList.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return sList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        int categoryId;
        ImageView categoryPhoto;
        TextView categoryName;
        TextView categoryStatus;

        public ViewHolder(View itemView) {
            super(itemView);
            categoryPhoto = (ImageView) itemView.findViewById(R.id.img_category_photo);
            categoryName = (TextView) itemView.findViewById(R.id.text_category_name);
            categoryStatus = (TextView) itemView.findViewById(R.id.text_category_status);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(sActivity, LessonActivity.class);
                    intent.putExtra(CommonConsts.KEY_CATEGORY_ID, categoryId);
                    sActivity.startActivity(intent);
                }
            });
        }
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

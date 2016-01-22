package com.example.nhs3108.fels102.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.nhs3108.fels102.R;
import com.example.nhs3108.fels102.models.Result;

import java.util.ArrayList;

/**
 * Created by nhs3108 on 1/20/16.
 */
public class LessonResultAdapter extends ArrayAdapter<Result> {
    private Activity mActivity;
    private int mIdLayout;
    private ArrayList<Result> mList;
    private LayoutInflater mInflater;

    public LessonResultAdapter(Activity activity, int idLayout, ArrayList<Result> list) {
        super(activity, idLayout, list);
        this.mActivity = activity;
        this.mIdLayout = idLayout;
        this.mList = list;
        this.mInflater = LayoutInflater.from(activity);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        Result lessonResult = mList.get(mList.size() - 1 - position);
        if (convertView == null) {
            convertView = mInflater.inflate(mIdLayout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.textLessonName = (TextView) convertView.findViewById(R.id.text_lesson_name);
            viewHolder.textCategoryName = (TextView) convertView.findViewById(R.id.text_category_name);
            viewHolder.textLessonScore = (TextView) convertView.findViewById(R.id.text_lesson_score);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.textLessonName.setText(lessonResult.getLessonName());
        viewHolder.textCategoryName.setText(lessonResult.getCategoryName());
        viewHolder.textLessonScore.setText(lessonResult.getScore());
        return convertView;
    }

    static class ViewHolder {
        TextView textLessonName;
        TextView textCategoryName;
        TextView textLessonScore;
    }
}

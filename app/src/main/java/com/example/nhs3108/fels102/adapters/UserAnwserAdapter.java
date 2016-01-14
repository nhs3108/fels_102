package com.example.nhs3108.fels102.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nhs3108.fels102.R;
import com.example.nhs3108.fels102.utils.UserAnswer;

import java.util.ArrayList;

/**
 * Created by nhs3108 on 1/13/16.
 */
public class UserAnwserAdapter extends ArrayAdapter<UserAnswer> {
    private Activity mActivity;
    private int mIdLayout;
    private ArrayList<UserAnswer> mList;
    private LayoutInflater mInflater;

    public UserAnwserAdapter(Activity activity, int idLayout, ArrayList<UserAnswer> list) {
        super(activity, idLayout, list);
        this.mActivity = activity;
        this.mIdLayout = idLayout;
        this.mList = list;
        this.mInflater = LayoutInflater.from(activity);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        UserAnswer userAnswer = mList.get(position);
        if (convertView == null) {
            convertView = mInflater.inflate(mIdLayout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.imageAnswerStatus = (ImageView) convertView.findViewById(R.id.image_answer_status);
            viewHolder.textOriginWord = (TextView) convertView.findViewById(R.id.text_origin_word);
            viewHolder.textUserAnswer = (TextView) convertView.findViewById(R.id.text_user_answer);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (userAnswer.isCorrect()) {
            viewHolder.imageAnswerStatus.setImageResource(R.mipmap.ic_correct);
        } else {
            viewHolder.imageAnswerStatus.setImageResource(R.mipmap.ic_incorrect);
        }
        viewHolder.textOriginWord.setText(userAnswer.getWordContent());
        viewHolder.textUserAnswer.setText(userAnswer.getAnwserContent());
        return convertView;
    }

    static class ViewHolder {
        ImageView imageAnswerStatus;
        TextView textOriginWord;
        TextView textUserAnswer;
    }
}

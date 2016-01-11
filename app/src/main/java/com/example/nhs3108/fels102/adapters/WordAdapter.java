package com.example.nhs3108.fels102.adapters;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.nhs3108.fels102.R;
import com.example.nhs3108.fels102.utils.Answer;
import com.example.nhs3108.fels102.utils.Word;

import java.util.ArrayList;

/**
 * Created by nhs3108 on 1/9/16.
 */
public class WordAdapter extends ArrayAdapter<Word> {
    private Activity mActivity;
    private int mIdLayout;
    private ArrayList<Word> mList;
    private LayoutInflater mInflater;

    public WordAdapter(Activity activity, int idLayout, ArrayList<Word> list) {
        super(activity, idLayout, list);
        this.mActivity = activity;
        this.mIdLayout = idLayout;
        this.mList = list;
        this.mInflater = LayoutInflater.from(activity);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        Word word = mList.get(position);
        Answer correctAnswerOfWord = word.getCorrectAnswer();
        if (convertView == null) {
            convertView = mInflater.inflate(mIdLayout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.originWord = (TextView) convertView.findViewById(R.id.text_origin_word);
            viewHolder.wordMeaning = (TextView) convertView.findViewById(R.id.text_word_meaning);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.originWord.setText(word.getContent());
        if (correctAnswerOfWord != null) {
            viewHolder.wordMeaning.setText(correctAnswerOfWord.getContent());
        } else {
            viewHolder.wordMeaning.setText(mActivity.getString(R.string.unknow_answer));
            viewHolder.wordMeaning.setTextColor(mActivity.getResources().getColor(R.color.colorAccent));
        }
        return convertView;
    }

    static class ViewHolder {
        TextView originWord;
        TextView wordMeaning;
    }
}

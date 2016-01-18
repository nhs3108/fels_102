package com.example.nhs3108.fels102.adapters;


import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nhs3108.fels102.R;
import com.example.nhs3108.fels102.utils.Answer;
import com.example.nhs3108.fels102.utils.Word;

import java.util.ArrayList;

/**
 * Created by nhs3108 on 1/9/16.
 */
public class WordAdapter extends RecyclerView.Adapter<WordAdapter.ViewHolder> {
    public static Activity sActivity;
    public static ArrayList<Word> sList;
    private LayoutInflater mInflater;

    public WordAdapter(Activity activity, ArrayList<Word> list) {
        this.sActivity = activity;
        this.sList = list;
        this.mInflater = LayoutInflater.from(activity);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.item_word, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Word word = sList.get(position);
        Answer correctAnswerOfWord = word.getCorrectAnswer();
        viewHolder.originWord.setText(word.getContent());
        if (correctAnswerOfWord != null) {
            viewHolder.wordMeaning.setText(correctAnswerOfWord.getContent());
        } else {
            viewHolder.wordMeaning.setText(sActivity.getString(R.string.unknow_answer));
            viewHolder.wordMeaning.setTextColor(sActivity.getResources().getColor(R.color.colorAccent));
        }
    }

    @Override
    public int getItemCount() {
        return sList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView originWord;
        TextView wordMeaning;

        public ViewHolder(View itemView) {
            super(itemView);
            originWord = (TextView) itemView.findViewById(R.id.text_origin_word);
            wordMeaning = (TextView) itemView.findViewById(R.id.text_word_meaning);
        }
    }
}

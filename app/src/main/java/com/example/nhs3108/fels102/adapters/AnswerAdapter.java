package com.example.nhs3108.fels102.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.example.nhs3108.fels102.R;
import com.example.nhs3108.fels102.utils.Answer;

import java.util.ArrayList;

/**
 * Created by nhs3108 on 1/13/16.
 */
public class AnswerAdapter extends ArrayAdapter<Answer> {
    private Activity mActivity;
    private int mIdLayout;
    private ArrayList<Answer> mList;
    private LayoutInflater mInflater;

    public AnswerAdapter(Activity activity, int idLayout, ArrayList<Answer> list) {
        super(activity, idLayout, list);
        this.mActivity = activity;
        this.mIdLayout = idLayout;
        this.mList = list;
        this.mInflater = LayoutInflater.from(activity);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        Answer answer = mList.get(position);
        if (convertView == null) {
            convertView = mInflater.inflate(mIdLayout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.anwserContent = (Button) convertView.findViewById(R.id.text_anwser_content);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.anwserContent.setText(answer.getContent());
        return convertView;
    }

    static class ViewHolder {
        Button anwserContent;
    }
}

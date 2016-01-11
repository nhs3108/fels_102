package com.example.nhs3108.fels102.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.nhs3108.fels102.R;
import com.example.nhs3108.fels102.utils.UserActivity;

import java.util.ArrayList;

/**
 * Created by nhs3108 on 1/9/16.
 */
public class UserActivityAdapter extends ArrayAdapter<UserActivity> {
    private Activity mActivity;
    private int mIdLayout;
    private ArrayList<UserActivity> mList;
    private LayoutInflater mInflater;

    public UserActivityAdapter(Activity activity, int idLayout, ArrayList<UserActivity> list) {
        super(activity, idLayout, list);
        this.mActivity = activity;
        this.mIdLayout = idLayout;
        this.mList = list;
        this.mInflater = LayoutInflater.from(activity);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        UserActivity userActivity = mList.get(position);
        if (convertView == null) {
            convertView = mInflater.inflate(mIdLayout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.activityContent = (TextView) convertView.findViewById(R.id.text_activity_content);
            viewHolder.activityTime = (TextView) convertView.findViewById(R.id.text_activity_time);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.activityContent.setText(userActivity.getContent());
        viewHolder.activityTime.setText(userActivity.getTime());
        return convertView;
    }

    static class ViewHolder {
        TextView activityContent;
        TextView activityTime;
    }
}

package com.example.nhs3108.fels102.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.nhs3108.fels102.R;
import com.example.nhs3108.fels102.adapters.UserActivityAdapter;
import com.example.nhs3108.fels102.constants.CommonConsts;
import com.example.nhs3108.fels102.utils.UserActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;


/**
 * Created by hongson on 30/12/2015.
 */
public class HomeActivity extends Activity {
    private SharedPreferences mSharedPreferences;
    private TextView mTextViewCurrentUserName;
    private TextView mTextViewCurrentUserEmail;
    private String mUserName;
    private String mUserEmail;
    private ArrayList<UserActivity> mActivitiesList = new ArrayList<UserActivity>();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initialize();
        mTextViewCurrentUserName.setText(mUserName);
        mTextViewCurrentUserEmail.setText(mUserEmail);

        ListView listViewUActivities = (ListView) findViewById(R.id.list_activities);
        UserActivityAdapter adapter = new UserActivityAdapter(this, R.layout.item_user_activity, mActivitiesList);
        listViewUActivities.setAdapter(adapter);
        Button btnWordList = (Button) findViewById(R.id.btn_words);
        Button btnLessons = (Button) findViewById(R.id.btn_lessons);
        btnWordList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, WordListActivity.class));
            }
        });
        btnLessons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, CategoryActivity.class));
            }
        });
    }

    private void initialize() {
        mSharedPreferences = getSharedPreferences(CommonConsts.USER_SHARED_PREF, MODE_PRIVATE);
        mTextViewCurrentUserName = (TextView) findViewById(R.id.text_current_user_name);
        mTextViewCurrentUserEmail = (TextView) findViewById(R.id.text_current_user_email);
        mUserName = mSharedPreferences.getString(CommonConsts.NAME_FIELD, "");
        mUserEmail = mSharedPreferences.getString(CommonConsts.EMAIL_FILED, "");
        Intent data = getIntent();
        String activitiesStr = data.getStringExtra("activities");
        mActivitiesList = convertFromJsonString(activitiesStr);
    }

    private ArrayList<UserActivity> convertFromJsonString(String jsonStr) {
        ArrayList<UserActivity> activitiesList = new ArrayList<UserActivity>();
        if (!TextUtils.isEmpty(jsonStr)) {
            try {
                JSONArray activitiesJson = new JSONArray(jsonStr);
                int numOfActivities = activitiesJson.length();
                for (int i = 0; i < numOfActivities; i++) {
                    String content = activitiesJson.optJSONObject(i).optString("content");
                    String time = activitiesJson.optJSONObject(i).optString("created_at");
                    activitiesList.add(0, new UserActivity(content, time));
                }
            } catch (JSONException e) {
                // mActivitiesList is not be changed
            }
        }
        return activitiesList;
    }

    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận thoát?")
                .setMessage("Chọn hành động bạn muốn")
                .setPositiveButton("Đăng xuất", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mSharedPreferences.edit().clear().commit();
                        finish();
                    }
                })
                .setNegativeButton("Ẩn", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        moveTaskToBack(true);
                    }
                }).show();
    }
}

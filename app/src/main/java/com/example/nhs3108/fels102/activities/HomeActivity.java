package com.example.nhs3108.fels102.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.nhs3108.fels102.R;
import com.example.nhs3108.fels102.adapters.UserActivityAdapter;
import com.example.nhs3108.fels102.utils.UserActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;


/**
 * Created by hongson on 30/12/2015.
 */
public class HomeActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Intent data = getIntent();
        String activitiesStr = data.getStringExtra("activities");
        JSONArray activitiesJson = new JSONArray();
        ArrayList<UserActivity> activitiesList = new ArrayList<UserActivity>();
        if (activitiesStr != null) {
            try {
                activitiesJson = new JSONArray(activitiesStr);
                for (int i = 0; i < activitiesJson.length(); i++) {
                    String content = activitiesJson.optJSONObject(i).optString("content");
                    String time = activitiesJson.optJSONObject(i).optString("created_at");
                    activitiesList.add(new UserActivity(content, time));
                }
            } catch (JSONException e) {
                // activitiesList is not be changed
            }
        }

        ListView listViewUActivities = (ListView) findViewById(R.id.list_activities);
        UserActivityAdapter adapter = new UserActivityAdapter(this, R.layout.item_user_activity, activitiesList);
        listViewUActivities.setAdapter(adapter);
        Button btnWordList = (Button) findViewById(R.id.btn_words);
        Button btnLessons = (Button) findViewById(R.id.btn_lessons);
        btnWordList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, WordListActivity.class));
            }
        });
    }
}

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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.nhs3108.fels102.R;
import com.example.nhs3108.fels102.adapters.UserActivityAdapter;
import com.example.nhs3108.fels102.constants.CommonConsts;
import com.example.nhs3108.fels102.utils.Category;
import com.example.nhs3108.fels102.utils.InternetUtils;
import com.example.nhs3108.fels102.utils.ObtainCategoriesAsyncTask;
import com.example.nhs3108.fels102.utils.SharePreferencesUtils;
import com.example.nhs3108.fels102.utils.UserActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;


/**
 * Created by hongson on 30/12/2015.
 */
public class HomeActivity extends Activity {
    int updateProfileRequestCode = 3;
    private String mAuthToken;
    private SharedPreferences mSharedPreferences;
    private TextView mTextViewCurrentUserName;
    private TextView mTextViewCurrentUserEmail;
    private ImageButton mBtnUpdateProfile;
    private Button mBtnWordList;
    private Button mBtnLessons;
    private String mUserName;
    private String mUserEmail;
    private ArrayList<UserActivity> mActivitiesList = new ArrayList<UserActivity>();
    private ArrayList<Category> mCategoriesList = new ArrayList<Category>();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initialize();
        mTextViewCurrentUserName.setText(mUserName);
        mTextViewCurrentUserEmail.setText(mUserEmail);

        ListView listViewUActivities = (ListView) findViewById(R.id.list_activities);
        UserActivityAdapter adapter = new UserActivityAdapter(this, R.layout.item_user_activity, mActivitiesList);
        listViewUActivities.setAdapter(adapter);
        setUpButtonsEventHanlder();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == updateProfileRequestCode) {
            if (resultCode == RESULT_OK) {
                String newName = data.getStringExtra(CommonConsts.NAME_FIELD);
                String newEmail = data.getStringExtra(CommonConsts.EMAIL_FILED);
                mTextViewCurrentUserName.setText(newName);
                mTextViewCurrentUserEmail.setText(newEmail);
                SharePreferencesUtils.putString(mSharedPreferences, CommonConsts.NAME_FIELD, newName);
                SharePreferencesUtils.putString(mSharedPreferences, CommonConsts.EMAIL_FILED, newEmail);
            }
        }
    }

    private void initialize() {
        mSharedPreferences = getSharedPreferences(CommonConsts.USER_SHARED_PREF, MODE_PRIVATE);
        mAuthToken = mSharedPreferences.getString(CommonConsts.AUTH_TOKEN_FIELD, null);
        mTextViewCurrentUserName = (TextView) findViewById(R.id.text_current_user_name);
        mTextViewCurrentUserEmail = (TextView) findViewById(R.id.text_current_user_email);
        mUserName = mSharedPreferences.getString(CommonConsts.NAME_FIELD, "");
        mUserEmail = mSharedPreferences.getString(CommonConsts.EMAIL_FILED, "");
        Intent data = getIntent();
        String activitiesStr = data.getStringExtra("activities");
        mActivitiesList = convertFromJsonString(activitiesStr);
        mBtnUpdateProfile = (ImageButton) findViewById(R.id.btn_update_profile);
        mBtnWordList = (Button) findViewById(R.id.btn_words);
        mBtnLessons = (Button) findViewById(R.id.btn_lessons);
    }


    private void setUpButtonsEventHanlder() {
        mBtnWordList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (InternetUtils.checkAvaiable(HomeActivity.this)) {
                    new ObtainCategoriesTask(HomeActivity.this, mCategoriesList, mAuthToken, 1).execute();
                }
            }
        });
        mBtnLessons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (InternetUtils.checkAvaiable(HomeActivity.this)) {
                    startActivity(new Intent(HomeActivity.this, CategoryActivity.class));
                }
            }
        });
        mBtnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(HomeActivity.this, UpdateProfileActivity.class), updateProfileRequestCode);
            }
        });
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

    private class ObtainCategoriesTask extends ObtainCategoriesAsyncTask {
        ObtainCategoriesTask(Activity activity, ArrayList<Category> categories, String authToken, int currentPage) {
            super(activity, categories, authToken, currentPage);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Intent intent = new Intent(HomeActivity.this, WordListActivity.class);
            intent.putExtra(CommonConsts.KEY_CATEGORY_LIST, mCategoriesList);
            startActivity(intent);
        }
    }
}

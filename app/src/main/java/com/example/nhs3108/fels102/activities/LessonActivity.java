package com.example.nhs3108.fels102.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.nhs3108.fels102.R;
import com.example.nhs3108.fels102.constants.CommonConsts;
import com.example.nhs3108.fels102.constants.HttpStatusConsts;
import com.example.nhs3108.fels102.constants.UrlConsts;
import com.example.nhs3108.fels102.utils.MyAsyncTask;
import com.example.nhs3108.fels102.utils.NameValuePair;
import com.example.nhs3108.fels102.utils.RequestHelper;
import com.example.nhs3108.fels102.utils.ResponseHelper;
import com.example.nhs3108.fels102.utils.SharePreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by nhs3108 on 1/11/16.
 */
public class LessonActivity extends Activity {
    private SharedPreferences mSharedPreferences;
    private TextView mTextViewLessonName;
    private String mAuthToken;
    private int mCategoryId;
    private int mLessonId;
    private String mLessonName;

    public void onCreate(Bundle savedInstaceState) {
        super.onCreate(savedInstaceState);
        setContentView(R.layout.activity_lesson);
        initialize();
        if (mCategoryId != -1 && !TextUtils.isEmpty(mAuthToken)) {
            new CreateLessionAsynTask(LessonActivity.this).execute(mAuthToken, String.valueOf(mCategoryId));
        } else {
            finish();
        }

        ImageButton btnStartTest = (ImageButton) findViewById(R.id.btn_start);
        btnStartTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LessonActivity.this, DoingLessonActivity.class);
                intent.putExtra(CommonConsts.KEY_LESSON_ID, mLessonId);
                intent.putExtra(CommonConsts.KEY_LESSON_NAME, mLessonName);
                startActivity(intent);
                finish();
            }
        });
    }

    private void initialize() {
        mTextViewLessonName = (TextView) findViewById(R.id.text_lesson_name);
        mSharedPreferences = getSharedPreferences(CommonConsts.USER_SHARED_PREF, Context.MODE_PRIVATE);
        mAuthToken = mSharedPreferences.getString(CommonConsts.AUTH_TOKEN_FIELD, null);
        Intent data = getIntent();
        mCategoryId = data.getIntExtra(CommonConsts.KEY_CATEGORY_ID, -1);
    }

    private class CreateLessionAsynTask extends MyAsyncTask<String, Void, Void> {
        String authTokenParamName = "auth_token";
        private int mStatusCode;
        private String mResponseBody;

        public CreateLessionAsynTask(Context context) {
            super(context);
        }

        @Override
        protected Void doInBackground(String... args) {
            String authToken = args[0];
            String categoryId = args[1];
            String createLessonUrl = String.format(UrlConsts.LESSON_URl_FORMAT, categoryId);
            NameValuePair nvp1 = new NameValuePair(authTokenParamName, authToken);
            ResponseHelper responseHelper = null;
            try {
                responseHelper = RequestHelper.executeRequest(createLessonUrl, RequestHelper.Method.POST, nvp1);
                mStatusCode = responseHelper.getResponseCode();
                mResponseBody = responseHelper.getResponseBody();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            switch (mStatusCode) {
                case HttpStatusConsts.OK:
                    try {
                        JSONObject lessonJson = new JSONObject(mResponseBody).optJSONObject("lesson");
                        String lessonName = lessonJson.optString("name", CommonConsts.DEFAULT_LESSON_NAME);
                        mTextViewLessonName.setText(lessonName);
                        String wordsData = lessonJson.optString("words");
                        mLessonId = lessonJson.optInt("id");
                        mLessonName = lessonJson.optString("name");
                        SharePreferencesUtils.putString(mSharedPreferences, CommonConsts.KEY_WORDS_DATA, wordsData);
                    } catch (JSONException e) {
                        // Do nothing
                    }
                    break;
                default:
                    ResponseHelper.httpStatusNotify(LessonActivity.this, mStatusCode);
            }
        }

    }

}

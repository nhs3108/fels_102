package com.example.nhs3108.fels102.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nhs3108.fels102.R;
import com.example.nhs3108.fels102.constants.CommonConsts;
import com.example.nhs3108.fels102.constants.HttpStatusConsts;
import com.example.nhs3108.fels102.constants.UrlConsts;
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
public class LessionActivity extends Activity {
    private SharedPreferences mSharedPreferences;
    private TextView mTextViewLessonName;
    private String mAuthToken;
    private int mCategoryId;

    public void onCreate(Bundle savedInstaceState) {
        super.onCreate(savedInstaceState);
        setContentView(R.layout.activity_lesson);
        initialize();
        if (mCategoryId != -1 && !TextUtils.isEmpty(mAuthToken)) {
            new CreateLessionAsynTask().execute(mAuthToken, String.valueOf(mCategoryId));
        } else {
            finish();
        }
    }

    private void initialize() {
        mTextViewLessonName = (TextView) findViewById(R.id.text_lesson_name);
        mSharedPreferences = getSharedPreferences(CommonConsts.USER_SHARED_PREF, Context.MODE_PRIVATE);
        mAuthToken = mSharedPreferences.getString(CommonConsts.AUTH_TOKEN_FIELD, null);
        Intent data = getIntent();
        mCategoryId = data.getIntExtra("categoryId", -1);
    }

    private class CreateLessionAsynTask extends AsyncTask<String, Void, Void> {
        String authTokenParamName = "auth_token";
        private ProgressDialog mProgressDialog;
        private int mStatusCode;
        private String mResponseBody;

        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(LessionActivity.this);
            mProgressDialog.setMessage(getString(R.string.msg_wait));
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
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
            mProgressDialog.dismiss();
            switch (mStatusCode) {
                case HttpStatusConsts.OK:
                    try {
                        JSONObject lessonJson = new JSONObject(mResponseBody).optJSONObject("lesson");
                        String lessonName = lessonJson.optString("name", CommonConsts.DEFAULT_LESSON_NAME);
                        mTextViewLessonName.setText(lessonName);
                        String wordsData = lessonJson.optString("words");
                        SharePreferencesUtils.putString(mSharedPreferences, "wordsData", wordsData);
                    } catch (JSONException e) {
                        // Do nothing
                    }
                    break;
                case HttpStatusConsts.UNAUTHORIZED:
                    String errorMessage = getString(R.string.error_unauthorized);
                    break;
                case HttpStatusConsts.NOT_FOUND:
                    Toast.makeText(LessionActivity.this, getString(R.string.error_server_not_found), Toast.LENGTH_SHORT).show();
                    break;
                case HttpStatusConsts.INTERNAL_SERVER_ERROR:
                    Toast.makeText(LessionActivity.this, getString(R.string.error_internal_server_error), Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(LessionActivity.this, getString(R.string.error_unknown), Toast.LENGTH_SHORT).show();
            }
        }

    }

}

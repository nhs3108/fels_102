package com.example.nhs3108.fels102.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.nhs3108.fels102.R;
import com.example.nhs3108.fels102.activities.HomeActivity;
import com.example.nhs3108.fels102.constants.CommonConsts;
import com.example.nhs3108.fels102.constants.HttpStatusConsts;
import com.example.nhs3108.fels102.constants.UrlConsts;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

/**
 * Created by nhs3108 on 1/27/16.
 */
public class LoginAsyncTask extends MyAsyncTask<String, Void, String> {
    String emailParamName = "session[email]";
    String passwordParamName = "session[password]";
    String userEmail;
    String userPassword;
    private int mStatusCode;
    private String mResponseBody;
    private SharedPreferences mSharedPreferences;

    public LoginAsyncTask(Context context, SharedPreferences sharedPreferences) {
        super(context);
        this.mSharedPreferences = sharedPreferences;
    }

    protected String doInBackground(String... args) {
        userEmail = args[0];
        userPassword = args[1];
        NameValuePair nvp1 = new NameValuePair(emailParamName, userEmail);
        NameValuePair nvp2 = new NameValuePair(passwordParamName, userPassword);
        ResponseHelper responseHelper = null;
        try {
            responseHelper = RequestHelper.executeRequest(UrlConsts.LOGIN, RequestHelper.Method.POST, nvp1, nvp2);
            mStatusCode = responseHelper.getResponseCode();
            mResponseBody = responseHelper.getResponseBody();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        switch (mStatusCode) {
            case HttpStatusConsts.OK:
                try {
                    storeUserInfo();
                    Intent intent = new Intent(context, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    String activitiesStr = new JSONObject(mResponseBody).optJSONObject("user").optString("activities");
                    SharePreferencesUtils.putString(mSharedPreferences, CommonConsts.KEY_ACTIVITIES, activitiesStr);
                    context.startActivity(intent);
                    ((Activity) context).finish();
                } catch (JSONException e) {
                    Toast.makeText(context, context.getString(R.string.error_response_data), Toast.LENGTH_SHORT).show();
                }
                break;
            case HttpStatusConsts.UNAUTHORIZED:
                String errorMessage = context.getString(R.string.error_unauthorized);
                notifyError(errorMessage);
                break;
            default:
                ResponseHelper.httpStatusNotify(context, mStatusCode);
        }
    }

    private void storeUserInfo() throws JSONException {
        JSONObject responseJson = new JSONObject(mResponseBody);
        JSONObject userDataJson = responseJson.optJSONObject("user");
        SharePreferencesUtils.putString(mSharedPreferences, CommonConsts.EMAIL_FILED, userEmail);
        SharePreferencesUtils.putString(mSharedPreferences, CommonConsts.NAME_FIELD, userDataJson.optString("name"));
        SharePreferencesUtils.putString(mSharedPreferences, CommonConsts.AUTH_TOKEN_FIELD, userDataJson.optString("auth_token"));
        SharePreferencesUtils.putString(mSharedPreferences, CommonConsts.KEY_USER_ID, userDataJson.optString("id"));
        SharePreferencesUtils.putString(mSharedPreferences, CommonConsts.KEY_USER_PASSWORD, userPassword);
        SharePreferencesUtils.putString(mSharedPreferences, CommonConsts.KEY_USER_AVATAR_URL, userDataJson.optString("avatar"));
    }

    private void notifyError(String defaultMessage) {
        try {
            JSONObject responseJson = new JSONObject(mResponseBody);
            Toast.makeText(context, responseJson.getString("message"), Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, defaultMessage, Toast.LENGTH_SHORT).show();
        }
    }
}

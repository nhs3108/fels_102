package com.example.nhs3108.fels102.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nhs3108.fels102.R;
import com.example.nhs3108.fels102.constants.CommonConsts;
import com.example.nhs3108.fels102.constants.HttpStatusConsts;
import com.example.nhs3108.fels102.constants.UrlConsts;
import com.example.nhs3108.fels102.utils.InternetUtils;
import com.example.nhs3108.fels102.utils.MyAsyncTask;
import com.example.nhs3108.fels102.utils.NameValuePair;
import com.example.nhs3108.fels102.utils.RequestHelper;
import com.example.nhs3108.fels102.utils.ResponseHelper;
import com.example.nhs3108.fels102.utils.SharePreferencesUtils;
import com.example.nhs3108.fels102.utils.ValidationUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.Iterator;

/**
 * Created by hongson on 30/12/2015.
 */
public class RegisterActivity extends Activity {
    EditText editTextName;
    EditText editTextEmail;
    EditText editTextPassword;
    EditText editTextRePassword;
    private SharedPreferences mSharedPreferences;

    public void onCreate(Bundle savedInstaceState) {
        super.onCreate(savedInstaceState);
        setContentView(R.layout.activity_register);
        mSharedPreferences = getSharedPreferences(CommonConsts.USER_SHARED_PREF, Context.MODE_PRIVATE);
        editTextName = (EditText) findViewById(R.id.edit_name);
        editTextEmail = (EditText) findViewById(R.id.edit_email);
        editTextPassword = (EditText) findViewById(R.id.edit_password);
        editTextRePassword = (EditText) findViewById(R.id.edit_repassword);

        Button registerBtn = (Button) findViewById(R.id.btn_register);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString();
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                String passwordConfirmation = editTextRePassword.getText().toString();
                if (isValidConditions()) {
                    editTextName.setError(null);
                    editTextEmail.setError(null);
                    editTextPassword.setError(null);
                    editTextRePassword.setError(null);
                    new RegisterAsyncTask(RegisterActivity.this).execute(name, email, password, passwordConfirmation);
                }
            }
        });
    }

    private boolean isValidConditions() {
        ValidationUtils validationUtils = new ValidationUtils(this);
        if (!InternetUtils.checkAvaiable(RegisterActivity.this)) {
            Toast.makeText(RegisterActivity.this, getString(R.string.msg_no_internet), Toast.LENGTH_SHORT).show();
            return false;
        } else {
            boolean isValidName = validationUtils.validateName(editTextName);
            boolean isValidEmail = validationUtils.validateEmail(editTextEmail);
            boolean isValidPassword = validationUtils.validatePassword(editTextPassword);
            boolean isValidPwdConfirmation = validationUtils.validatePassword(editTextRePassword, editTextPassword);
            return isValidName && isValidEmail && isValidPassword && isValidPwdConfirmation;
        }
    }

    private class RegisterAsyncTask extends MyAsyncTask<String, Void, String> {
        String userName;
        String userEmail;
        String userPassword;
        private String mNameParamName = "user[name]";
        private String mEmailParamName = "user[email]";
        private String mPasswordParamName = "user[password]";
        private String mPwdConfirmationParamName = "user[password_confirmation]";
        private int mStatusCode;
        private String mResponseBody;

        public RegisterAsyncTask(Context context) {
            super(context);
        }

        protected String doInBackground(String... args) {
            userName = args[0];
            userEmail = args[1];
            userPassword = args[2];
            String passwordConfirmation = args[3];
            NameValuePair nvp1 = new NameValuePair(mNameParamName, userName);
            NameValuePair nvp2 = new NameValuePair(mEmailParamName, userEmail);
            NameValuePair nvp3 = new NameValuePair(mPasswordParamName, userPassword);
            NameValuePair nvp4 = new NameValuePair(mPwdConfirmationParamName, passwordConfirmation);
            try {
                ResponseHelper responseHelper = RequestHelper.executeRequest(UrlConsts.REGISTER_URL, RequestHelper.Method.POST, nvp1, nvp2, nvp3, nvp4);
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
                    String errorMessage = getString(R.string.error_unauthorized);
                    notifyErrors(errorMessage);
                    break;
                default:
                    ResponseHelper.httpStatusNotify(RegisterActivity.this, mStatusCode);
            }
        }

        private void notifyErrors(String defaultMessage) {
            try {
                JSONObject responseJson = new JSONObject(mResponseBody);
                JSONObject messageJson = responseJson.getJSONObject("message");
                Iterator errors = messageJson.keys();
                while (errors.hasNext()) {
                    String key = (String) errors.next();
                    Toast.makeText(RegisterActivity.this, String.format("%s %s", key, messageJson.get(key)),
                            Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                Toast.makeText(RegisterActivity.this, defaultMessage, Toast.LENGTH_SHORT).show();
            }
        }

        private void storeUserInfo() throws JSONException {
            JSONObject responseJson = new JSONObject(mResponseBody);
            JSONObject userDataJson = responseJson.optJSONObject("user");
            SharePreferencesUtils.putString(mSharedPreferences, CommonConsts.EMAIL_FILED, userEmail);
            SharePreferencesUtils.putString(mSharedPreferences, CommonConsts.NAME_FIELD, userName);
            SharePreferencesUtils.putString(mSharedPreferences, CommonConsts.AUTH_TOKEN_FIELD, userDataJson.optString("auth_token"));
            SharePreferencesUtils.putString(mSharedPreferences, CommonConsts.KEY_USER_ID, userDataJson.optString("id"));
            SharePreferencesUtils.putString(mSharedPreferences, CommonConsts.KEY_USER_PASSWORD, userPassword);
            SharePreferencesUtils.putString(mSharedPreferences, CommonConsts.KEY_USER_AVATAR_URL, userDataJson.optString("avatar"));
        }
    }
}

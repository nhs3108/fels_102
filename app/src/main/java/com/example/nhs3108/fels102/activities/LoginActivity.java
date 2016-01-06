package com.example.nhs3108.fels102.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nhs3108.fels102.R;
import com.example.nhs3108.fels102.constants.CommonConsts;
import com.example.nhs3108.fels102.constants.HttpStatusConsts;
import com.example.nhs3108.fels102.constants.UrlConsts;
import com.example.nhs3108.fels102.utils.InternetUtils;
import com.example.nhs3108.fels102.utils.NameValuePair;
import com.example.nhs3108.fels102.utils.RequestHelper;
import com.example.nhs3108.fels102.utils.ResponseHelper;
import com.example.nhs3108.fels102.utils.ValidationUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

public class LoginActivity extends Activity {
    EditText editTextEmail;
    EditText editTextPassword;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mSharedPreferences = getSharedPreferences(CommonConsts.USER_SHARED_PREF, Context.MODE_PRIVATE);
        Button loginButton = (Button) findViewById(R.id.btn_login);
        editTextEmail = (EditText) findViewById(R.id.edit_email);
        editTextPassword = (EditText) findViewById(R.id.edit_password);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                if (isValidConditions()) {
                    editTextEmail.setError(null);
                    editTextPassword.setError(null);
                    new LoginAsyncTask().execute(email, password);
                }
            }
        });

        TextView textViewLinkToRegister = (TextView) findViewById(R.id.text_link_to_register);
        textViewLinkToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    private boolean isValidConditions() {
        ValidationUtils validationUtils = new ValidationUtils(this);
        if (!InternetUtils.isAvaiable(LoginActivity.this)) {
            Toast.makeText(LoginActivity.this, getString(R.string.msg_no_internet), Toast.LENGTH_SHORT).show();
            return false;
        } else {
            boolean isValidEmail = validationUtils.validateEmail(editTextEmail);
            boolean isValidPassword = validationUtils.validatePassword(editTextPassword);
            return isValidEmail && isValidPassword;
        }
    }

    private class LoginAsyncTask extends AsyncTask<String, Void, String> {
        String emailParamName = "session[email]";
        String passwordParamName = "session[password]";
        private ProgressDialog mProgressDialog;
        private int mStatusCode;
        private String mResponseBody;

        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(LoginActivity.this);
            mProgressDialog.setMessage(getString(R.string.msg_wait));
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        protected String doInBackground(String... args) {
            String email = args[0];
            String password = args[1];
            NameValuePair nvp1 = new NameValuePair(emailParamName, email);
            NameValuePair nvp2 = new NameValuePair(passwordParamName, password);
            ResponseHelper responseHelper = null;
            try {
                responseHelper = RequestHelper.executePostRequest(UrlConsts.LOGIN, nvp1, nvp2);
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
            mProgressDialog.dismiss();
            switch (mStatusCode) {
                case HttpStatusConsts.OK:
                    try {
                        storeUserInfo();
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } catch (JSONException e) {
                        Toast.makeText(LoginActivity.this, getString(R.string.error_response_data), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case HttpStatusConsts.UNAUTHORIZED:
                    String errorMessage = getString(R.string.error_unauthorized);
                    notifyError(errorMessage);
                    break;
                case HttpStatusConsts.NOT_FOUND:
                    Toast.makeText(LoginActivity.this, getString(R.string.error_server_not_found), Toast.LENGTH_SHORT).show();
                    break;
                case HttpStatusConsts.INTERNAL_SERVER_ERROR:
                    Toast.makeText(LoginActivity.this, getString(R.string.error_internal_server_error), Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(LoginActivity.this, getString(R.string.error_unknown), Toast.LENGTH_SHORT).show();
            }
        }

        private void storeUserInfo() throws JSONException {
            JSONObject responseJson = new JSONObject(mResponseBody);
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString(CommonConsts.EMAIL_FILED, responseJson.getJSONObject("user").getString("email"));
            editor.putString(CommonConsts.NAME_FIELD, responseJson.getJSONObject("user").getString("name"));
            editor.putString(CommonConsts.AUTH_TOKEN_FIELD, responseJson.getJSONObject("user").getString("auth_token"));
            editor.commit();
        }

        private void notifyError(String defaultMessage) {
            try {
                JSONObject responseJson = new JSONObject(mResponseBody);
                Toast.makeText(LoginActivity.this, responseJson.getString("message"), Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(LoginActivity.this, defaultMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }
}

package com.example.nhs3108.fels102.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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
import com.example.nhs3108.fels102.utils.NameValuePair;
import com.example.nhs3108.fels102.utils.RequestHelper;
import com.example.nhs3108.fels102.utils.ResponseHelper;
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
                    new RegisterAsyncTask().execute(name, email, password, passwordConfirmation);
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

    private class RegisterAsyncTask extends AsyncTask<String, Void, String> {
        private String mNameParamName = "user[name]";
        private String mEmailParamName = "user[email]";
        private String mPasswordParamName = "user[password]";
        private String mPwdConfirmationParamName = "user[password_confirmation]";
        private ProgressDialog mProgressDialog;
        private int mStatusCode;
        private String mResponseBody;

        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(RegisterActivity.this);
            mProgressDialog.setMessage(getString(R.string.msg_wait));
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        protected String doInBackground(String... args) {
            String name = args[0];
            String email = args[1];
            String password = args[2];
            String passwordConfirmation = args[3];
            NameValuePair nvp1 = new NameValuePair(mNameParamName, name);
            NameValuePair nvp2 = new NameValuePair(mEmailParamName, email);
            NameValuePair nvp3 = new NameValuePair(mPasswordParamName, password);
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
            mProgressDialog.dismiss();
            switch (mStatusCode) {
                case HttpStatusConsts.OK:
                    Toast.makeText(RegisterActivity.this, getString(R.string.msg_signup_successfully), Toast.LENGTH_SHORT).show();
                    break;
                case HttpStatusConsts.UNAUTHORIZED:
                    String errorMessage = getString(R.string.error_unauthorized);
                    notifyErrors(errorMessage);
                    break;
                case HttpStatusConsts.NOT_FOUND:
                    Toast.makeText(RegisterActivity.this, getString(R.string.error_server_not_found), Toast.LENGTH_SHORT).show();
                    break;
                case HttpStatusConsts.INTERNAL_SERVER_ERROR:
                    Toast.makeText(RegisterActivity.this, getString(R.string.error_internal_server_error), Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(RegisterActivity.this, getString(R.string.error_unknown), Toast.LENGTH_SHORT).show();
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
    }
}

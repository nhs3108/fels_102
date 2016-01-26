package com.example.nhs3108.fels102.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.nhs3108.fels102.R;
import com.example.nhs3108.fels102.constants.CommonConsts;
import com.example.nhs3108.fels102.constants.HttpStatusConsts;
import com.example.nhs3108.fels102.constants.UrlConsts;
import com.example.nhs3108.fels102.utils.DrawImageViewSrcTask;
import com.example.nhs3108.fels102.utils.InternetUtils;
import com.example.nhs3108.fels102.utils.MyAsyncTask;
import com.example.nhs3108.fels102.utils.NameValuePair;
import com.example.nhs3108.fels102.utils.RequestHelper;
import com.example.nhs3108.fels102.utils.ResponseHelper;
import com.example.nhs3108.fels102.utils.ValidationUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by nhs3108 on 1/18/16.
 */
public class UpdateProfileActivity extends Activity {
    static final int REQUEST_CODE_AVATAR_CHOSING = 99;
    private static final String TAG = UpdateProfileActivity.class.getSimpleName();
    private EditText mEditTextName;
    private EditText mEditTextEmail;
    private EditText mEditTextPassword;
    private EditText mEditTextRePassword;
    private ImageView mImageAvatar;
    private SharedPreferences mSharedPreferences;
    private String mAuthToken;
    private String mUserId;
    private String mName;
    private String mEmail;
    private String mPassword;
    private String mPwdConfirmation;
    private Button mBtnUpdateProfile;
    private String mOldName;
    private String mOldPassword;
    private String mOldEmail;
    private String mOldAvatarBase64;
    private String mOldAvatarUrl;

    public void onCreate(Bundle savedInstaceState) {
        super.onCreate(savedInstaceState);
        setContentView(R.layout.activity_update_profile);
        initialize();

        mBtnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mName = mEditTextName.getText().toString();
                mEmail = mEditTextEmail.getText().toString();
                mPassword = mEditTextPassword.getText().toString();
                mPwdConfirmation = mEditTextRePassword.getText().toString();
                if (isValidConditions()) {
                    mEditTextName.setError(null);
                    mEditTextEmail.setError(null);
                    mEditTextPassword.setError(null);
                    mEditTextRePassword.setError(null);

                    mOldAvatarBase64 = getBase64StringEncoding(mImageAvatar);

                    new UpdateProfileTask(UpdateProfileActivity.this).execute();
                }
            }
        });

        mImageAvatar = (ImageView) findViewById(R.id.image_avatar);
        new DrawImageViewSrcTask(mImageAvatar).execute(mOldAvatarUrl);
        mImageAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE_AVATAR_CHOSING);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_AVATAR_CHOSING:
                if (resultCode == RESULT_OK) {
                    Uri selectedImageUri = data.getData();
                    if (selectedImageUri != null) {
                        mImageAvatar.setImageURI(selectedImageUri);
                    }
                }
        }
    }

    private void initialize() {
        mSharedPreferences = getSharedPreferences(CommonConsts.USER_SHARED_PREF, Context.MODE_PRIVATE);
        mAuthToken = mSharedPreferences.getString(CommonConsts.AUTH_TOKEN_FIELD, null);
        mUserId = mSharedPreferences.getString(CommonConsts.KEY_USER_ID, null);
        mOldName = mSharedPreferences.getString(CommonConsts.NAME_FIELD, "");
        mOldEmail = mSharedPreferences.getString(CommonConsts.EMAIL_FILED, "");
        mOldPassword = mSharedPreferences.getString(CommonConsts.KEY_USER_PASSWORD, "");
        mOldAvatarUrl = mSharedPreferences.getString(CommonConsts.KEY_USER_AVATAR_URL, "");
        mEditTextName = (EditText) findViewById(R.id.edit_name);
        mEditTextName.setText(mOldName);
        mEditTextEmail = (EditText) findViewById(R.id.edit_email);
        mEditTextEmail.setText(mOldEmail);
        mEditTextPassword = (EditText) findViewById(R.id.edit_password);
        mEditTextRePassword = (EditText) findViewById(R.id.edit_repassword);
        mEditTextPassword.setText(mOldPassword);
        mEditTextRePassword.setText(mOldPassword);
        mBtnUpdateProfile = (Button) findViewById(R.id.btn_update_profile);
    }

    private String getBase64StringEncoding(ImageView imageView) {
        imageView.buildDrawingCache();
        Bitmap bitmap = imageView.getDrawingCache();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] imageBytes = stream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    private boolean isValidConditions() {
        ValidationUtils validationUtils = new ValidationUtils(this);
        if (!InternetUtils.checkAvaiable(UpdateProfileActivity.this)) {
            Toast.makeText(UpdateProfileActivity.this, getString(R.string.msg_no_internet), Toast.LENGTH_SHORT).show();
            return false;
        } else {
            boolean isValidName = validationUtils.validateName(mEditTextName);
            boolean isValidEmail = validationUtils.validateEmail(mEditTextEmail);
            boolean isValidPassword = validationUtils.validatePassword(mEditTextPassword);
            boolean isValidPwdConfirmation = validationUtils.validatePassword(mEditTextRePassword, mEditTextPassword);
            return isValidName && isValidEmail && isValidPassword && isValidPwdConfirmation;
        }
    }

    private class UpdateProfileTask extends MyAsyncTask<String, Void, String> {
        private String mNameParamName = "user[name]";
        private String mEmailParamName = "user[email]";
        private String mPasswordParamName = "user[password]";
        private String mAvatarParamName = "user[avatar]";
        private String mPwdConfirmationParamName = "user[password_confirmation]";
        private String mAuthTokenParamName = "auth_token";
        private int mStatusCode;
        private String mResponseBody;

        public UpdateProfileTask(Context context) {
            super(context);
        }

        protected String doInBackground(String... args) {
            ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new NameValuePair(mNameParamName, mName));
            params.add(new NameValuePair(mEmailParamName, mEmail));
            params.add(new NameValuePair(mPasswordParamName, mPassword));
            params.add(new NameValuePair(mPwdConfirmationParamName, mPwdConfirmation));

            params.add(new NameValuePair(mAvatarParamName, mOldAvatarBase64));
            params.add(new NameValuePair(mAuthTokenParamName, mAuthToken));
            String updateProfileUrl = String.format(UrlConsts.UPDATE_PROFILE_URL_FORMAT, mUserId);
            try {
                ResponseHelper responseHelper = RequestHelper.executeRequest(updateProfileUrl, RequestHelper.Method.PATCH, params);
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
                    Toast.makeText(UpdateProfileActivity.this, getString(R.string.msg_update_successfully), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putExtra(CommonConsts.NAME_FIELD, mName);
                    intent.putExtra(CommonConsts.EMAIL_FILED, mEmail);
                    intent.putExtra(CommonConsts.KEY_USER_AVATAR_URL, getNewAvatarUrl(mResponseBody));
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
                case HttpStatusConsts.UNAUTHORIZED:
                    String errorMessage = getString(R.string.error_unauthorized);
                    notifyErrors(errorMessage);
                    break;
                default:
                    ResponseHelper.httpStatusNotify(UpdateProfileActivity.this, mStatusCode);
            }
        }

        private void notifyErrors(String defaultMessage) {
            try {
                JSONObject responseJson = new JSONObject(mResponseBody);
                JSONObject messageJson = responseJson.getJSONObject("message");
                Iterator errors = messageJson.keys();
                while (errors.hasNext()) {
                    String key = (String) errors.next();
                    Toast.makeText(UpdateProfileActivity.this, String.format("%s %s", key, messageJson.get(key)),
                            Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                Toast.makeText(UpdateProfileActivity.this, defaultMessage, Toast.LENGTH_SHORT).show();
            }
        }

        private String getNewAvatarUrl(String responseBodyJsonStr) {
            try {
                JSONObject responseJson = new JSONObject(responseBodyJsonStr);
                JSONObject userDataJson = responseJson.optJSONObject("user");
                return userDataJson.optString("avatar");
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
                return mOldAvatarUrl;
            }
        }
    }
}

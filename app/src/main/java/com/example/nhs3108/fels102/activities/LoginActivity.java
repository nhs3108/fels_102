package com.example.nhs3108.fels102.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nhs3108.fels102.R;
import com.example.nhs3108.fels102.constants.CommonConsts;
import com.example.nhs3108.fels102.utils.InternetUtils;
import com.example.nhs3108.fels102.utils.LoginAsyncTask;
import com.example.nhs3108.fels102.utils.ValidationUtils;

public class LoginActivity extends Activity {
    EditText editTextEmail;
    EditText editTextPassword;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPreferences = getSharedPreferences(CommonConsts.USER_SHARED_PREF, Context.MODE_PRIVATE);
        if (TextUtils.isEmpty(mSharedPreferences.getString(CommonConsts.AUTH_TOKEN_FIELD, null))) {
            setContentView(R.layout.activity_login);
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
                        new LoginAsyncTask(LoginActivity.this, mSharedPreferences).execute(email, password);
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
        } else {
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
        }
    }

    private boolean isValidConditions() {
        ValidationUtils validationUtils = new ValidationUtils(this);
        if (!InternetUtils.checkAvaiable(LoginActivity.this)) {
            Toast.makeText(LoginActivity.this, getString(R.string.msg_no_internet), Toast.LENGTH_SHORT).show();
            return false;
        } else {
            boolean isValidEmail = validationUtils.validateEmail(editTextEmail);
            boolean isValidPassword = validationUtils.validatePassword(editTextPassword);
            return isValidEmail && isValidPassword;
        }
    }
}

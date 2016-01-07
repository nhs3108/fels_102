package com.example.nhs3108.fels102.utils;

import android.content.Context;
import android.widget.EditText;

import com.example.nhs3108.fels102.R;

/**
 * Created by nhs3108 on 1/6/16.
 */
public class ValidationUtils {
    public static final int NAME_MIN_LENGTH = 3;

    public static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    public static final int EMAIL_MIN_LENGTH = 6;
    public static final int EMAIL_MAX_LENGTH = 255;

    public static final int PASSWORD_MIN_LENGTH = 6;
    public static final int PASSWORD_MAX_LENGTH = 255;

    private Context mContext;

    public ValidationUtils(Context mContext) {
        this.mContext = mContext;
    }

    public boolean validateName(EditText editTextName) {
        boolean isValid = false;
        String name = editTextName.getText().toString();
        if (name.length() < NAME_MIN_LENGTH) {
            editTextName.setError(mContext.getString(R.string.error_name_short));
        } else {
            isValid = true;
        }
        return isValid;
    }

    public boolean validateEmail(EditText editTextEmail) {
        boolean isValid = false;
        String email = editTextEmail.getText().toString();
        if (email.length() < EMAIL_MIN_LENGTH) {
            editTextEmail.setError(mContext.getString(R.string.error_email_short));
        } else if (!EmailUtils.isValidPatternEmail(email, EMAIL_PATTERN)) {
            editTextEmail.setError(mContext.getString(R.string.error_email_invalid));
        } else {
            isValid = true;
        }
        return isValid;
    }

    public boolean validatePassword(EditText editTextPasssword) {
        boolean isValid = false;
        String password = editTextPasssword.getText().toString();
        if (password.length() < PASSWORD_MIN_LENGTH) {
            editTextPasssword.setError(mContext.getString(R.string.error_password_short));
        } else {
            isValid = true;
        }
        return isValid;
    }

    public boolean validatePassword(EditText editTextPasssword, EditText editTextRePasssword) {
        boolean isValid = false;
        String password = editTextPasssword.getText().toString();
        String passwordConfirmation = editTextRePasssword.getText().toString();
        if (password.length() < PASSWORD_MIN_LENGTH) {
            editTextPasssword.setError(mContext.getString(R.string.error_password_short));
        } else if (!password.equals(passwordConfirmation)) {
            editTextPasssword.setError(mContext.getString(R.string.error_repassword_not_match));
        } else {
            isValid = true;
        }
        return isValid;
    }
}

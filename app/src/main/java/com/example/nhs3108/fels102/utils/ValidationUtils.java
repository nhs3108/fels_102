package com.example.nhs3108.fels102.utils;

import android.widget.EditText;

/**
 * Created by nhs3108 on 1/6/16.
 */
public class ValidationUtils {
    public static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    public static final int EMAIL_MIN_LENGTH = 6;
    public static final int EMAIL_MAX_LENGTH = 255;

    public static final int PASSWORD_MIN_LENGTH = 6;
    public static final int PASSWORD_MAX_LENGTH = 255;

    public static boolean validateEmail(EditText editTextEmail) {
        boolean isValid = false;
        String email = editTextEmail.getText().toString();
        if (email.length() < EMAIL_MIN_LENGTH) {
            editTextEmail.setError("Email is too short!");
        } else if (!EmailUtils.isValidPatternEmail(email, EMAIL_PATTERN)) {
            editTextEmail.setError("Invalid email!");
        } else {
            isValid = true;
        }
        return isValid;
    }

    public static boolean validatePassword(EditText editTextPasssword) {
        boolean isValid = false;
        String password = editTextPasssword.getText().toString();
        if (password.length() < PASSWORD_MIN_LENGTH) {
            editTextPasssword.setError("Password is too short!");
        } else {
            isValid = true;
        }
        return isValid;
    }
}

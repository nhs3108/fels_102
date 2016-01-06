package com.example.nhs3108.fels102.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by nhs3108 on 1/4/16.
 */
public class EmailUtils {
    public static boolean isValidPatternEmail(String email, String patternStr) {
        Pattern pattern = Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}

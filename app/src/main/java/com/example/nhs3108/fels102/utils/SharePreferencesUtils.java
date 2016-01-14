package com.example.nhs3108.fels102.utils;

import android.content.SharedPreferences;

/**
 * Created by nhs3108 on 1/13/16.
 */
public class SharePreferencesUtils {
    public static void putString(SharedPreferences sharedPreferences, String key, String value) {
        sharedPreferences.edit().putString(key, value).commit();
    }
}

package com.example.nhs3108.fels102.utils;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by nhs3108 on 1/4/16.
 */
public class InternetUtils {
    public static boolean isAvaiable(Context context) {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }
}

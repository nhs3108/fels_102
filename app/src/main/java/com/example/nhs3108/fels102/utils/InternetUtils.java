package com.example.nhs3108.fels102.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.widget.Toast;

import com.example.nhs3108.fels102.R;

/**
 * Created by nhs3108 on 1/4/16.
 */
public class InternetUtils {
    public static boolean checkAvaiable(Context context) {
        boolean isAvaiable = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo() != null;
        if (!isAvaiable) {
            Toast.makeText(context, context.getString(R.string.msg_no_internet), Toast.LENGTH_SHORT).show();
        }
        return isAvaiable;
    }
}

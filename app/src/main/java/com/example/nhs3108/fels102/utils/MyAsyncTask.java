package com.example.nhs3108.fels102.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.example.nhs3108.fels102.R;

/**
 * Created by nhs3108 on 1/14/16.
 */
public abstract class MyAsyncTask<T1, T2, T3> extends AsyncTask<T1, T2, T3> {
    protected Context context;
    protected ProgressDialog progressDialog;

    public MyAsyncTask(Context context) {
        this.context = context;
    }

    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(context.getString(R.string.msg_wait));
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    protected void onPostExecute(T3 result) {
        progressDialog.dismiss();
    }
}

package com.example.nhs3108.fels102.utils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.nhs3108.fels102.R;
import com.example.nhs3108.fels102.constants.HttpStatusConsts;

/**
 * Created by nhs3108 on 1/14/16.
 */
public abstract class MyAsyncTask<T1, T2, T3> extends AsyncTask <T1, T2, T3> {
    protected Context context;
    private ProgressDialog mProgressDialog;

    public MyAsyncTask(Context context) {
        this.context = context;
    }

    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage(context.getString(R.string.msg_wait));
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    protected void onPostExecute(T3 result) {
        mProgressDialog.dismiss();
    }
}

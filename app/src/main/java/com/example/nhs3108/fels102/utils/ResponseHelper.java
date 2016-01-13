package com.example.nhs3108.fels102.utils;

import android.content.Context;
import android.widget.Toast;

import com.example.nhs3108.fels102.R;
import com.example.nhs3108.fels102.constants.HttpStatusConsts;

/**
 * Created by nhs3108 on 1/4/16.
 */
public class ResponseHelper {
    private int responseCode;
    private String responseBody;

    public ResponseHelper(int responseCode, String responseBody) {
        this.responseCode = responseCode;
        this.responseBody = responseBody;
    }

    public static void httpStatusNotify(Context context, int statusCode) {
        switch (statusCode) {
            case HttpStatusConsts.OK:
                Toast.makeText(context, "OK", Toast.LENGTH_SHORT).show();
                break;
            case HttpStatusConsts.BAD_REQUEST:
                Toast.makeText(context, context.getString(R.string.bad_request), Toast.LENGTH_SHORT).show();
                break;
            case HttpStatusConsts.UNAUTHORIZED:
                Toast.makeText(context, context.getString(R.string.error_unauthorized), Toast.LENGTH_SHORT).show();
                break;
            case HttpStatusConsts.NOT_FOUND:
                Toast.makeText(context, context.getString(R.string.error_server_not_found), Toast.LENGTH_SHORT).show();
                break;
            case HttpStatusConsts.INTERNAL_SERVER_ERROR:
                Toast.makeText(context, context.getString(R.string.error_internal_server_error), Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(context, context.getString(R.string.error_unknown), Toast.LENGTH_SHORT).show();
        }
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getResponseBody() {
        return responseBody;
    }
}

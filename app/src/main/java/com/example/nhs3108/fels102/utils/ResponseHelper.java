package com.example.nhs3108.fels102.utils;

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

    public int getResponseCode() {
        return responseCode;
    }

    public String getResponseBody() {
        return responseBody;
    }
}

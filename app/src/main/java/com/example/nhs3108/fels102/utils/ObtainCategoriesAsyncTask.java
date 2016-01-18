package com.example.nhs3108.fels102.utils;

import android.app.Activity;

import com.example.nhs3108.fels102.constants.HttpStatusConsts;
import com.example.nhs3108.fels102.constants.UrlConsts;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class ObtainCategoriesAsyncTask extends MyAsyncTask<String, Void, String> {
    protected int statusCode;
    protected String authToken;
    protected String responseBody;
    protected int page;
    protected ArrayList<Category> categories = new ArrayList<Category>();
    String authTokenParamName = "auth_token";
    String pageParamName = "page";

    public ObtainCategoriesAsyncTask(Activity activity, ArrayList<Category> categories, String authToken, int page) {
        super(activity);
        this.categories = categories;
        this.authToken = authToken;
        this.page = page;
    }

    protected String doInBackground(String... args) {
        NameValuePair nvp1 = new NameValuePair(authTokenParamName, authToken);
        NameValuePair nvp2 = new NameValuePair(pageParamName, String.valueOf(page));
        ResponseHelper responseHelper = null;
        try {
            responseHelper = RequestHelper.executeRequest(UrlConsts.CATEGORIES_URL, RequestHelper.Method.GET, nvp1, nvp2);
            statusCode = responseHelper.getResponseCode();
            responseBody = responseHelper.getResponseBody();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        switch (statusCode) {
            case HttpStatusConsts.OK:
                try {
                    updateCategoriesList();
                } catch (JSONException e) {
                    // Do nothing
                }
                break;
            default:
                ResponseHelper.httpStatusNotify(context, statusCode);
        }
    }

    protected void updateCategoriesList() throws JSONException {
        JSONArray categoriesJson = new JSONObject(responseBody).optJSONArray("categories");
        int numOfWords = categoriesJson.length();
        for (int i = 0; i < numOfWords; i++) {
            int categoryId = categoriesJson.optJSONObject(i).optInt("id");
            String categoryName = categoriesJson.optJSONObject(i).optString("name");
            String categoryPhotoUrl = categoriesJson.optJSONObject(i).optString("photo");
            int categoryLearnedWords = categoriesJson.optJSONObject(i).getInt("learned_words");
            categories.add(new Category(categoryId, categoryName, categoryPhotoUrl, categoryLearnedWords));
        }
    }
}
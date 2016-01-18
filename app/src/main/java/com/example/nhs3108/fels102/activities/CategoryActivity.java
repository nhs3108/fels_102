package com.example.nhs3108.fels102.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.nhs3108.fels102.R;
import com.example.nhs3108.fels102.adapters.CategoryAdapter;
import com.example.nhs3108.fels102.constants.CommonConsts;
import com.example.nhs3108.fels102.constants.HttpStatusConsts;
import com.example.nhs3108.fels102.constants.UrlConsts;
import com.example.nhs3108.fels102.listeners.EndlessRecyclerOnScrollListener;
import com.example.nhs3108.fels102.utils.Category;
import com.example.nhs3108.fels102.utils.NameValuePair;
import com.example.nhs3108.fels102.utils.RequestHelper;
import com.example.nhs3108.fels102.utils.ResponseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by nhs3108 on 1/11/16.
 */
public class CategoryActivity extends Activity {
    private String mAuthToken;
    private SharedPreferences mSharedPreferences;
    private ArrayList<Category> mCategoriesList = new ArrayList<Category>();
    private CategoryAdapter mCategoryAdapter;
    private RecyclerView mRecycleViewCategories;
    private LinearLayoutManager mLayoutManager;
    private int mCurrentPage = 0;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        initialize();
        setupCategoryRecycleView();
        if (!TextUtils.isEmpty(mAuthToken)) {
            new ObtainCategoriesAsynTask().execute(mAuthToken);
        }
        setUpEventHandlers();
    }

    private void initialize() {
        mSharedPreferences = getSharedPreferences(CommonConsts.USER_SHARED_PREF, Context.MODE_PRIVATE);
        mAuthToken = mSharedPreferences.getString(CommonConsts.AUTH_TOKEN_FIELD, null);
        mRecycleViewCategories = (RecyclerView) findViewById(R.id.list_categories);
        mCategoryAdapter = new CategoryAdapter(this, mCategoriesList);
        mLayoutManager = new LinearLayoutManager(this);
    }

    private void setupCategoryRecycleView() {
        mRecycleViewCategories.setHasFixedSize(true);
        mRecycleViewCategories.setLayoutManager(mLayoutManager);
        mRecycleViewCategories.setAdapter(mCategoryAdapter);
    }

    private void setUpEventHandlers() {
        mRecycleViewCategories.addOnScrollListener(new EndlessRecyclerOnScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore() {
                new ObtainCategoriesAsynTask().execute(mAuthToken);
            }
        });
    }

    private class ObtainCategoriesAsynTask extends AsyncTask<String, Void, String> {
        String authTokenParamName = "auth_token";
        String pageParamName = "page";
        private ProgressDialog mProgressDialog;
        private int mStatusCode;
        private String mResponseBody;

        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(CategoryActivity.this);
            mProgressDialog.setMessage(getString(R.string.msg_wait));
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(true);
            mProgressDialog.show();
            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    CategoryActivity.this.finish();
                }
            });
        }

        protected String doInBackground(String... args) {
            String authToken = args[0];
            NameValuePair nvp1 = new NameValuePair(authTokenParamName, authToken);
            NameValuePair nvp2 = new NameValuePair(pageParamName, String.valueOf(++mCurrentPage));
            ResponseHelper responseHelper = null;
            try {
                responseHelper = RequestHelper.executeRequest(UrlConsts.CATEGORIES_URL, RequestHelper.Method.GET, nvp1, nvp2);
                mStatusCode = responseHelper.getResponseCode();
                mResponseBody = responseHelper.getResponseBody();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mProgressDialog.dismiss();
            switch (mStatusCode) {
                case HttpStatusConsts.OK:
                    try {
                        updateCategoriesList();
                    } catch (JSONException e) {
                        // Do nothing
                    }
                    break;
                case HttpStatusConsts.UNAUTHORIZED:
                    String errorMessage = getString(R.string.error_unauthorized);
                    break;
                case HttpStatusConsts.NOT_FOUND:
                    Toast.makeText(CategoryActivity.this, getString(R.string.error_server_not_found), Toast.LENGTH_SHORT).show();
                    break;
                case HttpStatusConsts.INTERNAL_SERVER_ERROR:
                    Toast.makeText(CategoryActivity.this, getString(R.string.error_internal_server_error), Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(CategoryActivity.this, getString(R.string.error_unknown), Toast.LENGTH_SHORT).show();
            }
        }

        private void updateCategoriesList() throws JSONException {
            JSONArray categoriesJson = new JSONObject(mResponseBody).optJSONArray("categories");
            int numOfWords = categoriesJson.length();
            for (int i = 0; i < numOfWords; i++) {
                int categoryId = categoriesJson.optJSONObject(i).optInt("id");
                String categoryName = categoriesJson.optJSONObject(i).optString("name");
                String categoryPhotoUrl = categoriesJson.optJSONObject(i).optString("photo");
                int categoryLearnedWords = categoriesJson.optJSONObject(i).getInt("learned_words");
                mCategoriesList.add(new Category(categoryId, categoryName, categoryPhotoUrl, categoryLearnedWords));
            }
            mCategoryAdapter.notifyDataSetChanged();
        }
    }
}

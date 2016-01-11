package com.example.nhs3108.fels102.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.nhs3108.fels102.R;
import com.example.nhs3108.fels102.adapters.CategoryAdapter;
import com.example.nhs3108.fels102.constants.CommonConsts;
import com.example.nhs3108.fels102.constants.HttpStatusConsts;
import com.example.nhs3108.fels102.constants.UrlConsts;
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
    private SharedPreferences mSharedPreferences;
    private ArrayList<Category> mCategoriesList = new ArrayList<Category>();
    private CategoryAdapter mCategoryAdapter;
    private String mAuthToken;
    private ListView listViewCategories;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        initialize();
        if (!TextUtils.isEmpty(mAuthToken)) {
            mCategoryAdapter = new CategoryAdapter(this, R.layout.item_category, mCategoriesList);
            listViewCategories.setAdapter(mCategoryAdapter);
            new ObtainCategoriesAsynTask().execute(mAuthToken);
        }
        listViewCategories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CategoryActivity.this, LessionActivity.class);
                intent.putExtra("categoryId", mCategoriesList.get(position).getId());
                startActivity(intent);
            }
        });
    }

    private void initialize() {
        mSharedPreferences = getSharedPreferences(CommonConsts.USER_SHARED_PREF, Context.MODE_PRIVATE);
        mAuthToken = mSharedPreferences.getString(CommonConsts.AUTH_TOKEN_FIELD, null);
        listViewCategories = (ListView) findViewById(R.id.list_categories);
    }

    private class ObtainCategoriesAsynTask extends AsyncTask<String, Void, String> {
        String authTokenParamName = "auth_token";
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
            ResponseHelper responseHelper = null;
            try {
                responseHelper = RequestHelper.executeRequest(UrlConsts.CATEGORIES_URL, RequestHelper.Method.GET, nvp1);
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

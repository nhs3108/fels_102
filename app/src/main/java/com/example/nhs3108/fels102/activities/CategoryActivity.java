package com.example.nhs3108.fels102.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.example.nhs3108.fels102.R;
import com.example.nhs3108.fels102.adapters.CategoryAdapter;
import com.example.nhs3108.fels102.constants.CommonConsts;
import com.example.nhs3108.fels102.listeners.EndlessRecyclerOnScrollListener;
import com.example.nhs3108.fels102.utils.Category;
import com.example.nhs3108.fels102.utils.ObtainCategoriesAsyncTask;

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
    private int mCurrentPage = 1;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        initialize();
        setupCategoryRecycleView();
        if (!TextUtils.isEmpty(mAuthToken)) {
            new ObtainCategoriesTask(CategoryActivity.this, mCategoriesList, mAuthToken, mCurrentPage)
                    .execute(mAuthToken);
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
                new ObtainCategoriesTask(CategoryActivity.this, mCategoriesList, mAuthToken, mCurrentPage)
                        .execute();
            }
        });
    }

    private class ObtainCategoriesTask extends ObtainCategoriesAsyncTask {
        ObtainCategoriesTask(Activity activity, ArrayList<Category> categories, String authToken, int currentPage) {
            super(activity, categories, authToken, currentPage);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mCurrentPage++;
            mCategoryAdapter.notifyDataSetChanged();
        }
    }
}

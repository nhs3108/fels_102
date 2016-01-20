package com.example.nhs3108.fels102.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.nhs3108.fels102.R;
import com.example.nhs3108.fels102.adapters.LessonFragmentPagerAdapter;
import com.example.nhs3108.fels102.constants.CommonConsts;
import com.example.nhs3108.fels102.constants.HttpStatusConsts;
import com.example.nhs3108.fels102.constants.UrlConsts;
import com.example.nhs3108.fels102.fragments.QuestionFragment;
import com.example.nhs3108.fels102.utils.Answer;
import com.example.nhs3108.fels102.utils.MyAsyncTask;
import com.example.nhs3108.fels102.utils.NameValuePair;
import com.example.nhs3108.fels102.utils.RequestHelper;
import com.example.nhs3108.fels102.utils.ResponseHelper;
import com.example.nhs3108.fels102.utils.UserAnswer;
import com.example.nhs3108.fels102.utils.Word;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nhs3108 on 1/13/16.
 */
public class DoingLessonActivity extends FragmentActivity {
    private ArrayList<NameValuePair> mRequestParamsPair = new ArrayList<NameValuePair>();
    private ArrayList<UserAnswer> mUserAnswers = new ArrayList<UserAnswer>();
    private SharedPreferences mSharedPreferences;
    private ArrayList<Word> mQuestionList = new ArrayList<Word>();
    private ViewPager mViewPager;
    private TextView mTextViewLessonName;
    private String mAuthToken;
    private int mLessonId;
    private String mLessonName;
    private String mCategoryName;
    private List<Fragment> mQuestionFragmentList = new ArrayList<Fragment>();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doing_lesson);
        initialize();
        mTextViewLessonName.setText(mLessonName);
        LessonFragmentPagerAdapter lessonFragmentPagerAdapter =
                new LessonFragmentPagerAdapter(getSupportFragmentManager(), mQuestionFragmentList);
        mViewPager.setAdapter(lessonFragmentPagerAdapter);
        setupEventHanlders();
    }

    private void initialize() {
        mSharedPreferences = getSharedPreferences(CommonConsts.USER_SHARED_PREF, Context.MODE_PRIVATE);
        mAuthToken = mSharedPreferences.getString(CommonConsts.AUTH_TOKEN_FIELD, null);
        Intent data = getIntent();
        mLessonId = data.getIntExtra(CommonConsts.KEY_LESSON_ID, -1);
        mLessonName = data.getStringExtra(CommonConsts.KEY_LESSON_NAME);
        if (TextUtils.isEmpty(mLessonName)) {
            mLessonName = getString(R.string.default_lesson_name);
        }
        mCategoryName = data.getStringExtra(CommonConsts.KEY_CATEGORY_NAME);
        if (TextUtils.isEmpty(mCategoryName)) {
            mCategoryName = getString(R.string.default_category_name);
        }
        initQuestionList();
        mViewPager = (ViewPager) findViewById(R.id.view_pager_question);
        mViewPager.setOffscreenPageLimit(mQuestionList.size());
        mTextViewLessonName = (TextView) findViewById(R.id.text_lesson_name);
        initQuestionFragmentList();
    }

    private void initQuestionList() {
        String wordsData = mSharedPreferences.getString(CommonConsts.KEY_WORDS_DATA, null);
        try {
            JSONArray wordsJson = new JSONArray(wordsData);
            int numOfWords = wordsJson.length();
            for (int i = 0; i < numOfWords; i++) {
                String wordContent = wordsJson.optJSONObject(i).optString("content");
                JSONArray answersJson = wordsJson.optJSONObject(i).optJSONArray("answers");
                ArrayList<Answer> answers = new ArrayList<Answer>();
                int numOfAnswers = answersJson.length();
                for (int j = 0; j < numOfAnswers; j++) {
                    String answerContent = answersJson.optJSONObject(j).optString("content");
                    Boolean isCorrect = answersJson.optJSONObject(j).optBoolean("is_correct");
                    answers.add(new Answer(answerContent, isCorrect));
                }
                mQuestionList.add(new Word(wordContent, answers));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initQuestionFragmentList() {

        int questionCount = mQuestionList.size();
        for (int i = 0; i < questionCount; i++) {
            final int index = i;

            mQuestionFragmentList.add(new QuestionFragment() {
                @Override
                public Word getWord() {
                    return mQuestionList.get(index);
                }

                @Override
                public ArrayList<NameValuePair> getRequestParamsPairs() {
                    return mRequestParamsPair;
                }

                @Override
                public ArrayList<UserAnswer> getUserAnswers() {
                    return mUserAnswers;
                }

                @Override
                public void changePage() {
                    mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
                }

                @Override
                public int getFragmentIndex() {
                    return index;
                }
            });
        }
    }

    private void setupEventHanlders() {
        ImageButton finishDoingLesson = (ImageButton) findViewById(R.id.btn_finish);
        finishDoingLesson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLessonId != -1 && !TextUtils.isEmpty(mAuthToken)) {
                    new ObtainLessonResult(DoingLessonActivity.this).execute(mAuthToken, String.valueOf(mLessonId));
                } else {
                    finish();
                }
            }
        });
    }

    private class ObtainLessonResult extends MyAsyncTask<String, Void, String> {
        String authTokenParamName = "auth_token";
        private int mStatusCode;
        private String mResponseBody;

        public ObtainLessonResult(Context context) {
            super(context);
        }

        protected String doInBackground(String... args) {
            String authToken = args[0];
            String updateLessonUrl = String.format(UrlConsts.UPDATE_LESSON_URl_FORMAT, String.valueOf(mLessonId));
            NameValuePair nvp1 = new NameValuePair(authTokenParamName, authToken);
            NameValuePair nvp2 = new NameValuePair("lesson[learned]", "1");
            mRequestParamsPair.add(nvp1);
            mRequestParamsPair.add(nvp2);
            ResponseHelper responseHelper = null;
            try {
                responseHelper = RequestHelper.executeRequest(updateLessonUrl, RequestHelper.Method.PATCH, mRequestParamsPair);
                mStatusCode = responseHelper.getResponseCode();
                mResponseBody = responseHelper.getResponseBody();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            switch (mStatusCode) {
                case HttpStatusConsts.OK:
                    Intent intent = new Intent(DoingLessonActivity.this, ResultActivity.class);
                    intent.putExtra(CommonConsts.KEY_USER_ANSWERS, mUserAnswers);
                    intent.putExtra(CommonConsts.KEY_LESSON_NAME, mLessonName);
                    intent.putExtra(CommonConsts.KEY_CATEGORY_NAME, mCategoryName);
                    startActivity(intent);
                    finish();
                    break;
                default:
                    ResponseHelper.httpStatusNotify(DoingLessonActivity.this, mStatusCode);
            }
        }
    }
}

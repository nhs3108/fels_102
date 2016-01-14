package com.example.nhs3108.fels102.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ListView;
import android.widget.TextView;

import com.example.nhs3108.fels102.R;
import com.example.nhs3108.fels102.adapters.UserAnwserAdapter;
import com.example.nhs3108.fels102.constants.CommonConsts;
import com.example.nhs3108.fels102.utils.UserAnswer;

import java.util.ArrayList;

/**
 * Created by nhs3108 on 1/14/16.
 */
public class ResultActivity extends Activity {
    private String mLessonName;
    private ArrayList<UserAnswer> mUserAnswers = new ArrayList<>();
    private TextView mLessonScore;
    private ListView mListViewUserResults;
    private int mTotalCount = 0;
    private int mCorrectCount;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        initialize();
        calculateResult();
        UserAnwserAdapter userAnwserAdapter = new UserAnwserAdapter(this, R.layout.item_result, mUserAnswers);
        mListViewUserResults.setAdapter(userAnwserAdapter);
        mLessonScore.setText(String.format("%s/%s", mCorrectCount, mTotalCount));
    }

    private void initialize() {
        Intent data = getIntent();
        mUserAnswers = (ArrayList<UserAnswer>) data.getSerializableExtra(CommonConsts.KEY_USER_ANSWERS);
        mLessonName = data.getStringExtra(CommonConsts.KEY_LESSON_NAME);
        if (TextUtils.isEmpty(mLessonName)) {
            mLessonName = getString(R.string.default_lesson_name);
        }
        mLessonScore = (TextView) findViewById(R.id.score);
        mListViewUserResults = (ListView) findViewById(R.id.list_user_results);
    }

    public void calculateResult() {
        mTotalCount = mUserAnswers.size();
        for (int i = 0; i < mTotalCount; i++) {
            if (mUserAnswers.get(i).isCorrect()) {
                mCorrectCount++;
            }
        }
    }
}

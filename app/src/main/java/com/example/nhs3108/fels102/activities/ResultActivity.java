package com.example.nhs3108.fels102.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nhs3108.fels102.R;
import com.example.nhs3108.fels102.adapters.UserAnwserAdapter;
import com.example.nhs3108.fels102.constants.CommonConsts;
import com.example.nhs3108.fels102.listeners.CommonEventHandlerUtils;
import com.example.nhs3108.fels102.models.Result;
import com.example.nhs3108.fels102.models.ResultHelper;
import com.example.nhs3108.fels102.utils.UserAnswer;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by nhs3108 on 1/14/16.
 */
public class ResultActivity extends Activity {
    private String mLessonName;
    private String mCategoryName;
    private ArrayList<UserAnswer> mUserAnswers = new ArrayList<>();
    private TextView mTextViewLessonScore;
    private TextView mTextViewLessonName;
    private ListView mListViewUserResults;
    private int mTotalCount = 0;
    private int mCorrectCount = 0;
    private ImageButton mBtnBack;
    private ResultHelper mResultHelper;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        initialize();
        calculateResult();
        UserAnwserAdapter userAnwserAdapter = new UserAnwserAdapter(this, R.layout.item_result, mUserAnswers);
        mListViewUserResults.setAdapter(userAnwserAdapter);
        String score = String.format(CommonConsts.SCORE_FORMAT, mCorrectCount, mTotalCount);
        mTextViewLessonScore.setText(score);
        mTextViewLessonName.setText(mLessonName);

        storeResult(mLessonName, mCategoryName, score);
        CommonEventHandlerUtils.clickBack(ResultActivity.this, mBtnBack);
        ImageButton btnOk = (ImageButton) findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ResultActivity.this, ShowingLessonResultActivity.class));
                finish();
            }
        });
    }

    private void initialize() {
        mResultHelper = new ResultHelper(this);
        Intent data = getIntent();
        mUserAnswers = (ArrayList<UserAnswer>) data.getSerializableExtra(CommonConsts.KEY_USER_ANSWERS);
        mLessonName = data.getStringExtra(CommonConsts.KEY_LESSON_NAME);
        if (TextUtils.isEmpty(mLessonName)) {
            mLessonName = getString(R.string.default_lesson_name);
        }
        mCategoryName = data.getStringExtra(CommonConsts.KEY_CATEGORY_NAME);
        if (TextUtils.isEmpty(mCategoryName)) {
            mCategoryName = getString(R.string.default_category_name);
        }
        mTextViewLessonScore = (TextView) findViewById(R.id.score);
        mListViewUserResults = (ListView) findViewById(R.id.list_user_results);
        mTextViewLessonName = (TextView) findViewById(R.id.text_lesson_name);
        mBtnBack = (ImageButton) findViewById(R.id.btn_back);
    }

    public void calculateResult() {
        mTotalCount = mUserAnswers.size();
        for (int i = 0; i < mTotalCount; i++) {
            if (mUserAnswers.get(i).isCorrect()) {
                mCorrectCount++;
            }
        }
    }

    public void storeResult(String lessonName, String categoryName, String score) {
        Result result = new Result(lessonName, categoryName, score);
        try {
            mResultHelper.insert(result);
        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(ResultActivity.this, getString(R.string.error_cannot_save_result), Toast.LENGTH_SHORT).show();
        }
    }
}

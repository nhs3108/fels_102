package com.example.nhs3108.fels102.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.nhs3108.fels102.R;
import com.example.nhs3108.fels102.adapters.LessonResultAdapter;
import com.example.nhs3108.fels102.listeners.CommonEventHandlerUtils;
import com.example.nhs3108.fels102.models.Result;
import com.example.nhs3108.fels102.models.ResultHelper;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by nhs3108 on 1/20/16.
 */
public class ShowingLessonResultActivity extends Activity {
    private ResultHelper mResultHelper;
    private ArrayList<Result> mResultList = new ArrayList<Result>();
    private ListView mListViewLessonResult;
    private LessonResultAdapter mLessonResultAdapter;
    private ImageButton mBtnBack;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showing_lesson_result);
        initialize();
        mLessonResultAdapter = new LessonResultAdapter(this, R.layout.item_lesson_result, mResultList);
        mListViewLessonResult.setAdapter(mLessonResultAdapter);
        CommonEventHandlerUtils.clickBack(this, mBtnBack);
    }

    public void initialize() {
        mResultHelper = new ResultHelper(this);
        try {
            mResultList = mResultHelper.getAll();
        } catch (SQLException e) {
            // Do nothing. mResultList not change
        }
        mListViewLessonResult = (ListView) findViewById(R.id.list_lessons_result);
        mBtnBack = (ImageButton) findViewById(R.id.btn_back);
    }
}

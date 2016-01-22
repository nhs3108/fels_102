package com.example.nhs3108.fels102.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by nhs3108 on 1/20/16.
 */
public class ResultHelper extends DatabaseHelper {
    private String[] mColumns = {Result.RESULT_ID, Result.RESULT_LESSON_NAME, Result.RESULT_SCORE, Result.RESULT_CATEGORY_NAME};

    public ResultHelper(Context context) {
        super(context);
    }

    public long insert(Result result) throws SQLException {
        open();
        long rowIdInserted = 0;
        ContentValues insertValues;
        insertValues = new ContentValues();
        insertValues.put(Result.RESULT_LESSON_NAME, result.getLessonName());
        insertValues.put(Result.RESULT_SCORE, result.getScore());
        insertValues.put(Result.RESULT_CATEGORY_NAME, result.getCategoryName());
        rowIdInserted = sDatabase.insert(Result.TABLE_NAME, null, insertValues);
        close();
        return rowIdInserted;
    }

    public ArrayList<Result> getAll() throws SQLException {
        open();
        ArrayList<Result> list = new ArrayList<Result>();
        Cursor cursor = sDatabase.query(Result.TABLE_NAME, mColumns, null, null, null, null, null);
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(Result.RESULT_ID));
            String lessonName = cursor.getString(cursor.getColumnIndex(Result.RESULT_LESSON_NAME));
            String score = cursor.getString(cursor.getColumnIndex(Result.RESULT_SCORE));
            String categoryName = cursor.getString(cursor.getColumnIndex(Result.RESULT_CATEGORY_NAME));
            Result result = new Result(lessonName, categoryName, score);
            result.setId(id);
            list.add(result);
        }
        close();
        return list;
    }
}

package com.example.nhs3108.fels102.models;

/**
 * Created by nhs3108 on 1/20/16.
 */

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by hongson on 18/12/2015.
 */
public class DatabaseHelper {
    public static final int DATA_VERSION = 1;
    public static final String DATABASE_NAME = "fels.db";

    public static final String CREATE_TABLE_RESULT = "CREATE TABLE IF NOT EXISTS " + Result.TABLE_NAME + "("
            + Result.RESULT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + Result.RESULT_LESSON_NAME + " TEXT DEFAULT '',"
            + Result.RESULT_SCORE + " TEXT DEFAULT '0/0',"
            + Result.RESULT_CATEGORY_NAME + " TEXT DEFAULT '')";

    public static SQLiteDatabase sDatabase;
    protected static Context sContext;
    protected OpenHelper mOpenHelper;

    public DatabaseHelper(Context context) {
        DatabaseHelper.sContext = context;
    }

    public DatabaseHelper open() throws SQLException {
        mOpenHelper = new OpenHelper(sContext);
        sDatabase = mOpenHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mOpenHelper.close();
    }

    private static class OpenHelper extends SQLiteOpenHelper {
        public OpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATA_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE_RESULT);
        }

        public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
            db.execSQL("DROP TABLE IF EXISTS " + Result.TABLE_NAME);
            onCreate(db);
        }
    }
}

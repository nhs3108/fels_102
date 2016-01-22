package com.example.nhs3108.fels102.models;

/**
 * Created by nhs3108 on 1/20/16.
 */
public class Result {
    public static String TABLE_NAME = "results";
    public static String RESULT_ID = "id";
    public static String RESULT_LESSON_NAME = "lessonId";
    public static String RESULT_CATEGORY_NAME = "categoryName";
    public static String RESULT_SCORE = "score";
    private int id;
    private String lessonName;
    private String categoryName;
    private String score;

    public Result(String lessonName, String categoryName, String score) {
        this.lessonName = lessonName;
        this.categoryName = categoryName;
        this.score = score;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLessonName() {
        return lessonName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getScore() {
        return score;
    }
}

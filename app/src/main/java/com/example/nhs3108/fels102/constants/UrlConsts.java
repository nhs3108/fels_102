package com.example.nhs3108.fels102.constants;

/**
 * Created by nhs3108 on 1/4/16.
 */
public class UrlConsts {
    public static final String BASE_URL = "https://manh-nt.herokuapp.com";
    public static final String LOGIN = BASE_URL + "/login.json";
    public static final String REGISTER_URL = BASE_URL + "/users.json";
    public static final String WORDS_URL = BASE_URL + "/words.json";
    public static final String CATEGORIES_URL = BASE_URL + "/categories.json";
    public static final String LESSON_URl_FORMAT = BASE_URL + "/categories/%s/lessons.json";
    public static final String UPDATE_LESSON_URl_FORMAT = BASE_URL + "/lessons/%s.json";
    ;
}

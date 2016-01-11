package com.example.nhs3108.fels102.utils;

/**
 * Created by nhs3108 on 1/9/16.
 */
public class UserActivity {
    private String content;
    private String time;

    public UserActivity(String content, String time) {
        this.content = content;
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public String getTime() {
        return time;
    }
}

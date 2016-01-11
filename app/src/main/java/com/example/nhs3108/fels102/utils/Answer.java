package com.example.nhs3108.fels102.utils;

/**
 * Created by nhs3108 on 1/11/16.
 */
public class Answer {
    private String content;
    private boolean isCorrect;

    public Answer(String content, boolean isCorrect) {
        this.content = content;
        this.isCorrect = isCorrect;
    }

    public String getContent() {
        return content;
    }

    public boolean isCorrect() {
        return isCorrect;
    }
}

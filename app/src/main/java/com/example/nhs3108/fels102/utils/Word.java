package com.example.nhs3108.fels102.utils;

/**
 * Created by nhs3108 on 1/11/16.
 */
public class Word {
    private String content;
    private Answer answer;

    public Word(String content, Answer answer) {
        this.content = content;
        this.answer = answer;
    }

    public String getContent() {
        return content;
    }

    public Answer getAnswer() {
        return answer;
    }
}

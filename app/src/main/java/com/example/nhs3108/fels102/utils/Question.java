package com.example.nhs3108.fels102.utils;

/**
 * Created by nhs3108 on 1/27/16.
 */
public class Question {
    private Word word;
    private int resultId;

    public Question(Word word, int resultId) {
        this.word = word;
        this.resultId = resultId;
    }

    public Word getWord() {
        return word;
    }

    public int getResultId() {
        return resultId;
    }
}

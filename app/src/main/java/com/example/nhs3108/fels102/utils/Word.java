package com.example.nhs3108.fels102.utils;

import java.util.ArrayList;

/**
 * Created by nhs3108 on 1/11/16.
 */
public class Word {
    private String content;
    private ArrayList<Answer> answers;

    public Word(String content, ArrayList<Answer> answers) {
        this.content = content;
        this.answers = answers;
    }

    public String getContent() {
        return content;
    }

    public ArrayList<Answer> getAnswers() {
        return answers;
    }

    public Answer getCorrectAnswer() {
        Answer correctAnswer = null;
        for (Answer answer : answers) {
            if (answer.isCorrect()) {
                correctAnswer = answer;
                break;
            }
        }
        return correctAnswer;
    }
}

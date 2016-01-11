package com.example.nhs3108.fels102.utils;

/**
 * Created by nhs3108 on 1/11/16.
 */
public class Category {
    private int id;
    private String name;
    private String photoUrl;
    private int sumOfLearnedWords;

    public Category(int id, String name, String photoUrl, int sumOfLearnedWords) {
        this.id = id;
        this.name = name;
        this.photoUrl = photoUrl;
        this.sumOfLearnedWords = sumOfLearnedWords;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public int getSumOfLearnedWords() {
        return sumOfLearnedWords;
    }
}

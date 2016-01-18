package com.example.nhs3108.fels102.utils;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by nhs3108 on 1/11/16.
 */
public class Category implements Parcelable {
    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };
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

    protected Category(Parcel in) {
        id = in.readInt();
        name = in.readString();
        photoUrl = in.readString();
        sumOfLearnedWords = in.readInt();
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

    public String toString() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(photoUrl);
        dest.writeInt(sumOfLearnedWords);
    }
}

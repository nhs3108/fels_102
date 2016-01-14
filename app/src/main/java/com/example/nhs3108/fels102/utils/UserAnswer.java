package com.example.nhs3108.fels102.utils;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by nhs3108 on 1/14/16.
 */
public class UserAnswer implements Parcelable {
    public static final Creator<UserAnswer> CREATOR = new Creator<UserAnswer>() {
        @Override
        public UserAnswer createFromParcel(Parcel in) {
            return new UserAnswer(in);
        }

        @Override
        public UserAnswer[] newArray(int size) {
            return new UserAnswer[size];
        }
    };
    private String wordContent;
    private String anwserContent;
    private boolean isCorrect;

    public UserAnswer(String wordContent, String anwserContent, boolean isCorrect) {
        this.wordContent = wordContent;
        this.anwserContent = anwserContent;
        this.isCorrect = isCorrect;
    }

    protected UserAnswer(Parcel in) {
        wordContent = in.readString();
        anwserContent = in.readString();
        isCorrect = in.readByte() != 0;
    }

    public String getWordContent() {
        return wordContent;
    }

    public String getAnwserContent() {
        return anwserContent;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(wordContent);
        dest.writeString(anwserContent);
        dest.writeByte((byte) (isCorrect ? 1 : 0));
    }
}

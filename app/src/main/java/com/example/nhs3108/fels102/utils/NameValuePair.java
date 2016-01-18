package com.example.nhs3108.fels102.utils;

/**
 * Created by nhs3108 on 1/4/16.
 */
public class NameValuePair {
    private String name;
    private String value;

    public NameValuePair(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return name;
    }
}

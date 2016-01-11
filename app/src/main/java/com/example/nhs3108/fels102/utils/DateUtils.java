package com.example.nhs3108.fels102.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hongson on 22/12/2015.
 */
public class DateUtils {
    public static Date convertFromString(String dateString, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try {
            return simpleDateFormat.parse(dateString);
        } catch (ParseException e) {
            return new Date();
        }
    }

    public static String convertToString(Date date, String format) {
        date = (date != null) ? date : new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }
}

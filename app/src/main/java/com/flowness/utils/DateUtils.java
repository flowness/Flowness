package com.flowness.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    public static Date getDate(String dateStr) throws ParseException {
        DateFormat df = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
        return df.parse(dateStr);
    }
}

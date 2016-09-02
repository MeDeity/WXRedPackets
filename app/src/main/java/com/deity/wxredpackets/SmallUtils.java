package com.deity.wxredpackets;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Deity on 2016/9/2.
 */
public class SmallUtils {
    private static final SimpleDateFormat TIME_FORMAT= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

    public static long convertTime2Timestamp(String timeStr) throws ParseException {
        Date date = TIME_FORMAT.parse(timeStr);
        return date.getTime();
    }
}

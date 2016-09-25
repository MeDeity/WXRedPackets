package com.deity.wxredpackets;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Deity on 2016/9/2.
 */
public class SmallUtils {
    public static final SimpleDateFormat TIME_FORMAT= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

    public static long convertTime2Timestamp(String timeStr) throws ParseException {
        Date date = TIME_FORMAT.parse(timeStr);
        return date.getTime();
    }

    public static boolean isListEmpty(List list){
        if (null==list||list.size()==0){
            return true;
        }
        return false;
    }
}

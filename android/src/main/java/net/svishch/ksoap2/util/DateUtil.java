package net.svishch.ksoap2.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    public static Date parseDate1C(String date1C) throws ParseException {

        String[] date1Carr = date1C.split("T");
        String date1CNew = date1Carr[0]+ " "+date1Carr[1];
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.parse(date1CNew);
    }
}

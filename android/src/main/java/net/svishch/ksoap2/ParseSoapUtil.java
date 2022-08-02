package net.svishch.ksoap2;

import net.svishch.ksoap2.util.DateUtil;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

public class ParseSoapUtil {


    public static String checkString(String str) {
        if (str.equals("anyType{}")) {
            str = "";
        }

        return str;
    }

    public static int checkInt(String str) {

        if (str.equals("anyType{}")) {
            return 0;
        }
        return Integer.parseInt(str);
    }

    public static long checkLong(String str) {
        if (str.equals("anyType{}")) {
            return 0;
        }
        return Long.parseLong(str);
    }

    // Входящие данные в формате 2022-04-06T16:27:00
    public static Date checkDate1c(String str) {

        if (str.equals("anyType{}")) {
            return null;
        }
        Date dateResult;
        try {
            dateResult = DateUtil.parseDate1C(str);
        } catch (ParseException e) {
            return null;
        }
        return dateResult;
    }

    public static void loggErr(String errMsg) {
        System.err.println(errMsg);
    }

    public static boolean checkBoolean(Object value) {
        if (value == null) {
            return false;
        }

        String valueStr = value.toString().toLowerCase(Locale.ROOT);

        if (valueStr.equals("true") || valueStr.equals("false")) {
            return true;
        }
        return false;
    }

}

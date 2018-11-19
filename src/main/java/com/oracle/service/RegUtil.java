package com.oracle.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegUtil {

    public static String matcher(String str, String reg) {
        Matcher matcher = Pattern.compile(reg).matcher(str);
        while (matcher.find()) {
            str = matcher.group();
            return str;
        }
        return null;
    }

    public static List<String> matchers(String str, String reg) {
        Matcher matcher = Pattern.compile(reg).matcher(str);
        List<String> list = new ArrayList<String>();
        while (matcher.find()) {
            str = matcher.group();
            list.add(str);
        }
        return list;
    }

}

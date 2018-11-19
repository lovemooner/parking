package com.oracle.pojo;

import love.moon.util.NumberUtil;
import love.moon.util.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

public class Constants {
    public static final Logger LOG = LoggerFactory.getLogger(Constants.class);

    public static  Long FETCH_SLEEP_TIME;




    public static String URL_GET_CAR = "https://apex.oracle.com/pls/apex/fbi/parking/cars";
    public static String URL_GET_CAR_COUNT = "https://apex.oracle.com/pls/apex/fbi/parking/isCarUpdate";
    public static String URL_GET_CURRENT_PARKING = "https://apex.oracle.com/pls/apex/fbi/parking/currentParking";
    public static String URL_POST_PARKING_INFO = "https://apex.oracle.com/pls/apex/fbi/parking/parkingInfo";
    public static String URL_PARING_QUERY = "https://cloud.keytop.cn/service/payment/confirm/card?";



    static {
        try {
            Properties properties = PropertiesUtil.load("parking.properties");
            FETCH_SLEEP_TIME= NumberUtil.longValue(properties.getProperty("fetch.sleep.time"));
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

}

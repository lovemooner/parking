package com.oracle.pojo;

import love.moon.util.NumberUtil;
import love.moon.util.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

public class Constants {
    public static final Logger LOG = LoggerFactory.getLogger(Constants.class);

    public static boolean DEBUG;

    public static int POOL_CORE_SIZE;
    public static int POOL_MAXIMUM_SIZE;
    public static long POOL_KEEP_ALIVE_TIME;

    public static long AFTER_WORK_SLEEP_TIME;
    public static long ON_WORK_SLEEP_TIME;
    public static long SLEEP_TIME_DEFAULT;


    public static String PROXY_HOST_NAME;
    public static int PROXY_HOST_PORT;


    public static final String URL_GET_CAR = "https://apex.oracle.com/pls/apex/fbi/parking/cars";
    public static final String URL_GET_CAR_COUNT = "https://apex.oracle.com/pls/apex/fbi/parking/isCarUpdate";
    public static final String URL_GET_CURRENT_PARKING = "https://apex.oracle.com/pls/apex/fbi/parking/currentParking";
    public static final String URL_PARKING_INFO = "https://apex.oracle.com/pls/apex/fbi/parking/parkingInfo";
    public static final String URL_PARING_QUERY = "https://cloud.keytop.cn/service/payment/confirm/card?";


    static {
        try {
            Properties properties = PropertiesUtil.load("parking.properties");

            DEBUG = Boolean.valueOf(properties.getProperty("DEBUG"));

            POOL_CORE_SIZE = NumberUtil.intValue(properties.getProperty("pool.core.size"));
            POOL_MAXIMUM_SIZE = NumberUtil.intValue(properties.getProperty("pool.maximum.size"));
            POOL_KEEP_ALIVE_TIME = NumberUtil.longValue(properties.getProperty("pool.keepAlive.time"));


            AFTER_WORK_SLEEP_TIME = NumberUtil.longValue(properties.getProperty("after.work.sleep.time"));
            ON_WORK_SLEEP_TIME = NumberUtil.longValue(properties.getProperty("on.work.sleep.time"));
            SLEEP_TIME_DEFAULT = NumberUtil.longValue(properties.getProperty("sleep.time.default"));


            PROXY_HOST_NAME = properties.getProperty("proxy.host.name");
            PROXY_HOST_PORT = NumberUtil.intValue(properties.getProperty("proxy.host.port"));
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

}

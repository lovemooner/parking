import love.moon.util.DateUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateTest {


    public static void main(String[] args) {
//        System.out.println(DateUtil.getChinaTime());
        long date1=DateUtil.getTimeMillisByHourOfDay(20);
        System.out.println(DateUtil.convertDateLongToString(date1,DateUtil.ALL));


    }
}

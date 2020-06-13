package data.lab.elasticsearch.util;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: casia.isi.elasticsearch.util
 * @Description: TODO(时间类型数据操作工具)
 * @date 2019/5/30 17:13
 */
public class DateUtil {

    /**
     * @param timeMillis:毫秒时间
     * @return
     * @Description: TODO(毫秒转为时间字符串)
     */
    public static String millToTimeStr(long timeMillis) {
        Date d = new Date(timeMillis);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(d);
    }

    /**
     * @param date:日期STRING格式:yyyy-MM-dd HH:mm:ss
     * @return
     * @Description: TODO(日期转为毫秒)
     */
    public static long dateToMillisecond(String date) {
        long millisecond = 0;
        try {
            if (date != null && !"".equals(date)) {
                millisecond = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date).getTime();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return millisecond;
    }

    public static String getCurrentIndexTime() {
        Date d = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(d);
    }

    public static String datePlus(String completeDate, long interval) {
        String reDate = null;
        long currentDateMillisecond = 0L;

        try {
            currentDateMillisecond = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).parse(completeDate).getTime();
            long reMillisecond = currentDateMillisecond + interval;
            reDate = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date(reMillisecond));
        } catch (ParseException var8) {
            var8.printStackTrace();
        }

        return reDate;
    }

    public static String dateSub(String completeDate, long interval) {
        String reDate = null;
        long currentDateMillisecond = 0L;

        try {
            currentDateMillisecond = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).parse(completeDate).getTime();
            long reMillisecond = currentDateMillisecond - interval;
            reDate = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date(reMillisecond));
        } catch (ParseException var8) {
            var8.printStackTrace();
        }

        return reDate;
    }

    public static String subHowMin(String lastTime, String nextTime) {
        Date lastTimeDate = new Date(dateToMillisecond(lastTime));
        Date nextTimeDate = new Date(dateToMillisecond(nextTime));
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // 获得两个时间的毫秒时间差异
        long diff = nextTimeDate.getTime() - lastTimeDate.getTime();
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        // long sec = diff % nd % nh % nm / ns;
        return day + "天" + hour + "小时" + min + "分钟";
    }

    public static int subMillHowToMin(String lastTime, String nextTime) {
        Date lastTimeDate = new Date(dateToMillisecond(lastTime));
        Date nextTimeDate = new Date(dateToMillisecond(nextTime));
        // 获得两个时间的毫秒时间差异
        long diff = nextTimeDate.getTime() - lastTimeDate.getTime();
        String min = (diff / (1000 * 60)) + "";
//        String second= (time%(1000*60)/1000)+"";
        if (min.length() < 2) {
            min = 0 + min;
        }
        return Integer.parseInt(min);
    }
}

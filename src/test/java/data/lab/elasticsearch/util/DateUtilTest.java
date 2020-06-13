package data.lab.elasticsearch.util;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: casia.isi.elasticsearch.util
 * @Description: TODO(Date Util)
 * @date 2020/3/12 17:00
 */
public class DateUtilTest {

    @Test
    public void subHowMin() {
        String sub = DateUtil.subHowMin("2020-03-12 10:02:01", "2020-03-12 16:52:01");
        String regEx = "[0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(sub);
        String str= m.replaceAll("").trim();
        System.out.println(sub);
    }

    @Test
    public void subMillHowMin() {
        int min = DateUtil.subMillHowToMin("2020-03-11 16:02:01", "2020-03-12 16:52:01");
        System.out.println(min);
    }
}


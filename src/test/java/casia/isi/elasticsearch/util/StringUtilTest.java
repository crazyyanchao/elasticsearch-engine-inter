package casia.isi.elasticsearch.util;

import org.junit.Test;

import java.util.Scanner;

import static org.junit.Assert.*;
/**
 * 　　　　　　　 ┏┓       ┏┓+ +
 * 　　　　　　　┏┛┻━━━━━━━┛┻┓ + +
 * 　　　　　　　┃　　　　　　 ┃
 * 　　　　　　　┃　　　━　　　┃ ++ + + +
 * 　　　　　　 █████━█████  ┃+
 * 　　　　　　　┃　　　　　　 ┃ +
 * 　　　　　　　┃　　　┻　　　┃
 * 　　　　　　　┃　　　　　　 ┃ + +
 * 　　　　　　　┗━━┓　　　 ┏━┛
 * ┃　　  ┃
 * 　　　　　　　　　┃　　  ┃ + + + +
 * 　　　　　　　　　┃　　　┃　Code is far away from     bug with the animal protecting
 * 　　　　　　　　　┃　　　┃ +
 * 　　　　　　　　　┃　　　┃
 * 　　　　　　　　　┃　　　┃　　+
 * 　　　　　　　　　┃　 　 ┗━━━┓ + +
 * 　　　　　　　　　┃ 　　　　　┣┓
 * 　　　　　　　　　┃ 　　　　　┏┛
 * 　　　　　　　　　┗┓┓┏━━━┳┓┏┛ + + + +
 * 　　　　　　　　　 ┃┫┫　 ┃┫┫
 * 　　　　　　　　　 ┗┻┛　 ┗┻┛+ + + +
 */

/**
 * @author YanchaoMa yanchaoma@foxmail.com
 * @PACKAGE_NAME: casia.isi.elasticsearch.util
 * @Description: TODO(Describe the role of this JAVA class)
 * @date 2019/12/4 16:04
 */
public class StringUtilTest {

    @Test
    public void utilTest() {
        System.out.println(StringUtil.escapeSolrQueryChars("sda.cn/sd/sad*"));
    }

    @Test
    public void utilTest01() {
        System.out.println(StringUtil.urlShort("http://twitter.com/hoganindc2015/"));
    }

    @Test
    public void countAbcCode() {
        System.out.println("abc count:" + StringUtil.countAbcCode("71BF07"));
    }

    @Test
    public void isAbcCode() {
        System.out.println(StringUtil.isAbcCode("d".charAt(0)));
    }

    @Test
    public void is() {
        System.out.println("字母:" + StringUtil.isAbcCode("z".charAt(0)));
        System.out.println("小写字母:" + StringUtil.isLower("z".charAt(0)));
        System.out.println("大写字母:" + StringUtil.isUpper("z".charAt(0)));
    }

    @Test
    public void lowerUpperCombination() {
        String[] keywords = StringUtil.lowerUpperCombination("c7c6dB2");
        System.out.println("KEYWORD LOWER UPPER:");
        for (String key : keywords) {
            System.out.print(key+"|");
        }
    }
}


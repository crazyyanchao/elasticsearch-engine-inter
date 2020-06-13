package data.lab.elasticsearch.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则处理
 *
 * @author
 */
public class RegexUtil {
    //正则表达式
    public static String zz_s1 = "([\u4E00-\u9FA5]|[\uFE30-\uFFA0])+";//抽取汉字和中文标点符号

    /**
     * 正则
     *
     * @param count      内容
     * @param expression 表达式
     * @return
     */
    public static String match(String count, String expression) {
        String result = null;
        if (count == null) {
            return result;
        }
        Pattern p = Pattern.compile(expression);
        Matcher m = p.matcher(count);
        //	boolean r = m.matches();
        if (m.find()) {
            result = m.group();
        }
        return result;
    }

}
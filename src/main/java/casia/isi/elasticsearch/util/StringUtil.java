package casia.isi.elasticsearch.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符处理类
 *
 * @author wzy
 */
public class StringUtil {

    public static String urlEscape(String input) {
        return input != null ? input.replaceAll("\\s", "%20") : null;
    }

    /**
     * solr特殊字符转义处理
     *
     * @param input
     * @return
     */
    public static String escapeSolrQueryChars(String input) {
        StringBuffer sb = new StringBuffer();
        //String regex = "[+\\-&|!(){}\\[\\]^\"~*?:(\\)]";
        //去掉*号，用于模糊匹配
        String regex = "[+\\-&|!(){}\\[\\]^\"~?:(\\)]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            matcher.appendReplacement(sb, "\\\\" + matcher.group());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 倒序排序
     *
     * @param map
     * @return
     */
    public static List<Map.Entry<String, Integer>> sort(Map<String, Integer> map) {
        List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                //倒敘
                if (o1.getValue() > o2.getValue())
                    return -1;
                else if (o1.getValue() < o2.getValue())
                    return 1;
                else
                    return 0;
            }
        });

        return list;
    }

    /**
     * @param
     * @return
     * @Description: TODO(截取数字)
     */
    public static String cutNumber(String content) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            return matcher.group(0);
        }
        return null;
    }

}

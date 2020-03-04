package casia.isi.elasticsearch.util;

import java.util.*;
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

    /**
     * 从URL中提取主体，如http://news.163.com/xxx/xxx.html --> news.163.com/xxx/xxx.html
     *
     * @param url 输入的URL
     * @return String 主机地址
     */
    public static String urlShort(String url) {
        if (url == null) {
            return null;
        }

        String short_url = url.replaceAll("^https?://", "");
        short_url = short_url.replace("www.", "");

        return short_url;
    }

    /**
     * 1、0代表小写字母，1代表大写字母，那么16种排列组合就是从0000~1111。所以，16种排列组合可以用从0至15的二进制数字表示
     * 2、递归加回溯
     *
     * @param
     * @return
     * @Description: TODO(字符串的所有大小写组合)
     */
    public static String[] lowerUpperCombination(String keyword) {
        String line = keyword;
        char array[] = line.toCharArray();
        //标识字符串的某个位置是字母还是非字母
        boolean flag[] = new boolean[array.length];
        int n = 1;  //字符串大小写组合的总数
        int count = 0;   //统计给定的字符串中包含的字母个数
        for (int i = 0; i < array.length; i++) {
            if (Character.isLetter(array[i])) {
                flag[i] = true; //是字母
                n *= 2; //每出现一个字母，组合的总数乘以2
                count++;
            } else {
                flag[i] = false; //非字母
            }
        }
        int index = new Double(Math.pow(2, count)).intValue();
        String[] keywords = new String[index];
        //打印出每一种字符串组合
        for (int i = 0; i < n; i++) {
            //将0~n-1转化为二进制数字
            String temp = Integer.toBinaryString(i);
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < count - temp.length(); j++) {
                //追加‘0’，将0变为0000的形式
                sb.append("0");
            }
            sb.append(temp);
            char[] tempArray = sb.toString().toCharArray();
            int k = 0;
            String key="";
            for (int j = 0; j < flag.length; j++) {
                char val = array[j]; //先取出该位置上的字符
                if (flag[j]) { //该位置上是字母
                    if ('0' == tempArray[k]) { //小写
//                        System.out.print(Character.toLowerCase(val));
                        key+=Character.toLowerCase(val);
                    } else { //大写
//                        System.out.print(Character.toUpperCase(val));
                        key+=Character.toUpperCase(val);
                    }
                    k++;
                } else { //该位置上非字母
//                    System.out.print(val);
                    key+=val;
                }
            }
            keywords[i]=key;
//            System.out.print("\n");
        }
        return keywords;
    }

    /**
     * @param
     * @return
     * @Description: TODO(判断是否是字母)
     */
    public static boolean isAbcCode(char charAt) {
        // A~z的ASCII码值
        return charAt >= 65 && charAt <= 122;
    }

    /**
     * @param
     * @return
     * @Description: TODO(判断是否是小写字母)
     */
    public static boolean isLower(char charAt) {
        // a~z的ASCII码值
        return charAt >= 97 && charAt <= 122;
    }

    /**
     * @param
     * @return
     * @Description: TODO(判断是否是大写字母)
     */
    public static boolean isUpper(char charAt) {
        // A~Z的ASCII码值
        return charAt >= 65 && charAt <= 90;
    }

    /**
     * @param
     * @return
     * @Description: TODO(FOR循环的方式统计字符串中每个字母出现的次数)
     */
    public static Map<String, Integer> countAbcCode(String keyword) {
        Map<String, Integer> map = new HashMap<>();
        int countNum = 0;
        // A~z的ASCII码值
        for (int i = 65; i < 123; i++) {
            int count = 0;
            for (int j = 0; j < keyword.length(); j++) {
                if (i == keyword.charAt(j)) {
                    count++;
                }
            }
            // 当count的个数为0时说明没有出现过该字母
            if (count != 0) {
                map.put(String.valueOf(i), count);
                countNum += count;
            }
        }
        map.put("count", countNum);
        return map;
    }
}


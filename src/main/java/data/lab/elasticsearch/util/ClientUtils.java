package data.lab.elasticsearch.util;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: casia.isi.elasticsearch.util
 * @Description: TODO(Tool)
 * @date 2019/5/31 9:44
 */
public class ClientUtils {

    /**
     * See: <a href="https://www.google.com/?gws_rd=ssl#q=lucene+query+parser+syntax">Lucene query parser syntax</a>
     * for more information on Escaping Special Characters
     *
     * @param
     * @return
     * @Description: TODO(NOTE : its broken to link to any lucene - queryparser.jar docs, not in classpath ! ! ! ! !)
     */
    public static String escapeQueryChars(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            // These characters are part of the query syntax and must be escaped
            if (c == '\\' || c == '+' || c == '-' || c == '!' || c == '(' || c == ')' || c == ':'
                    || c == '^' || c == '[' || c == ']' || c == '\"' || c == '{' || c == '}' || c == '~'
                    || c == '*' || c == '?' || c == '|' || c == '&' || c == ';' || c == '/'
                    || Character.isWhitespace(c)) {
                sb.append('\\');
            }
            sb.append(c);
        }
        return sb.toString();
    }

    /**
     * @param
     * @return
     * @Description: TODO(Returns the value encoded properly so it can be appended after a < pre > name = < / pre > local - param.)
     */
    public static String encodeLocalParamVal(String val) {
        int len = val.length();
        int i = 0;
        if (len > 0 && val.charAt(0) != '$') {
            for (; i < len; i++) {
                char ch = val.charAt(i);
                if (Character.isWhitespace(ch) || ch == '}') break;
            }
        }

        if (i >= len) return val;

        // We need to enclose in quotes... but now we need to escape
        StringBuilder sb = new StringBuilder(val.length() + 4);
        sb.append('\'');
        for (i = 0; i < len; i++) {
            char ch = val.charAt(i);
            if (ch == '\'') {
                sb.append('\\');
            }
            sb.append(ch);
        }
        sb.append('\'');
        return sb.toString();
    }

    /**
     * @param url:完整接口地址 http://ip:port,ip:port,ip:port/testnews_ref_event,testwechat_info_ref_event/testdata,monitor_data/_search
     * @return
     * @Description: TODO(提取相对接口地址)
     */
    public static String referenceUrl(String url) {
        String[] colons = url.split(":");
        String tempUrl = colons[colons.length-1];
        String temPort = tempUrl.split("/")[0];
        return tempUrl.replace(temPort, "");
    }

}


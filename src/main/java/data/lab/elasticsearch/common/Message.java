package data.lab.elasticsearch.common;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import com.alibaba.fastjson.JSONObject;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: casia.isi.elasticsearch.common
 * @Description: TODO(消息处理)
 * @date 2019/5/27 9:24
 */
public class Message {

    /**
     * @param
     * @return
     * @Description: TODO(创建索引时返回的消息)
     */
    public static boolean indexMessage(String queryResultStr) {
        JSONObject queryResult = JSONObject.parseObject(queryResultStr);
        if (queryResult.containsKey("errors")) {
            return queryResult.getBoolean("errors");
        }
        return true;
    }

}

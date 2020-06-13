package data.lab.elasticsearch.operation.index;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import com.alibaba.fastjson.JSONObject;
import data.lab.elasticsearch.operation.http.HttpRequest;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.elasticsearch.operation.index
 * @Description: TODO(支持中文类型测试)
 * @date 2020/6/13 12:14
 */
public class EsIndexCreateTest2 {

    private static final String SERVER_ADDRESSES = "http://10.20.0.157:9200/";

    @Test
    public void createCnMapping() {
        HttpRequest httpRequest = new HttpRequest();
        String indexName = "cn_text";
        String mapping = "{\n" +
                "  \"settings\": {\n" +
                "    \"number_of_shards\": 5,\n" +
                "    \"number_of_replicas\": 1\n" +
                "  },\n" +
                "  \"mappings\": {\n" +
                "    \"测试类型标签\": {\n" +
                "      \"dynamic\": \"false\",\n" +
                "      \"_source\": {\n" +
                "        \"enabled\": true\n" +
                "      },\n" +
                "      \"properties\": {\n" +
                "        \"md5\": {\n" +
                "          \"index\": \"not_analyzed\",\n" +
                "          \"type\": \"keyword\"\n" +
                "        },\n" +
                "        \"name\": {\n" +
                "          \"index\": \"not_analyzed\",\n" +
                "          \"type\": \"keyword\"\n" +
                "        },\n" +
                "        \"content\": {\n" +
                "          \"analyzer\": \"standard\",\n" +
                "          \"store\": true,\n" +
                "          \"type\": \"text\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}\n" +
                "\n";
        String result = httpRequest.httpPut(SERVER_ADDRESSES + indexName, mapping);
        System.out.println(result);
    }

    /**
     * @param
     * @return
     * @Description: TODO(转储数据到ES)
     */
    @Test
    public void createImport() {
        EsIndexCreat esIndexCreat = new EsIndexCreat(SERVER_ADDRESSES.replace("http://", "").replace("//", ""), "cn_text", "测试类型标签");

        List<JSONObject> messageDataArray = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            JSONObject object = new JSONObject();
            object.put("md5", "dasdw32wreasdweqgrtyr"+i);
            object.put("name", "shechao");
            object.put("content", "信息监测->查询巡查站点。筛选条件(站点名称:大)");
            messageDataArray.add(object);
        }

        // 传入做为唯一键的字段名称
        // 此接口具备覆盖更新的能力（批量覆盖更新可以使用此接口） -  EsIndexUpdate的部分更新不同
        boolean bool = esIndexCreat.index(messageDataArray, "md5");
        System.out.println(bool);
    }
}

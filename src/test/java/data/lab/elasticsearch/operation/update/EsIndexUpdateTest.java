package data.lab.elasticsearch.operation.update;

import data.lab.elasticsearch.common.FieldOccurs;
import data.lab.elasticsearch.common.SortOrder;
import data.lab.elasticsearch.operation.index.EsIndexCreat;
import data.lab.elasticsearch.operation.modify.EsModify;
import data.lab.elasticsearch.operation.search.*;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: casia.isi.elasticsearch.operation.update
 * @Description: TODO(索引更新测试)
 * @date 2019/5/22 11:26
 */
public class EsIndexUpdateTest {

    private static EsIndexUpdate esSmallIndexUpdate;

    private String ipPort = "10.97.167.206:9210";

    private static HashMap<String, String> itMap = new HashMap<>();

    // 预警：
    // http://192.168.12.109:9210/testnews_ref_monitor/monitor_data/_search

    @Before
    public void setUp() throws Exception {
        itMap.put("c", "论坛");   // forum_threads
        itMap.put("d", "微博");   // mblog_info
        itMap.put("a", "新闻");   // news
        itMap.put("h", "微信");   // wechat_message_xigua
        itMap.put("i", "移动app");    // appdata
        itMap.put("e", "视频");   // video_brief
        itMap.put("b", "博客");   // blog
        itMap.put("j", "电子报纸"); // newspaper_info
    }

    @Before
    public void searchObject() {
        PropertyConfigurator.configureAndWatch("config/log4j.properties");

        // 更新时只能一个索引更新

//        ,blog_small,forum_threads_small,mblog_info_small,video_brief_small," +
//        "wechat_message_xigua_small,appdata_small,newspaper_info_small
//        String smallIndexName = "news_small";
//        esSmallIndexUpdate = new EsIndexUpdate(ipPort, smallIndexName, "monitor_caiji_small");

    }

    @Test
    public void UpdateParameterById() {

        EsIndexUpdate esIndexUpdate = new EsIndexUpdate(ipPort, "aircraft_info", "graph");

        String _id = "15470498081";

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", _id);
        map.put("mode_s", "40058a");
//        esIndexUpdate.updateParameterById(map,_id);
        esIndexUpdate.upsertParameterById(map, _id, map);
    }

    @Test
    public void update() {
        //新增字段
//		EsIndexCreat indexer = new EsIndexCreat("106.75.177.129",61233,"testdata_extract_result_v-201808","analysis_data");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "keyword");
//		map.put("index", "not_analyzed");
//		boolean a= indexer.insertField("con_md5", map);
//		System.out.println(a);

        EsIndexSearch searchClient = new EsIndexSearch("106.75.177.129:61233", "all_data_q-201808", "analysis_data");
        EsIndexUpdate es = new EsIndexUpdate("106.75.177.129:61233", "all_data_q-201808", "analysis_data");

        String a = "0";
        while (true) {
            searchClient.reset();
            searchClient.addRangeTerms("pid", a + "", null);
            searchClient.addSortField("pid", SortOrder.ASC);
//			searchClient.addPhraseQuery("eid","3", FieldOccurs.MUST);
            searchClient.setStart(0);
            searchClient.setRow(1000);
            searchClient.execute(new String[]{"_id", "pid", "content", "_index", "_type"});
            List<String[]> list = searchClient.getResults();
            System.out.println(searchClient.getTotal());
//			if(list.size()==0 ||list.size()==1 ){break;}
            for (String[] strings : list) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("con_md5", DigestUtils.md5Hex(strings[2]));
                es.updateParameterById(map, strings[0]);
                a = strings[1];
//				System.out.println(a);
            }
        }


//		EsIndexUpdate es = new EsIndexUpdate("106.75.137.175:61233", "testdata_extract_result_v", "analysis_data");
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("is_spam", "1");
////		map.put("province", "广东");
////		map.put("pubtime", "2018-06-25 11:23:12");
//		map.put("viewport", new String[]{"明天举着横幅到杭州市委门口，抗议"});
////		map.put("lal", "22.548766,114.043955");
//		boolean boo = es.UpdateParameterById(map, "103825");
//		System.out.println(boo);

    }

    @Test
    public void update2() {
        // 新增字段
        // 修改字段 - lon字段从float修改为keyword   - ！！！！ 无法覆盖只能先删除再插入
        EsIndexCreat indexer = new EsIndexCreat("localhost:9210", "monitor_site_testdata", "monitor_data");
        Map<String, String> map = new HashMap<>();
        map.put("type", "keyword");
        map.put("index", "not_analyzed");
        boolean a = indexer.insertField("data_source", map);
        System.out.println(a);
    }

    @Test
    public void _update_by_query_0() {

        /**
         * 下面更新测试为将索引ship_info中匹配到的数据mmsi字段修改为ms-kk
         * 精确匹配
         *
         * **/

        String ipPorts = "192.168.1.12:9200";
        String indexName = "ship_info";
        String indexType = "graph";

        // 构造索引更新对象
        EsIndexUpdate esIndexUpdate = new EsIndexUpdate(ipPorts, indexName, indexType);
        esIndexUpdate.setDebug(true);

        // 批量修改_update_by_query
        String[] array = new String[]{"aa-ff"};
        esIndexUpdate.addPrimitiveTermFilter("mmsi", array, FieldOccurs.MUST);

        // 以后台任务的形式执行更新【数据量较大时使用此配置】
        // isWaitResponse:是否等待响应
        esIndexUpdate.setWaitForCompletion(false);

        // 匹配到的数据中的字段值全部修改为某一值
        JSONObject result = esIndexUpdate.execute("mmsi", "aa-ff-2");
        System.out.println(result);
        esIndexUpdate.reset();

        // 执行刷新
        EsModify.executeAutoRefresh(ipPorts, indexName);
    }

    @Test
    public void _update_by_query_1() {

        /**
         * 下面更新测试为将索引ship_info中匹配到的数据mmsi字段修改为ms-kk
         * 精确匹配
         *
         * **/

        String ipPorts = "192.168.1.12:9200";
        String indexName = "ship_info";
        String indexType = "graph";

        // 构造索引更新对象
        EsIndexUpdate esIndexUpdate = new EsIndexUpdate(ipPorts, indexName, indexType);
        esIndexUpdate.setDebug(true);

        // 批量修改_update_by_query
        String[] array = new String[]{"mmsi-test0"};
        esIndexUpdate.addPrimitiveTermFilter("mmsi", array, FieldOccurs.MUST);

        // 匹配到的数据中的字段值全部修改为某一值
        JSONObject result = esIndexUpdate.execute("mmsi", "ms-kk", "field2", "ms-kk");
        System.out.println(result);
        esIndexUpdate.reset();

        // 执行刷新
        EsModify.executeAutoRefresh(ipPorts, indexName);
    }

    @Test
    public void _update_by_query_2() {

        /**
         * 下面更新测试为将索引ship_info中匹配到的数据mmsi字段修改为ms-kk
         * 模糊匹配
         *
         * **/

        String ipPorts = "192.168.1.12:9200";
        String indexName = "ship_info";
        String indexType = "graph";

        // 构造索引更新对象
        EsIndexUpdate esIndexUpdate = new EsIndexUpdate(ipPorts, indexName, indexType);
        esIndexUpdate.setDebug(true);

        // 批量修改_update_by_query
        /**
         * URL的模糊匹配使用下面的方式查询
         * wildcard通配符查询ES-KEYWORD
         * URL的通配符查询的条件添加
         * {
         *   "wildcard": {
         *     "url_short": "twitter.com/haku2013/status/*"
         *   }
         * }
         *
         * **/
        JSONObject wildcardObj = new JSONObject();
        wildcardObj.put("mmsi", "mmsi*");
        JSONObject wildcard = new JSONObject();
        wildcard.put("wildcard", wildcardObj);
        esIndexUpdate.queryFilterMustJarr.add(wildcard);

        // 匹配到的数据中的字段值全部修改为某一值
        JSONObject result = esIndexUpdate.execute("mmsi", "ms-kk");
        System.out.println(result);
        esIndexUpdate.reset();

        // 执行刷新
        EsModify.executeAutoRefresh(ipPorts, indexName);
    }

}



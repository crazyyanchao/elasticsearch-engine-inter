package casia.isi.elasticsearch.operation.update;

import casia.isi.elasticsearch.common.SortOrder;
import casia.isi.elasticsearch.operation.delete.EsIndexDelete;
import casia.isi.elasticsearch.operation.search.EsIndexSearch;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * @PACKAGE_NAME: casia.isi.elasticsearch.operation.update
 * @Description: TODO(索引更新测试)
 * @author YanchaoMa yanchaoma@foxmail.com
 * @date 2019/5/22 11:26
 *
 *
 */
public class EsIndexUpdateTest {

    private static EsIndexUpdate esSmallIndexUpdate;

    private String ipPort = "localhost:9200";

    private static HashMap<String, String> itMap = new HashMap<>();

    // 预警：
    // http://192.168.12.109:9210/event_news_ref_monitor/monitor_data/_search

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
        String smallIndexName = "news_small";
        esSmallIndexUpdate = new EsIndexUpdate(ipPort, smallIndexName, "monitor_caiji_small");

    }

    @Test
    public void UpdateParameterById(){

        EsIndexUpdate esIndexUpdate = new EsIndexUpdate(ipPort, "aircraft_info", "graph");

        String _id = "124";

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id",_id);
        map.put("test","榆林治沙: 一茬接着一茬干誓将沙海变绿洲-UPDATE");
//        esIndexUpdate.updateParameterById(map,_id);
        esIndexUpdate.upsertParameterById(map,_id,map);
    }

    @Test
    public void update(){
        //新增字段
//		EsIndexCreat indexer = new EsIndexCreat("106.75.177.129",61233,"event_data_extract_result_v-201808","analysis_data");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "keyword");
//		map.put("index", "not_analyzed");
//		boolean a= indexer.insertField("con_md5", map);
//		System.out.println(a);

        EsIndexSearch searchClient = new EsIndexSearch("106.75.177.129:61233","all_data_q-201808","analysis_data");
        EsIndexUpdate es = new EsIndexUpdate("106.75.177.129:61233", "all_data_q-201808", "analysis_data");

        String a = "0";
        while (true) {
            searchClient.reset();
            searchClient.addRangeTerms("pid", a+"", null );
            searchClient.addSortField("pid", SortOrder.ASC);
//			searchClient.addPhraseQuery("eid","3", FieldOccurs.MUST);
            searchClient.setStart(0);
            searchClient.setRow(1000);
            searchClient.execute(new String[]{"_id","pid","content","_index","_type"});
            List<String[]> list= searchClient.getResults();
            System.out.println(searchClient.getTotal());
//			if(list.size()==0 ||list.size()==1 ){break;}
            for (String[] strings : list) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("con_md5", DigestUtils.md5Hex(strings[2]) );
                es.updateParameterById(map,strings[0]);
                a = strings[1];
//				System.out.println(a);
            }
        }


//		EsIndexUpdate es = new EsIndexUpdate("106.75.137.175:61233", "event_data_extract_result_v", "analysis_data");
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("is_spam", "1");
////		map.put("province", "广东");
////		map.put("pubtime", "2018-06-25 11:23:12");
//		map.put("viewport", new String[]{"明天举着横幅到杭州市委门口，抗议"});
////		map.put("lal", "22.548766,114.043955");
//		boolean boo = es.UpdateParameterById(map, "103825");
//		System.out.println(boo);

    }

}



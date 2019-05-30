package casia.isi.elasticsearch.operation.search;

import casia.isi.elasticsearch.common.FieldOccurs;
import casia.isi.elasticsearch.common.SortOrder;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;

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
 * @author YanchaoMa yanchaoma@foxmail.com
 * @PACKAGE_NAME: casia.isi.elasticsearch.operation.search
 * @Description: TODO(索引检索聚合接口测试)
 * @date 2019/5/23 17:32
 */
public class EsIndexSearchImpTest {

    private static EsIndexSearch esSmallIndexSearch;

    private static EsIndexSearch esAllIndexSearch;


    @Before
    public void searchObject() {
        PropertyConfigurator.configureAndWatch("config/log4j.properties");

        String ipPort="192.168.12.109:9210";

        String smallIndexName = "news_small,blog_small,forum_threads_small,mblog_info_small,video_brief_small," +
                "wechat_message_xigua_small,appdata_small";
        esSmallIndexSearch = new EsIndexSearch(ipPort, smallIndexName, "monitor_caiji_small");

        String esAllIndexSearch = "news_all,blog_all,forum_threads_all,mblog_info_all,video_brief_all," +
                "wechat_message_xigua_all,appdata_all";
        esSmallIndexSearch = new EsIndexSearch(ipPort, esAllIndexSearch, "monitor_caiji_all");
    }

    @Test
    public void addPrimitiveTermFilter() {
        PropertyConfigurator.configureAndWatch("config/log4j.properties");
        esSmallIndexSearch.addRangeTerms("crawler_time", "2019-03-01 09:56:04", "2019-05-23 09:56:04");

        String[] ids = new String[]{"237501394733502460", "237501394733502461"};

        esSmallIndexSearch.addPrimitiveTermFilter("id", "237501394733502460", FieldOccurs.MUST);
        String[] fields = {"content", "crawler_time"};
        esSmallIndexSearch.execute(fields);
        System.out.println(esSmallIndexSearch.outputQueryJson());
        List<String[]> resultList = esSmallIndexSearch.getResults();
        //输出测试
        int i = 0;
        for (String[] infos : resultList) {
            System.out.print(i++ + ":");
            for (String info : infos)
                System.out.print(info + "\t");
            System.out.println("");
        }
    }

    @Test
    public void executeQuery() {
        String query = "{\"query\":{\"bool\":{\"must\":[{\"match_all\":{}}],\"must_not\":[],\"should\":[]}},\"from\":0,\"size\":10,\"sort\":[],\"aggs\":{}}";
        esSmallIndexSearch.execute(query);
        System.out.println(esSmallIndexSearch.outputResult());
    }

    @Test
    public void addPrimitiveTermQuery() {
        PropertyConfigurator.configureAndWatch("config/log4j.properties");

        esSmallIndexSearch.addRangeTerms("crawler_time", "2019-03-01 09:56:04", "2019-05-23 09:56:04");

        String[] ids = new String[]{"237501393831731200", "237501396138594300"};

        esSmallIndexSearch.addPrimitiveTermQuery("id", ids, FieldOccurs.MUST);

        String[] fields = {"content", "crawler_time"};
        esSmallIndexSearch.execute(fields);
        System.out.println(esSmallIndexSearch.outputQueryJson());

        List<String[]> resultList = esSmallIndexSearch.getResults();
        esSmallIndexSearch.outputResult(resultList);
    }

    @Test
    public void facetCountQuerysOrderByCount() {
//        esSmallIndexSearch.setStart(0);//分页
//        esSmallIndexSearch.setRow(400);//分页
//        List<String[]> resultList =   esSmallIndexSearch.facetDate("pubtime","yyyyMMdd","1d");
//        esSmallIndexSearch.outputResult(resultList);

        Map<String, Long> map = esSmallIndexSearch.facetCountQuerysOrderByCount(new String[]{"site", "gid"});
        System.out.println(map);
    }

    @Test
    public void addArrayTypeTermsQuery() {
        esSmallIndexSearch.addRangeTerms("pubtime", "2019-05-26 09:56:04", "2019-05-28 09:56:04");

//        List<String[]> result =  esSmallIndexSearch.facetDate("pubtimeAll","yyyy-MM-dd","1d");
//        List<String[]> result =  esSmallIndexSearch.facetDate("pubtimeAll","HH:mm:ss","10h");
//        esSmallIndexSearch.addPrimitiveTermFilter("area_list", new String[]{"长春"}, FieldOccurs.MUST);
//        esSmallIndexSearch.addPhraseQuery("area_list", "长春", FieldOccurs.MUST);
//        esSmallIndexSearch.addPrimitiveTermQuery("area_list", new String[]{"长春","北京"}, FieldOccurs.MUST);

        esSmallIndexSearch.execute(new String[]{"area_list"});
        List<String[]> result = esSmallIndexSearch.getResults();
        esSmallIndexSearch.outputResult(result);
    }

    @Test
    public void facetCountArrayTypeTermsQuery() {
        esSmallIndexSearch.addRangeTerms("pubtimeAll", "2019-04-18 09:56:04", "2019-05-28 09:56:04");

//        List<String[]> result =esSmallIndexSearch.facetCountQueryOrderByCount("area_list",10, SortOrder.DESC);
        Map<String, Long> result = esSmallIndexSearch.facetCountQueryOrderByCountToMap("domain", 0, SortOrder.DESC);
        esSmallIndexSearch.outputResult(result);

    }

    @Test
    public void addConditionQuery() {
//        String[] array = new String[]{"长春市","白城市","四平市","通化市"};
//        esSmallIndexSearch.addPhraseQuery(new String[]{"area_list"},array,FieldOccurs.MUST);

//        esSmallIndexSearch.addPrimitiveTermQuery("area_list","吉林",FieldOccurs.MUST);
        esSmallIndexSearch.addQueryCondition("+(area_list:( 长春 ) or ( 吉林 ) or ( 四平 ) or ( 辽源 ) or ( 通化 ) or ( 白山 ) or ( 松原 ) or ( 白城 ) or ( 延边))");
//        esSmallIndexSearch.addArrayTypeTermsQuery("area_list", new String[]{"长春市", "北京市"}, FieldOccurs.MUST);
        esSmallIndexSearch.debug = true;
//        esSmallIndexSearch.addArrayTypeTermsQuery("area_list", new String[]{"长春"}, FieldOccurs.MUST);
        Map<String, Long> results = esSmallIndexSearch.facetCountQueryOrderByCountToMap("area_list", 0, casia.isi.elasticsearch.common.SortOrder.DESC);
        esSmallIndexSearch.outputResult(results);
    }

    @Test
    public void addConditionQueryTest(){
        esSmallIndexSearch.addRangeTerms("pubtimeAll", "2019-05-21 18:39:54", "2019-05-28 18:39:54");

        esSmallIndexSearch.addArrayTypeTermsQuery("area_list", new String[]{"吉林"}, FieldOccurs.MUST);
        esSmallIndexSearch.addQueryCondition("+((title:\"吉林\") OR (content:\"吉林\"))");

        esSmallIndexSearch.execute(new String[]{"area_list"});
        List<String[]> result = esSmallIndexSearch.getResults();
        esSmallIndexSearch.outputResult(result);
    }


}




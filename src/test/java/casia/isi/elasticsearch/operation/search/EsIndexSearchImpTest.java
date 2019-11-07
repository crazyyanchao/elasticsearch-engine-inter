package casia.isi.elasticsearch.operation.search;

import casia.isi.elasticsearch.common.*;
import casia.isi.elasticsearch.common.condition.Must;
import casia.isi.elasticsearch.common.condition.MustNot;
import casia.isi.elasticsearch.common.condition.Should;
import casia.isi.elasticsearch.model.BoundBox;
import casia.isi.elasticsearch.model.BoundPoint;
import casia.isi.elasticsearch.model.Circle;
import casia.isi.elasticsearch.operation.search.analyzer.AggsAnalyzer;
import casia.isi.elasticsearch.util.DateUtil;
import casia.isi.elasticsearch.util.FileUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.*;

/**
 * @author YanchaoMa yanchaoma@foxmail.com
 * @PACKAGE_NAME: casia.isi.elasticsearch.operation.search
 * @Description: TODO(索引检索接口测试)
 * @date 2019/5/23 17:32
 */
public class EsIndexSearchImpTest {

    private EsIndexSearch esSmallIndexSearch;

    private EsIndexSearch esAllIndexSearch;

    private EsIndexSearch esWarningIndexSearch;

    private EsIndexSearch aircraftSearch;
    private EsIndexSearch statelliteSearch;

//    private String ipPort = "" +
//            "192.168.12.107:9210,192.168.12.107:9211,192.168.12.114:9210," +
//            "192.168.12.109:9211,192.168.12.112:9211,192.168.12.109:9210," +
//            "192.168.12.114:9211,192.168.12.114:9210,192.168.12.110:9210," +
//            "192.168.12.111:9210,192.168.122.111:9219";

    private String ipPort = "39.97.167.206:9210,39.97.243.92:9210,182.92.217.237:9210," +
            "39.97.243.129:9210,39.97.173.122:9210,39.97.242.194:9210";

//    private String ipPort = "localhost:9200";

    private static HashMap<String, String> itMap = new HashMap<>();

    // 预警：
    // http://192.168.12.109:9210/event_news_ref_monitor,event_blog_ref_monitor,event_threads_ref_monitor,
    // event_mblog_ref_monitor,event_video_ref_monitor,event_weichat_ref_monitor,event_appdata_ref_monitor/monitor_data/_search

    @Before
    public void searchObject() {
        PropertyConfigurator.configureAndWatch("config/log4j.properties");

        itMap.put("c", "论坛");   // forum_threads
        itMap.put("d", "微博");   // mblog_info
        itMap.put("a", "新闻");   // news
        itMap.put("h", "微信");   // wechat_message_xigua
        itMap.put("i", "移动app");    // appdata
        itMap.put("e", "视频");   // video_brief
        itMap.put("b", "博客");   // blog
        itMap.put("j", "电子报纸"); // newspaper_info

        String smallIndexName = "news_small,blog_small,forum_threads_small,mblog_info_small,video_info_small," +
                "wechat_info_small,appdata_small,newspaper_small";
        esSmallIndexSearch = new EsIndexSearch(ipPort, smallIndexName, "monitor_caiji_small");

        String allIndexSearch = "news_all,blog_all,forum_threads_all,mblog_info_all,video_info_all," +
                "wechat_info_all,appdata_all,newspaper_all";
        esAllIndexSearch = new EsIndexSearch(ipPort, allIndexSearch, "monitor_caiji_all");

        String waring = "event_news_ref_monitor,event_blog_ref_monitor,event_threads_ref_monitor,event_mblog_ref_monitor," +
                "event_video_ref_monitor,event_weichat_ref_monitor,event_appdata_ref_monitor";
        esWarningIndexSearch = new EsIndexSearch(ipPort, waring, "monitor_data");

        aircraftSearch = new EsIndexSearch(ipPort, "aircraft_info_latest_status,aircraft_info", "graph");

    }

    @After
    public void tearDown() throws Exception {
        esSmallIndexSearch.reset();
        esAllIndexSearch.reset();
        esWarningIndexSearch.reset();
        aircraftSearch.reset();
    }

    @Test
    public void addPrimitiveTermFilter() {
        PropertyConfigurator.configureAndWatch("config/log4j.properties");
        esSmallIndexSearch.addRangeTerms("insert_time", "2019-03-01 09:56:04", "2019-07-23 09:56:04");

        String[] ids = new String[]{"237501394733502460", "237501394733502461"};

        /**
         * 此方法用于对一些不可分词的数据进行检索，例如int,long型数据的genus，alarm等字段 <br>
         * 本方法也可以对分词的数据进行检索，但传入的内容不能有任何的标点符号以及空格，否则检索结果会出现异常<br>
         * 该方法用于int,long,string等类型的匹配，对于string类型，支持通配符(*)<br>
         *
         * @param field  字段
         * @param term   字段对应的值，只能转入一个，且不能有有空格.如果传入的值为负值，请用双引号包起来，例如：<br>
         *               client.addPrimitveTermQuery("eid", "\"-1\"",FieldOccurs.MUST);
         * @param occurs 是否必须作为过滤条件
         */
        esSmallIndexSearch.addPrimitiveTermFilter("id", "174324364", FieldOccurs.MUST);
        String[] fields = {"content", "insert_time"};
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

        esSmallIndexSearch.addRangeTerms("insert_time", "2019-03-01 09:56:04", "2019-07-23 09:56:04");

        String[] ids = new String[]{"237501393831731200", "237501396138594300"};

        /**
         * 与 {@link #addPrimitiveTermQuery(String, String, FieldOccurs)} 方法类似<br>
         * 字段对应的值可以输入多个，多个值之间为或的关系，满足其中一个值就会返回记录<br>
         * 该方法用于int,long,string等类型的匹配，对于string类型，支持通配符(*)<br>
         *
         * @param field  检索字段
         * @param terms  字段对应的多值，值之间是或的关系
         * @param occurs 是否必须作为过滤条件
         */
        esSmallIndexSearch.addPrimitiveTermQuery("id", ids, FieldOccurs.MUST);

        String[] fields = {"content", "insert_time"};
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

        /**
         * 多字段一次聚合
         * 此方法可以针对索引中多项数据进行统计（类似于数据库的count(*) 与 group by结合）并返回多项统计结果<br>
         * 该方法返回的列表中按照各字段的聚合数<br>
         * 统计的字段包括uid、gid、eid、ip等。 假设针对uid和gid进行统计，<br>
         * 会针对索引中每个不同uid和gid进行统计<br>
         * 返回Map<String,long> uid多少个，gid多少个<br>
         * <p>
         * ElasticSearch 利用Buketing 中的 terms aggregation 方式 默认分别返回100000,以便增加精确度。
         *
         * @param field 统计的字段
         * @return 注意 ： 执行 getTotal()方法               为条件过滤后未执行聚合分组的文档总量；
         */
        Map<String, Long> map = esSmallIndexSearch.facetCountQuerysOrderByCount(new String[]{"site", "gid"});
        System.out.println(map);
    }

    @Test
    public void addArrayTypeTermsQuery() {
        esSmallIndexSearch.addRangeTerms("pubtime", "2019-05-26 09:56:04", "2019-06-20 09:56:04");

//        List<String[]> result =  esSmallIndexSearch.facetDate("pubtimeAll","yyyy-MM-dd","1d");
//        List<String[]> result =  esSmallIndexSearch.facetDate("pubtimeAll","HH:mm:ss","10h");

        /**
         * 此方法用于对一些不可分词的数据进行检索，例如int,long型数据的genus，alarm等字段 <br>
         * 本方法也可以对分词的数据进行检索，但传入的内容不能有任何的标点符号以及空格，否则检索结果会出现异常<br>
         * 该方法用于int,long,string等类型的匹配，对于string类型，支持通配符(*)<br>
         *
         * @param field  字段
         * @param terms  字段对应的值，只能转入一个，且不能有有空格.如果传入的值为负值，请用双引号包起来，例如：<br>
         *               client.addPrimitveTermQuery("eid", "\"-1\"",FieldOccurs.MUST); 段对应的多值，值之间是或的关系
         * @param occurs 是否必须作为过滤条件
         */
//        esSmallIndexSearch.addPrimitiveTermFilter("area_list", new String[]{"长春"}, FieldOccurs.MUST);
        esSmallIndexSearch.addPhraseQuery("area_list", "长春 北京", FieldOccurs.MUST);
//        esSmallIndexSearch.addPrimitiveTermQuery("area_list", new String[]{"长春","北京"}, FieldOccurs.MUST);

        esSmallIndexSearch.execute(new String[]{"area_list"});
        List<String[]> result = esSmallIndexSearch.getResults();
        esSmallIndexSearch.outputResult(result);
    }

    @Test
    public void facetCountArrayTypeTermsQuery() {
        esSmallIndexSearch.addRangeTerms("pubtime", "2019-06-04 00:00:00", "2019-06-04 14:45:00");

        /**
         * 单字段一次聚合
         * 此方法可以针对索引中某项数据进行统计（类似于数据库的count(*) 与 group by结合）并返回统计结果<br>
         * 该方法返回的列表中按照统计数由大到小排序<br>
         * 统计的字段包括pubdate、pubtime、eid、ip、user_id、group_id 等。 假设针对user_id进行统计，<br>
         * 会针对索引中每个不同user_id进行统计<br>
         * ，分别得到匹配的文档数，按照每个website_id统计得到的文档数排序，返回前topN个website_id 和对应的文档数<br>
         * <p>
         * ElasticSearch 利用Buketing 中的 terms aggregation 方式 默认分别返回100000,以便增加精确度。
         *
         * @param field 统计的字段
         * @param topN  要求返回的结果数 ,topN 等于 0 时，将返回所有的统计结果 。topN 小于0时，不返回结果。
         * @return 前topN个结果的list ， 每一项为一个数组， 第一项为统计字段，第二项为字段等于该值的文档数 。
         * 注意 ： 执行 getTotal()方法               为条件过滤后未执行聚合分组的文档总量；
         * 执行 getCountTotal()方法               为条件过滤后执行聚合总量；
         */
//        List<String[]> result =esSmallIndexSearch.facetCountQueryOrderByCount("area_list",10, SortOrder.DESC);

        /**
         * @param field：统计的字段
         * @param topN：要求返回的结果数 ,topN 等于 0 时，将返回所有的统计结果 。topN 小于0时，不返回结果。
         * @param sort：可选的排序方式
         * @return
         * @Description: TODO(支持数组字段内的聚合)
         */
        Map<String, Long> result = esSmallIndexSearch.facetCountQueryOrderByCountToMap("it", 0, SortOrder.DESC);
        esSmallIndexSearch.outputResult(result);

    }

    @Test
    public void addConditionQuery() {
        esSmallIndexSearch.addRangeTerms("pubtime", "2019-06-02 18:39:54", "2019-06-06 18:39:54");
//        String[] array = new String[]{"长春市","白城市","四平市","通化市"};
//        esSmallIndexSearch.addPhraseQuery(new String[]{"area_list"},array,FieldOccurs.MUST);

//        esSmallIndexSearch.addPrimitiveTermQuery("area_list","吉林",FieldOccurs.MUST);
        esSmallIndexSearch.addQueryCondition("+(area_list:( 长春 ) or ( 吉林 ) or ( 四平 ) or ( 辽源 ) or ( 通化 ) or ( 白山 ) or ( 松原 ) or ( 白城 ) or ( 延边))");
//        esSmallIndexSearch.addArrayTypeTermsQuery("area_list", new String[]{"长春市", "北京市"}, FieldOccurs.MUST);
        esSmallIndexSearch.debug = true;
//        esSmallIndexSearch.addArrayTypeTermsQuery("area_list", new String[]{"长春"}, FieldOccurs.MUST);
        /**
         * 单字段一次聚合
         * 此方法可以针对索引中某项数据进行统计（类似于数据库的count(*) 与 group by结合）并返回统计结果<br>
         * 该方法返回的列表中按照统计数由大到小排序<br>
         * 统计的字段包括pubdate、pubtime、eid、ip、user_id、group_id 等。 假设针对user_id进行统计，<br>
         * 会针对索引中每个不同user_id进行统计<br>
         * ，分别得到匹配的文档数，按照每个website_id统计得到的文档数排序，返回前topN个website_id 和对应的文档数<br>
         * <p>
         * ElasticSearch 利用Buketing 中的 terms aggregation 方式 默认分别返回100000,以便增加精确度。
         *
         * @param field 统计的字段
         * @param topN  要求返回的结果数 ,topN 等于 0 时，将返回所有的统计结果 。topN 小于0时，不返回结果。
         * @return 前topN个结果的list ， 每一项为一个数组， 第一项为统计字段，第二项为字段等于该值的文档数 。
         * 注意 ： 执行 getTotal()方法               为条件过滤后未执行聚合分组的文档总量；
         * 执行 getCountTotal()方法               为条件过滤后执行聚合总量；
         */
        Map<String, Long> results = esSmallIndexSearch.facetCountQueryOrderByCountToMap("area_list", 0, casia.isi.elasticsearch.common.SortOrder.DESC);
        esSmallIndexSearch.outputResult(results);
    }

    @Test
    public void addConditionQueryTest() {
//        esSmallIndexSearch.addRangeTerms("pubtime", "2019-05-21 18:39:54", "2019-05-28 18:39:54");

        /**
         * 此方法用于对一些不可分词的数据进行检索，例如int,long型数据的genus，alarm等字段 <br>
         * 本方法也可以对分词的数据进行检索，但传入的内容不能有任何的标点符号以及空格，否则检索结果会出现异常<br>
         * 该方法用于int,long,string等类型的匹配，对于string类型，支持通配符(*)<br>
         *
         * @param field  字段
         * @param terms  字段对应的值，只能转入一个，且不能有有空格.如果传入的值为负值，请用双引号包起来，例如：<br>
         *               client.addPrimitveTermQuery("eid", "\"-1\"",FieldOccurs.MUST); 段对应的多值，值之间是或的关系
         * @param combine:多值之间的查询关系，AND OR
         * @param occurs 是否必须作为过滤条件
         */

        for (int i = 0; i < 100; i++) {
            esSmallIndexSearch = new EsIndexSearch(ipPort, "blog_all", "monitor_caiji_small");

            String allIndexSearch = "news_all,blog_all,forum_threads_all,mblog_info_all,video_info_all," +
                    "wechat_info_all,appdata_all,newspaper_all";
            esAllIndexSearch = new EsIndexSearch(ipPort, allIndexSearch, "monitor_caiji_all");

            String waring = "event_news_ref_monitor,event_blog_ref_monitor,event_threads_ref_monitor,event_mblog_ref_monitor," +
                    "event_video_ref_monitor,event_weichat_ref_monitor,event_appdata_ref_monitor";
            esWarningIndexSearch = new EsIndexSearch(ipPort, waring, "monitor_data");
        }

//        esSmallIndexSearch.addPrimitiveTermFilter("area_list", new String[]{"吉林", "北京"}, KeywordsCombine.AND, FieldOccurs.MUST);

//         查询需要同时包含与或关系，需要单独拼接lucene查询
        esSmallIndexSearch.addQueryCondition("(+(area_list:\"吉林\") +(area_list:\"北京\") -(area_list:\"黑龙江\"))");

        esSmallIndexSearch.execute(new String[]{"area_list"});
        List<String[]> result = esSmallIndexSearch.getResults();
        esSmallIndexSearch.outputResult(result);
        esSmallIndexSearch.reset();
    }

    @Test
    public void addSortField() {
        esSmallIndexSearch.addRangeTerms("pubtime", "2019-05-21 18:39:54", "2019-05-28 18:39:54");

        /**
         * 此方法用于对一些不可分词的数据进行检索，例如int,long型数据的genus，alarm等字段 <br>
         * 本方法也可以对分词的数据进行检索，但传入的内容不能有任何的标点符号以及空格，否则检索结果会出现异常<br>
         * 该方法用于int,long,string等类型的匹配，对于string类型，支持通配符(*)<br>
         *
         * @param field  字段
         * @param term   字段对应的值，只能转入一个，且不能有有空格.如果传入的值为负值，请用双引号包起来，例如：<br>
         *               client.addPrimitveTermQuery("eid", "\"-1\"",FieldOccurs.MUST);
         * @param occurs 是否必须作为过滤条件
         */
        esSmallIndexSearch.addPrimitiveTermQuery("content", "中国品牌更懂中国人", FieldOccurs.MUST);

        /**
         * 设置排序方式，本方法可多次调用，结果按照传递的排序方式顺序排列
         *
         * @param field 所需排序字段 （_score字段为elasticsearch内置字段）
         * @param order 顺序（升序、降序）
         */
        // 相关性评分搜索
        esSmallIndexSearch.addSortField("_score", SortOrder.DESC);

        esSmallIndexSearch.execute(new String[]{"pubtime", "content"});
        List<String[]> result = esSmallIndexSearch.getResults();
        esSmallIndexSearch.outputResult(result);
    }

    @Test
    public void addRangeTermsGetCountTotal() {
        esSmallIndexSearch.addRangeTerms("pubtime", "0000-05-27 19:41:12", "2019-05-27 19:41:12");

        esSmallIndexSearch.execute(new String[]{"pubtime", "url"});
        System.out.println(esAllIndexSearch.getCountTotal());
        List<String[]> result = esSmallIndexSearch.getResults();
        esSmallIndexSearch.outputResult(result);
    }

    @Test
    public void addMoreLikeThisQuery() {

        /**
         * field:被查询的字段
         * sentence:字段值
         * NOTE TODO(相似性查询 - 根据特殊字符标点符号进行文本分段)
         */
//        esSmallIndexSearch.addMoreLikeThisQuery("content", "摩登兄弟《昨日少年》网页链接 @QQ音乐 昨日少年");

        /**
         * field:被查询的字段
         * sentence:字段值
         * keywordSize:控制分词数量(负数时返回所有分词结果)
         * NOTE TODO(相似性查询 - 控制分词)
         */
//        esSmallIndexSearch.addMoreLikeThisQuery("content", "摩登兄弟《昨日少年》网页链接 @QQ音乐 昨日少年", 12);
        esSmallIndexSearch.addMoreLikeThisQuery("content", "近日，习近平总书记对张富清同志先进事迹作出重要指示：张富清用自己的朴实纯粹、淡泊名利书写了精彩人生，是广大部队官兵和退役军人学习的榜样。", -1);

        esSmallIndexSearch.execute(new String[]{"content"});
        System.out.println(esAllIndexSearch.getCountTotal());
        List<String[]> result = esSmallIndexSearch.getResults();
        esSmallIndexSearch.outputResult(result);
    }

    @Test
    public void searchTestAddRangeOccurs() {

        /**
         * GT:搜索大于某值的字段，不包含该值本身
         * GTE:搜索大于某值的字段，包含该值本身
         * LT:搜索小于某值的字段，不包含该值本身
         * LTE:搜索小于某值的字段，包含该值本身
         **/

        esSmallIndexSearch.addRangeTerms("pubtime", "2019-05-27 19:41:12", FieldOccurs.MUST, RangeOccurs.LTE);

        esSmallIndexSearch.execute(new String[]{"pubtime", "url"});
        System.out.println(esAllIndexSearch.getCountTotal());
        List<String[]> result = esSmallIndexSearch.getResults();
        esSmallIndexSearch.outputResult(result);
    }

    @Test
    public void facetCountQueryOrderByCount() {

        esSmallIndexSearch.addRangeTerms("pubtime", "2018-05-31 17:20:12", DateUtil.millToTimeStr(System.currentTimeMillis()));
        /**
         * 单字段一次聚合
         */
//        List<String[]> result = esSmallIndexSearch.facetCountQueryOrderByCount("it", 100, SortOrder.DESC);


        /**
         * 二次聚合
         * 此方法可以针对索引中某项数据分组后 ,再对子项数据进行分组统计（类似于数据库两个字段的count(*) 与 group by结合）并返回统计结果<br>
         * 类似sql：select a.eid,COUNT(0)  FROM (  SELECT eid,qid FROM `content`  GROUP BY eid,qid) a GROUP BY a.eid  ;<br>
         * 该方法返回的列表中按照统计数由大到小排序<br>
         * 统计的字段包括pubdate、pubtime、eid、ip、user_id、group_id 等。 假设针对不同eid下的user_id进行统计，<br>
         * 会针对索引中每个不同eid，不同user_id进行统计<br>
         * ElasticSearch 利用Buketing 中的 terms aggregation 方式 默认分别返回1000000000,以便增加精确度。
         *
         * @param field      统计分组的父字段
         * @param childField 统计分组的子字段
         * @param topN       要求返回的结果数 ,topN 等于0 时，将返回所有的统计结果
         * @param sort       子字段统计排序
         * @return 前topN个结果的list ， 每一项为一个数组，数组 【0】为统计父字段，【1】为父子段所查询文档的数量，【2】为子字段聚合的统计数
         * * 	注意 ： 执行 getTotal()方法               为条件过滤后未执行聚合分组的文档总量；
         * 执行 getCountTotal()方法               为条件过滤后执行聚合总量；
         */
        /**
         * 二次聚合
         * **/
        List<String[]> result = esSmallIndexSearch.facetTwoCountQueryOrderByCount("it", "id", 100, SortOrder.DESC);

//        esSmallIndexSearch.facetDateAggsTophits()

//        System.out.println(esSmallIndexSearch.outputQueryJson());
        esSmallIndexSearch.outputResult(result);
    }

    @Test
    public void rangeTermQuery() {
        /**
         * 自定义设定开闭区间:
         * GT:搜索大于某值的字段，不包含该值本身
         * GTE:搜索大于某值的字段，包含该值本身
         * LT:搜索小于某值的字段，不包含该值本身
         * LTE:搜索小于某值的字段，包含该值本身
         **/
        esSmallIndexSearch.addRangeTerms("pubtime", "2018-05-31 17:20:12", RangeOccurs.GTE, DateUtil.millToTimeStr(System.currentTimeMillis()), RangeOccurs.LTE, FieldOccurs.MUST);
        esSmallIndexSearch.setStart(0);
        esSmallIndexSearch.setRow(100);

        esSmallIndexSearch.execute(new String[]{"pubtime", "content"});
        List<String[]> result = esSmallIndexSearch.getResults();
        esSmallIndexSearch.outputResult(result);

    }

    @Test
    public void facetMultipleCountQueryOrderByCount() {

        esSmallIndexSearch.addRangeTerms("pubtime", "2018-05-31 17:20:12", DateUtil.millToTimeStr(System.currentTimeMillis()));

        /**
         * 多次聚合
         * 此方法可以针对索引中某项数据分组后 ,再对子项数据进行分组统计（类似于数据库两个字段的count(*) 与 group by结合）并返回统计结果<br>
         * 类似sql：select a.eid,COUNT(0)  FROM (  SELECT eid,qid FROM `content`  GROUP BY eid,qid) a GROUP BY a.eid  ;<br>
         * 该方法返回的列表中按照统计数由大到小排序<br>
         * 统计的字段包括pubdate、pubtime、eid、ip、user_id、group_id 等。 假设针对不同eid下的user_id进行统计，<br>
         * 会针对索引中每个不同eid，不同user_id进行统计<br>
         * ElasticSearch 利用Buketing 中的 terms aggregation 方式 默认分别返回1000000000,以便增加精确度。
         *
         * @param field       统计分组的父字段
         * @param childFields 统计分组的子字段,数组
         * @param topN        要求返回的结果数 ,topN 等于0 时，将返回所有的统计结果
         * @return 前topN个结果的list ， 每一项为一个数组，数组 【0】为统计父字段，【1】为父子段所查询文档的数量，【N】为子字段聚合的统计数
         * 默认排序为父字段的文档数
         * * 	注意 ： 执行 getTotal()方法               为条件过滤后未执行聚合分组的文档总量；
         * 执行 getCountTotal()方法               为条件过滤后执行聚合总量；
         */
        List<String[]> result = esSmallIndexSearch.facetMultipleCountQueryOrderByCount("it", new String[]{"id"}, 100);
        esSmallIndexSearch.outputResult(result);
    }

    @Test
    public void facetDateByCount() {

        esSmallIndexSearch.addRangeTerms("pubtime", "2018-05-31 17:20:12", DateUtil.millToTimeStr(System.currentTimeMillis()));

        /**
         * 根据时间粒度统计 聚合数量
         * 类似统计每一天有多少个用户；
         *
         * @param TimeField   查询的时间字段
         * @param format      时间格式 例如：yyyy-MM-dd
         * @param interval    粒度 (1M代表每月，1d代表每日，1H代表每小时)
         * @param CountFields 要聚合的字段s
         * @return
         */
        List<String[]> result = esSmallIndexSearch.facetDateByCount("pubtime", "yyyy-MM-dd", "1d", new String[]{"it"});
        esSmallIndexSearch.outputResult(result);
    }

    @Test
    public void dateHistogramCount() {

        /**
         * 根据时间粒度对某字段的各项进行分组统计(例如：按天统计it各个数据量)
         * **/
//        esAllIndexSearch.addRangeTerms("pubtime", "2019-05-31 17:20:12", DateUtil.millToTimeStr(System.currentTimeMillis()));
//        esSmallIndexSearch.addRangeTerms("pubtime", "2019-05-27 17:20:12", DateUtil.millToTimeStr(System.currentTimeMillis()));
//        esAllIndexSearch.addRangeTerms("pubtime", "2019-05-27 17:20:12", DateUtil.millToTimeStr(System.currentTimeMillis()));


        /**
         * 根据时间粒度统计 聚合数量
         * 类似统计每一天it字段下各个数据类型数据量
         *
         * @param TimeField   查询的时间字段
         * @param format      时间格式 例如：yyyy-MM-dd
         * @param interval    粒度 (1M代表每月，1d代表每日，1H代表每小时)
         * @param secondField 要聚合的字段s
         * @return
         * @Description: TODO(根据时间粒度统计)
         */
        EsIndexSearch.debug = true;
//        List<String[]> result = esSmallIndexSearch.facetDateBySecondFieldValueCount("pubtime", "yyyy-MM-dd", "1d", "it");
//        List<String[]> result = esAllIndexSearch.facetDateBySecondFieldValueCount("pubtime", "yyyy-MM-dd", "1d", "it");
//        List<String[]> result = esSmallIndexSearch.facetDateBySecondFieldValueCount("pubtime", "yyyy-MM-dd", "1d", "it");
//        List<String[]> result = esAllIndexSearch.facetDateBySecondFieldValueCount("pubtime", "yyyy-MM-dd", "1d", "it");

        List<String[]> result = esAllIndexSearch.facetDateBySecondFieldValueCount("index_time", "yyyy-MM-dd", "1d", "it");

//        List<String[]> result = esSmallIndexSearch.facetDateBySecondFieldValueCount("pubtime", "yyyy-MM-dd hh:MM:ss", "1h", "it");
        // OUTPUT
        int i = 0;
        for (String[] infos : result) {
            System.out.print(i++ + ":");

            int size = 0;
            for (String info : infos) {
                size++;
                if (itMap.get(info) != null) {
                    System.out.print(info + "-" + itMap.get(info) + "\t");
                } else {
                    if (size == 2) {
                        System.out.print("数据总量：" + info + "\t");
                    } else {
                        System.out.print(info + "\t");
                    }
                }
            }
            System.out.println("");
        }
    }

    @Test
    public void whileCountSmallIndexData() {
        while (true) {
            dateHistogramCount();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            esSmallIndexSearch.reset();
            esAllIndexSearch.reset();
            esWarningIndexSearch.reset();
        }
    }

    @Test
    public void addPrimitiveTermFilterTest() {
        /**
         * 精确匹配查询：
         * 此方法用于对一些不可分词的数据进行检索，例如int,long型数据的genus，alarm等字段 <br>
         * 本方法也可以对分词的数据进行检索，但传入的内容不能有任何的标点符号以及空格，否则检索结果会出现异常<br>
         * 该方法用于int,long,string等类型的匹配，对于string类型，支持通配符(*)<br>
         *
         * @param field        字段
         * @param term         字段对应的值，只能转入一个，且不能有有空格.如果传入的值为负值，请用双引号包起来，例如：<br>
         *                     client.addPrimitveTermQuery("eid", "\"-1\"",FieldOccurs.MUST);
         * @param occurs       是否必须作为过滤条件
         * @param isEscapeChar 是否转义特殊字符
         */
        esWarningIndexSearch.addPrimitiveTermFilter("blogger", "黄金外汇-宇凡", FieldOccurs.MUST);

//        esWarningIndexSearch.addKeywordsQuery("blogger", "黄金外汇-宇凡", FieldOccurs.MUST);

        esWarningIndexSearch.setStart(0);
        esWarningIndexSearch.setRow(100);
        esWarningIndexSearch.execute(new String[]{"blogger", "content"});

        List<String[]> result = esWarningIndexSearch.getResults();
        esWarningIndexSearch.outputResult(result);
    }

    @Test
    public void setLogger() {
        Logger logger = Logger.getLogger(EsIndexSearchImp.class);
        /**
         * 设置日志输出对象
         *
         * @param logger log4j对象
         */
        esSmallIndexSearch.setLogger(logger);
        System.out.println(esSmallIndexSearch.logger);
    }

    @Test
    public void analysis() {
        String text = "原标题形势严峻这个地方书记市长纪委书记为何连续空降市委书记市长市委副书记接连落马的广东江门市政治生态修复从补齐关键岗位开始在连续迎来空降市委书记市长候选人后江门新一任纪委书记近日也到岗了值得关注的是他也是从省里空降的还是纪检部门这位新纪委书记叫项天保任职省纪委年在案例管理派驻机构巡视部门等关键岗位都工作过经验十分丰富去年底江门成立市委巡察工作机构时任省委巡视办副主任的项天保亲赴江门参加了启动仪式长安街知事此前曾介绍过江门是腐败的重灾区市委书记毛荣楷市长邓伟根市委副书记政法委书记邹家军市委常委王积俊市人大常委会副主任聂党权曾任市委副书记落马班子塌方全国罕见中央派来了一个沙瑞金省委书记又派来了一个田国富纪委书记这是人民的名义里的一个情节以此说明推动从严治党的迫切性江门的情况与此类似市委书记林应武市长候选人刘毅都是从省委组织部副部长任上调来江门的补位落马前任如今新纪委书记又从省级纪检部门调来从一个侧面也反映出地方反腐形势的严峻性就在项天保就任的会议上前任纪委书记胡钛也以新身份亮相他已经出任市委副书记政法委书记也就是说现在江门市委常委班子中有两名来自纪检系统的领导胡钛是军转干部年底刚刚调任江门市纪委书记他有两次救火经历一次是梅州一次是江门年梅州市委书记朱泽君和纪委书记李纯德相继被调离此后又相继被查媒体对两人内斗多有报道胡钛正是接替了李的梅州纪委书记职务而去年赴江门履新正是该市市委书记毛荣楷和市委副书记邹家军落马之后胡钛之前的江门市纪委书记周伟万也是一名老纪检在纪检政法战线工作了年今年初当选市政协主席面对从严治党的新形势和班子塌方的旧局面接力反腐任重道远近日召开的江门全市领导干部大会上广东省委常委组织部长邹铭根据省委书记胡春华同志的指示对全市领导干部提出三点要求其中特别指出要进一步严明政治纪律和政治规矩营造良好的政治生态要保持干部队伍思想稳定和改革发展大局稳定积极引导广大干部群众把违纪违法的个人问题与江门整体工作区分开来不因人废事不因案划线不因此否定江门的工作影响江门的发展稳定营造良好的政治生态更好地推动发展无疑是江门工作当下的重中之重来源长安街知事责任编辑初晓慧文章关键词纪委书记市长纪检我要反馈保存网页";
        Set<String> set = EsIndexSearch.analysis(text, true, 2);
        for (String word : set) {
            System.out.println(word);
        }
    }

    @Test
    public void addRangeTermsCondition() {

        /**
         * （设置区间范围过滤条件）- 筛选某区间内的数据，筛选的字段必须为数字形式。 如时间、 id、 评论数等
         *
         * @param field       筛选的字段
         * @param value       区间结束值
         * @param occurs      是否必须作为过滤条件 一般为must
         * @param rangeOccurs 选择过滤方式（大于/大于等于/小于/小于等于）
         */
        // addRangeTerms(String field, String value, FieldOccurs occurs, RangeOccurs rangeOccurs)
    }

    @Test
    public void addRangeTerms1Condition() {
        /**
         * （设置区间范围过滤条件）- 筛选某区间内的数据，筛选的字段必须为数字形式。 如时间、 id、 评论数等
         *
         * @param field           筛选的字段
         * @param startTerm       区间开始值
         * @param startRangeOccur 指定开始值的开闭区间
         * @param endTerm         区间结束值
         * @param stopRangeOccur  指定结束值的开闭区间
         * @param occurs          是否必须作为过滤条件 一般为must
         */
        //addRangeTerms(String field, String startTerm, RangeOccurs startRangeOccur, String endTerm, RangeOccurs stopRangeOccur, FieldOccurs occurs)
    }

    @Test
    public void search() {
        PropertyConfigurator.configureAndWatch("config" + File.separator + "log4j.properties");

//		EsIndexSearch searchClient = new EsIndexSearch("106.75.136.149:61230&106.75.136.149:61231","all_data","analysis_data");
//		EsIndexSearch searchClient = new EsIndexSearch("106.75.177.129:61233","test2","wechat_new");
        EsIndexSearch searchClient = new EsIndexSearch("192.168.12.110:9200", "aircraft_info", "graph");
//		EsIndexUpdate es = new EsIndexUpdate("106.75.137.175:61233", "event_data_extract_result_v*,event_data_extract_result_q*", "analysis_data");

        System.out.println("\r\n---------------------------1.关键词、范围、分页、排序------------------------\r\n");
//		searchClient.addKeywordsQuery( "content" , "北京大学", FieldOccurs.MUST , KeywordsCombine.OR);//针对分词字段检索，检索内容也会分词，例如检索北京大学，返回结果可能存在北京理工大学
//		searchClient.addRangeTerms("id", "0", "19741");//数字类型范围
//		searchClient.addRangeTerms("pubtime", "2017-04-01 00:16:27", null);//日期类型范围
        searchClient.setStart(0);//分页
        searchClient.setRow(4);//分页
//		searchClient.addSortField("pubtime", SortOrder.ASC);//排序
        String[] fields = {"_id", "pubtime"};//返回字段
        searchClient.execute(fields);//执行查询
        System.out.println("总量：" + searchClient.getTotal());//获取结果总量
        List<String[]> resultList = searchClient.getResults();//获取结果
        for (String[] strings : resultList) {
            for (String string : strings) {
                System.out.print(string + "\t");
            }
            System.out.println("");
        }

		/*System.out.println("\r\n---------------------------2.原子、短语、非空------------------------\r\n");
		searchClient.reset();//重置
		searchClient.debug = true;
		List<String> list = new ArrayList<String>();
		list.add("北京 深圳");
		list.add("上海 深圳");
//		searchClient.addPhraseQuery("content",list, FieldOccurs.MUST,KeywordsCombine.OR,KeywordsCombine.AND);
//		searchClient.addPhraseQuery("uid", "b966ac68db", FieldOccurs.MUST,KeywordsCombine.AND);//content必须包含完整的北京大学才能匹配到
		searchClient.addPhraseQuery("province", "北京", FieldOccurs.MUST,KeywordsCombine.OR);
		searchClient.addPhraseQuery("city", "北京市", FieldOccurs.MUST,KeywordsCombine.OR);
		searchClient.addPhraseQuery("prefecture", "怀柔区", FieldOccurs.MUST,KeywordsCombine.OR);//content必须包含完整的北京大学才能匹配到
//		searchClient.addPrimitiveTermQuery("_id", "554491",  FieldOccurs.MUST );//不可分割数据检索，例如int,long型数据的genus，alarm等字段
//		searchClient.addExistsFilter( "content" );//非空
//		searchClient.addMissingFilter( "content" );//为空
		searchClient.addSortField("pubtime", SortOrder.DESC);
		searchClient.setStart(0);
		searchClient.setRow(10);
		String[] fieldss = {"_id","country","province","city","prefecture"};
		searchClient.execute(fieldss);
		System.out.println("总量："+searchClient.getTotal());
		List<String[]> resultLists = searchClient.getResults();
		for (String[] strings : resultLists) {
			for (String string : strings) {
				System.out.print(searchClient.extractStringGroup(string)+"\t");
			}
			System.out.println("");
		}
		Date a = new Date();
		a.setMinutes(0);
		a.setSeconds(0);
		System.out.println(new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(a));*/

		/*System.out.println("\r\n---------------------------3.获取数据量------------------------\r\n");
		searchClient.reset();//重置
//		searchClient.addPhraseQuery("_id", "9bae4d74920644e33d3197bbb55ed5a9", FieldOccurs.MUST,KeywordsCombine.AND);//content必须包含完整的北京大学才能匹配到
		searchClient.addRangeTerms("insertime", "2018-07-03 13:50:00", "2018-07-03 14:10:00");
		long total = searchClient.searchTotal();
		System.out.println(total);*/

		/*System.out.println("\r\n----------------------------4.单字段聚合-----------------------\r\n");
		searchClient.reset();//重置

		//针对uid字段进行聚合，按uid聚合的文档数倒序，返回前5条(返回参数小于0时返回0条，参数等于0时返回全部，参数大于0时返回参数指定数)
//		searchClient.addPhraseQuery("gid", "zx858x0004 y44yz3xyx ywz83500xz x303yx434y xx9z58y9wx w9055508xw xz389x8z03 80wzxz0340 8yz3334yx5 x85x3x04w0 y358xzx3z4 8yx054x083 8yy0wy8935 w849zxxx58 ww80340yw5 wz305y9904 x3540y5095 x8wwyx5xxw xw8w3w8yyx 4z9x0yxww 8y54w8983x 8yyw5w9099 w04550x304 w085993449 w09wyz4y40 w30y93z58z w583095y8w w5ww5z00wy w5x0xz08x3 w80w54ww3y w8550w439w w8x080x888 ww0z339098 wx90448xzy wyw5y5z90x wyyx4xz358 wzxx5y5y39 x3zw0y535y x438zwxxwz x498y5yyxx y4zz384w85 53yx0ywy5y 805x5440x8 80z4w50090 8y30wy508w 8y44339ww4 8y8w5z4zx4 8y93zyzy43 8yy35xw854 8yzy5x9430 8z54xz900z 8z55389z53 8zx0wyx8z0 8zzx5x9334 9y4wz55xw w0yw3w9w3z w3wy3y8w3z w485zy4544 w59454458y w59843x8z5 w5x9334w40 w5yyx3x5z4 w8943934xz w90454y0x3 w9w0390x49 w9z05095w3 w9zww4x45z ww0y80xwx3 ww39409353 wwyy593543 wx35w4405y wy8w3w8z0z wy9zy5x40w wyyy4y3y8y wzx95yxz30 x338y50908 x35y5zx4y0 x443385543 x53w39z503 x55xyz4z35 x5x8z985yw x80z3ww094 x894380z4y x898zwy5z0 x9y94534x5 xw4330y4x3 xw8x3w8y3z xw935z5384 xx50504y0w xx5z0y5354 xyxy08z33 y850354wz8 yy83yy0w98 z3x09yxw9w z909z004y5 308y050080 3z3x8980w 58zwyz59w 8033389xz0 8054390y44 80585yy8z5 8080x3yyzz 809w99y09z 80xyw434zw 80yxx5y33x 8x094y8w5 8y3x339z50 8yx459535z 8yy5044zxx 8z4w544953 8z55w58y48 8z84408400 8zw03z5zwy 8zw9x3xyxx 8zx53893xy 8zx8w4w30y 8zxx548z5w 8zyy339388 w08xy5y303 w0x0zy3w33 w0x4333x8x w0xwy5z9wy w0xz5y4339 w0y5y5z888 w0yx3x859y w308wx3385 w39xwxyx35 w3xw405w84 w3yx3ww954 w44z893090 w45z405zz3 w48xx9x0zw w498339390 w5095w58xz w53w0939xy w545y0w9x4 w555y35393 w583x0y0w4 w59xx550zw w5w8w89995 w5y445ww98 w5z0x598wz w830wx4385 w840w3z8xx w843333wz3 w84939044w w84953y545 w84w340535 w84xz0xy8w w8943900wy w8ww3y55xw w8xy340zx5 w8yy3893zw w8z9339z03 w8zyy4z900 w94z5wx5x0 w95wwz4403 w95x08y3x3 w9858yww95 w985xw59x9 w99434wxzz w99yz0y4xz w9w9389350 ww895809xw ww8x543yxw www438895w wwy4x4yyy0 wwz854098w wx3w355y0x wx3xwzyyzy wx59w983y9 wx84339004 wxx3w48w54 wxxw0894zx wxz53898z5 wy30y0x48x wy3x349z5y wy853wz449 wy933wz33y wy99w8w404 wyw9y53y9z wyy0458zx9 wyyw39y49y wyyx0x583x wyyxyz3984 wyz5z3wy3x wz0w330y39 wz384w8995 wz40y5y4x9 wz4zy5y4yx x38zyz08zx x39308zy45 x39wy5y539 x3w05z4940 x3w80x053y x3x4yz459y x4093w005w x440y54449 x4z95z9y4y x5033zy483 x5xw3493z3 x5y3zz0509 x8393x9w4z x85z38533z x8939wyy4x x895yz3885 x89y40x989 x89yy03x0w x89yyz3983 x8x94zw949 x8xx30wx3y x8zz5z0998 x90z4z890y x94xyz4y49 x99x08x040 x9w3yz4330 x9yy340yxx xw05y08594 xw34z3x38w xw850y4844 xwy4yz045w xwyx0y48z3 xx054393w3 xx05yz4zw9 xx394848x4 xx44y5z8w8 xx8yy098w5 xx90y54ywy xx93y5y4ww xxyy3zxz3y y33x5yw840 y3838yx80x y394xyyw08 y409yxx945 y5000038y9 y5444w99z4 y80yzyx8x9 y94xxz45x0 yw003w5898 yw0w33wx ywx00583y3 ywy5094z4w z0x8w4z094 z30y48xxy8 z3yzw5w00y z4430449wx z4y3yx8w9y z543834z95 z94933xxw8 z94xz8y5z9 zw983yx533 zw9xzxwx53 zz80995434 30wzx8z895 33583504z 34y595xywy 394wx4xxy 3wxyz8x03 3yx3w4483y 533w998485 540y9wz305 585y9x95x0 5ww598y99y 5ywwz4939 5z559w9z4 65111 800055583y 8005544x3y 8033393z05 8083x4x8yy 808w39zw9w 808zy35489 809x590y8z 80w35y84yw 80w3xy8zx8 80wz4y040x 80wz5z339z 80wzx00054 80x55444x4 80x939z003 80xx339x8x 80yy389xyz 80zy4349y3 84y38w4y 8y044zz9x4 8y0830xz0z 8y0wwzw5yx 8y0y54w85y 8y3w5w0z95 8y3x4wyxz0 8y50385wy4 8y55598xw4 8y555x5089 8y90w849xy 8yw0548489 8yxyx34w0z 8yy339z4w5 8yz353wxz4 8yz8w9ww59 8yzw54wx90 8z03w9wz9z 8z38w99y3w 8z38xx05xw 8z39y98w4w 8z4w509zz3 8z54340zy4 8z80wy339x 8z8339wx53 8z8y39zzw4 8z9y333yxy 8zwxxw3083 8zwz550w89 8zxzx34y50 8zy4xzy00x 8zyz5949xy w009y5yx50 w00yy58959 w00z4w385x w03w3w84z4 w03w493zy4 w04y4x90z9 w059yz4yx4 w08308xyxw w0833wz5wz w08y4y34x8 w09345zx3z w095y80305 w099y5x9w8 w0x94x90zx w0y53zyyxz w0y5404wzy w0yz40xyw8 w30895533z w3599xz5xy w393083zy9 w3w9wz4z98 w4090498w9 w49xw85w5w w4w94z90yw w50y054403 w53354398z w539339xww w53w390zz8 w53x335w8x w53xxwx55w w54xw08804 w54y58ww3x w558593w59 w55x4y5zy4 w55x5x4855 w55z54905w w594544855 w599389990 w59x3w0w93 w59x5w3zz0 w5w83wzy04 w5x4w4w34x w5y439z0z3 w5y4yzz59z w5y533955z w5yz38949w w5z3389wx0 w5z9x00w8x w5zw5w8xz3 w80454xz0z w804w95ww3 w80859y844 w83439z4xz w8344zw330 w8483z4y88 w854xy8z94 w85953xz90 w88054zw38 w8845x90x8 w884xzzwx5 w89z395y00 w8w0xy9xx4 w8x0yy9z59 w8xy80y3zy w8y5x3xx30 w8yx4800y3 w8zz344yy4 w9354500z3 w9383w05zz w93y389xx5 w9485w9094 w95354wxyy w954xz3zw3 w955588954 w958538y59 w958w90x58 w98x43y80w w990x38x30 w994y3x43z w99wy84889 w99y588w45 w99yw0y085 w9w0390y35 w9w0w4wxx0 w9wxwzyy05 w9wxx3z9z4 w9wy3888z5 w9x0zw4xx3 w9x338wxxz w9x455w30y w9x9404y05 w9y0099z4y w9y0x4w450 w9z3390yxz w9z8z40yy3 w9zz3333x9 ww03x5xz5 ww055xx933 ww09wz43x4 ww0w48zz00 ww0y44w8yx ww0z5wxx83 ww3y54wx4x ww3yxww880 ww3zzw805x ww44z8wyy9 ww45x8zx98 ww485xzx45 ww503zyz88 ww533395y9 ww5w5y40w5 ww5yzwwx0x ww5zx5w8x3 ww8w0558w0 ww8z558449 ww94390xw9 ww9w5z8353 ww9x5y8948 wwx45x5853 wwx4x3w5y8 wwx8353zz9 wwy0x94w45 wwy45583w8 wx0x53z5yx wx3455zy3x wx48040909 wx53w8394x wx8zxy35yz wx9w39zyzw wx9yx88899 wxw9558x83 wxy85yzwy5 wxz9zywww5 wy03484w4y wy0y485w4y wy34434554 wy3538xx09 wy44y93440 wy49433304 wyw9548zxy wyx0545wy0 wyx839z9y9 wyy3yz3wz0 wz0345x498 wz043x8xy8 wz3509305w wz3y3xx8y0 wz3y5x0yw5 wz580y50wx wz5zy5y3w8 wz8y3x93w5 wz9854xzyw wz9w3x8003 wz9y4y80w0 wzw90049yw wzw9x0009y wzwyyw9zx5 wzy04w350x wzy0x3xy43 wzy43ywyz5 wzy93wy0z3 wzyxy59933 wzzw38430y x30909y44x x3335z304 x33w5yw8zy x343y5z9xx x34x3z85xy x353yz00z0 x353yz535w x355449y0w x358y5y4z0 x38wy8zx93 x390yz5y34 x3935880wx x393zy09ww x39z5y8w8z x3w8y54z9x x3wzy5yxw4 x3yw49598w x3yy3wz449 x403y5y958 x433y48zyy x4344x9495 x4444z95xx ",FieldOccurs.MUST, KeywordsCombine.OR);
		searchClient.addPhraseQuery("eid", "3",FieldOccurs.MUST, KeywordsCombine.OR);
//		searchClient.addExistsFilter("action");
		List<String[]> s = searchClient.facetCountQueryOrderByCount("action", 50, SortOrder.DESC );
		//返回结果s为list，其中每个String[]的 第0位为uid字段值， 第1位为该uid字段匹配到的文档数。
		System.out.println("文档总量："+searchClient.getTotal());//检索的文档总数
		System.out.println("结果总量："+searchClient.getCountTotal());//结果文档总数
		String a ="";
		for(String[] infos: s){
			for(String info:infos)
				System.out.print(info + "\t");
			System.out.println("");
			a=a+infos[0]+" ";
		}
		System.out.println(a);*/

	/*	System.out.println("\r\n----------------------------5.多字段聚合统计-----------------------\r\n");
		searchClient.reset();//重置
		//针对uid，gid字段分别聚合统计文档数
		Map<String, Long> s = searchClient.facetCountQuerysOrderByCount(new String[]{"uid","gid"} );
		//返回结果map类型，key值为聚合的字段，value为该字段聚合的文档数
		System.out.println("文档总量："+searchClient.getTotal());
		Set<String> keys = s.keySet();
		for (String key : keys) {
			System.out.println(key+"\t"+s.get(key));
		}*/

		/*System.out.println("\r\n----------------------------6.二次聚合-----------------------\r\n");
		searchClient.reset();//重置
		searchClient.debug=true;
		searchClient.addPhraseQuery("eid","1",FieldOccurs.MUST);
//		searchClient.addExistsFilter("action");
		//返回结果较慢，不建议用,针对第一个字段eid聚合情况下，再聚合info_type
		//参数5为限制返回结果5条；参数boolean为true时结果返回info_type详细数据；参数SortOrder为排序
		List<String[]> ss = searchClient.facetTwoCountQueryOrderByCount("uid","info_type",5, true, SortOrder.DESC );
		//结果ss为list数组，其中每个Sting[]，第0位为eid的值，第1位为该eid匹配到的文档数，第2位为info_type字段的聚合数，第3位为info_type字段的聚合的详细信息
		System.out.println("文档总量："+searchClient.getTotal());
		for(String[] infos: ss){
			for(String info:infos)
				System.out.print(info + "\t");
			System.out.println("");
		}*/


	/*	System.out.println("\r\n----------------------------7.多次聚合-----------------------\r\n");
		searchClient.reset();//重置

		searchClient.addPhraseQuery("uid","5zyz0y8x 64168 26022 24106",FieldOccurs.MUST,KeywordsCombine.OR);
		// 1、field 统计分组的父字段；2、 childFields 统计分组的子字段,数组格式  ；3、topN 要求返回的结果数 ,topN 等于0 时，将返回所有的统计结果；4、orderFiled 排序的字段，为空时， 默认排序为父字段的文档数； 5、sort 排序
		List<String[]> ss = searchClient.facetMultipleCountQueryOrderByCount("ip", new String[]{"uid"}, 5, "ip", SortOrder.DESC );
		// 返回结果 前topN个结果的list ， 每一项为一个数组，数组 【0】为统计父字段值，【1】为父子段所查询文档的数量，【N】为子字段聚合的统计数(顺序与设置childFields字段顺序一致)
		System.out.println("文档总量："+searchClient.getTotal());
		System.out.println("结果总量："+searchClient.getCountTotal());
		for(String[] infos: ss){
			for(String info:infos)
				System.out.print(info + "\t");
			System.out.println("");
		}*/


		/*System.out.println("\r\n----------------------------8.按时间粒度统计文档数-----------------------\r\n");
		searchClient.reset();//重置
		searchClient.addPhraseQuery("eid","1",FieldOccurs.MUST);
//		searchClient.addRangeTerms("pubtime", "2017-04-16 00:00:00", "2017-04-19 00:00:00");
		//针对时间类型字段进行时间间隔文档数量统计；参数：1、field为时间类型字段；2、format为时间类型格式；3、interval为间隔粒度；4、startTime为开始时间；5、endTime为结束时间；6、每个时间段返回最小文档数
		List<String[]> list = searchClient.facetDate("pubtime", "yyyy-MM-dd", "1d","2017-04-16","2017-04-19",0);
		//返回结果list数组，每个String[]的第0位为时间区间，第1位为该区间的文档数；
		for(String[] infos: list){
			for(String info:infos)
				System.out.print(info + "\t");
			System.out.println("");
		}*/

		/*System.out.println("\r\n----------------------------9.按时间粒度统计聚合字段-----------------------\r\n");
		searchClient.reset();//重置

		searchClient.addPhraseQuery("eid","1",FieldOccurs.MUST);
		searchClient.addRangeTerms("pubtime", "2017-04-13 00:00:00", "2017-04-19 00:00:00");
		//针对时间类型字段进行时间间隔文档数量统计，并且进一步聚合统计每个时间区间的uid字段数量；参数：1、field为时间类型字段；2、format为时间类型格式；3、interval为间隔粒度；4、startTime为开始时间；5、endTime为结束时间；6、每个时间段返回最小文档数；7、CountField为聚合的字段
		List<String[]> list7 = searchClient.facetDateByCount("pubtime", "yyyy-MM-dd", "1d","2017-04-13","2017-04-19", "uid");
		//返回结果list数组，每个String[]的第0位为时间区间，第1位为该区间的文档数,第2位为该区间内聚合的uid字段值；
		for(String[] infos: list7){
			for(String info:infos)
				System.out.print(info + "\t");
			System.out.println("");
		}*/

		/*System.out.println("\r\n----------------------------10.按时间粒度统计聚合字段----------------------\r\n");
		searchClient.reset();//重置

		searchClient.addPhraseQuery("eid","3",FieldOccurs.MUST);
		searchClient.addRangeTerms("pubtime", "2017-05-13 00:00:00", "2017-05-19 00:00:00");
		//针对时间类型字段进行时间间隔文档数量统计；参数：1、field为时间类型字段；2、format为时间类型格式；3、interval为间隔粒度；4、CountField为聚合的字段
		List<String[]> list = searchClient.facetDateByCount("pubtime", "HH", "1H",new String[]{"warning_level"});
		//返回结果list数组，每个String[]的第0位为时间区间，第1位为该区间的文档数；
		for(String[] infos: list){
			for(String info:infos)
				System.out.print(info + "\t");
			System.out.println("");
		}*/

		/*System.out.println("\r\n----------------------------11.按时间粒度统计聚合字段-----------------------\r\n");
		searchClient.reset();//重置

		searchClient.addPhraseQuery("eid","1",FieldOccurs.MUST);
//		searchClient.addRangeTerms("pubtime", "2018-04-13 00:00:00", "2018-04-19 00:00:00");
		List<String[]> list = searchClient.facetDate("pubtime", "HH",0);//可用于小时粒度统计
		for(String[] infos: list){
			for(String info:infos)
				System.out.print(info + "\t");
			System.out.println("");
		}*/

		/*System.out.println("\r\n----------------------------12.按时间粒度统计聚合字段各类型数据量----------------------\r\n");
		searchClient.reset();//重置
		searchClient.debug=true;
//		searchClient.addPhraseQuery("eid","1",FieldOccurs.MUST);
		searchClient.addRangeTerms("pubtime", "2017-05-13 00:00:00", "2017-05-19 00:00:00");
		//针对时间类型字段进行时间间隔文档数量统计；参数：1、field为时间类型字段；2、format为时间类型格式；3、interval为间隔粒度；4、CountField为聚合的字段
		List<String[]> list = searchClient.facetDateByTypeCount("pubtime","yyyy-MM-dd HH","1H","2017-05-13 00","2017-05-19 00","msg_type");
		//返回结果list数组，每个String[]的第0位为时间区间，第1位为该区间的文档数；第2位为聚合字段各类型是数据量；
		//结果数组样例样例 ：2017-04-10	3	[{"doc_count":2,"key":3},{"doc_count":1,"key":4}]
		for(String[] infos: list){
			for(String info:infos)
				System.out.print(info + "\t");
			System.out.println("");
		}*/

		/*System.out.println("\r\n---------------------------13.自定义查询------------------------\r\n");
		searchClient.reset();//重置
		searchClient.debug=true;
		searchClient.addPhraseQuery("eid", "1", FieldOccurs.MUST,KeywordsCombine.AND);
		String querstring = "+( (content:\"北京\" AND \"上访\") OR (content:\"长春\" OR \"呼吁给孩子无条件接受教育的权利\") )";
		searchClient.addQueryCondition(querstring);//自定义
		String [] fieldsss = {"id","content"};
		searchClient.execute(fieldsss);
		System.out.println("总量："+searchClient.getTotal());
		List<String[]> resultLists2 = searchClient.getResults();
		for (String[] strings : resultLists2) {
			for (String string : strings) {
				System.out.print(string+"\t");
			}
			System.out.println("");
		}*/


		/*System.out.println("\r\n---------------------------14.自定义查询------------------------\r\n");
		searchClient.reset();//重置
		String lucene = "{\"query\":{\"match\":{\"eid\":1}}}";
		String json = searchClient.addQueryConditionBylucene(lucene);
		System.out.println(json);*/

		/*System.out.println("\r\n----------------------------15.分词工具-----------------------\r\n");
		String content = "中国科学院自动化研究所";
		System.out.println("语料："+content);
		List<String> li = searchClient.extractKeywords(content, -1);
		for (String string : li) {
			System.out.print(string+"|");
		}*/
    }

    @Test
    public void extractKeywords() {
        /**
         * 工具类：对传入的字符串进行分词
         *
         * @param //keywords 要分词的字符串
         * @param size       返回数量,负数时返回全部
         * @return 返回一个{@code Set<String>}对象，存放分词后的关键词
         */
        // extractKeywords(String text, int size)
    }

    @Test
    public void extractStringGroup2() {
        /**
         * 工具类：对数组类型的字符串进行转换为数组
         *
         * @param //keywords 要转换的字符串
         * @return 返回一个{@code String[]}对象
         */
        // @SuppressWarnings("static-access")
        // extractStringGroup2(String text)
    }

    @Test
    public void extractStringGroup() {
        /**
         * 工具类：对数组类型的字符串进行转换为以分号间隔的字符串
         * 例如：["a","b","c"]  转换为    a;b;c;
         *
         * @param //keywords 要转换的字符串
         * @return 返回一个{@code String[]}对象
         */
        // extractStringGroup(String text)
    }

    @Test
    public void addQueryString() {
        /**
         * 添加查询条件，查询条件必须满足lucene的查询语法
         *
         * @param query_string
         */

//        aircraftSearch. addQueryString("(airline:IndiGo Airlines)", FieldOccurs.MUST);
        aircraftSearch.addQueryCondition("(airline:IndiGo Airlines)");

        aircraftSearch.execute(new String[]{"airline"});
        aircraftSearch.outputResult(aircraftSearch.getResults());
    }

    @Test
    public void outputQueryJson() {
        /**
         * @param
         * @return
         * @Description: TODO(输出索引查询语句)
         */
        // JSONObject outputQueryJson()
    }

    @Test
    public void outputResult() {
        /**
         * @param
         * @return
         * @Description: TODO(输出索引查询结果)
         */
        // JSONObject outputResult()
    }

    @Test
    public void outputResult1() {
        /**
         * @param resultList:索引的查询结果 - 包含具体字段名
         * @return
         * @Description: TODO(输出索引查询结果)
         */
        // void outputResult(List<String[]> resultList)
    }

    @Test
    public void outputResult2() {
        /**
         * @param result:索引的查询结果 - 包含具体字段名
         * @return
         * @Description: TODO(输出索引查询结果)
         */
        // void outputResult(Map<String, Long> result)
    }

    @Test
    public void facetCountQueryOrderByCountToMap() {
        /**
         * @param field：统计的字段
         * @param topN：要求返回的结果数 ,topN 等于 0 时，将返回所有的统计结果 。topN 小于0时，不返回结果。
         * @param sort：可选的排序方式
         * @return
         * @Description: TODO(支持数组字段内的聚合)
         */
        // Map<String, Long> facetCountQueryOrderByCountToMap(String field, int topN, SortOrder sort)
    }

    @Test
    public void addMoreLikeThisQuery1() {
        /**
         * @param field:被查询的字段
         * @param sentence:字段值
         * @return
         * @Description: TODO(相似性查询)
         */
        // void addMoreLikeThisQuery(String field, String sentence)

        /**
         * @param field:被查询的字段
         * @param sentence:字段值
         * @param keywordSize:控制分词数量(负数时返回所有分词结果)
         * @return
         * @Description: TODO(相似性查询 - 控制分词)
         */
        // void addMoreLikeThisQuery(String field, String sentence, int keywordSize)
    }

    @Test
    public void addKeywordsQuery() {

        esSmallIndexSearch.addKeywordsQuery("content", "北京大学", FieldOccurs.MUST, KeywordsCombine.OR);//针对分词字段检索，检索内容也会分词，例如检索北京大学，返回结果可能存在北京理工大学\

        /**
         * 构造关键词查询片段,如果输入的关键词以空格分割，那么将以空格切分开，作为多个关键词处理，多个关键词之间的关系由KeywordsCombine对象指定<br>
         * 关键词查询会走分词，如关键词为 北京科技大学，则有可能会搜索出北京大学的记录来<br>
         * 该方法对索引中字段类型配置为text（即有分词的类型）才有效<br>
         *
         * @param field    要查询的字段
         * @param keywords 关键词
         * @param occurs   字段出现情况
         * @param combine  以空格隔开的关键词的关系
         */
        // void addKeywordsQuery(String field, String keywords, FieldOccurs occurs, KeywordsCombine combine)

        /**
         * 构造关键词查询片段,关键词查询会走分词，如关键词为 北京科技大学，则有可能会搜索出北京大学的记录来<br>
         * 该方法对索引中字段类型配置为text（即有分词的类型）才有效<br>
         *
         * @param field    要查询的字段
         * @param keywords 关键词
         * @param occurs   字段出现情况
         */
        // void addKeywordsQuery(String field, String keywords, FieldOccurs occurs)

        /**
         * 构造关键词查询片段,关键词查询会走分词，如关键词为 北京科技大学，则有可能会搜索出北京大学的记录来<br>
         * <p>
         * 该方法对索引中字段类型配置为text（即有分词的类型）才有效<br>
         *
         * @param fields   要查询的字段
         * @param keywords 关键词
         * @param combine  多个字段间的关系
         */
        // void addKeywordsQuery(String[] fields, String keywords, KeywordsCombine combine)

        /**
         * 关键词查询：单字段多组关键词的全文检索查询，组间是或关系<br>
         * 构造关键词查询,keywords是数组，每个词组之间是或的关系<br>
         * 每一组词中可以包含空格，空格之间是与的关系<br>
         * 关键词查询会走分词，如关键词为 北京科技大学，则有可能会搜索出北京大学的记录来<br>
         * 该方法对索引中字段类型配置为text（即有分词的类型）才有效<br>
         *
         * @param field    要查询的字段
         * @param keywords 关键词
         * @param occurs   字段出现情况
         */
        // void addKeywordsQuery(String field, List<String> keywords, FieldOccurs occurs)

    }

    @Test
    public void addPhraseQuery() {
        /**
         * 构造短语查询片段,如果输入的短语以空格分割，那么将以空格切分开，作为多个短语处理，多个短语之间的关系由KeywordsCombine对象指定<br>
         * 短语查询不走分词，如短语为 北京科技大学，则必须完整含有北京科技大学的记录才会被匹配<br>
         * 该方法对solr中字段类型配置为text（即有分词的类型）才有效<br>
         *
         * @param field    要查询的字段
         * @param keywords 关键词
         * @param occurs   字段出现情况
         * @param combine  关键词组合情况
         */
        // void addPhraseQuery(String field, String keywords, FieldOccurs occurs, KeywordsCombine combine)
    }

    @Test
    public void addPhraseQuery1() {
        /**
         * 构造短语查询片段,如果输入的短语以空格分割，那么将以空格切分开，作为多个短语处理，多个短语之间的关系由KeywordsCombine对象指定<br>
         * 短语查询不走分词，如短语为 北京科技大学，则必须完整含有北京科技大学的记录才会被匹配<br>
         * 该方法对solr中字段类型配置为text（即有分词的类型）才有效<br>
         *
         * @param fields   要查询的字段多个字段
         * @param keywords 关键词
         * @param occurs   字段出现情况
         * @param combine  关键词组合情况
         */
//         void addPhraseQuery(String[] fields, String keywords, FieldOccurs occurs, KeywordsCombine combine, FieldCombine filedCombine)
    }

    @Test
    public void addPhraseQuery2() {
        /**
         * 构造多组短语查询片段,多组关系由KeywordsCombine groupRelation指定，如果每组输入的短语以空格分割，那么将以空格切分开，作为多个短语处理，多个短语之间的关系由KeywordsCombine combine对象指定<br>
         * 短语查询不走分词，如短语为 北京科技大学，则必须完整含有北京科技大学的记录才会被匹配<br>
         * 该方法对solr中字段类型配置为text（即有分词的类型）才有效<br>
         *
         * @param field         要查询的字段
         * @param keywords      多组关键词，每一组中的关键词可按空格分割
         * @param occurs        字段出现情况
         * @param groupRelation 多组之间组合情况
         * @param combine       每组关键词组合情况
         */
        // void addPhraseQuery(String field, List<String> keywords, FieldOccurs occurs, KeywordsCombine groupRelation, KeywordsCombine combine)
    }

    @Test
    public void addPhraseQuery3() {
        /**
         * 构造短语查询片段,如果输入的短语以空格分割，那么将以空格切分开，作为多个短语处理，多个短语之间为与的关系<br>
         * 短语查询不走分词，如短语为 北京科技大学，则必须完整含有北京科技大学的记录才会被匹配<br>
         * 该方法对solr中字段类型配置为text（即有分词的类型）才有效<br>
         *
         * @param field    要查询的字段
         * @param keywords 关键词
         * @param occurs   字段出现情况
         */
        // void addPhraseQuery(String field, String keywords, FieldOccurs occurs)
    }

    @Test
    public void addPhraseQuery4() {
        /**
         * 短语查询：多字段多组关键词的短语查询<br>
         * 构造关键词查询片段,多字段之间是或的关系<br>
         * keywords是数组，每个词组之间是或的关系，如果输入的关键词含有空格，空格之间是与的关系<br>
         * 短语查询不走分词，如短语为 北京科技大学，则必须完整含有北京科技大学的记录才会被匹配<br>
         * <p>
         * 该方法对索引中字段类型配置为text（即有分词的类型）才有效<br>
         * //* @param field
         * 要查询的字段
         *
         * @param keywords 关键词
         * @param occurs   字段出现情况
         */
        // void addPhraseQuery(String[] fields, String[] keywords, FieldOccurs occurs)
    }

    @Test
    public void addPrimitiveTermQuery1() {
        /**
         * 此方法用于对一些不可分词的数据进行检索，例如int,long型数据的genus，alarm等字段 <br>
         * 本方法也可以对分词的数据进行检索，但传入的内容不能有任何的标点符号以及空格，否则检索结果会出现异常<br>
         * 该方法用于int,long,string等类型的匹配，对于string类型，支持通配符(*)<br>
         *
         * @param field  字段
         * @param term   字段对应的值，只能转入一个，且不能有有空格.如果传入的值为负值，请用双引号包起来，例如：<br>
         *               client.addPrimitveTermQuery("eid", "\"-1\"",FieldOccurs.MUST);
         * @param occurs 是否必须作为过滤条件
         */
        // void addPrimitiveTermQuery(String field, String term, FieldOccurs occurs)

        /**
         * 与 {@link #addPrimitiveTermQuery(String, String, FieldOccurs)} 方法类似<br>
         * 字段对应的值可以输入多个，多个值之间为或的关系，满足其中一个值就会返回记录<br>
         * 该方法用于int,long,string等类型的匹配，对于string类型，支持通配符(*)<br>
         *
         * @param field  检索字段
         * @param terms  字段对应的多值，值之间是或的关系
         * @param occurs 是否必须作为过滤条件
         */
        // void addPrimitiveTermQuery(String field, String[] terms, FieldOccurs occurs)
    }

    @Test
    public void addPrimitiveTermFilter1() {
        /**
         * 此方法用于对一些不可分词的数据进行检索，例如int,long型数据的genus，alarm等字段 <br>
         * 本方法也可以对分词的数据进行检索，但传入的内容不能有任何的标点符号以及空格，否则检索结果会出现异常<br>
         * 该方法用于int,long,string等类型的匹配，对于string类型，支持通配符(*)<br>
         *
         * @param field  字段
         * @param term   字段对应的值，只能转入一个，且不能有有空格.如果传入的值为负值，请用双引号包起来，例如：<br>
         *               client.addPrimitveTermQuery("eid", "\"-1\"",FieldOccurs.MUST);
         * @param occurs 是否必须作为过滤条件
         */
        // void addPrimitiveTermFilter(String field, String term, FieldOccurs occurs)
    }

    @Test
    public void addPrimitiveTermFilter2() {
        /**
         * 此方法用于对一些不可分词的数据进行检索，例如int,long型数据的genus，alarm等字段 <br>
         * 本方法也可以对分词的数据进行检索，但传入的内容不能有任何的标点符号以及空格，否则检索结果会出现异常<br>
         * 该方法用于int,long,string等类型的匹配，对于string类型，支持通配符(*)<br>
         *
         * @param field  字段
         * @param terms  字段对应的值，只能转入一个，且不能有有空格.如果传入的值为负值，请用双引号包起来，例如：<br>
         *               client.addPrimitveTermQuery("eid", "\"-1\"",FieldOccurs.MUST); 段对应的多值，值之间是或的关系
         * @param occurs 是否必须作为过滤条件
         */
        // void addPrimitiveTermFilter(String field, String[] terms, FieldOccurs occurs)


        /**
         * 此方法用于对一些不可分词的数据进行检索，例如int,long型数据的genus，alarm等字段 <br>
         * 本方法也可以对分词的数据进行检索，但传入的内容不能有任何的标点符号以及空格，否则检索结果会出现异常<br>
         * 该方法用于int,long,string等类型的匹配，对于string类型，支持通配符(*)<br>
         *
         * @param field  字段
         * @param terms  字段对应的值，只能转入一个，且不能有有空格.如果传入的值为负值，请用双引号包起来，例如：<br>
         *               client.addPrimitveTermQuery("eid", "\"-1\"",FieldOccurs.MUST); 段对应的多值，值之间是或的关系
         * @param occurs 是否必须作为过滤条件
         */
        // void addPrimitiveTermFilter(String field, Set<String> terms, FieldOccurs occurs)
    }

    @Test
    public void addRangeTerms() {
        /**
         * 范围过滤.startTerm和endTerm构成闭区间 <br/>
         * 如果field为pubtime、pubdate,查询时参数请按照下面的格式进行传递：<br>
         * pubdate:长度为8，例如20110822,表示2011年08月22日<br>
         * pubtime:长度为14,例如:20110822142613,表示2011年08月22日14时26分13秒 <br>
         * startTerm、endTerm构成闭区间
         *
         * @param field     查询字段
         * @param startTerm 开始项
         * @param endTerm   结束项
         */
        // void addRangeTerms(String field, String startTerm, String endTerm)
    }

    @Test
    public void addRangeTerms1() {
        /**
         * 筛选某区间内的数据，筛选的字段必须为数字形式。 如时间、 id、 评论数等
         *
         * @param field     筛选的字段
         * @param startTerm 区间开始值
         * @param endTerm   区间结束值
         * @param occurs    是否必须作为过滤条件 一般为must
         */
        // void addRangeTerms(String field, String startTerm, String endTerm, FieldOccurs occurs)

        /**
         * 筛选某区间内的数据，筛选的字段必须为数字形式。 如时间、 id、 评论数等
         *
         * @param field       筛选的字段
         * @param value       区间结束值
         * @param occurs      是否必须作为过滤条件 一般为must
         * @param rangeOccurs 选择过滤方式（大于/大于等于/小于/小于等于）
         */
        // void addRangeTerms(String field, String value, FieldOccurs occurs, RangeOccurs rangeOccurs)

        /**
         * 筛选某区间内的数据，筛选的字段必须为数字形式。 如时间、 id、 评论数等
         *
         * @param field           筛选的字段
         * @param startTerm       区间开始值
         * @param startRangeOccur 指定开始值的开闭区间
         * @param endTerm         区间结束值
         * @param stopRangeOccur  指定结束值的开闭区间
         * @param occurs          是否必须作为过滤条件 一般为must
         */
        // void addRangeTerms(String field, String startTerm, RangeOccurs startRangeOccur, String endTerm, RangeOccurs stopRangeOccur, FieldOccurs occurs)
    }

    @Test
    public void addExistsFilter() {
        /**
         * 添加非空过滤
         *
         * @param field
         */
        // void addExistsFilter(String field)
    }

    @Test
    public void addMissingFilter() {
        /**
         * 添加空值过滤
         *
         * @param field
         */
        // void addMissingFilter(String field)
    }

    @Test
    public void setStart() {
        /**
         * 从第几条结果取数据
         *
         * @param start 开始记录号
         */
        // void setStart(int start)
    }

    @Test
    public void setRow() {
        /**
         * 取多少条数据
         *
         * @param rows 要取出的记录数
         */
        // void setRow(int rows)
    }

    @Test
    public void setTrackScores() {
        /**
         * 设置是否计算score
         */
        // void setTrackScores()
    }

    @Test
    public void setMinScore() {
        /**
         * 设置最小匹配度
         *
         * @param minscore
         */
        // void setMinScore(double minscore)
    }

    @Test
    public void reset() {
        /**
         * 重置搜索条件
         */
        // void reset()
    }

    @Test
    public void getQueryString() {
        /**
         * 获取提交请求串
         *
         * @param fields 索引查询后要返回值的字段，只有建索引时，有存储的字段此处才可能有返回值，对于只索引不存储的字段，此处得不到返回值
         * @return
         */
        // String getQueryString(String[] fields)
    }

    @Test
    public void searchTotal() {
        /**
         * 查询文档总量
         * 分页，排序条件将进行无效过滤。
         */
        // long searchTotal()
    }

    @Test
    public void execute() {
        /**
         * 提交请求
         *
         * @param fields 索引查询后要返回值的字段，只有建索引时，有存储的字段此处才可能有返回值，对于只索引不存储的字段，此处得不到返回值
         * @return
         */
        // void execute(String[] fields)
    }

    @Test
    public void execute1() {
        /**
         * 提交请求
         *
         * @param esQuery 索引查询后要返回值的字段，只有建索引时，有存储的字段此处才可能有返回值，对于只索引不存储的字段，此处得不到返回值
         * @return
         */
        // void execute(String esQuery)
    }

    @Test
    public void getResults() {
        /**
         * 返回检索结果，返回的检索字段以及字段顺序由{@link #execute(String[])} 方法中的参数fields指定
         *
         * @return 检索的结果列表
         */
        // List<String[]> getResults()
    }

    @Test
    public void getTotal() {
        /**
         * 获取搜索结果（非聚合）总长度
         *
         * @return 检索结果的总长度
         */
        // long getTotal()
    }

    @Test
    public void getCountTotal() {
        /**
         * 获取搜索聚合分组结果总长度
         *
         * @return 聚合分组结果的总长度
         */
        // long getCountTotal()
    }

    @Test
    public void facetCountQueryOrderByCount1() {
        /**
         * 单字段一次聚合
         * 此方法可以针对索引中某项数据进行统计（类似于数据库的count(*) 与 group by结合）并返回统计结果<br>
         * 该方法返回的列表中按照统计数由大到小排序<br>
         * 统计的字段包括pubdate、pubtime、eid、ip、user_id、group_id 等。 假设针对user_id进行统计，<br>
         * 会针对索引中每个不同user_id进行统计<br>
         * ，分别得到匹配的文档数，按照每个website_id统计得到的文档数排序，返回前topN个website_id 和对应的文档数<br>
         * <p>
         * ElasticSearch 利用Buketing 中的 terms aggregation 方式 默认分别返回100000,以便增加精确度。
         *
         * @param field 统计的字段
         * @param topN  要求返回的结果数 ,topN 等于 0 时，将返回所有的统计结果 。topN 小于0时，不返回结果。
         * @return 前topN个结果的list ， 每一项为一个数组， 第一项为统计字段，第二项为字段等于该值的文档数 。
         * 注意 ： 执行 getTotal()方法               为条件过滤后未执行聚合分组的文档总量；
         * 执行 getCountTotal()方法               为条件过滤后执行聚合总量；
         */
        // List<String[]> facetCountQueryOrderByCount(String field, int topN, SortOrder sort)


        /**
         * 单字段一次聚合 并且有限制返回聚合字段的详细数据
         * 此方法可以针对索引中某项数据进行统计（类似于数据库的count(*),详细信息  与 group by结合）并返回统计结果<br>
         * 该方法返回的列表中按照统计数由大到小排序<br>
         * ElasticSearch 利用Buketing 中的 terms aggregation 方式 默认分别返回100000,以便增加精确度。
         *
         * @param field          统计的字段
         * @param topN           要求返回的结果数 ,topN 等于 0 时，将返回所有的统计结果 。topN 小于0时，不返回结果。
         * @param sort           聚合字段的排序方式
         * @param returnFields   聚合返回详细字段,为null时返回全部字段
         * @param childSortField 聚合返回详细数据的排序字段，为null时将以相关性自动排序
         * @param childSortOrder 聚合返回详细数据排序方式
         * @param childSize      聚合返回详细数据条数
         * @return 前topN个结果的list ， 每一项为一个数组， 第一项为统计字段，第二项为字段等于该值的文档数，第三项为字段为详细数据  。facetCountQueryOrderByCount
         * 注意 ： 执行 getTotal()方法               为条件过滤后未执行聚合分组的文档总量；
         * 执行 getCountTotal()方法               为条件过滤后执行聚合总量；
         */
        // List<String[]> facetCountQueryOrderByCount(String field, int topN, SortOrder sort, String[] returnFields, String childSortField, SortOrder childSortOrder, int childSize)
    }

    @Test
    public void facetTwoCountQueryOrderByCount() {
        /**
         * 二次聚合(返回结果较慢，不建议用)
         * 此方法可以针对索引中某项数据分组后 ,再对子项数据进行分组统计（类似于数据库两个字段的count(*) 与 group by结合）并返回统计结果<br>
         * 类似sql：select a.eid,COUNT(0)  FROM (  SELECT eid,qid FROM `content`  GROUP BY eid,qid) a GROUP BY a.eid  ;<br>
         * 该方法返回的列表中按照统计数由大到小排序<br>
         * 统计的字段包括pubdate、pubtime、eid、ip、user_id、group_id 等。 假设针对不同eid下的user_id进行统计，<br>
         * 会针对索引中每个不同eid，不同user_id进行统计<br>
         * ElasticSearch 利用Buketing 中的 terms aggregation 方式 默认分别返回1000000000,以便增加精确度。
         *
         * @param field      统计分组的父字段
         * @param childField 统计分组的子字段
         * @param topN       要求返回的结果数 ,topN 等于0 时，将返回所有的统计结果
         * @param childbool  结果是否返回子字段详情，true or false
         * @param sort       子字段统计排序
         * @return 前topN个结果的list ， 每一项为一个数组，数组 【0】为统计父字段，【1】为父字段检索文档数，,【2】为父字段下子字段聚合的统计数，【3】子字段统计返回结果详情
         */
        //@Deprecated
        // List<String[]> facetTwoCountQueryOrderByCount(String field, String childField, int topN, boolean childbool, SortOrder sort)
    }

    @Test
    public void facetTwoCountQueryOrderByCount1() {
        /**
         * 二次聚合
         * 此方法可以针对索引中某项数据分组后 ,再对子项数据进行分组统计（类似于数据库两个字段的count(*) 与 group by结合）并返回统计结果<br>
         * 类似sql：select a.eid,COUNT(0)  FROM (  SELECT eid,qid FROM `content`  GROUP BY eid,qid) a GROUP BY a.eid  ;<br>
         * 该方法返回的列表中按照统计数由大到小排序<br>
         * 统计的字段包括pubdate、pubtime、eid、ip、user_id、group_id 等。 假设针对不同eid下的user_id进行统计，<br>
         * 会针对索引中每个不同eid，不同user_id进行统计<br>
         * ElasticSearch 利用Buketing 中的 terms aggregation 方式 默认分别返回1000000000,以便增加精确度。
         *
         * @param field      统计分组的父字段
         * @param childField 统计分组的子字段
         * @param topN       要求返回的结果数 ,topN 等于0 时，将返回所有的统计结果
         * @param sort       子字段统计排序
         * @return 前topN个结果的list ， 每一项为一个数组，数组 【0】为统计父字段，【1】为父子段所查询文档的数量，【2】为子字段聚合的统计数
         * * 	注意 ： 执行 getTotal()方法               为条件过滤后未执行聚合分组的文档总量；
         * 执行 getCountTotal()方法               为条件过滤后执行聚合总量；
         */
        // List<String[]> facetTwoCountQueryOrderByCount(String field, String childField, int topN, SortOrder sort)
    }

    @Test
    public void facetMultipleCountQueryOrderByCount1() {
        /**
         * 多次聚合
         * 此方法可以针对索引中某项数据分组后 ,再对子项数据进行分组统计（类似于数据库两个字段的count(*) 与 group by结合）并返回统计结果<br>
         * 类似sql：select a.eid,COUNT(0)  FROM (  SELECT eid,qid FROM `content`  GROUP BY eid,qid) a GROUP BY a.eid  ;<br>
         * 该方法返回的列表中按照统计数由大到小排序<br>
         * 统计的字段包括pubdate、pubtime、eid、ip、user_id、group_id 等。 假设针对不同eid下的user_id进行统计，<br>
         * 会针对索引中每个不同eid，不同user_id进行统计<br>
         * ElasticSearch 利用Buketing 中的 terms aggregation 方式 默认分别返回1000000000,以便增加精确度。
         *
         * @param field       统计分组的父字段
         * @param childFields 统计分组的子字段,数组
         * @param topN        要求返回的结果数 ,topN 等于0 时，将返回所有的统计结果
         * @return 前topN个结果的list ， 每一项为一个数组，数组 【0】为统计父字段，【1】为父子段所查询文档的数量，【N】为子字段聚合的统计数
         * 默认排序为父字段的文档数
         * * 	注意 ： 执行 getTotal()方法               为条件过滤后未执行聚合分组的文档总量；
         * 执行 getCountTotal()方法               为条件过滤后执行聚合总量；
         */
        // List<String[]> facetMultipleCountQueryOrderByCount(String field, String[] childFields, int topN)

        /**
         * 多次聚合
         * 此方法可以针对索引中某项数据分组后 ,再对子项数据进行分组统计（类似于数据库两个字段的count(*) 与 group by结合）并返回统计结果<br>
         * 类似sql：select a.eid,COUNT(0)  FROM (  SELECT eid,qid FROM `content`  GROUP BY eid,qid) a GROUP BY a.eid  ;<br>
         * 该方法返回的列表中按照统计数由大到小排序<br>
         * 统计的字段包括pubdate、pubtime、eid、ip、user_id、group_id 等。 假设针对不同eid下的user_id进行统计，<br>
         * 会针对索引中每个不同eid，不同user_id进行统计<br>
         * ElasticSearch 利用Buketing 中的 terms aggregation 方式 默认分别返回1000000000,以便增加精确度。
         *
         * @param field       统计分组的父字段
         * @param childFields 统计分组的子字段,数组
         * @param topN        要求返回的结果数 ,topN 等于0 时，将返回所有的统计结果
         * @param orderFiled  排序的字段，为空时， 默认排序为父字段的文档数
         * @param sort        排序
         * @return 前topN个结果的list ， 每一项为一个数组，数组 【0】为统计父字段，【1】为父子段所查询文档的数量，【N】为子字段聚合的统计数
         * * 	注意 ： 执行 getTotal()方法               为条件过滤后未执行聚合分组的文档总量；
         * 执行 getCountTotal()方法               为条件过滤后执行聚合总量；
         */
        // List<String[]> facetMultipleCountQueryOrderByCount(String field, String[] childFields, int topN, String orderFiled, SortOrder sort)
    }

    @Test
    public void facetDate() {
        /**
         * 根据时间粒度统计数量
         *
         * @param field         查询的时间字段
         * @param format        时间格式 例如：yyyy-MM-dd
         * @param interval      粒度 (1M代表每月，1d代表每日，1h代表每小时)
         * @param startTime     开始时间
         * @param endTime       结束时间
         * @param min_doc_count 最小返回值
         * @return
         */
        // List<String[]> facetDate(String field, String format, String interval, String startTime, String endTime, int min_doc_count)
    }

    @Test
    public void facetDate1() {
        /**
         * 根据时间粒度统计数量
         *
         * @param field     查询的时间字段
         * @param format    时间格式 例如：yyyy-MM-dd
         * @param interval  粒度 (1M代表每月，1d代表每日，1h代表每小时)
         * @param startTime 开始时间
         * @param endTime   结束时间
         * @return
         */
        // List<String[]> facetDate(String field, String format, String interval, String startTime, String endTime)
    }

    @Test
    public void facetDate2() {
        /**
         * 根据时间粒度统计数量
         *
         * @param field  查询的时间字段
         * @param format 时间格式 例如：yyyy-MM-dd HH
         * @param size   返回数量，小于等于0时返回10000条
         * @return
         */
        // List<String[]> facetDate(String field, String format, int size)
    }

    @Test
    public void facetDate3() {
        /**
         * 根据时间粒度统计数量
         *
         * @param field    查询的时间字段
         * @param format   时间格式 例如：yyyy-MM-dd
         * @param interval 粒度 (1M代表每月，1d代表每日，1h代表每小时)
         * @return
         */
        // List<String[]> facetDate(String field, String format, String interval)
    }

    @Test
    public void facetDateByCount1() {
        /**
         * 根据时间粒度统计 聚合数量
         * 类似统计每一天有多少个用户；
         *
         * @param TimeField  查询的时间字段
         * @param format     时间格式 例如：yyyy-MM-dd
         * @param interval   粒度 (1M代表每月，1d代表每日，1H代表每小时)
         * @param CountField 要聚合的字段
         * @return
         */
        // List<String[]> facetDateByCount(String TimeField, String format, String interval, String startTime, String endTime, String CountField)
    }

    @Test
    public void facetDateByTypeCount() {
        /**
         * 根据时间粒度统计 聚合不同类型数量
         * 类似统计每一天有多少个用户；
         *
         * @param TimeField  查询的时间字段
         * @param format     时间格式 例如：yyyy-MM-dd
         * @param interval   粒度 (1M代表每月，1d代表每日，1H代表每小时)
         * @param CountField 要聚合的字段
         * @return
         */
        // List<String[]> facetDateByTypeCount(String TimeField, String format, String interval, String CountField)
    }

    @Test
    public void facetDateByTypeCount1() {
        /**
         * 根据时间粒度统计 聚合不同类型数量
         * 类似统计每一天有多少个用户；
         *
         * @param TimeField  查询的时间字段
         * @param format     时间格式 例如：yyyy-MM-dd
         * @param interval   粒度 (1M代表每月，1d代表每日，1H代表每小时)
         * @param CountField 要聚合的字段
         * @return
         */
        // List<String[]> facetDateByTypeCount(String TimeField, String format, String interval, String startTime, String endTime, String CountField)
    }

    @Test
    public void facetDateAggsTophits() {

        /**
         * 根据时间粒度，根据排序 筛选返回每个时间段内前N条数据
         *
         * @param TimeField 查询的时间字段
         * @param format    时间格式 例如：yyyy-MM-dd
         * @param interval  粒度 (1M代表每月，1d代表每日，1H代表每小时)
         * @param startTime 起始时间
         * @param endTime   结束时间
         * @param size      时间段内返回数据条数
         * @return
         */
        // List<String[]> facetDateAggsTophits(String TimeField, String format, String interval, String startTime, String endTime, int size)
    }

    @Test
    public void facetDateAggsTophits1() {
        /**
         * 根据时间粒度，根据排序 筛选返回每个时间段内前N条数据
         *
         * @param TimeField    查询的时间字段
         * @param format       时间格式 例如：yyyy-MM-dd
         * @param interval     粒度 (1M代表每月，1d代表每日，1H代表每小时)
         * @param startTime    起始时间
         * @param endTime      结束时间
         * @param sortField    时间段内数据排序字段，为null时将以相关性自动排序
         * @param sortOrder    时间段内数据排序方式
         * @param returnFields 时间段内数据返回字段，为null时字段全部返回
         * @param size         时间段内返回数据条数
         * @return
         */
        // List<String[]> facetDateAggsTophits(String TimeField, String format, String interval, String startTime, String endTime, String sortField, SortOrder sortOrder, String[] returnFields, int size)

        String startTime = "2019-07-02 00:00:00";
        String stopTime = "2019-07-03 00:00:00";
        List<String[]> result = esSmallIndexSearch.facetDateAggsTophits("pubtime", "yyyy-MM-dd HH:mm:ss", "30s", startTime, stopTime,
                "pubtime", SortOrder.ASC, null, 1);
    }

    @Test
    public void addQueryCondition() {
        /**
         * 自定义查询
         *
         * @param QuerString 为 luncene语法，例如： +(content:"北京" AND "上海") OR +(title:"北京" AND "上海") 意思为：只要 content 包含 北京与上海  或者  title 包含 北京与上海   都会返回结果；
         *                   其中 + 是必须存在， - 是必须不存在。 OR AND 都必须大写。
         */
        // void addQueryCondition(String QuerString)
    }

    @Test
    public void addQueryConditionBylucene() {
        /**
         * 自定义查询
         *
         * @param QuerString 为 全部luncene语法，不与其它条件函数共用
         * @param QuerString
         * @return(结果json)
         */
        // String addQueryConditionBylucene(String QuerString)
    }

    @Test
    public void facetDateBySecondFieldValueCount() {
        /**
         * 根据时间粒度统计 聚合数量
         * 类似统计每一天it字段下各个类型数据量
         *
         * @param TimeField   查询的时间字段
         * @param format      时间格式 例如：yyyy-MM-dd
         * @param interval    粒度 (1M代表每月，1d代表每日，1H代表每小时)
         * @param secondField 要聚合的字段s
         * @return
         * @Description: TODO(根据时间粒度统计)
         */
        // List<String[]> facetDateBySecondFieldValueCount(String TimeField, String format, String interval, String secondField)
    }

    /**
     * @param
     * @return
     * @Description: TODO(二次检索)
     */
    @Test
    public void quadraticSearch() {
        // 二次检索的实现参考万方数据的高级检索：http://wanfangdata.com.cn/searchResult/getAdvancedSearch.do?searchType=all#a_001
        // ---------------------------------------一次检索---------------------------------------
//        esSmallIndexSearch.addKeywordsQuery("title", "中国", FieldOccurs.MUST);
//        esSmallIndexSearch.setStart(0);
//        esSmallIndexSearch.setRow(10);
//        esSmallIndexSearch.execute(new String[]{"title"});
//        List<String[]> result = esSmallIndexSearch.getResults();
//        esSmallIndexSearch.outputResult(result);
//        esSmallIndexSearch.reset();

        // ---------------------------------------二次检索---------------------------------------
//        esSmallIndexSearch.addKeywordsQuery("title", "中国", FieldOccurs.MUST);
//        esSmallIndexSearch.addKeywordsQuery("title", "北京", FieldOccurs.MUST);
//        esSmallIndexSearch.setStart(0);
//        esSmallIndexSearch.setRow(1000);
//        esSmallIndexSearch.execute(new String[]{"title"});
//        List<String[]> result2 = esSmallIndexSearch.getResults();
//        esSmallIndexSearch.outputResult(result2);
//        esSmallIndexSearch.reset();

//        // ---------------------------------------二次检索---------------------------------------
//        esSmallIndexSearch.addKeywordsQuery("title", "中国", FieldOccurs.MUST);
//        esSmallIndexSearch.addKeywordsQuery("title", "北京", FieldOccurs.MUST);
//        esSmallIndexSearch.addKeywordsQuery("title", "海淀", FieldOccurs.MUST);
//        esSmallIndexSearch.setStart(0);
//        esSmallIndexSearch.setRow(1000);
//        esSmallIndexSearch.execute(new String[]{"title"});
//        List<String[]> result2 = esSmallIndexSearch.getResults();
//        esSmallIndexSearch.outputResult(result2);
//        esSmallIndexSearch.reset();

//        // ---------------------------------------一次检索---------------------------------------
//        String[] fields = new String[]{"title","content"};
//        esSmallIndexSearch.addKeywordsQuery(fields,"中国",KeywordsCombine.OR);
//        esSmallIndexSearch.setStart(0);
//        esSmallIndexSearch.setRow(100);
//        esSmallIndexSearch.execute(new String[]{"title","content"});
//        List<String[]> result2 = esSmallIndexSearch.getResults();
//        esSmallIndexSearch.outputResult(result2);
//        esSmallIndexSearch.reset();

//        // ---------------------------------------二次检索---------------------------------------
//        String[] fields = new String[]{"title","content"};
//        esSmallIndexSearch.addRangeTerms("pubtime","2019-06-13 01:01:01",FieldOccurs.MUST,RangeOccurs.GTE); // 大于某个时间
//        esSmallIndexSearch.addKeywordsQuery(fields,"中国",KeywordsCombine.OR);
//        esSmallIndexSearch.addKeywordsQuery(fields,"自动化所",KeywordsCombine.OR);
//        esSmallIndexSearch.setStart(0);
//        esSmallIndexSearch.setRow(100);
//        esSmallIndexSearch.execute(new String[]{"title","content"});
//        List<String[]> result2 = esSmallIndexSearch.getResults();
//        esSmallIndexSearch.outputResult(result2);
//        esSmallIndexSearch.reset();

        // ---------------------------------------二次检索---------------------------------------
        String[] fields = new String[]{"title", "content"};
        esSmallIndexSearch.addKeywordsQuery(fields, "中国", KeywordsCombine.OR);
        esSmallIndexSearch.addKeywordsQuery(fields, "华为5G", KeywordsCombine.OR);
        esSmallIndexSearch.setStart(0);
        esSmallIndexSearch.setRow(100);
        esSmallIndexSearch.execute(new String[]{"title", "content"});
        List<String[]> result2 = esSmallIndexSearch.getResults();
        esSmallIndexSearch.outputResult(result2);
        esSmallIndexSearch.reset();

    }


    @Test
    public void fuzzy() {
        // 模糊
        aircraftSearch.addQueryCondition("+aircraft:(*KL*)");
        aircraftSearch.addQueryCondition("+aircraft:(*bL*)");
        aircraftSearch.setStart(0);
        aircraftSearch.setRow(100);
        aircraftSearch.execute(new String[]{"flight_number", "aircraft"});
        List<String[]> result = aircraftSearch.getResults();
        aircraftSearch.outputResult(result);
        aircraftSearch.reset();
    }

    @Test
    public void addGeoDistance() {
        /**
         * @param locPointField:字段名-geo类型数据的字段名
         * @param lat:经度
         * @param lon:维度
         * @param distance:距离-传入NULL则默认单位是米
         * @param distanceUnit:指定距离单位(1km/1mi/)
         * @param occur:距离计算算法选择
         * @return
         * <p>
         * The full list of units is listed below:
         * Mile-mi or miles
         * Yard - yd or yards
         * Feet - ft or feet
         * Inch - in or inch
         * Kilometer - km or kilometers
         * Meter - m or meters
         * Centimeter - cm or centimeters
         * Millimeter - mm or millimeters
         */
        // 距离当前飞机位置2000km以内的飞机
        aircraftSearch.addGeoDistance("location_point", 55.47981, 13.67931, 2000, DistanceUnit.KILOMETER, GeoDistanceOccurs.PLANE);
        aircraftSearch.addPrimitiveTermFilter("flight_number", "SU2658", FieldOccurs.MUST_NOT);
        aircraftSearch.setStart(0);
        aircraftSearch.setRow(5);
        aircraftSearch.execute(new String[]{"flight_number"});
        List<String[]> result = aircraftSearch.getResults();
        aircraftSearch.outputResult(result);
        aircraftSearch.reset();
    }

    @Test
    public void addSortFieldGeo() {

        aircraftSearch.addRangeTerms("pubtime", "2019-06-01 00:00:00", "2019-06-24 00:00:00");

        // 距离当前飞机位置200km以内的飞机
        aircraftSearch.addGeoDistance("location_point", 55.47981, 13.67931, 20, DistanceUnit.KILOMETER, GeoDistanceOccurs.SLOPPY_ARC);

        aircraftSearch.addPrimitiveTermFilter("flight_number", "SU2658", FieldOccurs.MUST_NOT);
        /**
         * @param locPointField:字段名-geo类型数据的字段名
         * @param lat:维度
         * @param lon:经度
         * @param distanceUnit:指定距离单位(km/mi/...) - 将距离以...为单位写入到每个返回结果的 sort 键中
         * @param occur:距离计算算法选择
         * @return
         * @Description: TODO(geo - 按照距离排序的接口 - 扩展addSortField接口)
         */
        aircraftSearch.addSortField("location_point", 55.47981, 13.67931, DistanceUnit.KILOMETER, GeoDistanceOccurs.SLOPPY_ARC, SortOrder.DESC);
        aircraftSearch.setStart(0);
        aircraftSearch.setRow(200);
        /**
         * sort字段是es内置的距离排序字段 - 索引在建设mapping时字段最好不要和es内置字段重复
         * **/
        aircraftSearch.execute(new String[]{"flight_number", "location_point", "sort"});
        List<String[]> result = aircraftSearch.getResults();
        aircraftSearch.outputResult(result);
        aircraftSearch.reset();
    }

    @Test
    public void addGeoBoundingBox() {

        /**
         * @param locPointField:字段名-geo类型数据的字段名
         * @param firstBoundPoint:设置矩形框第一个点
         * @param nextBoundPoint:设置矩形框第二个点
         * @param occurs:必须满足/必须不满足
         * @return
         * @Description: TODO(geo - 盒模型过滤器 - 指定矩形框的两个对角)
         */
//        aircraftSearch.addGeoBoundingBox("location_point",
//                new BoundPoint(55.47981, 13.67931, GeoBoundOccurs.TOP_LEFT),
//                new BoundPoint(45.47981, 15.67931, GeoBoundOccurs.BOTTOM_RIGHT), FieldOccurs.MUST);
        aircraftSearch.addGeoBoundingBox("location_point",
                new BoundPoint(55.47981, 13.67931, GeoBoundOccurs.TOP_LEFT),
                new BoundPoint(45.47981, 15.67931, GeoBoundOccurs.BOTTOM_RIGHT), FieldOccurs.MUST_NOT);

        aircraftSearch.setStart(0);
        aircraftSearch.setRow(200);
        /**
         * @param locPointField:字段名-geo类型数据的字段名
         * @param lat:维度
         * @param lon:经度
         * @param distanceUnit:指定距离单位(km/mi/...) - 将距离以...为单位写入到每个返回结果的 sort 键中
         * @param occur:距离计算算法选择
         * @return
         * @Description: TODO(geo - 按照距离排序的接口 - 扩展addSortField接口)
         */
        aircraftSearch.addSortField("location_point", 55.47981, 13.67931, DistanceUnit.KILOMETER, GeoDistanceOccurs.SLOPPY_ARC, SortOrder.DESC);
        /**
         * sort字段是es内置的距离排序字段 - 索引在建设mapping时字段最好不要和es内置字段重复
         * **/
        aircraftSearch.execute(new String[]{"flight_number", "location_point", "sort"});
        List<String[]> result = aircraftSearch.getResults();
        System.out.println("Total data:" + aircraftSearch.getTotal());
        aircraftSearch.outputResult(result);
        aircraftSearch.reset();
    }

    @Test
    public void addGeoBoundingMultiBox() {

        /**
         * @param locPointField:字段名-geo类型数据的字段名
         * @param List<BoundBox> boundBoxList:设多个矩形框
         * @param occurs:必须满足/必须不满足/OR条件
         * @return
         * @Description: TODO(geo - 盒模型过滤器 - 指定多个矩形框)
         * <p>【百度经纬度拾取器】http://api.map.baidu.com/lbsapi/getpoint/index.html</>
         */
        List<BoundBox> boundBoxList = new ArrayList<>();
        // 设置多个不相交的矩形，测试MUST，结果为空则正常 （测试SHOULD有一个满足即可）（测试MUST_NOT查询此区域列表以外的点）
        BoundBox boundBox1 = new BoundBox();
        boundBox1.setFirstBoundPoint(new BoundPoint(41.114763, 113.855668, GeoBoundOccurs.TOP_LEFT));
        boundBox1.setNextBoundPoint(new BoundPoint(37.103182, 120.809846, GeoBoundOccurs.BOTTOM_RIGHT));
        boundBoxList.add(boundBox1);

        BoundBox boundBox2 = new BoundBox();
        boundBox2.setFirstBoundPoint(new BoundPoint(31.897413, 86.922026, GeoBoundOccurs.TOP_LEFT));
        boundBox2.setNextBoundPoint(new BoundPoint(19.672306, 111.206457, GeoBoundOccurs.BOTTOM_RIGHT));
        boundBoxList.add(boundBox2);

        aircraftSearch.addGeoBoundingMultiBox("location_point", boundBoxList, FieldOccurs.SHOULD);
        aircraftSearch.setStart(0);
        aircraftSearch.setRow(200);
        aircraftSearch.execute(new String[]{"flight_number", "location_point", "sort"});
        List<String[]> result = aircraftSearch.getResults();
        System.out.println("Total data:" + aircraftSearch.getTotal());
        aircraftSearch.outputResult(result);
        aircraftSearch.reset();
    }

    @Test
    public void statisticsOriginDestination() {
        FileUtil.saveFile("data/aircraft/targetLoc.txt", "LOCATION" + "  " + "COUNT\n", true);
        String[] loc = new String[]{"origin", "destination"};

        int count = 0;
        for (int j = 0; j < loc.length; j++) {
            String location = loc[j];
            List<String[]> resultOrigin = aircraftSearch.facetCountQueryOrderByCount(location, 0, SortOrder.DESC);

            // 写入文件
            // OUTPUT
            int i = 0;
            for (String[] infos : resultOrigin) {
                count++;
                FileUtil.saveFile("data/aircraft/targetLoc.txt", infos[0] + "  " + infos[1] + "\n", true);
            }

            aircraftSearch.reset();
        }
        System.out.println("Count:" + count);
    }

    @Test
    public void facetDateAggsTophitsTest() {
        aircraftSearch = new EsIndexSearch(ipPort, "aircraft_info", "graph");

        /**
         * 根据时间粒度，根据排序 筛选返回每个时间段内前N条数据
         *
         * @param TimeField    查询的时间字段
         * @param format       时间格式 例如：yyyy-MM-dd
         * @param interval     粒度 (1M代表每月，1d代表每日，1H代表每小时)
         * @param startTime    起始时间
         * @param endTime      结束时间
         * @param sortField    时间段内数据排序字段，为null时将以相关性自动排序
         * @param sortOrder    时间段内数据排序方式
         * @param returnFields 时间段内数据返回字段，为null时字段全部返回
         * @param size         时间段内返回数据条数
         * @return
         * */
        // 区间内时间排序拿出第一条数据排序方式（降序或者升序），不同的排序方式拿取数据的规则不一样
        String startTime = "2019-07-19 18:00:30";
        String stopTime = "2019-07-19 18:10:00";
        aircraftSearch.addRangeTerms("pubtime", startTime, stopTime);
        List<String[]> result = aircraftSearch.facetDateAggsTophits("pubtime", "yyyy-MM-dd HH:mm:ss", "30s", startTime, stopTime,
                "pubtime", SortOrder.ASC, new String[]{"site", "aircraft", "pubtime"}, 1);

        aircraftSearch.outputResult(result);

        // DESC
        // 序号 时间 数据量统计值 返回的字段数据
//        8878:2019-07-22 18:55:30	229	[{"pubtime":"2019-07-22 18:55:59","site":"adsbexchange.com","aircraft":"VT-ALJ"}]
//        8879:2019-07-22 18:56:00	258	[{"pubtime":"2019-07-22 18:56:29","site":"adsbexchange.com","aircraft":"HP-1536CMP"}]
//        8880:2019-07-22 18:56:30	206	[{"pubtime":"2019-07-22 18:56:59","site":"radarbox24.com","aircraft":"TC-NBL"}]
//        8881:2019-07-22 18:57:00	225	[{"pubtime":"2019-07-22 18:57:29","site":"adsbexchange.com","aircraft":"N339JB"}]
//        8882:2019-07-22 18:57:30	276	[{"pubtime":"2019-07-22 18:57:59","site":"radarbox24.com","aircraft":"PH-EZX"}]

        // ASC
//        0:2019-07-19 18:00:30	15	[{"pubtime":"2019-07-19 18:00:30","site":"radarbox24.com","aircraft":"N647UA"}]
//        1:2019-07-19 18:01:00	27	[{"pubtime":"2019-07-19 18:01:00","site":"radarbox24.com","aircraft":"VT-IHT"}]
//        2:2019-07-19 18:01:30	22	[{"pubtime":"2019-07-19 18:01:36","site":"radarbox24.com","aircraft":""}]
//        3:2019-07-19 18:02:00	10	[{"pubtime":"2019-07-19 18:02:04","site":"radarbox24.com","aircraft":""}]
//        4:2019-07-19 18:02:30	11	[{"pubtime":"2019-07-19 18:02:31","site":"radarbox24.com","aircraft":"HL7717"}]
//        5:2019-07-19 18:03:00	6	[{"pubtime":"2019-07-19 18:03:06","site":"radarbox24.com","aircraft":"9V-SKS"}]
//        6:2019-07-19 18:03:30	17	[{"pubtime":"2019-07-19 18:03:30","site":"flightradar24.com","aircraft":"N949FD"}]
    }

    /**
     * @param
     * @return
     * @Description: TODO(飞机航线加载 - 加载一架航班的实时航线)
     */
    @Test
    public void facetDateAggsTophitsFlightCourse() {

        aircraftSearch = new EsIndexSearch(ipPort, "aircraft_info", "graph");

        // 设置唯一航班
        aircraftSearch.addPrimitiveTermFilter("aircraft", "B-KPI", FieldOccurs.MUST);
        aircraftSearch.addPrimitiveTermFilter("mode_s", "780220", FieldOccurs.MUST);
        aircraftSearch.addPrimitiveTermFilter("site", "adsbexchange.com", FieldOccurs.MUST);

        // 设置时间范围（10小时内的航线数据）
        String startTime = DateUtil.dateSub(DateUtil.getCurrentIndexTime(), 3600_000 * 12);
        String stopTime = DateUtil.getCurrentIndexTime();
        aircraftSearch.addRangeTerms("pubtime", startTime, stopTime);

        // 聚合航线 30s 5m 1h 1d （五分钟范围聚合）
        List<String[]> result = aircraftSearch.facetDateAggsTophits("pubtime", "yyyy-MM-dd HH:mm:ss", "1m", startTime, stopTime,
                "pubtime", SortOrder.DESC, new String[]{"flight_number", "pubtime", "latitude", "longitude", "altitude",
                        "speed", "aircraft", "origin", "destination", "airline", "insert_time", "site", "callsign", "type", "mode_s",
                        "country", "heading", "op", "sqk", "manufacturer", "random_code", "is_am", "source", "location_point"
                }, 1);
        //"site", "aircraft", "mode_s", "location_point", "destination", "origin"

        // 添加出点（没有出发点则初始化为NULL即可）
        BoundPoint startPoint = null;
        // 获取出发点经纬度构造点对象
        startPoint = new BoundPoint(33.94250, -118.40800);

        aircraftSearch.outputResult(result);

        // 没有获取到出发点
        if (startPoint == null) {

            // 分析航段数据
            /**
             * @param result:航线数据聚合结果
             * @param countBlankThreshold:航段分隔时允许中间间隔多少个点
             * @return
             * @Description: TODO(分析航线航段数据)
             */
            List<List<Map<String, Object>>> mapList = AggsAnalyzer.flightCourseSegment(result, 20);

            JSONArray allSegment = JSONArray.parseArray(JSON.toJSONString(mapList));
            System.out.println("所有航段：" + allSegment.toJSONString());
            System.out.println("最近的一个航段：" + allSegment.getJSONArray(allSegment.size() - 1).toJSONString());

            aircraftSearch.outputResult(result);

        } else {

            // 分析航段数据
            /**
             * @param result:航线数据聚合结果
             * @param startPoint:出发地
             * @return
             * @Description: TODO(分析航线航段数据)
             */
            // 计算较快
//            List<Map<String, Object>> mapList = AggsAnalyzer.flightCourseSegment(result, startPoint);

            // 计算较慢
            List<Map<String, Object>> mapList = AggsAnalyzer.flightCourseSegmentByAirport(result, startPoint, 20);

            JSONArray segment = JSONArray.parseArray(JSON.toJSONString(mapList));
            System.out.println("包含出发点的航段：" + segment.toJSONString());
        }
        aircraftSearch.reset();

    }

    /**
     * @param
     * @return
     * @Description: TODO(飞机航线加载 - 根据航班的当前状态加载航线)
     */
    @Test
    public void facetDateAggsTophitsFlightHistoryCourse() {

        aircraftSearch = new EsIndexSearch(ipPort, "aircraft_info", "graph");

        // 设置唯一航班
        aircraftSearch.addPrimitiveTermFilter("aircraft", "G-MGPS", FieldOccurs.MUST);
        aircraftSearch.addPrimitiveTermFilter("mode_s", "407734", FieldOccurs.MUST);
        aircraftSearch.addPrimitiveTermFilter("site", "adsbexchange.com", FieldOccurs.MUST);

        // 设置时间范围（10小时内的航线数据）（当前航班状态的发布时间前后十个小时）
        String pubtime = "2019-08-17 15:22:32";
        String startTime = DateUtil.dateSub(pubtime, 3600_000 * 10);
        String stopTime = DateUtil.datePlus(pubtime, 3600_000 * 10);

        aircraftSearch.addRangeTerms("pubtime", startTime, stopTime);

        // 聚合航线 30s 5m 1h 1d （五分钟范围聚合）
        List<String[]> result = aircraftSearch.facetDateAggsTophits("pubtime", "yyyy-MM-dd HH:mm:ss", "1m", startTime, stopTime,
                "pubtime", SortOrder.DESC, new String[]{"site", "aircraft", "mode_s", "location_point", "destination", "origin", "pubtime"}, 1);

        aircraftSearch.outputResult(result);

        // 添加出发点（没有出发点则初始化为NULL即可）
        // origin:COU
//        BoundPoint startPoint = new BoundPoint(33.94250, -118.40800);
        BoundPoint startPoint = null;

        // 添加目的地点
        // destination:MKC
//        BoundPoint endPoint = new BoundPoint(22.30890, 113.91500);
        BoundPoint endPoint = null;

        // 当前飞机的位置
        //
        BoundPoint currentFlightLoc = new BoundPoint(52.35926, -0.11818);

        List<Map<String, Object>> mapList = AggsAnalyzer.flightHistoryCourseSegment(startPoint, endPoint, currentFlightLoc, result, 40);
        JSONArray segment = JSONArray.parseArray(JSON.toJSONString(mapList));
        System.out.println("历史航段：" + segment.toJSONString());
        aircraftSearch.reset();
    }

    @Test
    public void aircraftSearcherTest() {
        aircraftSearch = new EsIndexSearch(ipPort, "aircraft_info_latest_status", "graph");
        aircraftSearch.addRangeTerms("pubtime", "2019-07-27 11:43:20", "2019-07-27 12:03:20");
//        aircraftSearch.addPrimitiveTermFilter("destination", new String[]{},FieldOccurs.MUST);
        aircraftSearch.addQueryCondition("+destination:(*NAY*sdsada*)");
//        aircraftSearch.addQueryCondition("+destination:(*P*)");

        aircraftSearch.execute(new String[]{"destination", "site", "pubtime"});
        aircraftSearch.outputResult(aircraftSearch.getResults());
        System.out.println(aircraftSearch.getTotal());
    }

    @Test
    public void aircraftSearcherTest2() {
        // 排除某些航班
        aircraftSearch = new EsIndexSearch(ipPort, "aircraft_info_latest_status", "graph");
        aircraftSearch.addRangeTerms("pubtime", "2019-07-23 11:23:14", "2019-07-23 11:23:16");
        // "-(mode_s:\"71C237\" AND aircraft:\"HL8228\")"
        aircraftSearch.addQueryCondition("-(mode_s:\"71C237\" AND aircraft:\"HL8228\")");
        aircraftSearch.execute(new String[]{"mode_s", "aircraft"});
        aircraftSearch.outputResult(aircraftSearch.getResults());
    }

    /**
     * @param
     * @return
     * @Description: TODO(统计事件的评论量)
     */
    @Test
    public void statsEventComment() {
        EsIndexSearch eventSearch = new EsIndexSearch(ipPort, "event_mblog_info_ref_event", "event_data");
        int eid = 705;
        String commentField = "commtcount";
        Map<String, Double> map = eventSearch.facetStatsCount(String.valueOf(eid), commentField);
        System.out.println("总评论量：" + map.get("sum"));
        System.out.println("平均评论量：" + map.get("avg"));
        System.out.println("最大评论量：" + map.get("max"));
        System.out.println("最小评论量：" + map.get("min"));
        System.out.println("总帖数：" + map.get("count"));
    }

    /**
     * @param
     * @return
     * @Description: TODO(嵌套聚集获得结果分组排序过滤)
     */
    @Test
    public void facetStatsTermsAggsTophits() {
        esSmallIndexSearch = new EsIndexSearch(ipPort, "blog_small", "monitor_caiji_small");
        /**
         * @param field:用来统计的字段
         * @param sortStatsField:父聚集桶的排序方式
         * @param _source:需要返回的字段
         * @param size:父聚聚集桶内返回的数据量（例如返回前十个统计量的站点等）(-1返回全部)
         * @param childSortTimeField:子聚集桶内时间字段
         * @param childSortTimeOrder:子聚集桶内时间字段的排序方式
         * @param childSize:子聚集桶内返回的数据量
         * @return
         * @Description: TODO(嵌套聚集获得结果分组排序过滤)(查所有订阅卫星的最新一条数据)
         */
        List<String[]> result = esSmallIndexSearch.facetStatsTermsAggsTophits("site", SortOrder.DESC, new String[]{"site", "pubtime", "title"}, -1,
                "pubtime", SortOrder.DESC, 1);
        esSmallIndexSearch.outputResult(result);
        esSmallIndexSearch.reset();
        /**
         * 返回每个站点最新的一条数据：
         * 0:新浪微博	19067756	[{"pubtime":"2019-08-03 17:19:02","site":"新浪微博"}]
         * 1:微信	2711996	[{"pubtime":"2019-08-03 17:11:01","site":"微信","title":"中国传统文化中的四大思想智慧"}]
         * 2:百度贴吧	2335119	[{"pubtime":"2019-08-03 17:08:00","site":"百度贴吧","title":"请教大佬"}]
         * 3:百度	346460	[{"pubtime":"2019-08-03 20:18:00","site":"百度","title":"大连一方全取三分，故治大国若烹小鲜"}]
         * 4:今日头条	289304	[{"pubtime":"2019-08-03 17:15:57","site":"今日头条","title":"大众途岳优惠幅度大 你该怎么选？"}]
         * 5:知乎	206665	[{"pubtime":"2019-08-03 23:59:00","site":"知乎","title":"好律师网：保险纠纷解决的途径有哪些？"}]
         * **/
    }

    /**
     * @param
     * @return
     * @Description: TODO(嵌套聚集获得结果分组排序过滤)
     */
    @Test
    public void facetStatsTermsAggsTophitsTest() {
        statelliteSearch = new EsIndexSearch(ipPort, "statellite_info", "graph");
        /**
         * @param field:用来统计的字段
         * @param sortStatsField:父聚集桶的排序方式
         * @param _source:需要返回的字段
         * @param size:父聚聚集桶内返回的数据量（例如返回前十个统计量的站点等）(-1返回全部)
         * @param childSortTimeField:子聚集桶内时间字段
         * @param childSortTimeOrder:子聚集桶内时间字段的排序方式
         * @param childSize:子聚集桶内返回的数据量
         * @return
         * @Description: TODO(嵌套聚集获得结果分组排序过滤)(查所有订阅卫星的最新一条数据)
         */
        statelliteSearch.addRangeTerms("pubtime", "2019-08-07 01:01:00", "2019-08-07 11:12:00");
        List<String[]> result = statelliteSearch.facetStatsTermsAggsTophits("NORADID", SortOrder.DESC, new String[]{"site", "pubtime", "title", "NORADID"}, -1,
                "pubtime", SortOrder.DESC, 1);
        statelliteSearch.outputResult(result);
        statelliteSearch.reset();
    }


    @Test
    public void addGeoShape() {
        // 设置一些矩形框
        // 北京
        BoundBox boundBox1 = new BoundBox(new BoundPoint(41.114763, 113.855668, GeoBoundOccurs.TOP_LEFT),
                new BoundPoint(37.103182, 120.809846, GeoBoundOccurs.BOTTOM_RIGHT));
        // 沈阳
        BoundBox boundBox2 = new BoundBox(new BoundPoint(31.897413, 86.922026, GeoBoundOccurs.TOP_LEFT),
                new BoundPoint(19.672306, 111.206457, GeoBoundOccurs.BOTTOM_RIGHT));
        // 京津冀地区
        BoundBox boundBox3 = new BoundBox(new BoundPoint(31.897413, 86.922026, GeoBoundOccurs.TOP_LEFT),
                new BoundPoint(19.672306, 111.206457, GeoBoundOccurs.BOTTOM_RIGHT));
        BoundBox boundBox4 = new BoundBox(new BoundPoint(31.897413, 86.922026, GeoBoundOccurs.TOP_LEFT),
                new BoundPoint(19.672306, 111.206457, GeoBoundOccurs.BOTTOM_RIGHT));

        // 设置一些圆形
        Circle circle1 = new Circle();
        circle1.setDistance(10, DistanceUnit.METER);
        circle1.setCentre(123.1231, 2312.23);

        /**
         * @param locPointField:GEO类型的字段名
         * @param occur:距离计算算法选择
         * @param _conditions:多形状条件组合
         * @return
         * @Description: TODO(多形状组合查询)-支持(MUST/MUST_NOT/SHOULD)任意组合
         */
        // 设置多个形状，（测试MUST一定在这个形状中） （测试SHOULD在一个形状中即可）（测试MUST_NOT不在这些形状中）
        // 如下测试含义：一定在circle1中，并且一定不在boundBox1与boundBox2中，并且在boundBox3或者boundBox4都可以
        aircraftSearch.addGeoShape("location_point", GeoDistanceOccurs.PLANE,
                Must.init().add(circle1),
                MustNot.init().addMulti(boundBox1, boundBox2),
                Should.init().addMulti(boundBox3, boundBox4));

        aircraftSearch.setStart(0);
        aircraftSearch.setRow(100);
        aircraftSearch.execute(new String[]{"mode_s"});
        aircraftSearch.outputResult(aircraftSearch.getResults());
    }

    @Test
    public void addGeoShapeTest2() {
        // 设置一些矩形框
        // 北京
        BoundBox beijing = new BoundBox(new BoundPoint(40.333563, 115.919615, GeoBoundOccurs.TOP_LEFT),
                new BoundPoint(39.782209, 116.83948, GeoBoundOccurs.BOTTOM_RIGHT));
        // 沈阳
        BoundBox shenyang = new BoundBox(new BoundPoint(42.302922, 122.248285, GeoBoundOccurs.TOP_LEFT),
                new BoundPoint(41.671531, 124.106412, GeoBoundOccurs.BOTTOM_RIGHT));
        // 保定
        BoundBox baoding = new BoundBox(new BoundPoint(38.997033, 115.27341, GeoBoundOccurs.TOP_LEFT),
                new BoundPoint(38.799349, 115.61376, GeoBoundOccurs.BOTTOM_RIGHT));
        // 设置一些圆形
        // 京津冀地区（北京为中心的300KM内的区域）
        Circle jjjCircle = new Circle();
        jjjCircle.setDistance(300, DistanceUnit.KILOMETER);
        jjjCircle.setCentre(40.008949, 116.416342);

        // 以天津为中心附近50KM的区域
        Circle tianjinCircle = new Circle();
        tianjinCircle.setDistance(50, DistanceUnit.KILOMETER);
        tianjinCircle.setCentre(39.000622, 117.218924);

        /**
         * @param locPointField:GEO类型的字段名
         * @param occur:距离计算算法选择
         * @param... conditions:（泛型参数）多形状条件组合
         * @return
         * @Description: TODO(addGeoShape多形状组合查询)-支持(MUST/MUST_NOT/SHOULD)任意组合-(目前支持两个形状圆形和矩形)
         */
        // 北京与沈阳无交汇测试MUST查询结果为空
//        aircraftSearch.addGeoShape("location_point", GeoDistanceOccurs.PLANE, Must.init().addMulti(beijing,shenyang));

        // 北京与沈阳无交汇测试OR-SHOULD条件（任意一个区域满足即可）
//        aircraftSearch.addGeoShape("location_point", GeoDistanceOccurs.PLANE, Should.init().addMulti(beijing,shenyang));

        // 一定在京津冀地区但是不在北京地区
//        aircraftSearch.addGeoShape("location_point", GeoDistanceOccurs.PLANE, Must.init().add(jjjCircle), MustNot.init().add(beijing));

        // 一定在京津冀地区但是不在北京地区，另外在保定或者天津
//        aircraftSearch.addGeoShape("location_point", GeoDistanceOccurs.PLANE, Must.init().add(jjjCircle), MustNot.init().add(beijing),
//                Should.init().addMulti(baoding, shenyang));

        aircraftSearch.addGeoShape("location_point", GeoDistanceOccurs.PLANE, Must.init().add(beijing), MustNot.init().add(beijing),
                Should.init().addMulti(baoding, shenyang));

//        aircraftSearch.addGeoShape("location_point", GeoDistanceOccurs.PLANE, Must.init().add(beijing), MustNot.init().add(beijing),
//                Should.init().addMulti(baoding, shenyang));

        aircraftSearch.setStart(0);
        aircraftSearch.setRow(100);
        aircraftSearch.execute(new String[]{"mode_s", "location_point", "site", "pubtime"});
        aircraftSearch.outputResult(aircraftSearch.getResults());
        aircraftSearch.reset();
    }

    @Test
    public void zdrFacetTwoCountQueryOrderByCount() {
        EsIndexSearch esc = new EsIndexSearch(ipPort, "user_mblog_info_ref_zdr,user_facebook_info_ref_zdr,user_wechat_info_ref_zdr,user_forum_threads_ref_zdr,user_blog_ref_zdr,user_instagram_thread_ref_zdr,user_youtube_info_ref_zdr,user_twitter_info_ref_zdr,user_linkedin_thread_ref_zdr" +
                ",blog_comment_zdr,mblog_comment_zdr,forum_replys_zdr,facebook_comment_zdr,wechat_comment_zdr,instagram_comment_zdr,twitter_comment_zdr,youtube_comment_zdr", "zdr_data,zdr_caiji");
//        EsIndexSearch esc = SearchIndexer.getSearchClient("user_mblog_info_ref_zdr,user_facebook_info_ref_zdr,user_wechat_info_ref_zdr,user_forum_threads_ref_zdr,user_blog_ref_zdr,user_instagram_thread_ref_zdr,user_youtube_info_ref_zdr,user_twitter_info_ref_zdr,user_linkedin_thread_ref_zdr", "zdr_data");
        esc.addRangeTerms("pubtime", "2019-08-06 00:00:00", "2019-08-06 15:03:38", FieldOccurs.MUST);
        //List<String[]> resList = esc.facetCountQueryOrderByCount("blogger_id", 0, SortOrder.DESC);
        // 二次分组统计-多个child字段
        List<String[]> resList = esc.facetTwoCountQueryOrderByCount("it", new String[]{"blogger_id", "user_md5"}, 0, true, SortOrder.DESC);
        esc.outputResult(resList);
        System.out.println(esc.queryJson);
    }

    @Test
    public void searchByIdRange() {
        EsIndexSearch.debug = true;
        EsIndexSearch esc = new EsIndexSearch(ipPort, "event_wechat_info_ref_monitor", "monitor_data");
        esc.addPrimitiveTermFilter("eid", "630", FieldOccurs.MUST);
        esc.addRangeTerms("id", "7331762", "7331770");
        esc.execute(new String[]{"id", "eid"});
        List<String[]> result = esc.getResults();
        esc.outputResult(result);
    }

    @Test
    public void searchByIdRange2() {
        EsIndexSearch.debug = true;
        EsIndexSearch esc = new EsIndexSearch(ipPort, "event_wechat_info_ref_monitor", "monitor_data");
        esc.addPrimitiveTermFilter("eid", "630", FieldOccurs.MUST);
        esc.addRangeTerms("id", "7331762", FieldOccurs.MUST, RangeOccurs.GTE);
        esc.addSortField("id", SortOrder.ASC);
        esc.setRow(10);
        esc.execute(new String[]{"id", "eid"});
        List<String[]> result = esc.getResults();
        esc.outputResult(result);
    }

    @Test
    public void searchByIdRange3() {
        EsIndexSearch.debug = true;
        EsIndexSearch esc = new EsIndexSearch(ipPort, "event_mblog_info_ref_monitor", "monitor_data");
        esc.addPrimitiveTermFilter("eid", "630", FieldOccurs.MUST);
        esc.addRangeTerms("id", "7331762", FieldOccurs.MUST, RangeOccurs.GTE);
        esc.addSortField("id", SortOrder.ASC);
        esc.setRow(10);
        esc.execute(new String[]{"id", "eid"});
        List<String[]> result = esc.getResults();
        esc.outputResult(result);
    }

    @Test
    public void searchByIdRange4() {
        // _id没有映射的话不支持范围查询
        EsIndexSearch.debug = true;
        long auto_id = -1;
        EsIndexSearch esIndexSearch = new EsIndexSearch(ipPort, "event_mblog_info_ref_monitor", "monitor_data");
        esIndexSearch.addPrimitiveTermFilter("eid", String.valueOf(630), FieldOccurs.MUST);
        esIndexSearch.addRangeTerms("_id", auto_id != -1 ? String.valueOf(auto_id) : "0", FieldOccurs.MUST, RangeOccurs.GTE);
        esIndexSearch.addSortField("_id", SortOrder.ASC);
        esIndexSearch.setRow(10);
        esIndexSearch.execute(new String[]{"blogger_id", "blogger", "eid", "id"});
        List<String[]> result = esIndexSearch.getResults();
        // OUTPUT
        esIndexSearch.outputResult(result);
    }

}



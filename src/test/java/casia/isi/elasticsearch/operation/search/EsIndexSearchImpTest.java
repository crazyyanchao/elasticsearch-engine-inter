package casia.isi.elasticsearch.operation.search;

import casia.isi.elasticsearch.common.FieldOccurs;
import casia.isi.elasticsearch.common.RangeOccurs;
import casia.isi.elasticsearch.common.SortOrder;
import casia.isi.elasticsearch.util.DateUtil;
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
 * @author YanchaoMa yanchaoma@foxmail.com
 * @PACKAGE_NAME: casia.isi.elasticsearch.operation.search
 * @Description: TODO(索引检索接口测试)
 * @date 2019/5/23 17:32
 */
public class EsIndexSearchImpTest {

    private static EsIndexSearch esSmallIndexSearch;

    private static EsIndexSearch esAllIndexSearch;

    private static EsIndexSearch esWarningIndexSearch;

    private String ipPort = "192.168.12.109:9210";
//    private String ipPort = "localhost:9200";

    private static HashMap<String, String> itMap = new HashMap<>();

    // 预警：
    // http://192.168.12.109:9210/event_news_ref_monitor,event_blog_ref_monitor,event_threads_ref_monitor,
    // event_mblog_ref_monitor,event_video_ref_monitor,event_weichat_ref_monitor,event_appdata_ref_monitor/monitor_data/_search

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


        String smallIndexName = "news_small,blog_small,forum_threads_small,mblog_info_small,video_brief_small," +
                "wechat_message_xigua_small,appdata_small,newspaper_info_small";
        esSmallIndexSearch = new EsIndexSearch(ipPort, smallIndexName, "monitor_caiji_small");

        String allIndexSearch = "news_all,blog_all,forum_threads_all,mblog_info_all,video_brief_all," +
                "wechat_message_xigua_all,appdata_all,newspaper_info_all";
        esAllIndexSearch = new EsIndexSearch(ipPort, allIndexSearch, "monitor_caiji_all");

        String waring = "event_news_ref_monitor,event_blog_ref_monitor,event_threads_ref_monitor,event_mblog_ref_monitor," +
                "event_video_ref_monitor,event_weichat_ref_monitor,event_appdata_ref_monitor";
        esWarningIndexSearch = new EsIndexSearch(ipPort, waring, "monitor_data");

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
        esSmallIndexSearch.addRangeTerms("pubtime", "2019-05-26 09:56:04", "2019-06-20 09:56:04");

//        List<String[]> result =  esSmallIndexSearch.facetDate("pubtimeAll","yyyy-MM-dd","1d");
//        List<String[]> result =  esSmallIndexSearch.facetDate("pubtimeAll","HH:mm:ss","10h");
        esSmallIndexSearch.addPrimitiveTermFilter("area_list", new String[]{"长春"}, FieldOccurs.MUST);
//        esSmallIndexSearch.addPhraseQuery("area_list", "长春", FieldOccurs.MUST);
//        esSmallIndexSearch.addPrimitiveTermQuery("area_list", new String[]{"长春","北京"}, FieldOccurs.MUST);

        esSmallIndexSearch.execute(new String[]{"area_list"});
        List<String[]> result = esSmallIndexSearch.getResults();
        esSmallIndexSearch.outputResult(result);
    }

    @Test
    public void facetCountArrayTypeTermsQuery() {
        esSmallIndexSearch.addRangeTerms("pubtime", "2019-06-04 00:00:00", "2019-06-04 14:45:00");

//        List<String[]> result =esSmallIndexSearch.facetCountQueryOrderByCount("area_list",10, SortOrder.DESC);
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
        esSmallIndexSearch.addRangeTerms("pubtime", "2019-05-21 18:39:54", "2019-05-28 18:39:54");

        esSmallIndexSearch.addArrayTypeTermsQuery("area_list", new String[]{"吉林"}, FieldOccurs.MUST);
        esSmallIndexSearch.addQueryCondition("+((title:\"吉林\") OR (content:\"吉林\"))");

        esSmallIndexSearch.execute(new String[]{"area_list"});
        List<String[]> result = esSmallIndexSearch.getResults();
        esSmallIndexSearch.outputResult(result);
    }

    @Test
    public void addSortField() {
        esSmallIndexSearch.addRangeTerms("pubtime", "2019-05-21 18:39:54", "2019-05-28 18:39:54");

        esSmallIndexSearch.addPrimitiveTermQuery("content", "中国品牌更懂中国人", FieldOccurs.MUST);

        // 相关性评分搜索
        esSmallIndexSearch.addSortField("_score", SortOrder.DESC);

        esSmallIndexSearch.execute(new String[]{"pubtime", "content"});
        List<String[]> result = esSmallIndexSearch.getResults();
        esSmallIndexSearch.outputResult(result);
    }

    @Test
    public void deleteDataByShellTest() {
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

        // it数据类型：新闻 a; 博客 b; 论坛 c; 微博 d; 视频 e; qq群 f; mblog_userinfo g;微信 h;移动app i

        /**
         * 二次聚合
         * **/
        List<String[]> result = esSmallIndexSearch.facetTwoCountQueryOrderByCount("it", "id", 100, SortOrder.DESC);

//        esSmallIndexSearch.facetDateAggsTophits()

        System.out.println(esSmallIndexSearch.outputQueryJson());
//        esSmallIndexSearch.outputResult(result);
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
        esSmallIndexSearch.addRangeTerms("pubtime", "2018-05-31 17:20:12", DateUtil.millToTimeStr(System.currentTimeMillis()));
        List<String[]> result = esSmallIndexSearch.facetMultipleCountQueryOrderByCount("it", new String[]{"id"}, 100);
        esSmallIndexSearch.outputResult(result);
    }

    @Test
    public void facetDateByCount() {
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
        esSmallIndexSearch.addRangeTerms("pubtime", "2018-05-31 17:20:12", DateUtil.millToTimeStr(System.currentTimeMillis()));
        List<String[]> result = esSmallIndexSearch.facetDateByCount("pubtime", "yyyy-MM-dd", "1d", new String[]{"it"});
        esSmallIndexSearch.outputResult(result);
    }

    @Test
    public void dateHistogramCount() {

        /**
         * 根据时间粒度对某字段的各项进行分组统计(例如：按天统计it各个数据量)
         * **/
//        esSmallIndexSearch.addRangeTerms("pubtime", "2019-05-31 17:20:12", DateUtil.millToTimeStr(System.currentTimeMillis()));
        esSmallIndexSearch.addRangeTerms("pubtime", "2019-05-27 17:20:12", DateUtil.millToTimeStr(System.currentTimeMillis()));


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
//        List<String[]> result = esSmallIndexSearch.facetDateBySecondFieldValueCount("pubtime", "yyyy-MM-dd", "1d", "it");
        List<String[]> result = esSmallIndexSearch.facetDateBySecondFieldValueCount("pubtime", "yyyy-MM-dd", "1d", "it");

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

}




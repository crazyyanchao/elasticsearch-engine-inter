package casia.isi.elasticsearch.operation.delete;

import casia.isi.elasticsearch.common.FieldOccurs;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;

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
 * @PACKAGE_NAME: casia.isi.elasticsearch.operation.delete
 * @Description: TODO(删除接口测试)
 * @date 2019/5/29 14:16
 */
public class EsIndexDeleteTest {

    private static EsIndexDelete esSmallIndexDelete;

    private static EsIndexDelete esAllIndexDelete;

    private final static String ipPort = "localhost:9200";


    @Before
    public void searchObject() {
        PropertyConfigurator.configureAndWatch("config/log4j.properties");

        String smallIndexName = "news_small,blog_small,forum_threads_small,mblog_info_small,video_brief_small," +
                "wechat_message_xigua_small,appdata_small";
        esSmallIndexDelete = new EsIndexDelete(ipPort, smallIndexName, "monitor_caiji_small");

        String esAllIndexSearch = "news_all,blog_all,forum_threads_all,mblog_info_all,video_brief_all," +
                "wechat_message_xigua_all,appdata_all";
        esAllIndexDelete = new EsIndexDelete(ipPort, esAllIndexSearch, "monitor_caiji_all");
    }

    @Test
    public void deleteByIndexName() {
        String[] strings = new String[]{"aircraft_info", "tb_linkedin_volunteers", "tb_linkedin_summary_attachements", "tb_linkedin_skills",
                "tb_linkedin_similiars", "tb_linkedin_recommenders", "tb_linkedin_recommendationreceived", "tb_linkedin_recommendationgiven",
                "tb_linkedin_publications", "tb_linkedin_people", "tb_linkedin_patents", "tb_linkedin_overviewwebsites", "tb_linkedin_overview",
                "tb_linkedin_organizations", "tb_linkedin_operation", "tb_linkedin_languages", "tb_linkedin_inventors", "tb_linkedin_interests",
                "tb_linkedin_imgcode", "tb_linkedin_honors", "tb_linkedin_groups", "tb_linkedin_follow_school", "tb_linkedin_follow_people",
                "tb_linkedin_follow_company", "tb_linkedin_follow_channels", "tb_linkedin_experiences_attachements", "tb_linkedin_experiences",
                "tb_linkedin_endorsements", "tb_linkedin_educations_attachements", "tb_linkedin_educations_activities", "tb_linkedin_educations",
                "tb_linkedin_course", "tb_linkedin_connections", "tb_linkedin_certifications", "tb_linkedin_authors", "tb_linkedin_additionals",
                "tb_linkedin_additionalinfo", "peoplein"};
        for (int i = 0; i < strings.length; i++) {
            String indexName = strings[i];
            EsIndexDelete esIndexDelete = new EsIndexDelete(ipPort, indexName, "graph");
            System.out.println(esIndexDelete.deleteIndexNameRun());
        }
    }

    /**
     * @param
     * @return
     * @Description: TODO(删除小索引)
     */
    @Test
    public void deleteBySmallIndexName() {
        String[] strings = new String[]{"wechat_message_xigua_all", "video_brief_all", "newspaper_info_all",
                "news_all", "mblog_info_all", "forum_threads_all", "blog_all",
                "appdata_all"};
        for (int i = 0; i < strings.length; i++) {
            String indexName = strings[i];
            EsIndexDelete esIndexDelete = new EsIndexDelete(ipPort, indexName, "monitor_caiji_all");
            System.out.println(esIndexDelete.deleteIndexNameRun());
        }
    }

    /**
     * @param
     * @return
     * @Description: TODO(删除大索引)
     */
    @Test
    public void deleteByAllIndexName() {
        String[] strings = new String[]{"wechat_message_xigua_small", "video_brief_small", "newspaper_info_small",
                "news_small", "mblog_info_small", "forum_threads_small", "blog_small", "appdata_small"};
        for (int i = 0; i < strings.length; i++) {
            String indexName = strings[i];
            EsIndexDelete esIndexDelete = new EsIndexDelete(ipPort, indexName, "monitor_caiji_small");
            System.out.println(esIndexDelete.deleteIndexNameRun());
        }
    }

    @Test
    public void deleteDataByTimeRange() {

        esSmallIndexDelete.setDebug(true);

        esSmallIndexDelete.addRangeTerms("pubtime", "2019-05-15 00:00:00", "2019-05-21 00:00:00", FieldOccurs.MUST);
        esSmallIndexDelete.execute();
        System.out.println(esSmallIndexDelete.getDeleteTotal());

    }

    @Test
    public void deleteById() {
        EsIndexDelete esIndexDelete = new EsIndexDelete(ipPort, "aircraft_info", "graph");
        System.out.println(esIndexDelete.deleteById("123"));
    }

    /**
     * 通过索引名称删除索引
     *
     * @return
     */
    @Test
    public void deleteIndexNameRun() {
        EsIndexDelete esIndexDelete = new EsIndexDelete("localhost:9200", "ship_info", "graph");
        System.out.println(esIndexDelete.deleteIndexNameRun());
    }

}



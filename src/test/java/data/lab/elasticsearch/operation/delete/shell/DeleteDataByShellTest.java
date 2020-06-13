package data.lab.elasticsearch.operation.delete.shell;

import data.lab.elasticsearch.operation.http.HttpRequest;
import org.junit.Test;

/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: casia.isi.elasticsearch.operation.delete.shell
 * @Description: TODO(监控删除索引数据)
 * @date 2019/5/30 19:08
 */
public class DeleteDataByShellTest {

    @Test
    public void forceMerge() {
        HttpRequest httpRequest = new HttpRequest();
        String url = "http://localhost:9210/news_small,blog_small,forum_threads_small,mblog_info_small,video_brief_small,wechat_message_xigua_small,appdata_small,newspaper_info_small/_forcemerge?\n" +
                "only_expunge_deletes=true&max_num_segments=1";
    }

    @Test
    public void main() {

        /**
         * @param indexType：索引类型
         * @param indexName：索引名称-多个索引名称使用逗号分隔
         * @param ipPort：IP和端口-使用冒号分隔
         * @param timeField：索引mapping中的时间字段
         * @param delayTime：延时执行-支持按天/小时/分钟（格式数字加d/h/m：1d/24h/60m/60s）
         * @param beforeDataTime：执行一次时删除多久以前的数据-支持按天/小时/分钟（格式数字加d/h/m：1d/24h/60m/60s）
         **/
        String indexType = "monitor_caiji_small";
        String indexName = "news_small,blog_small,forum_threads_small,mblog_info_small,video_brief_small,wechat_message_xigua_small,appdata_small";
        String ipPort = "localhost:9210";
        String timeField = "pubtime";

        // 每隔5s执行一次删除数据操作
        String delayTime = "5s";

        // 删除两天以前的数据
        String beforeDataTime = "3d";

        DeleteDataByShell.debug = true;

        // 是否启用force merge（释放磁盘空间 - cpu/io消耗增加）
        boolean isForceMerge = true;

        new DeleteDataByShell(indexType, indexName, ipPort, timeField, delayTime, beforeDataTime,isForceMerge).run();

    }

}



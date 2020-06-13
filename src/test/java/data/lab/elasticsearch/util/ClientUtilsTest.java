package data.lab.elasticsearch.util;

import org.junit.Test;
/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @PACKAGE_NAME: casia.isi.elasticsearch.util
 * @Description: TODO(TOOL TEST)
 * @author Yc-Ma
 * @date 2019/6/28 9:50
 *
 *
 */
public class ClientUtilsTest {

    @Test
    public void referenceUrl() {
        String url = "http://192.168.12.09:9210,192.168.12.107:9210,192.168.12.112:9210/testnews_ref_event,testwechat_info_ref_event/testdata,monitor_data/_search";
        System.out.println(ClientUtils.referenceUrl(url));
    }
    @Test
    public void referenceUrl2() {
        String url = "http://192.168.12.09:9210/_mapping";
        System.out.println(ClientUtils.referenceUrl(url));
    }

}
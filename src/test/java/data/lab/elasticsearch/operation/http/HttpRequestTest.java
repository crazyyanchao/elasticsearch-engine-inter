package data.lab.elasticsearch.operation.http;

import org.junit.Before;
import org.junit.Test;
/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: casia.isi.elasticsearch.operation.http
 * @Description: TODO(HTTP REQUEST TEST)
 * @date 2019/7/27 15:53
 */
public class HttpRequestTest {

    private HttpRequest httpRequest;

    private String address = "192.168.12.107:9210";

    @Before
    public void setUp() throws Exception {
        httpRequest = new HttpRequest();
    }

    /**
     * @param
     * @return
     * @Description: TODO(统计索引占用的存储大小)
     */
    @Test
    public void httpGet() {
        String url = "http://" + address + "/_cat/indices?v";
        String response = httpRequest.httpGet(url);
        System.out.println(response);
        String[] array = response.split("\n");
        double gbCount = 0;
        double mbCount = 0;
        for (int i = 1; i < array.length; i++) {
            String s = array[i];
            s = s.split(" ")[s.split(" ").length - 1];
            if (s.contains("gb")) {
                double gb = Double.valueOf(s.replace("gb", ""));
                gbCount += gb;
            } else if (s.contains("mb")) {
                double mb = Double.valueOf(s.replace("mb", ""));
                mbCount += mb;
            }
        }
        System.out.println("GB COUNT:" + gbCount);
        System.out.println("MB COUNT:" + mbCount);
    }

}


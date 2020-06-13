package data.lab.elasticsearch.bboss;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import data.lab.elasticsearch.operation.http.HttpDiscover;
import com.alibaba.fastjson.JSONObject;
import casia.isi.component.http.HttpHost;
import casia.isi.component.http.HttpRequestProxy;
import casia.isi.component.http.proxy.HttpProxyUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: casia.isi.elasticsearch.bboss
 * @Description: TODO(bboss - http - test)
 * @date 2019/6/27 11:23
 */
public class HttpRequestProxyTest {

    @Test
    public void httpProxyTest() {
        Map<String, Object> configs = new HashMap<String, Object>();
        configs.put("http.poolNames", "default");
        configs.put("http.health", "/health");
//        configs.put("http.authAccount","elastic");
//        configs.put("http.authPassword","changeme");
        configs.put("http.hosts", "192.168.12.109:9210,192.168.12.107:9210,192.168.12.112:9210");//health监控检查地址必须配置，否则将不会启动健康检查机制
        HttpRequestProxy.startHttpPools(configs);

        String url = "/testnews_ref_event,testwechat_info_ref_event/testdata,monitor_data/_search";
        JSONObject query = JSONObject.parseObject("{\n" +
                "  \"_source\": [\n" +
                "    \"content\"\n" +
                "  ],\n" +
                "  \"query\": {\n" +
                "    \"bool\": {\n" +
                "      \"must\": [\n" +
                "        {\n" +
                "          \"query_string\": {\n" +
                "            \"query\": \"+(content:\\\"北京\\\")\"\n" +
                "          }\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  },\n" +
                "  \"from\": 0,\n" +
                "  \"size\": 4\n" +
                "}");
        String response2 = HttpRequestProxy.sendJsonBody(query, url);
        System.out.println(response2);
    }

    /**
     * @param
     * @return
     * @Description: TODO(配置加载)
     */
    @Test
    public void loadPrperties() {
        /**
         * http负载均衡器配置 - 通过配置文件或者map进行配置
         *
         * **/
        // ---------------------------------加载Map属性配置启动负载均衡器示例---------------------------------

//        HttpDiscover.setHosts("192.168.12.109:9210,192.168.12.107:9210,192.168.12.112:9210");

        Map<String, Object> configs = new HashMap<String, Object>();
        configs.put("http.poolNames", "default,datapool-es");

        HttpDiscover httpDiscover = new HttpDiscover();
        configs.put("http.discoverService", httpDiscover);

//        configs.put("report.http.authAccount","elastic");//health监控检查地址必须配置，否则将不会启动健康检查机制
//        configs.put("report.http.authPassword","changeme");//health监控检查地址必须配置，否则将不会启动健康检查机制
//        configs.put("report.http.hosts", "1111:90222,http://1111:90222,https://1111:90222");//health监控检查地址必须配置，否则将不会启动健康检查机制

        // health监控检查地址必须配置，否则将不会启动健康检查机制
        configs.put("datapool-es.http.health", "/health");

        // 通过discoverService服务发现的地址都会加入到清单中
        configs.put("datapool-es.http.discoverService", "casia.isi.elasticsearch.operation.http.HttpDiscover");
        HttpRequestProxy.startHttpPools(configs);

        // ---------------------------------加载配置文件启动示例---------------------------------
        // 加载配置文件，启动负载均衡器
//        HttpRequestProxy.startHttpPools("application.properties");
//        String response = HttpRequestProxy.httpGetforString("datapool-es","_cluster/state?pretty");
//        String response = HttpRequestProxy.httpGetforString("_cluster/state?pretty");
//        System.out.println(response);

        String url = "/testnews_ref_event,testwechat_info_ref_event/testdata,monitor_data/_search";
        JSONObject query = JSONObject.parseObject("{\n" +
                "  \"_source\": [\n" +
                "    \"content\"\n" +
                "  ],\n" +
                "  \"query\": {\n" +
                "    \"bool\": {\n" +
                "      \"must\": [\n" +
                "        {\n" +
                "          \"query_string\": {\n" +
                "            \"query\": \"+(content:\\\"北京\\\")\"\n" +
                "          }\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  },\n" +
                "  \"from\": 0,\n" +
                "  \"size\": 4\n" +
                "}");
//
//
//        HttpRequestProxy.sendJson
//
//        HttpRequestUtil.httpPostforString(inter, para, map);

        String response2 = HttpRequestProxy.sendJsonBody("datapool-es", query, url);
        System.out.println(response2);
//
//        String response = HttpRequestProxy.sendJsonBody(httpPool, entity, url, headers, responseHandler);
//
//        System.out.println(response);

    }

    /**
     * @param
     * @return
     * @Description: TODO(调用服务API及示例)
     */
    @Test
    public void httpGetforString() {
        // ---------------------------------默认服务组示例---------------------------------
        String data = HttpRequestProxy.httpGetforString("/interfaceAddress");
        String data1 = HttpRequestProxy.httpPostforString("/interfaceAddress");

        // ---------------------------------指定服务组示例---------------------------------
        String data5 = HttpRequestProxy.httpGetforString("report", "/interfaceAddress");

    }

    /**
     * 在指定服务集群组report调用rest服务 / interfaceAddress, 返回json字符串报文 ， 通过循环调用 ， 测试负载均衡机制
     *
     * @param
     * @return
     * @Description: TODO(使用负载均衡器调用服务)
     */
    @Test
    public void testGet() {
        String data = HttpRequestProxy.httpGetforString("/interfaceAddress");
        System.out.println(data);
        do {
            try {
                data = HttpRequestProxy.httpGetforString("/interfaceAddress");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(3000l);
            } catch (Exception e) {
                break;
            }
            try {
                data = HttpRequestProxy.httpGetforString("/interfaceAddress");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                data = HttpRequestProxy.httpGetforString("/interfaceAddress");
            } catch (Exception e) {
                e.printStackTrace();
            }
//       break;
        }
        while (true);
    }

    /**
     * @param
     * @return
     * @Description: TODO(服务发现机制 - 被动发现模式)
     */
    @Test
    public void discoverService() {
        // 模拟被动获取监听地址清单
        List<HttpHost> hosts = new ArrayList<HttpHost>();
        // https服务必须带https://协议头,例如https://192.168.12.109:9210
        HttpHost host = new HttpHost("192.168.12.109:9210");
        hosts.add(host);

        host = new HttpHost("192.168.12.107:9210");
        hosts.add(host);

        host = new HttpHost("192.168.12.112:9210");
        hosts.add(host);
        // 将被动获取到的地址清单加入服务地址组report中
        HttpProxyUtil.handleDiscoverHosts("report", hosts);
    }

}



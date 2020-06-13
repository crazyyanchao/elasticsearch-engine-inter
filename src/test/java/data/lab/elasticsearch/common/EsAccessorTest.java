package data.lab.elasticsearch.common;

import data.lab.elasticsearch.operation.search.EsIndexSearch;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: casia.isi.elasticsearch.common
 * @Description: TODO(Describe the role of this JAVA class)
 * @date 2019/11/28 16:11
 */
public class EsAccessorTest {

    //        private final static String ipPort = "10.97.167.206:9210,192.168.12.107:9211";
//    private final static String ipPort = "http://10.97.167.206:9210,http://10.97.167.19:9210";
    private final static String ipPort = "http://10.97.167.206:9210/";
//    private final static String ipPort = "https://10.97.167.206:9210,https://10.97.167.19:9210";

    private final static String ipPorts = "192.168.12.107:9210,192.168.12.107:9211,192.168.12.114:9210,192.168.12.109:9211,192.168.12.112:9211,192.168.12.109:9210,192.168.12.114:9211,192.168.12.114:9210,192.168.12.110:9210,192.168.12.111:9210,192.168.122.111:9219";

    private EsIndexSearch searcher;

    @Before
    public void setUp() throws Exception {
        searcher = new EsIndexSearch(ipPorts, ".tasks", "task");
//        searcher = new EsIndexSearch(new HttpSymbol() {
//            @Override
//            public String name() {
//                return "Cluster_1";
//            }
//        }, ipPort, ".tasks", "task");
    }

    @Test
    public void removeLastHttpsAddNewAddress() {

        // 更换集群
        searcher.removeLastHttpsAddNewAddress(ipPort);

        for (int i = 0; i < 10; i++) {
            List<String[]> result = searcher.facetCountQueryOrderByCount("task.type", 10, SortOrder.DESC);
            searcher.outputResult(result);
        }

        searcher.removeLastHttpsAddNewAddress(ipPorts);
        for (int i = 0; i < 10; i++) {
            List<String[]> result = searcher.facetCountQueryOrderByCount("task.type", 10, SortOrder.DESC);
            searcher.outputResult(result);
        }
    }

}



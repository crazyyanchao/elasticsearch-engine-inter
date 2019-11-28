package casia.isi.elasticsearch.common;

import casia.isi.elasticsearch.operation.http.HttpSymbol;
import casia.isi.elasticsearch.operation.search.EsIndexSearch;
import casia.isi.elasticsearch.operation.search.EsIndexSearchTest;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;
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
 * @PACKAGE_NAME: casia.isi.elasticsearch.common
 * @Description: TODO(Describe the role of this JAVA class)
 * @date 2019/11/28 16:11
 */
public class EsAccessorTest {

    //        private final static String ipPort = "39.97.167.206:9210,192.168.12.107:9211";
//    private final static String ipPort = "http://39.97.167.206:9210,http://39.97.167.19:9210";
    private final static String ipPort = "http://39.97.167.206:9210/,http://39.97.167.19:9210/";
//    private final static String ipPort = "https://39.97.167.206:9210,https://39.97.167.19:9210";

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void removeLastHttpsAddNewAddress() {
        EsIndexSearch searcher = new EsIndexSearch(new HttpSymbol() {
            @Override
            public String name() {
                return "Cluster_1";
            }
        }, ipPort, ".tasks", "task");

        List<String[]> result = searcher.facetCountQueryOrderByCount("task.type", 10, SortOrder.DESC);
        searcher.outputResult(result);
    }

}



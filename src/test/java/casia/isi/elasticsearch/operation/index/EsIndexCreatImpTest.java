package casia.isi.elasticsearch.operation.index;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
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
 * @PACKAGE_NAME: casia.isi.elasticsearch.operation.index
 * @Description: TODO(Describe the role of this JAVA class)
 * @date 2019/8/20 17:00
 */
public class EsIndexCreatImpTest {

    private static EsIndexCreat indexCreat;

    private String ipPort = "" +
            "192.168.12.107:9210,192.168.12.107:9211,localhost:9200,192.168.12.114:9210," +
            "192.168.12.109:9211,192.168.12.112:9211,192.168.12.109:9210," +
            "192.168.12.114:9211,192.168.12.114:9210,192.168.12.110:9210," +
            "192.168.12.111:9210,192.168.122.111:9219";

//    private String ipPort = "39.97.167.206:9210,39.97.243.92:9210,182.92.217.237:9210," +
//            "39.97.243.129:9210,39.97.173.122:9210,39.97.242.194:9210";

//    private String ipPort = "" +
//            "192.168.12.107:9210,localhost:9200";


    @Before
    public void setUp() throws Exception {
        indexCreat = new EsIndexCreat(ipPort, "statellite_info", "graph");
    }

    @Test
    public void insertField_intime() {
        //创建新字段
        Map<String, String> map = new HashMap<String, String>();
        map.put("format", "yyyy-MM-dd HH:mm:ss");
        map.put("type", "date");
        boolean boo = indexCreat.insertField("intime", map);
        System.out.println(boo);
    }

    @Test
    public void insertField_site() {
        //创建新字段
        Map<String, String> map = new HashMap<String, String>();
        map.put("index", "not_analyzed");
        map.put("type", "keyword");
        boolean boo = indexCreat.insertField("site", map);
        System.out.println(boo);
    }

    @Test
    public void insertFields() {
        // 批量增加字段-使用模糊匹配索引
//        indexCreat = new EsIndexCreat(ipPort, "*_small", "monitor_caiji_small");
//        indexCreat = new EsIndexCreat(ipPort, "*_all", "monitor_caiji_all");
        indexCreat = new EsIndexCreat(ipPort, "*_preprocess", "monitor_caiji_preprocess");
        //创建新字段
        Map<String, String> map = new HashMap<>();
        map.put("index", "not_analyzed");
        map.put("type", "keyword");
        map.put("store", "true");
        boolean boo = indexCreat.insertField("url_short", map);
        System.out.println(boo);
    }
}



package casia.isi.elasticsearch.operation.search.aircraft;

import casia.isi.elasticsearch.common.DistanceUnit;
import casia.isi.elasticsearch.common.GeoBoundOccurs;
import casia.isi.elasticsearch.common.SortOrder;
import casia.isi.elasticsearch.model.BoundBox;
import casia.isi.elasticsearch.model.BoundPoint;
import casia.isi.elasticsearch.model.Circle;
import casia.isi.elasticsearch.model.Shape;
import casia.isi.elasticsearch.operation.search.EsIndexSearch;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * @PACKAGE_NAME: casia.isi.elasticsearch.operation.search.aircraft
 * @Description: TODO(CONFIG TASK TEST)
 * @date 2019/10/8 15:29
 */
public class ConfigTaskTest {

    private EsIndexSearch aircraftSearch;

    private String ipPort = "" +
            "192.168.12.107:9210,192.168.12.107:9211,192.168.12.114:9210," +
            "192.168.12.109:9211,192.168.12.112:9211,192.168.12.109:9210," +
            "192.168.12.114:9211,192.168.12.114:9210,192.168.12.110:9210," +
            "192.168.12.111:9210,192.168.122.111:9219";

//    private String ipPort = "39.97.167.206:9210,39.97.243.92:9210,182.92.217.237:9210," +
//            "39.97.243.129:9210,39.97.173.122:9210,39.97.242.194:9210";

//    private String ipPort = "localhost:9200";

    @Before
    public void searchObject() {
        PropertyConfigurator.configureAndWatch("config/log4j.properties");

        aircraftSearch = new EsIndexSearch(ipPort, "aircraft_info_latest_status,aircraft_info", "graph");

    }


    @Test
    public void configTaskTest() {

        // 配置任务
        List<ConfigTask> configTasks = new ArrayList<>();

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

        List<Shape> shapeList1 = new ArrayList<>();
        shapeList1.add(beijing);

        List<Shape> shapeList2 = new ArrayList<>();
        shapeList2.add(tianjinCircle);

        // 添加任务1
        configTasks.add(new ConfigTask(shapeList1, "美国", "未知", "code123", "s1mode", "123"));
        // 添加任务2
        configTasks.add(new ConfigTask(shapeList2, "加拿大", "未知", "code124", "s2mode", null));

        // 多个任务拼接查询

        // 排序字段
        String sortFieldName = "pubtime";
        // 返回的字段名称
        String[] _source = new String[]{"flight_number", "pubtime", "latitude", "longitude",
                "altitude", "speed", "aircraft", "origin", "destination", "airline", "insert_time",
                "site", "callsign", "type", "mode_s", "country", "heading", "op", "sqk",
                "manufacturer", "random_code", "is_am", "source"
        };
        // 字段映射-拼接查询时使用的字段名称，只需要修改KEY对应的VALUE
        Map<String, String> mapField = new HashMap<>();
        mapField.put("areas", "location_point");
        mapField.put("country", "country");
        mapField.put("species", "type");
        mapField.put("identificationCode", "sqk");
        mapField.put("modeS", "mode_s");
        mapField.put("registrationNum", "aircraft");

        JSONObject queryDSL = aircraftSearch.toQueryCraftDSL(configTasks, 0, 20, SortOrder.DESC, sortFieldName, _source, mapField);

        // 继续添加条件
        JSONObject bool = queryDSL.getJSONObject("query").getJSONObject("bool");

        // 时间
        JSONArray must = new JSONArray();
        JSONObject time = JSONObject.parseObject(" {\n" +
                "              \"range\": {\n" +
                "                \"pubtime\": {\n" +
                "                  \"gte\": \"2019-10-08 00:00:00\",\n" +
                "                  \"lte\": \"2019-10-08 15:40:46\"\n" +
                "                }\n" +
                "              }\n" +
                "            }");

        must.add(time);

        // 站点
        JSONObject site = JSONObject.parseObject("{\n" +
                "          \"term\": {\n" +
                "            \"site\": \"radarbox24.com\"\n" +
                "          }\n" +
                "        }");
        must.add(site);

        // 其它
        JSONObject other = JSONObject.parseObject(" {\n" +
                "          \"query_string\": {\n" +
                "            \"query\": \"+aircraft:*9V-SNB* +mode_s:*76CEEA* +type:*B77*\"\n" +
                "          }\n" +
                "        }");
        must.add(other);
        bool.put("must", must);

        String dsl = queryDSL.toJSONString();
        aircraftSearch.executeDSL(dsl);
        aircraftSearch.getTotal();
        aircraftSearch.reset();
    }
}


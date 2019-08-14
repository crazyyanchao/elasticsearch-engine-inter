package casia.isi.elasticsearch.operation.index;

import casia.isi.elasticsearch.operation.http.HttpRequest;
import casia.isi.elasticsearch.util.FileUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
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
 * @PACKAGE_NAME: casia.isi.elasticsearch.operation.index
 * @Description: TODO(创建映射到ES / 数据转储到ES)
 * @date 2019/5/22 10:53
 */
public class EsIndexCreatTest {

    private EsIndexCreat esIndexCreat;

    private String ipPort = "" +
            "192.168.12.107:9210,192.168.12.107:9211,localhost:9200,192.168.12.114:9210," +
            "192.168.12.109:9211,192.168.12.112:9211,192.168.12.109:9210," +
            "192.168.12.114:9211,192.168.12.114:9210,192.168.12.110:9210," +
            "192.168.12.111:9210,192.168.122.111:9219";

    private String dxEsIpPorts = "" +
            "39.97.167.206:9210,39.97.243.92:9210,182.92.217.237:9210," +
            "39.97.243.129:9210,39.97.173.122:9210,39.97.242.194:9210";


    @Before
    public void setUp() throws Exception {
        esIndexCreat = new EsIndexCreat(dxEsIpPorts, "aircraft_info", "graph");
    }

    /**
     * @param
     * @return
     * @Description: TODO(创建映射到ES)
     */
    @Test
    public void mapping() {
        // TODO Auto-generated method stub
        PropertyConfigurator.configureAndWatch("config" + File.separator + "log4j.properties");
        String fileName = "mapping/aircraft_info.json";
        String json = JSONObject.parseObject(FileUtil.readAllLine(fileName, "UTF-8")).toJSONString();
        try {
            System.out.println(json);
            System.out.println(esIndexCreat.CreatIndex(json) + "end");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            System.out.println(esIndexCreat.CreatIndex(json) + "end");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            System.out.println(esIndexCreat.CreatIndex(json) + "end");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param
     * @return
     * @Description: TODO(创建映射到ES)
     */
    @Test
    public void singleMapping() {
        //		String fileName = "mapping/linkin/peoplein.json";
//		String fileName = "mapping/linkin/tb_linkedin_additionalinfo.json";

        String fileName = "mapping/loading/tb_linkedin_projects.json";

        String url = "http://localhost:9200/tb_linkedin_projects";

        HttpRequest httpExcutetor = new HttpRequest();

        try {
            String json = JSONObject.parseObject(FileUtil.readAllLine(fileName, "UTF-8")).toJSONString();
            System.out.println(json);
            String rt = httpExcutetor.httpPut(url, json);
            System.out.println(rt);
            System.out.println("end");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * @param
     * @return
     * @Description: TODO(创建映射到ES)
     */
    @Test
    public void multiMapping() {
        String esIpPort = "127.0.0.1:9200";
        String catalogPath = "mapping/loading/";

        File file = new File(catalogPath);
        String[] fileArray = file.list();
        for (int i = 0; i < fileArray.length; i++) {
            String fileN = fileArray[i];

            String fileName = catalogPath + fileN;

            String onlyName = fileN.substring(0, fileN.lastIndexOf("."));

            String url = "http://" + esIpPort + "/" + onlyName;

            HttpRequest httpExcutetor = new HttpRequest();
            try {
                String json = JSONObject.parseObject(FileUtil.readAllLine(fileName, "UTF-8")).toJSONString();
                System.out.println(json);
                String rt = httpExcutetor.httpPut(url, json);
                System.out.println(rt);
                System.out.println("end");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    /**
     * @param
     * @return
     * @Description: TODO(转储数据到ES)
     */
    @Test
    public void createImport() {
        List<JSONObject> messageDataArray = new ArrayList<>();
        JSONObject object = new JSONObject();
        object.put("id", 123);
        object.put("test", "test");
        messageDataArray.add(object);
        // 传入做为唯一键的字段名称
        // 此接口具备覆盖更新的能力（批量覆盖更新可以使用此接口） -  EsIndexUpdate的部分更新不同
        boolean bool = esIndexCreat.index(messageDataArray, "id");
        System.out.println(bool);
    }

    /**
     * @param
     * @return
     * @Description: TODO(转储数据到ES)
     */
    @Test
    public void createImportData() {
        EsIndexCreat esIndexCreat = new EsIndexCreat("10.16.100.31:9200", "think_tank_small", "monitor_caiji_small");

        List<JSONObject> dataList = new ArrayList<JSONObject>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("md5", "rocco+m+altavilla/reading+pa/2029617170");
        jsonObject.put("id", 859266);
        jsonObject.put("content", "暗示恶女陪我核武器");
        dataList.add(jsonObject);

        // 需要生成MD5字段数据
        boolean it = esIndexCreat.index(dataList, "md5");
    }

    @Test
    public void smallAllIndex() {
        String fileName = "mapping/test/smallAllIndex.json";

        String url = "http://192.168.12.109:9210/small_all_index";

        HttpRequest httpExcutetor = new HttpRequest();

        try {
            String json = JSONObject.parseObject(FileUtil.readAllLine(fileName, "UTF-8")).toJSONString();
            System.out.println(json);
            String rt = httpExcutetor.httpPut(url, json);
            System.out.println(rt);
            System.out.println("end");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void insertField() {
        EsIndexCreat esIndexCreat = new EsIndexCreat("192.168.12.109:9210", "aircraft_info_latest_status,aircraft_info", "graph");
        Map<String, String> map = new HashMap<>();
        map.put("index", "not_analyzed");
        map.put("store", "true");
        map.put("type", "keyword");
        /**
         * 在已有索引上新增字段
         *
         * @param fieldName 字段
         * @param map       类型参数
         */
        System.out.println(esIndexCreat.insertField("type", map));
    }

    @Test
    public void isIndexName() {
        // 查询索引名是否存在
        System.out.println(esIndexCreat.isIndexName());
    }

    @Test
    public void searchIndexNames() {
        // 查询所有索引
        List<String> indexNames = esIndexCreat.searchIndexNames();
        for (int i = 0; i < indexNames.size(); i++) {
            String indexName = indexNames.get(i);
            System.out.println(indexName);
        }
    }

}

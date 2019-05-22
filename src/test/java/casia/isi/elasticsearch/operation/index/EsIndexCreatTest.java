package casia.isi.elasticsearch.operation.index;

import casia.isi.elasticsearch.operation.http.HttpRequest;
import casia.isi.elasticsearch.util.FileUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

    /**
     * @param
     * @return
     * @Description: TODO(创建映射到ES)
     */
    @Test
    public void mapping() {
        // TODO Auto-generated method stub
        PropertyConfigurator.configureAndWatch("config" + File.separator + "log4j.properties");
        try {
            String fileName = "test_mapping.json";
            String json = JSONObject.parseObject(FileUtil.readAllLine(fileName, "UTF-8")).toJSONString();
            System.out.println(json);
            EsIndexCreat es = new EsIndexCreat("127.0.0.1:9200", "test2", null);
            boolean rt = es.CreatIndex(json);
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
    public void singleMapping() {
        //		String fileName = "mapping/linkin/peoplein.json";
//		String fileName = "mapping/linkin/tb_linkedin_additionalinfo.json";

        String fileName = "mapping/linkin/tb_linkedin_projects.json";

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
        EsIndexCreat creat = new EsIndexCreat("192.168.12.110:9200", "statellite_info", "graph");
        List<JSONObject> messageDataArray = new ArrayList<>();
        // 传入做为唯一键的字段名称
        boolean bool = creat.index(messageDataArray, "md5");
        System.out.println(bool);
    }

    /**
     * @param
     * @return
     * @Description: TODO(转储数据到ES)
     */
    @Test
    public void createImportData() {
        EsIndexCreat esIndexCreat = new EsIndexCreat("127.0.0.1:9200","peoplein","graph");

        List<JSONObject> dataList = new ArrayList<JSONObject>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("UID", "rocco+m+altavilla/reading+pa/2029617170");
        jsonObject.put("nameid", 859266);
        jsonObject.put("ID", 1);
        dataList.add(jsonObject);

        // 需要生成MD5字段数据
        boolean it = esIndexCreat.index( dataList , "ID" );
    }

}

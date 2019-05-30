package casia.isi.elasticsearch.operation.index;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import casia.isi.elasticsearch.common.Message;
import casia.isi.elasticsearch.util.FileUtil;
import com.alibaba.fastjson.JSONArray;
import org.apache.log4j.Logger;

import casia.isi.elasticsearch.common.Symbol;
import casia.isi.elasticsearch.operation.http.HttpRequest;
import casia.isi.elasticsearch.util.Validator;

import com.alibaba.fastjson.JSONObject;

/**
 * ElasticSearch的索引创建接口(Http方式)
 *
 * @author wzy
 * @version elasticsearch - 5.6.3
 */
public class EsIndexCreatImp {
    private Logger logger = Logger.getLogger(EsIndexCreatImp.class);
    /**
     * 索引ip
     */
    private String IP;
    /**
     * 索引端口
     */
    private int Port;
    /**
     * 索引ip+port
     */
    private String IpPort;
    /**
     * 索引名称
     */
    private String indexName;
    /**
     * 索引类型
     */
    private String IndexType;
    /**
     * 索引地址
     */
    private String IndexUrl;
    /**
     * http访问对象
     */
    private HttpRequest httpRequest;

    public EsIndexCreatImp() {
    }

    public String getIndexType() {
        return IndexType;
    }

    /**
     * 构造函数，初始化配置
     *
     * @param IP
     * @param Port
     * @param indexName 索引块名称
     * @param typeName  索引类型
     */
    public EsIndexCreatImp(String IP, int Port, String indexName, String typeName) {
        EsIndexCreate_imp(IP, Port, indexName, typeName);
    }

    /**
     * 构造函数，初始化配置
     *
     * @param IPADRESS
     * @param indexName
     * @param typeName
     */
    public EsIndexCreatImp(String IPADRESS, String indexName, String typeName) {
        EsIndexCreate_imp(IPADRESS, indexName, typeName);
    }

    /**
     * 构造函数，初始化配置
     *
     * @param IPADRESS
     * @param indexName
     * @param typeName
     */
    private void EsIndexCreate_imp(String IPADRESS, String indexName, String typeName) {
        String[] servers = IPADRESS.split(Symbol.SPACE_CHARACTER.toString());
        //构造查询url
        this.IpPort = "http://" + servers[new Random().nextInt(servers.length)];
        this.IndexUrl = this.IpPort;
        this.indexName = indexName;
        this.IndexUrl = indexName != null ? (this.IndexUrl + "/" + indexName) : this.IndexUrl;
        this.IndexType = typeName;
        this.IndexUrl = typeName != null ? (this.IndexUrl + "/" + typeName) : this.IndexUrl;
        this.IndexUrl = this.IndexUrl + "/_bulk";
        this.httpRequest = new HttpRequest();
    }

    /**
     * 构造函数，初始化配置
     *
     * @param IP
     * @param Port
     * @param indexName 索引块名称
     * @param typeName  索引类型
     */
    private void EsIndexCreate_imp(String IP, int Port, String indexName, String typeName) {

        this.IP = "http://" + IP + ":";
        this.Port = Port;
        this.IpPort = this.IP + Port;
        this.IndexUrl = this.IpPort;
        this.indexName = indexName;
        this.IndexUrl = indexName != null ? (this.IndexUrl + "/" + indexName) : this.IndexUrl;
        this.IndexType = typeName;
        this.IndexUrl = typeName != null ? (this.IndexUrl + "/" + typeName) : this.IndexUrl;
        this.httpRequest = new HttpRequest();
    }

    /**
     * 写入索引数据
     *
     * @param dataList      数据列表
     * @param uniqueKeyName 数据在索引中的主键名
     * @param //bakingName  类型归属分片,默认为null,指定主键
     */
    public boolean index(List<JSONObject> dataList, String uniqueKeyName) {
        boolean rs = index(dataList, uniqueKeyName, null);
        return rs;
    }

    /**
     * 写入索引数据
     *
     * @param dataList      数据列表
     * @param uniqueKeyName 数据在索引中的主键名
     * @param bakingName    类型归属分片,默认为null,指定主键
     */
    public boolean index(List<JSONObject> dataList, String uniqueKeyName, String bakingName) {
        this.IndexUrl = this.IndexUrl.contains("/_bulk") ? this.IndexUrl : this.IndexUrl + "/_bulk";

        boolean rt = true;
        StringBuffer indexStrBuffer = new StringBuffer();
        if (dataList == null || dataList.size() == 0)
            return rt;
        //设置类型数据存储分片
        JSONObject bakingJson = new JSONObject();
        if (bakingName != null && bakingName.length() != 0) {
            bakingJson.put(bakingName, "baking");
        }
        JSONObject indexJson = new JSONObject();
        for (JSONObject dataJson : dataList) {
            //构造插入语句
            if (dataJson.containsKey(uniqueKeyName)) {
                indexJson.put("_id", dataJson.get(uniqueKeyName).toString());
                indexStrBuffer.append("{\"index\":").append(indexJson.toString())
                        .append(bakingJson.toString().length() == 0 ? "" : bakingJson.toString()).append("}\n");
                indexStrBuffer.append(dataJson.toString()).append("\n");
                indexJson.clear();
            }
        }
        String queryResultStr = httpRequest.httpPost(this.IndexUrl, indexStrBuffer.toString());
        if (queryResultStr != null && !Message.indexMessage(queryResultStr)) {
            rt = true;
        } else {
            rt = false;
        }
        return rt;
    }

    /**
     * 在已有索引上新增字段
     *
     * @param fieldName 字段
     * @param map       类型参数
     */
    public boolean insertField(String fieldName, Map<String, String> map) {
        this.IndexUrl = this.IndexUrl.contains("/_mapping?pretty") ? this.IndexUrl : this.IndexUrl + "/_mapping?pretty";
        boolean rs = true;
        JSONObject json = new JSONObject();
        JSONObject jsonProperties = new JSONObject();
        JSONObject jsonField = new JSONObject();
        JSONObject jsonType = new JSONObject();
        Set<String> keys = map.keySet();
        for (String key : keys) {
            jsonType.put(key, map.get(key));
        }
        jsonField.put(fieldName, jsonType);
        jsonProperties.put("properties", jsonField);
        json.put(this.IndexType, jsonProperties);
        String creatResultStr = httpRequest.httpPost(this.IndexUrl, json.toString());

        try {
            JSONObject jsonResult = new JSONObject();
            jsonResult = jsonResult.parseObject(creatResultStr);
            if (!jsonResult.getBoolean("acknowledged")) {
                return !rs;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return !rs;
        }
        return rs;
    }

    /**
     * 创建索引
     *
     * @param typeJson 类型json字符串
     * @return boolean
     */
    public boolean CreatIndex(String typeJson) {
        boolean rs = true;
        if (!Validator.check(typeJson)) {
            return !rs;
        }
        String ResultStr = httpRequest.httpPut(this.IpPort + "/" + this.indexName, typeJson);
        try {
            JSONObject jsonResult = new JSONObject();
            jsonResult = jsonResult.parseObject(ResultStr);
            if (jsonResult.containsKey("acknowledged")) {
                return rs;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.info("creat " + this.indexName + " error ! error:" + e.getMessage());
            return !rs;
        }
        return !rs;
    }

    /**
     * 查询索引是否存在
     *
     * @param //indexName 索引名
     * @return boolean
     */
    public boolean isIndexName() {
        boolean rs = true;
        String ResultStr = httpRequest.httpGet(this.IpPort + "/" + this.indexName);
        try {
            JSONObject jsonResult = new JSONObject();
            jsonResult = jsonResult.parseObject(ResultStr);
            if (jsonResult.containsKey(this.indexName)) {
                return rs;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.info("indexName:" + this.indexName + " is not exist !");
            return !rs;
        }
        return !rs;
    }

    /**
     * 查询所有索引名
     *
     * @return boolean
     */
    public List<String> searchIndexNames() {
        List<String> list = new ArrayList<String>();
        String ResultStr = httpRequest.httpGet(this.IpPort + "/_cat/indices?h=index");
        try {
            if (Validator.check(ResultStr)) {
                String[] indexnames = ResultStr.split("\n");
                for (String indexname : indexnames) {
                    list.add(indexname.replaceAll(" |\t", "").trim());
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.info("search index names Exception:" + e.getMessage());
            return list;
        }
        return list;
    }

    /**
     * @param url:http://localhost:9200/tb_linkedin_projects
     * @param mappingFile:mapping/loading/tb_linkedin_projects.json
     * @return
     * @Description: TODO(当前mapping文件创建映射)
     */
    public String singleMapping(String url, String mappingFile) throws Exception {
        HttpRequest httpExcutetor = new HttpRequest();
        String json = JSONObject.parseObject(FileUtil.readAllLine(mappingFile, "UTF-8")).toJSONString();
        System.out.println(json);
        return httpExcutetor.httpPut(url, json);
    }

    /**
     * @param esIpPort:127.0.0.1:9200
     * @param folder:mapping/loading/
     * @return
     * @Description: TODO(当前文件夹下mapping文件创建映射)
     */
    public String multiMapping(String esIpPort, String folder) throws Exception {
        JSONArray folderMappingMessage = new JSONArray();
        File file = new File(folder);
        String[] fileArray = file.list();
        for (int i = 0; i < fileArray.length; i++) {
            String fileN = fileArray[i];
            if (fileN.contains(".")) {
                String fileName = folder + fileN;

                String onlyName = fileN.substring(0, fileN.lastIndexOf("."));

                String url = "http://" + esIpPort + "/" + onlyName;

                HttpRequest httpExcutetor = new HttpRequest();
                String json = JSONObject.parseObject(FileUtil.readAllLine(fileName, "UTF-8")).toJSONString();
                System.out.println(json);
                folderMappingMessage.add(httpExcutetor.httpPut(url, json));
            }
        }
        return folderMappingMessage.toJSONString();
    }

}

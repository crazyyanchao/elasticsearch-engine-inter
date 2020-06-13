package data.lab.elasticsearch.operation.update;

import java.util.Map;
import java.util.Random;
import java.util.Set;

import data.lab.elasticsearch.common.EsAccessor;
import data.lab.elasticsearch.util.ClientUtils;
import com.alibaba.fastjson.JSON;

import data.lab.elasticsearch.common.Symbol;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
//import com.spreada.utils.chinese.ZHConverter;
import data.lab.elasticsearch.operation.http.HttpProxyRegister;
import data.lab.elasticsearch.operation.http.HttpSymbol;

/**
 * ElasticSearch
 *
 * @author
 * @version elasticsearch - 5.6.3
 */
public class EsIndexUpdateImp extends EsAccessor {

    // 索引集群连接的IP-PORT用冒号分隔
    public String ipPort;

    // 索引名称/多个索引名称使用逗号分隔
    public String indexName;

    // 索引类型
    public String typeName;

    /**
     * 空格符
     */
    public final String BLANK = " ";

    /**
     * 更新地址
     */
    public static String update_index;
    /**
     * IP
     */
    public static String IP;
    /**
     * Port
     */
    public static int Port = 0;

//    public static ZHConverter converter = ZHConverter
//            .getInstance(ZHConverter.SIMPLIFIED);

    // 记录关键词，以及关键词出现情况
    public String keywordString = "";

    // 是否将查询语法繁转简
    public static boolean ZH_Converter = false;

    /**
     * 查询索引的url
     */
    public String queryUrl;

    /**
     * 查询的返回值
     */
    public JSONObject queryJsonResult;

    /**
     * 构造查询条件的json串
     */
    public JSONObject queryJson;
    /**
     * 构造查询必须条件的json串
     */
    public JSONArray queryMustJarr;
    /**
     * 构造查询否定条件的json串
     */
    public JSONArray queryMustNotJarr;
    /**
     * 构造过滤必须条件的json串
     */
    public JSONArray queryFilterMustJarr;
    /**
     * 构造过滤否定条件的json串
     */
    public JSONArray queryFilterMustNotJarr;

    /**
     * 重置条件
     */
    public void reset() {
        try {
            this.keywordString = "";
            this.queryJson.clear();
            this.queryJsonResult.clear();
            this.queryMustJarr.clear();
            this.queryMustNotJarr.clear();
            this.queryFilterMustJarr.clear();
            this.queryFilterMustNotJarr.clear();
        } catch (Exception e) {
        }
    }

    @Deprecated
    public EsIndexUpdateImp() {
    }

    public EsIndexUpdateImp(HttpSymbol httpPoolName, String ipPorts, String indexName, String typeName) {
        super(httpPoolName, ipPorts);
        EsIndexUpdate_imp(ipPorts, indexName, typeName);
    }

    /**
     * 构造函数，初始化配置 - 支持配置一个地址
     *
     * @param //IpPort
     * @param indexName
     * @param typeName
     */
    @Deprecated
    public EsIndexUpdateImp(String IP, int Port, String indexName, String typeName) {
        EsIndexUpdate_imp(IP, Port, indexName, typeName);
    }

    /**
     * 构造函数，初始化配置 - 支持配置多个地址
     *
     * @param IpPort
     * @param indexName
     * @param typeName
     */
    public EsIndexUpdateImp(String IpPort, String indexName, String typeName) {
        EsIndexUpdate_imp(IpPort, indexName, typeName);
    }

    /**
     * 初始化
     *
     * @param //IP      索引IP
     * @param //Port    端口
     * @param indexname 索引名称
     * @param typename  类型名称
     */
    private void EsIndexUpdate_imp(String IPADRESS, String indexname, String typename) {
        try {
            String[] servers = IPADRESS.split(Symbol.COMMA_CHARACTER.toString());
            //构造查询url
            this.ipPort = IPADRESS;
            this.indexName = indexname;
            this.typeName = typename;
            this.update_index = "http://" + servers[new Random().nextInt(servers.length)];
            this.update_index = indexname == null ? "" : this.update_index + "/" + indexname;
            this.update_index = typename == null ? "" : this.update_index + "/" + typename;

            // 新增HTTP负载均衡器
            HttpProxyRegister.register(IPADRESS);

            this.queryJson = new JSONObject();
            this.queryJsonResult = new JSONObject();
            this.queryMustJarr = new JSONArray();
            this.queryMustNotJarr = new JSONArray();
            this.queryFilterMustJarr = new JSONArray();
            this.queryFilterMustNotJarr = new JSONArray();

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 初始化
     *
     * @param IP        索引IP
     * @param Port      端口
     * @param indexname 索引名称
     * @param typename  类型名称
     */
    private void EsIndexUpdate_imp(String IP, int Port, String indexname, String typename) {
        try {
            this.IP = IP;
            this.Port = Port;
            this.ipPort = this.IP + ":" + this.Port;
            this.indexName = indexname;
            this.typeName = typename;
            this.update_index = "http://" + this.IP + ":" + this.Port;
            this.update_index = indexname == null ? "" : this.update_index + "/" + indexname;
            this.update_index = typename == null ? "" : this.update_index + "/" + typename;

            // 新增HTTP负载均衡器
            HttpProxyRegister.register(IP + ":" + Port);

            this.queryJson = new JSONObject();
            this.queryJsonResult = new JSONObject();
            this.queryMustJarr = new JSONArray();
            this.queryMustNotJarr = new JSONArray();
            this.queryFilterMustJarr = new JSONArray();
            this.queryFilterMustNotJarr = new JSONArray();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 添加修改一个id的信息
     *
     * @param parameter map类型 ，字段名：值
     * @param _id       要修改的主键
     */
    public boolean updateParameterById(Map<String, Object> parameter, String _id) {
        try {

            if (parameter.size() == 0 || _id == null || _id.trim().length() == 0) {
                logger.info("error: Parameter cannot be null !");
                return true;
            }
            JSONObject allJson = new JSONObject();
            JSONObject parJson = new JSONObject();
            Set<String> keys = parameter.keySet();
            for (String key : keys) {
                parJson.put(key, parameter.get(key));
            }
            allJson.put("doc", parJson);
            String posturl = this.update_index + "/" + _id + "/_update";

            if (debug) {
                logger.info("url:" + posturl);
                logger.info("papameter: -d " + allJson);
            }

            String queryResultStr = request.httpPost(ClientUtils.referenceUrl(posturl), allJson.toString());

            if (queryResultStr == null) {
                return false;
            }

            JSONObject result = new JSONObject().parseObject(queryResultStr);
            if (result.containsKey("status") && result.containsKey("error")) {
                logger.info("update data by _id=" + _id + " error ! ");
                logger.info("status=" + result.getString("status") + " ! error=" + result.getJSONObject("error"));
                return false;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 添加修改一个id的信息(不存在则插入)
     *
     * @param parameter       map类型 ，字段名：值
     * @param _id             要修改的主键（唯一字段值MD5也可以）
     * @param parameterUpsert 如果不存在则将完整的数据插入
     */
    public boolean upsertParameterById(Map<String, Object> parameter, String _id, Map<String, Object> parameterUpsert) {
        try {

            if (parameter.size() == 0 || _id == null || _id.trim().length() == 0) {
                logger.info("error: Parameter cannot be null !");
                return true;
            }
            JSONObject allJson = new JSONObject();
            JSONObject parJson = new JSONObject();
            Set<String> keys = parameter.keySet();
            for (String key : keys) {
                parJson.put(key, parameter.get(key));
            }
            allJson.put("doc", parJson);
            String posturl = this.update_index + "/" + _id + "/_update";

            if (debug) {
                logger.info("url:" + posturl);
                logger.info("papameter: -d " + allJson);
            }

            allJson = addUpsert(allJson, parameterUpsert);
            String queryResultStr = request.httpPost(ClientUtils.referenceUrl(posturl), allJson.toString());

            if (queryResultStr == null) {
                return false;
            }

            JSONObject result = new JSONObject().parseObject(queryResultStr);
            if (result.containsKey("status") && result.containsKey("error")) {
                logger.info("update data by _id=" + _id + " error ! ");
                logger.info("status=" + result.getString("status") + " ! error=" + result.getJSONObject("error"));
                return false;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
    }

    /**
     * @param
     * @return
     * @Description: TODO(组合被插入的数据)
     */
    private JSONObject addUpsert(JSONObject allJson, Map<String, Object> parameterUpsert) {

        JSONObject upsertData = JSONObject.parseObject(JSON.toJSONString(parameterUpsert));
        allJson.put("upsert", upsertData);
        return allJson;
    }

    /**
     * 添加修改一批id的信息
     *
     * @param parameter Map < String, Map< String, Object > >
     *                  Map < id值 , Map< 字段名, 修改值 > >
     */
    public void updateParameterById(Map<Object, Map<String, Object>> parameter) {
        if (parameter.size() == 0) {
            logger.info("error: Parameter cannot be null !");
            return;
        }
        Set keys = parameter.keySet();
        for (Object key : keys) {
            updateParameterById(parameter.get(key), key.toString());
        }
    }
}

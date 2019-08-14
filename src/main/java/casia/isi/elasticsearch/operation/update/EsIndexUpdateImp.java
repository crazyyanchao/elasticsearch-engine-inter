package casia.isi.elasticsearch.operation.update;

import java.util.Map;
import java.util.Random;
import java.util.Set;

import casia.isi.elasticsearch.operation.http.*;
import casia.isi.elasticsearch.util.ClientUtils;
import com.alibaba.fastjson.JSON;
import org.apache.log4j.Logger;

import casia.isi.elasticsearch.common.Symbol;

import com.alibaba.fastjson.JSONObject;

/**
 * ElasticSearch
 *
 * @author wzy
 * @version elasticsearch - 5.6.3
 */
public class EsIndexUpdateImp {
    private static Logger logger = Logger.getLogger(EsIndexUpdateImp.class);
    /**
     * 是否开启debug模式，debug模式下过程语句将会输出
     */
    public static boolean debug = false;
    /**
     * 索引名称
     */
    private static String indexname = null;
    /**
     * 类型名称
     */
    private static String typename = null;
    /**
     * 空格符
     */
    private final String BLANK = " ";
    /**
     * 更新地址
     */
    private static String update_index = null;
    /**
     * IP
     */
    private static String IP = null;
    /**
     * Port
     */
    private static int Port = 0;

    /**
     * http访问对象 仅仅支持绝对地址接口访问
     */
//    public HttpRequest httpRequest =  new HttpRequest();

    /**
     * http访问对象 支持绝对接口地址和相对接口地址
     **/
    public HttpProxyRequest httpRequest = new HttpProxyRequest(HttpPoolSym.DEFAULT.getSymbolValue());

    /**
     * 重置条件
     */
    public void reset() {
        try {
            this.debug = false;
        } catch (Exception e) {
        }
    }

    @Deprecated
    public EsIndexUpdateImp() {
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
            this.indexname = indexname;
            this.typename = typename;
            this.update_index = "http://" + servers[new Random().nextInt(servers.length)];
            this.update_index = indexname == null ? "" : this.update_index + "/" + indexname;
            this.update_index = typename == null ? "" : this.update_index + "/" + typename;

            // 新增HTTP负载均衡器
            HttpProxyRegister.register(IPADRESS);

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
            this.indexname = indexname;
            this.typename = typename;
            this.update_index = "http://" + this.IP + ":" + this.Port;
            this.update_index = indexname == null ? "" : this.update_index + "/" + indexname;
            this.update_index = typename == null ? "" : this.update_index + "/" + typename;

            // 新增HTTP负载均衡器
            HttpProxyRegister.register(IP + ":" + Port);
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

            String queryResultStr = httpRequest.httpPost(ClientUtils.referenceUrl(posturl), allJson.toString());

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
            String queryResultStr = httpRequest.httpPost(ClientUtils.referenceUrl(posturl), allJson.toString());

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

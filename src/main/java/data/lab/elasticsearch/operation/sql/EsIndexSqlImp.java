package data.lab.elasticsearch.operation.sql;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import data.lab.elasticsearch.common.EsAccessor;
import data.lab.elasticsearch.operation.http.HttpProxyRegister;
import data.lab.elasticsearch.operation.http.HttpSymbol;
import data.lab.elasticsearch.common.Symbol;
import data.lab.elasticsearch.operation.http.HttpRequest;
import data.lab.elasticsearch.util.StringUtil;
import data.lab.elasticsearch.util.Validator;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class EsIndexSqlImp extends EsAccessor {

    /**
     * http访问对象
     */
    private HttpRequest request = null;
    /**
     * 查询索引的url
     */
    private String queryUrl;

    /**
     * 设置日志输出对象
     *
     * @param //LOGGER log4j对象
     */
    public void setLogger(Logger logger) {
        super.logger = logger;
    }

    /**
     * 查询的返回值
     */
    private JSONObject queryJsonResult = null;

    /**
     * 重置搜索条件
     */
    public void reset() {

        try {
            this.queryJsonResult = null;
        } catch (Exception e) {
        }
    }

    @Deprecated
    public EsIndexSqlImp() {
    }

    public EsIndexSqlImp(HttpSymbol httpPoolName, String ipPorts) {
        super(httpPoolName, ipPorts);
        if (ipPorts == null) {
            logger.error("ip must not be null");
        }
        String[] servers = ipPorts.split(Symbol.COMMA_CHARACTER.toString());
        //构造查询url
        this.queryUrl = "http://" + servers[new Random().nextInt(servers.length)];
        this.queryUrl = this.queryUrl + "/_sql";
        this.request = new HttpRequest();

        // 新增HTTP负载均衡器
        HttpProxyRegister.register(ipPorts);
    }

    /**
     * 构造函数，传入索引地址，索引名和类型名
     *
     * @param IPADRESS 索引的ip和端口，格式 ip:port 多个以,隔开
     */
    public EsIndexSqlImp(String IPADRESS) {
        if (IPADRESS == null) {
            logger.error("ip must not be null");
        }
        String[] servers = IPADRESS.split(Symbol.COMMA_CHARACTER.toString());
        //构造查询url
        this.queryUrl = "http://" + servers[new Random().nextInt(servers.length)];
        this.queryUrl = this.queryUrl + "/_sql";
        this.request = new HttpRequest();

        // 新增HTTP负载均衡器
        HttpProxyRegister.register(IPADRESS);
    }

    /**
     * 构造函数，传入索引地址，索引名和类型名
     *
     * @param IP   索引的ip
     * @param Port 索引的ip端口
     */
    @Deprecated
    public EsIndexSqlImp(String IP, int Port) {
        if (IP == null || Port == 0) {
            logger.error("ip must not be null");
        }
        //构造查询url
        this.queryUrl = "http://" + IP + ":" + Port;
        this.queryUrl = this.queryUrl + "/_sql";
        this.request = new HttpRequest();
    }

    /**
     * 通过sql语法查询es索引
     * 例如：select * from indexName limit 10;
     * 查询indexName索引所有数据前十条
     *
     * @param sql
     */
    public void queryBySql(String sql) {
        if (!Validator.check(sql)) {
            logger.error("parameter sql cannot be empty!");
            return;
        }
        if (debug) {
            logger.info("query -d " + this.queryUrl + "; sql:" + sql);
        }
        String query_url = this.queryUrl + "?format=txt";
        System.out.println(StringUtil.urlEscape(query_url));

        String queryResult = request.httpGet(StringUtil.urlEscape(query_url));

        JSONObject para = new JSONObject();
        para.put("query", sql);
//        String queryResult = request.httpProxySendJsonBody(ClientUtils.referenceUrl(query_url), para.toString());
        if (debug) {
            logger.info("queryResult: -d " + queryResult);
        }
        if (queryResult != null)
            this.queryJsonResult = JSONObject.parseObject(queryResult);
    }

    /**
     * 返回检索结果，返回的检索字段以及字段顺序由{@link #//execute(String[])} 方法中的参数fields指定
     *
     * @return 检索的结果列表
     */
    public List<Map<String, String>> getResults() {
        List<Map<String, String>> list = new LinkedList<Map<String, String>>();
        if (this.queryJsonResult == null || this.queryJsonResult.size() == 0 || !this.queryJsonResult.containsKey("hits"))
            return list;
        JSONArray hitJsons = this.queryJsonResult.getJSONObject("hits").getJSONArray("hits");
        for (int index = 0; index < hitJsons.size(); index++) {
            JSONObject hitJson = hitJsons.getJSONObject(index);
            JSONObject json = hitJson.getJSONObject("_source");
            Set<String> keys = json.keySet();
            Map<String, String> map = new HashMap<String, String>();
            for (String key : keys) {
                map.put(key, json.getString(key));
            }
            list.add(map);
        }
        return list;
    }
}

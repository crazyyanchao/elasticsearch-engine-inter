package casia.isi.elasticsearch.operation.update;

import casia.isi.elasticsearch.common.FieldOccurs;
import casia.isi.elasticsearch.operation.http.HttpSymbol;
import casia.isi.elasticsearch.util.ClientUtils;
import casia.isi.elasticsearch.util.StringUtil;
import casia.isi.elasticsearch.util.Validator;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.spreada.utils.chinese.ZHConverter;
import org.apache.log4j.Logger;

/**
 * @author
 */
public class EsIndexUpdate extends EsIndexUpdateImp {

    public static ZHConverter converter = ZHConverter
            .getInstance(ZHConverter.SIMPLIFIED);

    // 索引集群连接的IP-PORT用冒号分隔
    private String ipPort;

    // 索引名称/多个索引名称使用逗号分隔
    private String indexName;

    // 索引类型
    private String typeName;


    // 记录关键词，以及关键词出现情况
    private String keywordString = "";

    // 空格符
    public final String BLANK = " ";

    // 是否将查询语法繁转简
    public static boolean ZH_Converter = false;

    /**
     * 查询索引的url
     */
    public String queryUrl;

    /**
     * 查询的字段
     */
    private String[] fields;

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
     * 构建聚合结果数量
     */
    public long countTotle = 0;
    /**
     * 分片返回最大数量
     */
    private static long shard_size = 100000;


    /**
     * @param
     * @return
     * @Description: TODO(不支持多个INDEX NAME更新)
     */
    @Deprecated
    public EsIndexUpdate(String IP, int Port, String indexName, String typeName) {
        super(IP, Port, indexName, typeName);
    }

    public EsIndexUpdate(HttpSymbol httpPoolName, String ipPorts, String indexName, String typeName) {
        super(httpPoolName,ipPorts,indexName,typeName);
    }

    /**
     * @param
     * @return
     * @Description: TODO(不支持多个INDEX NAME更新)
     */
    public EsIndexUpdate(String IpPort, String indexName, String typeName) {
        super(IpPort, indexName, typeName);
    }

    /**
     * 此方法用于对一些不可分词的数据进行检索，例如int,long型数据的genus，alarm等字段 <br>
     * 本方法也可以对分词的数据进行检索，但传入的内容不能有任何的标点符号以及空格，否则检索结果会出现异常<br>
     * 该方法用于int,long,string等类型的匹配，对于string类型，支持通配符(*)<br>
     *
     * @param field  字段
     * @param terms  字段对应的值，只能转入一个，且不能有有空格.如果传入的值为负值，请用双引号包起来，例如：<br>
     *               client.addPrimitveTermQuery("eid", "\"-1\"",FieldOccurs.MUST); 段对应的多值，值之间是或的关系
     * @param occurs 是否必须作为过滤条件
     */
    public void addPrimitiveTermFilter(String field, String[] terms, FieldOccurs occurs) {
        if (terms == null || terms.length == 0)
            return;
        keywordString = keywordString + BLANK + occurs.getSymbolValue() + field
                + ":(";
        for (int i = 0; i < terms.length; i++) {
            String term = terms[i];
            if (term == null || term.trim().equals("")) {
                continue;
            }
            if (ZH_Converter) {
                term = converter.convert(term);
            }
            term = StringUtil.escapeSolrQueryChars(term);
            if (i > 0) {
                keywordString = keywordString + " OR ";
            }
            keywordString = keywordString + term;
        }
        keywordString = keywordString + ")";
    }

    /**
     * 获取提交请求串
     *
     * @return
     */
    public String getQueryString() {

        JSONObject queryboolJson = new JSONObject();
        JSONObject queryFilterBoolJson = new JSONObject();

        //当不设置查询条件时，匹配全部
        if (keywordString == null || keywordString.trim().equals("")) {

            // 不设置查询条件时，取消匹配全部的操作
            // keywordString = "*:*";
        }

        if (keywordString != null && !keywordString.trim().equals("")) {
            //添加条件
            if (keywordString.startsWith(BLANK)) {
                keywordString = keywordString.substring(1);
            }
            String queryCondition = "";
            if (this.queryJson.containsKey("query")) {
                queryCondition = this.queryJson.getString("query") + BLANK + keywordString.trim();
            } else {
                queryCondition = keywordString.trim();
            }
            JSONObject queryJson = new JSONObject();
            JSONObject queryStringJson = new JSONObject();
            queryJson.put("query", queryCondition);
            queryStringJson.put("query_string", queryJson);
            this.queryMustJarr.add(queryStringJson);
        }

        if (this.queryFilterMustJarr.size() > 0) {
            //添加过滤必须区间
            queryFilterBoolJson.put("must", this.queryFilterMustJarr);
        }
        if (this.queryFilterMustNotJarr.size() > 0) {
            //添加过滤非区间
            queryFilterBoolJson.put("must_not", this.queryFilterMustNotJarr);
        }

//        if (this.termFilterJarry.size() > 0) {
//            //过滤查询
//            //过滤区间
//            JSONObject andtermJson = new JSONObject();
//            andtermJson.put("and", termFilterJarry.toString());
//            andFilterCondition.add(andtermJson);
//        }

        if (Validator.check(queryFilterBoolJson)) {
            //添加过滤区间
            JSONObject queryFilter = new JSONObject();
            queryFilter.put("bool", queryFilterBoolJson);
            queryboolJson.put("filter", queryFilter);
        }

        //添加过滤条件
//		if(andFilterCondition.size() > 0){
//			JSONObject andFilterJson = new JSONObject();
//			andFilterJson.put("and", andFilterCondition);
////			filterConditionJson.put("filter", andFilterJson);
//		}

        if (Validator.check(this.queryMustJarr)) {
            queryboolJson.put("must", this.queryMustJarr);
        }
        if (Validator.check(this.queryMustNotJarr)) {
            queryboolJson.put("must_not", this.queryMustNotJarr);
        }
//        if (Validator.check(this.fields)) {
//            //返回值字段
//            this.queryJson.put("_source", this.fields);
//        }

        JSONObject queryJson = new JSONObject();
        queryJson.put("bool", queryboolJson);
        this.queryJson.put("query", queryJson);
        String queryStr = this.queryJson.toString();
        return queryStr;
    }

    public void addGroovyScriptField() {
//        POST soc-system/_update_by_query
//        {
//            "script": {
//            "source": "ctx._source['area']='无'"
//        },
//            "query": {
//            "bool": {
//                "must_not": [
//                {
//                    "exists": {
//                    "field": "area"
//                }
//                }
//      ]
//            }
//        }
//        }
    }

    /**
     * 提交更新请求
     *
     * @return
     */
    public JSONObject execute() {
        String queryUrl = queryUrl();
        String esQuery = getQueryString();
        if (debug) {
            logger.info("curl:" + queryUrl + " -d " + esQuery);
            System.out.println("curl:" + queryUrl + " -d " + esQuery);
        }
        String queryResult = super.request.httpPost(ClientUtils.referenceUrl(queryUrl), esQuery);
        if (queryResult != null)
            return JSONObject.parseObject(queryResult);
        if (debug) {
            logger.info("queryResult: -d " + queryResult);
        }
        return null;
    }

    /**
     * @param
     * @return
     * @Description: TODO(构造接口URL)
     */
    private String queryUrl() {
        return "http://" + ipPort + "/" + indexName + "/" + typeName + "/_update_by_query";
    }

    /**
     * 重置搜索条件
     */
    public void reset() {
        try {
            this.keywordString = "";
        } catch (Exception e) {
        }
    }

}


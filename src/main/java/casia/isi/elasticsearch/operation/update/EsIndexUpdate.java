package casia.isi.elasticsearch.operation.update;

import casia.isi.elasticsearch.common.FieldOccurs;
import casia.isi.elasticsearch.operation.http.HttpSymbol;
import casia.isi.elasticsearch.util.ClientUtils;
import casia.isi.elasticsearch.util.StringUtil;
import casia.isi.elasticsearch.util.Validator;
import com.alibaba.fastjson.JSONObject;

/**
 * @author
 */
public class EsIndexUpdate extends EsIndexUpdateImp {

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
        super(httpPoolName, ipPorts, indexName, typeName);
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

    /**
     * 获取提交请求串
     *
     * @return
     */
    public String getQueryString(String... fieldValue) {
        String esQuery = getQueryString();
        this.queryJson = JSONObject.parseObject(esQuery);
        this.queryJson.put("script", putGroovyScriptField(fieldValue));
        String queryStr = this.queryJson.toString();
        return queryStr;
    }

    private JSONObject putGroovyScriptField(String[] fieldValue) {
        JSONObject object = new JSONObject();
        if (fieldValue.length % 2 == 0) {
            StringBuilder builder = new StringBuilder();
            String[] keys = fieldValue;

            for (int i = 0; i < keys.length; i++) {
                String key = keys[i];
                Object value = keys[i + 1];
                builder.append("ctx._source['" + key + "'] = '" + value + "';");
                i += 1;
                if (i >= keys.length) break;
            }
            String inline = builder.substring(0, builder.length() - 1);
            object.put("inline", inline);
        }
        return object;
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
     * @param isWaitResponse:是否等待响应
     * @return
     * @Description: TODO(是否在集群中以后台任务的形式执行删除)
     */
    public void setWaitForCompletion(boolean isWaitResponse) {
        this.isWaitResponse=isWaitResponse;
    }

    /**
     * 提交更新请求
     *
     * @return
     */
    public JSONObject execute(String... fieldValue) {
        this.queryUrl = queryUrl();
        String esQuery = getQueryString(fieldValue);
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
        this.queryUrl = "http://" + ipPort + "/" + indexName + "/" + typeName + "/_update_by_query";
        this.queryUrl = this.queryUrl + "?wait_for_completion=" + isWaitResponse + "";
        return queryUrl;
    }

}


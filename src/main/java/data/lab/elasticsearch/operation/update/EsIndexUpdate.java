package data.lab.elasticsearch.operation.update;

import data.lab.elasticsearch.common.EsAccessor;
import data.lab.elasticsearch.common.FieldOccurs;
import data.lab.elasticsearch.operation.http.HttpSymbol;
import data.lab.elasticsearch.util.ClientUtils;
import data.lab.elasticsearch.util.StringUtil;
import data.lab.elasticsearch.util.Validator;
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
        super.keywordString = super.keywordString + super.BLANK + occurs.getSymbolValue() + field
                + ":(";
        for (int i = 0; i < terms.length; i++) {
            String term = terms[i];
            if (term == null || term.trim().equals("")) {
                continue;
            }
            if (EsIndexUpdateImp.ZH_Converter) {
//                term = super.converter.convert(term);
            }
            term = StringUtil.escapeSolrQueryChars(term);
            if (i > 0) {
                super.keywordString = super.keywordString + " OR ";
            }
            super.keywordString = super.keywordString + term;
        }
        super.keywordString = super.keywordString + ")";
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
        if (super.keywordString == null || super.keywordString.trim().equals("")) {

            // 不设置查询条件时，取消匹配全部的操作
            // keywordString = "*:*";
        }

        if (super.keywordString != null && !super.keywordString.trim().equals("")) {
            //添加条件
            if (super.keywordString.startsWith(super.BLANK)) {
                super.keywordString = super.keywordString.substring(1);
            }
            String queryCondition = "";
            if (super.queryJson.containsKey("query")) {
                queryCondition = super.queryJson.getString("query") +super. BLANK + super.keywordString.trim();
            } else {
                queryCondition = super.keywordString.trim();
            }
            JSONObject queryJson = new JSONObject();
            JSONObject queryStringJson = new JSONObject();
            queryJson.put("query", queryCondition);
            queryStringJson.put("query_string", queryJson);
            super.queryMustJarr.add(queryStringJson);
        }

        if (super.queryFilterMustJarr.size() > 0) {
            //添加过滤必须区间
            queryFilterBoolJson.put("must", super.queryFilterMustJarr);
        }
        if (super.queryFilterMustNotJarr.size() > 0) {
            //添加过滤非区间
            queryFilterBoolJson.put("must_not", super.queryFilterMustNotJarr);
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

        if (Validator.check(super.queryMustJarr)) {
            queryboolJson.put("must", super.queryMustJarr);
        }
        if (Validator.check(super.queryMustNotJarr)) {
            queryboolJson.put("must_not", super.queryMustNotJarr);
        }
//        if (Validator.check(this.fields)) {
//            //返回值字段
//            this.queryJson.put("_source", this.fields);
//        }

        JSONObject queryJson = new JSONObject();
        queryJson.put("bool", queryboolJson);
        super.queryJson.put("query", queryJson);
        String queryStr = super.queryJson.toString();
        return queryStr;
    }

    /**
     * 获取提交请求串
     *
     * @return
     */
    public String getQueryString(String... fieldValue) {
        String esQuery = getQueryString();
        super.queryJson = JSONObject.parseObject(esQuery);
        super.queryJson.put("script", putGroovyScriptField(fieldValue));
        String queryStr = super.queryJson.toString();
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
        EsAccessor.isWaitResponse =isWaitResponse;
    }

    /**
     * 提交更新请求
     *
     * @return
     */
    public JSONObject execute(String... fieldValue) {
        super.queryUrl = queryUrl();
        String esQuery = getQueryString(fieldValue);
        if (EsAccessor.debug) {
            super.logger.info("curl:" + super.queryUrl + " -d " + esQuery);
            System.out.println("curl:" + super.queryUrl + " -d " + esQuery);
        }
        String queryResult = super.request.httpPost(ClientUtils.referenceUrl(super.queryUrl), esQuery);
        if (queryResult != null)
            return JSONObject.parseObject(queryResult);
        if (EsAccessor.debug) {
            super.logger.info("queryResult: -d " + queryResult);
        }
        return null;
    }

    /**
     * @param
     * @return
     * @Description: TODO(构造接口URL)
     */
    private String queryUrl() {
        super.queryUrl = "http://" + super.ipPort + "/" + super.indexName + "/" + super.typeName + "/_update_by_query";
        super.queryUrl = super.queryUrl + "?wait_for_completion=" + EsAccessor.isWaitResponse + "";
        return super.queryUrl;
    }

}


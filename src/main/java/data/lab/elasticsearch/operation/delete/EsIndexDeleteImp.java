package data.lab.elasticsearch.operation.delete;

import java.util.List;
import java.util.Random;

import data.lab.elasticsearch.common.*;
import data.lab.elasticsearch.operation.http.HttpProxyRegister;
import data.lab.elasticsearch.operation.http.HttpSymbol;
import data.lab.elasticsearch.util.ClientUtils;
import data.lab.elasticsearch.util.StringUtil;
import data.lab.elasticsearch.util.Validator;
import org.apache.lucene.queryparser.classic.QueryParser;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * delete
 *
 * @author
 * @version elasticsearch - 5.6.0
 */
public class EsIndexDeleteImp extends EsAccessor {

//    private static ZHConverter converter = ZHConverter
//            .getInstance(ZHConverter.SIMPLIFIED);
    /**
     * 索引ip
     */
    private static String IP;
    /**
     * 索引端口
     */
    private static int Port;
    /**
     * 索引ip+port
     */
    private static String IpPort;
    /**
     * 索引名称
     */
    public static String IndexName;
    /**
     * 索引类型
     */
    private static String IndexType;
    /**
     * 删除索引地址
     */
    public static String deleteUrl;

    /**
     * 接口地址
     **/
    public static String delete_url;

    /**
     * 空格符
     */
    private final String BLANK = " ";
    /**
     * 是否将查询语法繁转简
     */
    public static boolean ZH_Converter = false;
    /**
     * queryResult 结果
     */
    public String queryResult;
    /**
     * 构造删除条件的json串
     */
    public JSONObject queryJson = null;
    /**
     * 构造查询必须条件的Arry串
     */
    private JSONArray queryMustJarr = null;
    /**
     * 构造查询否定条件的Arry串
     */
    private JSONArray queryMustNotJarr = null;
    /**
     * 构造过滤否定条件的json串
     */
    public JSONArray queryFilterMustNotJarr = null;
    /**
     * 构造过滤肯定条件的json串
     */
    public JSONArray queryFilterMustJarr = null;
    /**
     * 记录关键词，以及关键词出现情况
     */
    private String keywordString = "";

    /**
     * 删除接口配置的参数
     **/
    public static String deleteParameters = "";

    /**
     * 构造函数 - 支持配置多个地址
     *
     * @param ipAndport
     * @param indexName 索引块名
     * @param typeName  索引类型
     */
    public EsIndexDeleteImp(String ipAndport, String indexName, String typeName) {
        IndexDelete(ipAndport, indexName, typeName);
    }

    public EsIndexDeleteImp(HttpSymbol httpPoolName, String ipPorts, String indexName, String typeName) {
        super(httpPoolName, ipPorts);
        IndexDelete(ipPorts, indexName, typeName);
    }

    /**
     * 构造函数 - 支持配置一个地址
     *
     * @param ip
     * @param port
     * @param indexName 索引块名
     * @param typeName  索引类型
     */
    @Deprecated
    public EsIndexDeleteImp(String ip, String port, String indexName, String typeName) {
        IndexDelete(ip, port, indexName, typeName);
    }

    /**
     * 构造函数
     *
     * @param IPADRESS
     * @param indexName 索引块名
     * @param typeName  索引类型
     */
    private void IndexDelete(String IPADRESS, String indexName, String typeName) {
        if (indexName == null) {
            logger.error("indexName must not be null");
        }
        String[] servers = IPADRESS.split(Symbol.COMMA_CHARACTER.getSymbolValue());
        //构造查询url
        this.deleteUrl = "http://" + servers[new Random().nextInt(servers.length)];
        this.IndexName = indexName;
        this.IndexType = typeName;
        newObject();


        // 新增HTTP负载均衡器
        HttpProxyRegister.register(IPADRESS);
    }

    /**
     * 构造函数
     *
     * @param ip
     * @param port
     * @param indexName 索引块名
     * @param typeName  索引类型
     */
    private void IndexDelete(String ip, String port, String indexName, String typeName) {
        if (indexName == null) {
            logger.error("indexName must not be null");
        }
        //构造查询url
        this.deleteUrl = "http://" + ip;
        this.deleteUrl = this.deleteUrl + ":" + port;
        this.IndexName = indexName;
        this.IndexType = typeName;
        newObject();

        // 新增HTTP负载均衡器
        HttpProxyRegister.register(IP + ":" + Port);
    }

    private void newObject() {
        this.queryJson = new JSONObject();
        this.queryMustJarr = new JSONArray();
        this.queryMustNotJarr = new JSONArray();
        this.queryFilterMustNotJarr = new JSONArray();
        this.queryFilterMustJarr = new JSONArray();
    }

    /**
     * 重置
     */
    public void reset() {
        this.debug = false;
        this.queryResult = "";
        this.keywordString = "";
        this.queryJson.clear();
        this.queryMustJarr.clear();
        this.queryMustNotJarr.clear();
        this.queryFilterMustNotJarr.clear();
        this.queryFilterMustJarr.clear();
        this.deleteParameters = "";
        this.delete_url = "";
    }

    /**
     * 根据主键id删除单个索引
     *
     * @param //indexName 索引名
     * @param //id        主键
     * @return
     */
    @Deprecated
    public boolean deleteByIds(List<String> ids) {
        boolean rs = deleteByIdsRun(ids);
        return rs;
    }

    /**
     * 根据主键id删除单个索引
     *
     * @param //indexName 索引名
     * @param id          主键
     * @return
     */
    public boolean deleteById(String id) {
        String delete_url = this.deleteUrl + "/" + this.IndexName + "/" + this.IndexType + "/";
        this.queryResult = request.postDeleteRequest(ClientUtils.referenceUrl(delete_url + id), null);
        try {
            if (this.queryResult != null) {
                JSONObject json = new JSONObject().parseObject(this.queryResult);
                if (json.getBoolean("found")) {
                    return true;
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        return false;
    }

    /**
     * 根据多个主键id删除索引
     *
     * @param //indexName 索引名
     * @param //id        主键
     * @return
     */
    private boolean deleteByIdsRun(List<String> idlist) {
        String delete_Url = this.deleteUrl + "/" + this.IndexName + "/" + this.IndexType + "/_query?pretty";
        JSONArray jsonArray = new JSONArray();
        for (String id : idlist) {
            jsonArray.add(id);
        }
        JSONObject json = new JSONObject();
        json.put("id", jsonArray);

        JSONObject termJson = new JSONObject();
        termJson.put("match", json);

        JSONObject queryJson = new JSONObject();
        queryJson.put("query", termJson);
        String queryStr = queryJson.toString();
        if (debug) {
            logger.info("curl:" + delete_Url + " -d " + queryStr);
            System.out.println("curl:" + delete_Url + " -d " + queryStr);
        }
        this.queryResult = request.postDeleteRequest(ClientUtils.referenceUrl(delete_Url), queryStr);
        return true;
    }

    /**
     * 根据字段检索匹配删除数据
     *
     * @param Field 字段名
     * @param par   参数
     * @return
     */
    public boolean deleteByQueryRun(String Field, String par) {
        try {
            String delete_url = this.deleteUrl + "/" + this.IndexName + "/" + this.IndexType + "/_delete_by_query";

            JSONObject json = new JSONObject();
            json.put(Field, par);

            JSONObject termJson = new JSONObject();
            termJson.put("match", json);

            JSONObject queryJson = new JSONObject();
            queryJson.put("query", termJson);

            String queryStr = queryJson.toString();
            if (debug) {
                logger.info("curl:" + delete_url + " -d " + queryStr);
                System.out.println("curl:" + delete_url + " -d " + queryStr);
            }
            this.queryResult = request.httpPost(ClientUtils.referenceUrl(delete_url), queryStr);
        } catch (Exception e) {
            logger.error("delete fail, " + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 根据字段检索匹配删除数据
     *
     * @param Field 字段名
     * @param par   参数
     * @return
     */
    public boolean deleteByNotQueryRun(String Field, String par) {
        try {
            String delete_url = this.deleteUrl + "/" + this.IndexName + "/" + this.IndexType + "/_delete_by_query";

            JSONObject json = new JSONObject();
            json.put(Field, par);

            JSONObject termJson = new JSONObject();
            termJson.put("match", json);

            JSONArray match = new JSONArray();
            match.add(termJson);

            JSONObject must_not = new JSONObject();
            must_not.put("must_not", match);

            JSONObject boolJson = new JSONObject();
            boolJson.put("bool", must_not);

            JSONObject queryJson = new JSONObject();
            queryJson.put("query", boolJson);

            String queryStr = queryJson.toString();
            if (debug) {
                logger.info("curl:" + delete_url + " -d " + queryStr);
                System.out.println("curl:" + delete_url + " -d " + queryStr);
            }
            this.queryResult = request.httpPost(ClientUtils.referenceUrl(delete_url), queryStr);
        } catch (Exception e) {
            logger.error("delete fail, " + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 根据字段检索设定范围匹配，删除数据
     *
     * @param Field 字段名
     * @param start 范围参数
     * @param end   范围参数
     * @return
     */
    public boolean deleteByRangeRun(String Field, String start, String end) {
        try {
            String delete_url = this.deleteUrl + "/" + this.IndexName + "/" + this.IndexType + "/_delete_by_query";

            JSONObject se = new JSONObject();
            se.put("gte", start);
            se.put("gte", end);

            JSONObject json = new JSONObject();
            json.put(Field, se);

            JSONObject termJson = new JSONObject();
            termJson.put("range", json);

            JSONObject queryJson = new JSONObject();
            queryJson.put("query", termJson);

            String queryStr = queryJson.toString();
            if (debug) {
                logger.info("curl:" + delete_url + " -d " + queryStr);
                System.out.println("curl:" + delete_url + " -d " + queryStr);
            }
            this.queryResult = request.httpPost(ClientUtils.referenceUrl(delete_url), queryStr);
        } catch (Exception e) {
            logger.error("delete fail, " + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 根据字段检索非设定范围匹配，删除数据
     *
     * @param Field 字段名
     * @param start 范围参数
     * @param end   范围参数
     * @return
     */
    public boolean deleteByNotRangeRun(String Field, String start, String end) {
        try {
            String delete_url = this.deleteUrl + "/" + this.IndexName + "/" + this.IndexType + "/_delete_by_query";

            JSONObject se = new JSONObject();
            se.put("gte", start);
            se.put("gte", end);

            JSONObject json = new JSONObject();
            json.put(Field, se);

            JSONObject termJson = new JSONObject();
            termJson.put("range", json);

            JSONArray arr = new JSONArray();
            arr.add(termJson);

            JSONObject must_not = new JSONObject();
            must_not.put("must_not", arr);

            JSONObject boolJson = new JSONObject();
            boolJson.put("bool", must_not);

            JSONObject queryJson = new JSONObject();
            queryJson.put("query", boolJson);

            String queryStr = queryJson.toString();
            if (debug) {
                logger.info("curl:" + delete_url + " -d " + queryStr);
                System.out.println("curl:" + delete_url + " -d " + queryStr);
            }
            this.queryResult = request.httpPost(ClientUtils.referenceUrl(delete_url), queryStr);
        } catch (Exception e) {
            logger.error("delete fail, " + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 根据索引名及其类型，删除所有数据
     *
     * @return
     */
    public boolean deleteByIndexNameRun() {
        try {
            String delete_url = this.deleteUrl + "/" + this.IndexName + "/" + this.IndexType + "/_delete_by_query";

            JSONObject json = new JSONObject();
            JSONObject termJson = new JSONObject();
            termJson.put("match_all", json);

            JSONObject queryJson = new JSONObject();
            queryJson.put("query", termJson);

            String queryStr = queryJson.toString();
            if (debug) {
                logger.info("curl:" + delete_url + " -d " + queryStr);
                System.out.println("curl:" + delete_url + " -d " + queryStr);
            }
            this.queryResult = request.httpPost(ClientUtils.referenceUrl(delete_url), queryStr);
        } catch (Exception e) {
            logger.error("delete fail, " + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 删除索引
     *
     * @return
     */
    public boolean deleteIndexNameRun() {
        if (!this.IndexName.contains("_all")) {
            String delete_url = this.deleteUrl + "/" + this.IndexName;
            if (debug) {
                logger.info("curl:" + delete_url + " -d " + null);
                System.out.println("curl:" + delete_url + " -d " + null);
            }
            String queryResult = request.postDeleteRequest(ClientUtils.referenceUrl(delete_url), null);
            JSONObject deleteJsonResult = JSONObject.parseObject(queryResult);
            if (deleteJsonResult.containsKey("acknowledged")) {
                return deleteJsonResult.getBoolean("acknowledged");
            }
        }
        return false;
    }

    /**
     * 获取结果，删除文档数量
     *
     * @return
     */
    public long getDeleteTotal() {
        JSONObject deleteJsonResult = JSONObject.parseObject(queryResult);
        long delnumber = deleteJsonResult.getLong("deleted");
        logger.info("delete datas " + delnumber + " by " + this.IndexName + "/" + this.IndexType);
        return delnumber;
    }

    /*--------------定制化条件--------------------*/

    /**
     * 添加非空过滤
     *
     * @param field
     */
    public void addExistsFilter(String field) {
        JSONObject existsJson = new JSONObject();
        JSONObject json = new JSONObject();
        json.put("field", field);
        existsJson.put("exists", json);
        this.queryFilterMustJarr.add(existsJson);
    }

    /**
     * 添加空值过滤
     *
     * @param field
     */
    public void addMissingFilter(String field) {
        JSONObject existsJson = new JSONObject();
        JSONObject json = new JSONObject();
        json.put("field", field);
        existsJson.put("exists", json);
        this.queryFilterMustNotJarr.add(existsJson);
//		existsJson.put("missing", json);
//		this.queryFilterMustJarr.add( existsJson );
    }

    /**
     * 筛选某区间内的数据，筛选的字段必须为数字形式。 如时间、 id、 评论数等
     *
     * @param field     筛选的字段
     * @param startTerm 区间开始值
     * @param endTerm   区间结束值
     * @param occurs    是否必须作为过滤条件 一般为must
     */
    public void addRangeTerms(String field, String startTerm, String endTerm, FieldOccurs occurs) {
        if (!Validator.check(field)) {
            return;
        }
        if ((!Validator.check(startTerm)) && (!Validator.check(endTerm))) {
            return;
        }
        JSONObject fieldJson = new JSONObject();
        if (Validator.check(startTerm)) {
            //大于等于
            fieldJson.put("gte", startTerm);
        }
        if (Validator.check(endTerm)) {
            //小于
            fieldJson.put("lte", endTerm);
        }
        JSONObject json = new JSONObject();
        JSONObject rangejson = new JSONObject();
        json.put(field, fieldJson);
        rangejson.put("range", json);
        if (occurs.equals(FieldOccurs.MUST)) {
            this.queryFilterMustJarr.add(rangejson);
        } else if (occurs.equals(FieldOccurs.MUST_NOT)) {
            this.queryFilterMustNotJarr.add(rangejson);
        }
    }

    /**
     * 构造删除关键词匹配片段,如果输入的关键词以空格分割，那么将以空格切分开，作为多个关键词处理，多个关键词之间的关系由KeywordsCombine对象指定<br>
     * 关键词查询会走分词，如关键词为 北京科技大学，则有可能会搜索出北京大学的记录来<br>
     * 该方法对索引中字段类型配置为 "analyzer": "ik_max_word"（即有分词的类型）才有效<br>
     *
     * @param field    要匹配的字段
     * @param keywords 关键词
     * @param occurs   字段出现情况
     * @param combine  以空格隔开的关键词的关系
     */
    private void addKeywordsQuery(String field, String keywords, FieldOccurs occurs, KeywordsCombine combine) {
        if (!Validator.check(keywords))
            return;
        if (ZH_Converter) {
//            keywords = converter.convert(keywords);
        }

        String[] phraseSplit = keywords.split("\\s+");
        if (field != null) {
            String queryString = occurs.getSymbolValue() + field + ":(" + "";
            boolean first = true;
            for (String p : phraseSplit) {
                if (!Validator.check(p))
                    continue;
                //对搜索词中的特殊字符做转移处理
                p = QueryParser.escape(p);
                if (first) {
                    queryString += p;
                    first = false;
                } else {
                    queryString += (BLANK + combine.name() + BLANK + p);
                }
            }
            queryString += ")";
            keywordString += (BLANK + queryString);
        }
    }

    /**
     * 构造删除关键词匹配片段,如果输入的关键词以空格分割，那么将以空格切分开，作为多个关键词处理，多个关键词之间的关系由KeywordsCombine对象指定<br>
     * 关键词查询会走分词，如关键词为 北京科技大学，则有可能会搜索出北京大学的记录来<br>
     * 该方法对索引中字段类型配置为 "analyzer": "ik_max_word"（即有分词的类型）才有效<br>
     *
     * @param //field  要匹配的字段
     * @param keywords 关键词
     * @param combine  多个字段间的关系
     */
    private void addKeywordsQuery(String[] fields, String keywords, KeywordsCombine combine) {
        if (!Validator.check(keywords))
            return;
        if (ZH_Converter) {
//            keywords = converter.convert(keywords);
        }
        String query = "";
        query = BLANK + "(";

        for (int i = 0; i < fields.length; i++) {
            String queryString = fields[i] + ":(";
            String p = keywords;
            if (!Validator.check(p))
                continue;
            //对搜索词中的特殊字符做转移处理
            p = QueryParser.escape(p);
            queryString += p;
            queryString += ")";
            query += (BLANK + queryString);
            if (i < fields.length - 1)
                query += (BLANK + combine.name());
        }
        query = query + ")";
        keywordString += query;
    }

    /**
     * 构造删除短语匹配片段,如果输入的短语以空格分割，那么将以空格切分开，作为多个短语处理，多个短语之间的关系由KeywordsCombine对象指定<br>
     * 短语匹配不走分词，如短语为 北京科技大学，则必须完整含有北京科技大学的记录才会被匹配<br>
     * 该方法对es中字段类型配置为  "analyzer": "ik_max_word",（即有分词的类型）才有效<br>
     *
     * @param field    要删除的字段
     * @param keywords 关键词
     * @param occurs   字段出现情况
     * @param combine  关键词组合情况
     */
    public void addPhraseQuery(String field, String keywords, FieldOccurs occurs, KeywordsCombine combine) {
        if (!Validator.check(keywords))
            return;
        if (ZH_Converter) {
//            keywords = converter.convert(keywords);
        }
        String[] phraseSplit = keywords.split("\\s+");
        if (field != null) {
            String queryString = occurs.getSymbolValue() + field + ":(" + "";
            boolean first = true;
            for (String p : phraseSplit) {
                if (!Validator.check(p))
                    continue;
                //对搜索词中的特殊字符做转义处理
                p = QueryParser.escape(p);
                if (first) {
                    queryString += ("\"" + p + "\"");
                    first = false;
                } else {
                    queryString += (BLANK + combine.name() + BLANK + "\"" + p + "\"");
                }
            }
            queryString += ")";
            keywordString += (BLANK + queryString);
        }
    }

    /**
     * 构造删除短语匹配片段,如果输入的短语以空格分割，那么将以空格切分开，作为多个短语处理，多个短语之间的关系由KeywordsCombine对象指定<br>
     * 短语匹配不走分词，如短语为 北京科技大学，则必须完整含有北京科技大学的记录才会被匹配<br>
     * 该方法对es中字段类型配置为  "analyzer": "ik_max_word",才有效<br>
     *
     * @param fields   要匹配的字段多个字段
     * @param keywords 关键词
     * @param occurs   字段出现情况
     * @param combine  关键词组合情况
     */
    public void addPhraseQuery(String[] fields, String keywords, FieldOccurs occurs, KeywordsCombine combine, FieldCombine filedCombine) {
        if (!Validator.check(keywords))
            return;
        if (ZH_Converter) {
//            keywords = converter.convert(keywords);
        }
        String[] phraseSplit = keywords.split("\\s+");
        if (Validator.check(fields)) {
            String queryString = occurs.getSymbolValue() + "(" + BLANK;
            for (int i = 0; i < fields.length; i++) {
                if (i != 0) {
                    queryString += BLANK + filedCombine.name() + BLANK;
                }
                queryString += fields[i] + ":(";
                boolean first = true;
                for (String p : phraseSplit) {
                    if (!Validator.check(p))
                        continue;
                    //对搜索词中的特殊字符做转义处理
                    p = QueryParser.escape(p);
                    if (first) {
                        queryString += ("\"" + p + "\"");
                        first = false;
                    } else {
                        queryString += (BLANK + combine.name() + BLANK + "\"" + p + "\"");
                    }
                }
                queryString += ")";
            }
            queryString += ")";
            keywordString += (BLANK + queryString);
        }
    }

    /**
     * 此方法用于对一些不可分词的数据进行检索删除，例如int,long型数据等字段 <br>
     * 本方法也可以对分词的数据进行检索，但传入的内容不能有任何的标点符号以及空格，否则检索结果会出现异常<br>
     * 该方法用于int,long,string等类型的匹配，对于string类型，支持通配符(*)<br>
     *
     * @param field  字段
     * @param term   字段对应的值，只能转入一个，且不能有有空格.如果传入的值为负值，请用双引号包起来，例如：<br>
     *               client.addPrimitveTermQuery("eid", "\"-1\"",FieldOccurs.MUST);
     * @param occurs 是否必须作为过滤条件
     */
    public void addPrimitiveTermFilter(String field, String term, FieldOccurs occurs) {
        if (!Validator.check(term))
            return;
        if (ZH_Converter) {
//            term = converter.convert(term);
        }
        term = StringUtil.escapeSolrQueryChars(term);

        JSONObject termJson = new JSONObject();
        JSONObject json = new JSONObject();
        json.put(field, term);
        termJson.put("term", json);

        if (occurs == FieldOccurs.MUST) {
            //非
            this.queryMustJarr.add(termJson);
        } else if (occurs == FieldOccurs.MUST_NOT) {
            //必须
            this.queryMustNotJarr.add(termJson);
        }
    }

    /**
     * 获取提交请求串
     *
     * @param //fields 索引查询后要返回值的字段，只有建索引时，有存储的字段此处才可能有返回值，对于只索引不存储的字段，此处得不到返回值
     * @return
     */
    private String getDeleteString() {
        JSONObject queryboolJson = new JSONObject();
        JSONObject queryFilterBoolJson = new JSONObject();

        if (Validator.check(this.keywordString)) {
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
        if (Validator.check(queryFilterBoolJson)) {
            //添加过滤区间
            JSONObject queryFilter = new JSONObject();
            queryFilter.put("bool", queryFilterBoolJson);
            queryboolJson.put("filter", queryFilter);
        }

        if (Validator.check(this.queryMustJarr)) {
            queryboolJson.put("must", this.queryMustJarr);
        }
        if (Validator.check(this.queryMustNotJarr)) {
            queryboolJson.put("must_not", this.queryMustNotJarr);
        }

        JSONObject queryJson = new JSONObject();
        queryJson.put("bool", queryboolJson);
        this.queryJson.put("query", queryJson);
        String queryStr = this.queryJson.toString();
        return queryStr;
    }

    /**
     * 提交删除请求
     *
     * @return
     */
    public void execute() {
        String queryStr = getDeleteString();

        delete_url = getDleteHttpUrl();
        if (debug) {
            logger.info("curl:" + delete_url + " -d " + queryStr);
            System.out.println("curl:" + delete_url + " -d " + queryStr);
        }
        this.queryResult = request.httpPost(ClientUtils.referenceUrl(delete_url), queryStr);
        if (debug) {
            logger.info("queryResult: -d " + this.queryResult);
        }
    }

    /**
     * @return
     * @Description: TODO(获取删除接口地址)
     */
    private String getDleteHttpUrl() {
        if (!"".equals(deleteParameters)) {
            deleteParameters = "?" + deleteParameters.replace("?", "&").substring(1, deleteParameters.length());
        }
        return this.deleteUrl + "/" + this.IndexName + "/" + this.IndexType + "/_delete_by_query" + deleteParameters;

    }

}



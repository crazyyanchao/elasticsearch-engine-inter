package casia.isi.elasticsearch.operation.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import casia.isi.elasticsearch.common.*;
import casia.isi.elasticsearch.operation.http.*;
import casia.isi.elasticsearch.util.ClientUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.wltea.analyzer.lucene.IKAnalyzer;

import casia.isi.elasticsearch.util.StringUtil;
import casia.isi.elasticsearch.util.Validator;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.spreada.utils.chinese.ZHConverter;

/**
 * ElasticSearch
 *
 * @author
 * @version elasticsearch
 */
public class EsIndexSearchImp {

    /**
     * 日志对象
     */
    public static Logger logger = Logger.getLogger(EsIndexSearchImp.class);
    public static ZHConverter converter = ZHConverter
            .getInstance(ZHConverter.SIMPLIFIED);
    protected static IKAnalyzer analyzer = AnalyzerFactory.getInstanceMax();

    /**
     * 是否将查询语法繁转简
     */
    public static boolean ZH_Converter = false;
    /**
     * 是否开启debug模式，debug模式下过程语句将会输出
     */
    public static boolean debug = false;

    /**
     * 查询索引的url
     */
    public String queryUrl;


    /**
     * http访问对象 仅仅支持绝对地址接口访问
     */
//    public HttpRequest request =  new HttpRequest();

    /**
     * http访问对象 支持绝对接口地址和相对接口地址
     **/
    public HttpProxyRequest request = new HttpProxyRequest(HttpPoolSym.DEFAULT.getSymbolValue());

    /**
     * 查询的字段
     */
    public String[] fields;

    /**
     * 查询的返回值
     */
    public JSONObject queryJsonResult;

    /**
     * 空格符
     */
    public final String BLANK = " ";
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
     * 记录关键词，以及关键词出现情况
     */
    public String keywordString = "";

    /**
     * 构建聚合结果数量
     */
    public long countTotle = 0;
    /**
     * 分片返回最大数量
     */
    private static long shard_size = 100000;

    /**
     * 设置日志输出对象
     *
     * @param logger log4j对象
     */
    public void setLogger(Logger logger) {
        EsIndexSearchImp.logger = logger;
    }

    @Deprecated
    public EsIndexSearchImp() {
    }

    /**
     * 构造函数，传入索引地址，索引名和类型名 - 此构造函数支持传入多个地址
     *
     * @param IPADRESS  索引的ip和端口，格式 ip:port 多个逗号隔开
     * @param indexName 索引名称，允许多个索引，索引间用逗号隔开，格式 indexName1,indexName2
     * @param typeName  索引下的类型名称，允许多个类型，用逗号隔开,格式typeName1,typeName2
     */
    public EsIndexSearchImp(String IPADRESS, String indexName, String typeName) {
        if (IPADRESS == null) {
            logger.error("ip must not be null");
        }
        if (indexName == null && typeName == null) {
            logger.error("prefix must not be null");
        }
        String[] servers = IPADRESS.split(Symbol.COMMA_CHARACTER.getSymbolValue());
        //构造查询url
        this.queryUrl = "http://" + servers[new Random().nextInt(servers.length)];
        if (indexName != null)
            this.queryUrl = this.queryUrl + "/" + indexName;

        if (typeName != null)
            this.queryUrl = this.queryUrl + "/" + typeName;

        this.queryUrl = this.queryUrl + "/_search";
        this.queryJson = new JSONObject();
        this.queryJsonResult = new JSONObject();
        this.queryMustJarr = new JSONArray();
        this.queryMustNotJarr = new JSONArray();
        this.queryFilterMustJarr = new JSONArray();
        this.queryFilterMustNotJarr = new JSONArray();

        // 新增HTTP负载均衡器
        HttpProxyRegister.register(IPADRESS);
    }

    /**
     * 构造函数，传入索引地址，索引名和类型名 - 此构造函数只支持传入一个地址
     *
     * @param IP        索引的ip
     * @param Port      索引的ip端口
     * @param indexName 索引名称，允许多个索引，索引间用逗号隔开，格式 indexName1,indexName2
     * @param typeName  索引下的类型名称，允许多个类型，用逗号隔开,格式typeName1,typeName2
     */
    @Deprecated
    public EsIndexSearchImp(String IP, int Port, String indexName, String typeName) {
        if (IP == null || Port == 0) {
            logger.error("ip must not be null");
        }
        if (indexName == null && typeName == null) {
            logger.error("prefix must not be null");
        }
        //构造查询url
        this.queryUrl = "http://" + IP + ":" + Port;
        if (indexName != null)
            this.queryUrl = this.queryUrl + "/" + indexName;

        if (typeName != null)
            this.queryUrl = this.queryUrl + "/" + typeName;

        this.queryUrl = this.queryUrl + "/_search";
        this.queryJson = new JSONObject();
        this.queryJsonResult = new JSONObject();
        this.queryMustJarr = new JSONArray();
        this.queryMustNotJarr = new JSONArray();
        this.queryFilterMustJarr = new JSONArray();
        this.queryFilterMustNotJarr = new JSONArray();

        // 新增HTTP负载均衡器
        HttpProxyRegister.register(IP + ":" + Port);
    }

    /**
     * 构造关键词查询片段,如果输入的关键词以空格分割，那么将以空格切分开，作为多个关键词处理，多个关键词之间的关系由KeywordsCombine对象指定<br>
     * 关键词查询会走分词，如关键词为 北京科技大学，则有可能会搜索出北京大学的记录来<br>
     * 该方法对索引中字段类型配置为text（即有分词的类型）才有效<br>
     *
     * @param field    要查询的字段
     * @param keywords 关键词
     * @param occurs   字段出现情况
     * @param combine  以空格隔开的关键词的关系
     */
    public void addKeywordsQuery(String field, String keywords, FieldOccurs occurs, KeywordsCombine combine) {
        if (!Validator.check(keywords))
            return;
        if (ZH_Converter) {
            keywords = converter.convert(keywords);
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
     * 构造关键词查询片段,关键词查询会走分词，如关键词为 北京科技大学，则有可能会搜索出北京大学的记录来<br>
     * 该方法对索引中字段类型配置为text（即有分词的类型）才有效<br>
     *
     * @param field    要查询的字段
     * @param keywords 关键词
     * @param occurs   字段出现情况
     */
    public void addKeywordsQuery(String field, String keywords, FieldOccurs occurs) {
        if (!Validator.check(keywords))
            return;
        if (ZH_Converter) {
            keywords = converter.convert(keywords);
        }

        if (field != null) {
            String queryString;

            if (occurs != null)
                queryString = occurs.getSymbolValue() + field + ":(" + "";
            else
                queryString = field + ":(" + "";
            String p = keywords;
            if (!Validator.check(p))
                return;

            //对搜索词中的特殊字符做转移处理
            p = QueryParser.escape(p);

            queryString += p;

            queryString += ")";
            keywordString += (BLANK + queryString);
        }
    }

    /**
     * 构造关键词查询片段,关键词查询会走分词，如关键词为 北京科技大学，则有可能会搜索出北京大学的记录来<br>
     * <p>
     * 该方法对索引中字段类型配置为text（即有分词的类型）才有效<br>
     *
     * @param fields   要查询的字段
     * @param keywords 关键词
     * @param combine  多个字段间的关系
     */
    public void addKeywordsQuery(String[] fields, String keywords, KeywordsCombine combine) {
        if (!Validator.check(keywords))
            return;
        if (ZH_Converter) {
            keywords = converter.convert(keywords);
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
     * 关键词查询：单字段多组关键词的全文检索查询，组间是或关系<br>
     * 构造关键词查询,keywords是数组，每个词组之间是或的关系<br>
     * 每一组词中可以包含空格，空格之间是与的关系<br>
     * 关键词查询会走分词，如关键词为 北京科技大学，则有可能会搜索出北京大学的记录来<br>
     * 该方法对索引中字段类型配置为text（即有分词的类型）才有效<br>
     *
     * @param field    要查询的字段
     * @param keywords 关键词
     * @param occurs   字段出现情况
     */
    public void addKeywordsQuery(String field, List<String> keywords, FieldOccurs occurs) {
        if (keywords == null || keywords.size() == 0)
            return;

        String queryString = "";
        for (int i = 0; i < keywords.size(); i++) {
            String[] phraseSplit = keywords.get(i).split("\\s+");

            queryString += field + ":(" + "";
            boolean first = true;
            for (String p : phraseSplit) {
                if (!Validator.check(p))
                    continue;
                //对搜索词中的特殊字符做转移处理
                p = ClientUtils.escapeQueryChars(p);
                if (first) {
                    queryString += p;
                    first = false;
                } else {
                    queryString += (BLANK + KeywordsCombine.AND + BLANK + p);
                }
            }
            queryString += ")";

            if (i < keywords.size() - 1)
                queryString += (BLANK + KeywordsCombine.OR + BLANK);

        }
        keywordString += (BLANK + occurs.getSymbolValue() + "(" + queryString) + ")";
    }

    /**
     * 构造短语查询片段,如果输入的短语以空格分割，那么将以空格切分开，作为多个短语处理，多个短语之间的关系由KeywordsCombine对象指定<br>
     * 短语查询不走分词，如短语为 北京科技大学，则必须完整含有北京科技大学的记录才会被匹配<br>
     * 该方法对solr中字段类型配置为text（即有分词的类型）才有效<br>
     *
     * @param field    要查询的字段
     * @param keywords 关键词
     * @param occurs   字段出现情况
     * @param combine  关键词组合情况
     */
    public void addPhraseQuery(String field, String keywords, FieldOccurs occurs, KeywordsCombine combine) {
        if (!Validator.check(keywords))
            return;
        if (ZH_Converter) {
            keywords = converter.convert(keywords);
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
     * 构造短语查询片段,如果输入的短语以空格分割，那么将以空格切分开，作为多个短语处理，多个短语之间的关系由KeywordsCombine对象指定<br>
     * 短语查询不走分词，如短语为 北京科技大学，则必须完整含有北京科技大学的记录才会被匹配<br>
     * 该方法对solr中字段类型配置为text（即有分词的类型）才有效<br>
     *
     * @param fields   要查询的字段多个字段
     * @param keywords 关键词
     * @param occurs   字段出现情况
     * @param combine  关键词组合情况
     */
    public void addPhraseQuery(String[] fields, String keywords, FieldOccurs occurs, KeywordsCombine combine, FieldCombine filedCombine) {
        if (!Validator.check(keywords))
            return;
        if (ZH_Converter) {
            keywords = converter.convert(keywords);
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
     * 构造多组短语查询片段,多组关系由KeywordsCombine groupRelation指定，如果每组输入的短语以空格分割，那么将以空格切分开，作为多个短语处理，多个短语之间的关系由KeywordsCombine combine对象指定<br>
     * 短语查询不走分词，如短语为 北京科技大学，则必须完整含有北京科技大学的记录才会被匹配<br>
     * 该方法对solr中字段类型配置为text（即有分词的类型）才有效<br>
     *
     * @param field         要查询的字段
     * @param keywords      多组关键词，每一组中的关键词可按空格分割
     * @param occurs        字段出现情况
     * @param groupRelation 多组之间组合情况
     * @param combine       每组关键词组合情况
     */
    public void addPhraseQuery(String field, List<String> keywords, FieldOccurs occurs, KeywordsCombine groupRelation, KeywordsCombine combine) {
        if (!Validator.check(keywords) || !Validator.check(field))
            return;
        String queryString = occurs.getSymbolValue() + field + ":(" + "";
        StringBuffer sb = new StringBuffer();
        for (String keysword : keywords) {
            if (!Validator.check(keysword))
                continue;

            if (ZH_Converter) {
                keysword = converter.convert(keysword);
            }

            String[] phraseSplit = keysword.split("\\s+");
            boolean first = true;
            String sv = "";
            for (String p : phraseSplit) {
                if (!Validator.check(p))
                    continue;
                //对搜索词中的特殊字符做转义处理
                p = QueryParser.escape(p);
                if (first) {
                    sv += ("\"" + p + "\"");
                    first = false;
                } else {
                    sv += (BLANK + combine.name() + BLANK + "\"" + p + "\"");
                }
            }
            if (Validator.check(sv)) {
                sb.append(sb.length() == 0 ? BLANK + "(" + sv + ")" + BLANK : groupRelation.name() + BLANK + "(" + sv + ")" + BLANK);
            }
        }
        queryString += sb.toString() + ")";
        keywordString += (BLANK + queryString);
    }

    /**
     * 构造短语查询片段,如果输入的短语以空格分割，那么将以空格切分开，作为多个短语处理，多个短语之间为与的关系<br>
     * 短语查询不走分词，如短语为 北京科技大学，则必须完整含有北京科技大学的记录才会被匹配<br>
     * 该方法对solr中字段类型配置为text（即有分词的类型）才有效<br>
     *
     * @param field    要查询的字段
     * @param keywords 关键词
     * @param occurs   字段出现情况
     */
    public void addPhraseQuery(String field, String keywords, FieldOccurs occurs) {
        addPhraseQuery(field, keywords, occurs, KeywordsCombine.AND);
    }

    /**
     * 短语查询：多字段多组关键词的短语查询<br>
     * 构造关键词查询片段,多字段之间是或的关系<br>
     * keywords是数组，每个词组之间是或的关系，如果输入的关键词含有空格，空格之间是与的关系<br>
     * 短语查询不走分词，如短语为 北京科技大学，则必须完整含有北京科技大学的记录才会被匹配<br>
     * <p>
     * 该方法对索引中字段类型配置为text（即有分词的类型）才有效<br>
     * //* @param field
     * 要查询的字段
     *
     * @param keywords 关键词
     * @param occurs   字段出现情况
     */
    public void addPhraseQuery(String[] fields, String[] keywords, FieldOccurs occurs) {
        if (!Validator.check(keywords))
            return;

        String query = "";
        query = BLANK + occurs.getSymbolValue() + "(";

        for (int k = 0; k < keywords.length; k++) {

            for (int i = 0; i < fields.length; i++) {
                String queryString = fields[i] + ":(";

                String[] phraseSplit = keywords[k].split("\\s+");

                //String[] phraseSplit = new String[1];
                //phraseSplit[0] = keywords[k];

                boolean first = true;
                for (String p : phraseSplit) {
                    if (!Validator.check(p))
                        continue;
                    //对搜索词中的特殊字符做转移处理
                    p = QueryParser.escape(p);
                    if (first) {
                        queryString += ("\"" + p + "\"");
                        first = false;
                    } else {
                        queryString += (BLANK + KeywordsCombine.AND + BLANK + "\"" + p + "\"");

                    }
                }
                queryString += ")";

                query += (BLANK + queryString);

                if (i < fields.length - 1)
                    query += (BLANK + KeywordsCombine.OR);
            }

            if (k < keywords.length - 1)
                query += (BLANK + KeywordsCombine.OR + BLANK);


        }
        query = query + ")";
        keywordString += query;
    }

    /**
     * 此方法用于对一些不可分词的数据进行检索，例如int,long型数据的genus，alarm等字段 <br>
     * 本方法也可以对分词的数据进行检索，但传入的内容不能有任何的标点符号以及空格，否则检索结果会出现异常<br>
     * 该方法用于int,long,string等类型的匹配，对于string类型，支持通配符(*)<br>
     *
     * @param field  字段
     * @param term   字段对应的值，只能转入一个，且不能有有空格.如果传入的值为负值，请用双引号包起来，例如：<br>
     *               client.addPrimitveTermQuery("eid", "\"-1\"",FieldOccurs.MUST);
     * @param occurs 是否必须作为过滤条件
     */
    public void addPrimitiveTermQuery(String field, String term, FieldOccurs occurs) {
        if (!Validator.check(term))
            return;
        if (ZH_Converter) {
            term = converter.convert(term);
        }
        term = StringUtil.escapeSolrQueryChars(term);
        keywordString += (BLANK + occurs.getSymbolValue() + field + ":" + term);
    }

    /**
     * 与 {@link #addPrimitiveTermQuery(String, String, FieldOccurs)} 方法类似<br>
     * 字段对应的值可以输入多个，多个值之间为或的关系，满足其中一个值就会返回记录<br>
     * 该方法用于int,long,string等类型的匹配，对于string类型，支持通配符(*)<br>
     *
     * @param field  检索字段
     * @param terms  字段对应的多值，值之间是或的关系
     * @param occurs 是否必须作为过滤条件
     */
    public void addPrimitiveTermQuery(String field, String[] terms, FieldOccurs occurs) {
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
     * 此方法用于对一些不可分词的数据进行检索，例如int,long型数据的genus，alarm等字段 <br>
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
            term = converter.convert(term);
        }
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
        if (terms.length <= 500) {
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
        } else {
            logger.error("Set parameter error!Array is too large!", new IllegalArgumentException());
        }
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
    public void addPrimitiveTermFilter(String field, Set<String> terms, FieldOccurs occurs) {
        if (terms == null || terms.size() == 0)
            return;
        keywordString = keywordString + BLANK + occurs.getSymbolValue() + field
                + ":(";
        int i = 0;
        for (String term : terms) {
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
            i++;
        }
        keywordString = keywordString + ")";
    }

    /**
     * 范围过滤.startTerm和endTerm构成闭区间 <br/>
     * 如果field为pubtime、pubdate,查询时参数请按照下面的格式进行传递：<br>
     * pubdate:长度为8，例如20110822,表示2011年08月22日<br>
     * pubtime:长度为14,例如:20110822142613,表示2011年08月22日14时26分13秒 <br>
     * startTerm、endTerm构成闭区间
     *
     * @param field     查询字段
     * @param startTerm 开始项
     * @param endTerm   结束项
     */
    public void addRangeTerms(String field, String startTerm, String endTerm) {
        addRangeTerms(field, startTerm, endTerm, FieldOccurs.MUST);
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
            //小于等于
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
     * 从第几条结果取数据
     *
     * @param start 开始记录号
     */
    public void setStart(int start) {
        this.queryJson.put("from", start);
    }

    /**
     * 取多少条数据
     *
     * @param rows 要取出的记录数
     */
    public void setRow(int rows) {
        this.queryJson.put("size", rows);
    }

    /**
     * 设置是否计算score
     */
    public void setTrackScores() {
        this.queryJson.put("track_scores", true);
    }

    /**
     * 设置最小匹配度
     *
     * @param minscore
     */
    public void setMinScore(double minscore) {
        this.queryJson.put("min_score", minscore);
    }

    /**
     * 重置搜索条件
     */
    public void reset() {

        try {
            this.keywordString = "";

            this.queryJson.clear();
            this.queryJsonResult.clear();
            this.queryJson.clear();
            this.queryJsonResult.clear();
            this.queryMustJarr.clear();
            this.queryMustNotJarr.clear();
            this.queryFilterMustJarr.clear();
            this.queryFilterMustNotJarr.clear();

            countTotle = 0;
            fields = null;
        } catch (Exception e) {
        }
    }

    /**
     * 设置排序方式，本方法可多次调用，结果按照传递的排序方式顺序排列
     *
     * @param field 所需排序字段
     * @param order 顺序（升序、降序）
     */
    public void addSortField(String field, SortOrder order) {
        if (Validator.check(field)) {

            if (!this.queryJson.containsKey("sort")) {
                this.queryJson.put("sort", new JSONArray());
            }
            JSONObject sortJson = new JSONObject();
            sortJson.put(field, order.toString());
            this.queryJson.getJSONArray("sort").add(sortJson);
        }
    }

    /**
     * 获取提交请求串
     *
     * @param fields 索引查询后要返回值的字段，只有建索引时，有存储的字段此处才可能有返回值，对于只索引不存储的字段，此处得不到返回值
     * @return
     */
    public String getQueryString(String[] fields) {
        this.fields = fields;

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
        if (Validator.check(this.fields)) {
            //返回值字段
            this.queryJson.put("_source", this.fields);
        }

        JSONObject queryJson = new JSONObject();
        queryJson.put("bool", queryboolJson);
        this.queryJson.put("query", queryJson);
        String queryStr = this.queryJson.toString();
        return queryStr;
    }

    /**
     * 查询文档总量
     * 分页，排序条件将进行无效过滤。
     */
    public long searchTotal() {
        String queryUrl = this.queryUrl.replace("/_search", "/_count");

        this.queryJson.remove("from");
        this.queryJson.remove("size");
        this.queryJson.remove("sort");
        String queryStr = getQueryString(null);

        if (debug) {
            logger.info("curl:" + queryUrl + " -d " + queryStr);
            System.out.println("curl:" + queryUrl + " -d " + queryStr);
        }
        String queryResult = request.httpPost(queryUrl, queryStr);
        if (queryResult != null)
            this.queryJsonResult = JSONObject.parseObject(queryResult);
        if (debug) {
            logger.info("queryResult: -d " + queryResult);
        }
        if (this.queryJsonResult == null || this.queryJsonResult.size() == 0 || !this.queryJsonResult.containsKey("count"))
            return 0;
        return this.queryJsonResult.getLong("count");
    }

    /**
     * 提交请求
     *
     * @param fields 索引查询后要返回值的字段，只有建索引时，有存储的字段此处才可能有返回值，对于只索引不存储的字段，此处得不到返回值
     * @return
     */
    public void execute(String[] fields) {
        String queryStr = getQueryString(fields);

        if (debug) {
            logger.info("curl:" + this.queryUrl + " -d " + queryStr);
            System.out.println("curl:" + this.queryUrl + " -d " + queryStr);
        }
        String queryResult = request.httpPost(ClientUtils.referenceUrl(this.queryUrl), queryStr);
        if (queryResult != null)
            this.queryJsonResult = JSONObject.parseObject(queryResult);
        if (debug) {
            logger.info("queryResult: -d " + queryResult);
        }
    }

    /**
     * 提交请求
     *
     * @param esQuery 索引查询后要返回值的字段，只有建索引时，有存储的字段此处才可能有返回值，对于只索引不存储的字段，此处得不到返回值
     * @return
     */
    public void execute(String esQuery) {
        if (debug) {
            logger.info("curl:" + this.queryUrl + " -d " + esQuery);
            System.out.println("curl:" + this.queryUrl + " -d " + esQuery);
        }
        String queryResult = request.httpPost(this.queryUrl, esQuery);
        if (queryResult != null)
            this.queryJsonResult = JSONObject.parseObject(queryResult);
        if (debug) {
            logger.info("queryResult: -d " + queryResult);
        }
    }

    /**
     * 提交请求
     *
     * @param esQuery 索引查询后要返回值的字段，只有建索引时，有存储的字段此处才可能有返回值，对于只索引不存储的字段，此处得不到返回值
     * @return
     */
    public String executeDSL(String esQuery) {
        if (debug) {
            logger.info("curl:" + this.queryUrl + " -d " + esQuery);
            System.out.println("curl:" + this.queryUrl + " -d " + esQuery);
        }
        String queryResult = request.httpPost(this.queryUrl, esQuery);
        if (queryResult != null)
            this.queryJsonResult = JSONObject.parseObject(queryResult);
        if (debug) {
            logger.info("queryResult: -d " + queryResult);
        }
        return queryResult;
    }

    /**
     * 返回检索结果，返回的检索字段以及字段顺序由{@link #execute(String[])} 方法中的参数fields指定
     *
     * @return 检索的结果列表
     */
    public List<String[]> getResults() {
        List<String[]> list = new LinkedList<String[]>();
        if (this.queryJsonResult == null || this.queryJsonResult.size() == 0 || !this.queryJsonResult.containsKey("hits"))
            return list;
        JSONArray hitJsons = this.queryJsonResult.getJSONObject("hits").getJSONArray("hits");
        for (int index = 0; index < hitJsons.size(); index++) {
            JSONObject hitJson = hitJsons.getJSONObject(index);
            JSONObject json = hitJson.getJSONObject("_source");
            String[] values = new String[this.fields.length];
            for (int i = 0; i < this.fields.length; i++) {
                if (json.containsKey(this.fields[i])) {
                    values[i] = json.getString(this.fields[i]);
                } else if (this.fields[i].equals("_id") || this.fields[i].equals("_score") ||
                        this.fields[i].equals("_index") || this.fields[i].equals("_type") || this.fields[i].equals("sort")) {
                    //_id,_score字段存储位置在hits下，而非在fields下
                    values[i] = hitJson.getString(this.fields[i]);
                }
                if (values[i] == null) {
                    values[i] = "";
                }
            }
            list.add(values);
        }
        return list;
    }

    /**
     * 获取搜索结果（非聚合）总长度
     *
     * @return 检索结果的总长度
     */
    public long getTotal() {
        if (this.queryJsonResult == null || this.queryJsonResult.size() == 0 || !this.queryJsonResult.containsKey("hits"))
            return 0;
        return this.queryJsonResult.getJSONObject("hits").getLong("total");
    }

    /**
     * 获取搜索聚合分组结果总长度
     *
     * @return 聚合分组结果的总长度
     */
    public long getCountTotal() {
        if (this.queryJsonResult == null || this.queryJsonResult.size() == 0 || !this.queryJsonResult.containsKey("hits"))
            return 0;
        return this.queryJsonResult.getJSONObject("aggregations").getJSONObject("field_count").getLong("value");
    }

    /**
     * 单字段一次聚合
     * 此方法可以针对索引中某项数据进行统计（类似于数据库的count(*) 与 group by结合）并返回统计结果<br>
     * 该方法返回的列表中按照统计数由大到小排序<br>
     * 统计的字段包括pubdate、pubtime、eid、ip、user_id、group_id 等。 假设针对user_id进行统计，<br>
     * 会针对索引中每个不同user_id进行统计<br>
     * ，分别得到匹配的文档数，按照每个website_id统计得到的文档数排序，返回前topN个website_id 和对应的文档数<br>
     * <p>
     * ElasticSearch 利用Buketing 中的 terms aggregation 方式 默认分别返回100000,以便增加精确度。
     *
     * @param field 统计的字段
     * @param topN  要求返回的结果数 ,topN 等于 0 时，将返回所有的统计结果 。topN 小于0时，不返回结果。
     * @return 前topN个结果的list ， 每一项为一个数组， 第一项为统计字段，第二项为字段等于该值的文档数 。
     * 注意 ： 执行 getTotal()方法               为条件过滤后未执行聚合分组的文档总量；
     * 执行 getCountTotal()方法               为条件过滤后执行聚合总量；
     */
    public List<String[]> facetCountQueryOrderByCount(String field, int topN, SortOrder sort) {
        //添加查询
        setRow(0);
        getQueryString(null);

        //添加统计字段
        JSONObject aggregationJsonObject = new JSONObject();


        if (topN > -1) {
            JSONObject facetJsonObject = new JSONObject();

            JSONObject termsJson = new JSONObject();
            termsJson.put("shard_size", shard_size);
            termsJson.put("field", field);
            termsJson.put("size", topN == 0 ? 1000000 : topN);
            /**
             *  _count Sort by document count. Works with terms, histogram, date_histogram.
             _term	Sort by the string value of a term alphabetically. Works only with terms.
             _key Sort by the numeric value of each bucket’s key (conceptually similar to _term). Works only with histogram and date_histogram.
             */
            JSONObject order = new JSONObject();
            order.put("_count", sort);
            termsJson.put("order", order);
            facetJsonObject.put("terms", termsJson);
            aggregationJsonObject.put(field, facetJsonObject);
        }

        JSONObject cardinalityJsonObject = new JSONObject();
        cardinalityJsonObject.put("field", field);
        cardinalityJsonObject.put("precision_threshold", "100000");
        JSONObject countJsonObject = new JSONObject();
        countJsonObject.put("cardinality", cardinalityJsonObject);
        aggregationJsonObject.put("field_count", countJsonObject);

        this.queryJson.put("aggs", aggregationJsonObject);

        String queryStr = this.queryJson.toString();
        if (this.debug) {
            logger.info("url -d " + this.queryUrl);
            logger.info("-d " + queryStr);
        }
        String queryResult = request.httpPost(ClientUtils.referenceUrl(this.queryUrl), queryStr);
        if (this.debug) {
            logger.info(queryResult);
        }
        if (queryResult != null)
            this.queryJsonResult = JSONObject.parseObject(queryResult);
        if (topN <= -1) {
            return new ArrayList<String[]>();
        }
        //解析结果
        return getFacetResult(field);
    }

    /**
     * 单字段一次聚合 并且有限制返回聚合字段的详细数据
     * 此方法可以针对索引中某项数据进行统计（类似于数据库的count(*),详细信息  与 group by结合）并返回统计结果<br>
     * 该方法返回的列表中按照统计数由大到小排序<br>
     * ElasticSearch 利用Buketing 中的 terms aggregation 方式 默认分别返回100000,以便增加精确度。
     *
     * @param field          统计的字段
     * @param topN           要求返回的结果数 ,topN 等于 0 时，将返回所有的统计结果 。topN 小于0时，不返回结果。
     * @param sort           聚合字段的排序方式
     * @param returnFields   聚合返回详细字段,为null时返回全部字段
     * @param childSortField 聚合返回详细数据的排序字段，为null时将以相关性自动排序
     * @param childSortOrder 聚合返回详细数据排序方式
     * @param childSize      聚合返回详细数据条数
     * @return 前topN个结果的list ， 每一项为一个数组， 第一项为统计字段，第二项为字段等于该值的文档数，第三项为字段为详细数据  。facetCountQueryOrderByCount
     * 注意 ： 执行 getTotal()方法               为条件过滤后未执行聚合分组的文档总量；
     * 执行 getCountTotal()方法               为条件过滤后执行聚合总量；
     */
    public List<String[]> facetCountQueryOrderByCount(String field, int topN, SortOrder sort, String[] returnFields, String childSortField, SortOrder childSortOrder, int childSize) {

        //添加查询
        setRow(0);
        getQueryString(null);

        //添加统计字段
        JSONObject aggregationJsonObject = new JSONObject();

        if (topN > -1) {
            JSONObject facetJsonObject = new JSONObject();

            JSONObject termsJson = new JSONObject();
            termsJson.put("shard_size", shard_size);
            termsJson.put("field", field);
            termsJson.put("size", topN == 0 ? 1000000 : topN);
            /**
             *  _count Sort by document count. Works with terms, histogram, date_histogram.
             _term	Sort by the string value of a term alphabetically. Works only with terms.
             _key Sort by the numeric value of each bucket’s key (conceptually similar to _term). Works only with histogram and date_histogram.
             */
            JSONObject order = new JSONObject();
            order.put("_count", sort);
            termsJson.put("order", order);
            facetJsonObject.put("terms", termsJson);

            JSONObject childTermsJson = new JSONObject();
            //添加统计子字段
            JSONObject top_hits = new JSONObject();
            JSONObject fieldJsonObject = new JSONObject();

            //排序子字段
            if (Validator.check(childSortField)) {
                JSONArray childsort = new JSONArray();
                JSONObject childSortorder = new JSONObject();
                childSortorder.put("order", childSortOrder);
                JSONObject sortfildj = new JSONObject();
                sortfildj.put(childSortField, childSortorder);
                childsort.add(sortfildj);
                fieldJsonObject.put("sort", childsort);
            }
            //返回子字段
            if (Validator.check(returnFields)) {
                JSONArray jsonArray = JSON.parseArray(JSON.toJSONString(returnFields));
                JSONObject source = new JSONObject();
                source.put("includes", jsonArray);
                fieldJsonObject.put("_source", source);
            }
            fieldJsonObject.put("size", childSize);
            top_hits.put("top_hits", fieldJsonObject);
            childTermsJson.put("topHitsData", top_hits);
            facetJsonObject.put("aggs", childTermsJson);

            aggregationJsonObject.put(field, facetJsonObject);

        }

        JSONObject cardinalityJsonObject = new JSONObject();
        cardinalityJsonObject.put("field", field);
        cardinalityJsonObject.put("precision_threshold", "100000");
        JSONObject countJsonObject = new JSONObject();
        countJsonObject.put("cardinality", cardinalityJsonObject);
        aggregationJsonObject.put("field_count", countJsonObject);

        this.queryJson.put("aggs", aggregationJsonObject);

        String queryStr = this.queryJson.toString();
        if (this.debug) {
            logger.info("url -d " + this.queryUrl);
            logger.info("-d " + queryStr);
        }
        String queryResult = request.httpPost(ClientUtils.referenceUrl(this.queryUrl), queryStr);
        if (this.debug) {
            logger.info(queryResult);
        }
        if (queryResult != null)
            this.queryJsonResult = JSONObject.parseObject(queryResult);
        if (topN <= -1) {
            return new ArrayList<String[]>();
        }
        //解析结果
        List<String[]> list = new LinkedList<String[]>();

        if (this.queryJsonResult == null || this.queryJsonResult.size() == 0 || !this.queryJsonResult.containsKey("hits"))
            return list;

        JSONArray bucketJsons = this.queryJsonResult.getJSONObject("aggregations").getJSONObject(field).getJSONArray("buckets");
        this.countTotle = bucketJsons.size();
        for (int index = 0; index < bucketJsons.size(); index++) {
            JSONObject bucketJson = bucketJsons.getJSONObject(index);
            String key = bucketJson.getString("key");
            Long doc_count = bucketJson.getLong("doc_count");
            JSONArray hitss = bucketJson.getJSONObject("topHitsData").getJSONObject("hits").getJSONArray("hits");

            JSONArray rs = new JSONArray();
            if (Validator.check(hitss)) {
                for (int j = 0; j < hitss.size(); j++) {
                    JSONObject jsonObject = hitss.getJSONObject(j).getJSONObject("_source");
                    if (Validator.check(jsonObject)) {
                        rs.add(jsonObject);
                    }
                }
            }

            list.add(new String[]{key, doc_count + "", rs.toJSONString()});
        }

        return list;
    }

    /**
     * 多字段一次聚合
     * 此方法可以针对索引中多项数据进行统计（类似于数据库的count(*) 与 group by结合）并返回多项统计结果<br>
     * 该方法返回的列表中按照各字段的聚合数<br>
     * 统计的字段包括uid、gid、eid、ip等。 假设针对uid和gid进行统计，<br>
     * 会针对索引中每个不同uid和gid进行统计<br>
     * 返回Map<String,long> uid多少个，gid多少个<br>
     * <p>
     * ElasticSearch 利用Buketing 中的 terms aggregation 方式 默认分别返回100000,以便增加精确度。
     *
     * @param field 统计的字段
     * @return 注意 ： 执行 getTotal()方法               为条件过滤后未执行聚合分组的文档总量；
     */
    public Map<String, Long> facetCountQuerysOrderByCount(String[] field) {

        //添加查询
        setRow(0);
        getQueryString(null);
        //添加统计字段
        JSONObject aggregationJsonObject = new JSONObject();
        for (int i = 0; i < field.length; i++) {
            if (field[i] != null && !field[i].equals("")) {
                JSONObject countJsonObject = new JSONObject();
                JSONObject cardinalityJsonObject = new JSONObject();
                cardinalityJsonObject.put("field", field[i]);
                cardinalityJsonObject.put("precision_threshold", "100000");
                countJsonObject.put("cardinality", cardinalityJsonObject);
                aggregationJsonObject.put(field[i], countJsonObject);
            }
        }
        this.queryJson.put("aggs", aggregationJsonObject);
        this.queryJson.put("size", 0);

        String queryStr = this.queryJson.toString();
        if (this.debug) {
            logger.info("url:" + this.queryUrl + " -d " + queryStr);
        }
        String queryResult = request.httpPost(ClientUtils.referenceUrl(this.queryUrl), queryStr);
        if (this.debug) {
            logger.info(queryResult);
        }
        if (queryResult != null)
            this.queryJsonResult = JSONObject.parseObject(queryResult);

        Map<String, Long> map = new HashMap<String, Long>();

        if (this.queryJsonResult == null || this.queryJsonResult.size() == 0 || !this.queryJsonResult.containsKey("hits")) {
            return map;
        }

        JSONObject rsJsons = this.queryJsonResult.getJSONObject("aggregations");
        Set<String> keys = rsJsons.keySet();
        for (String key : keys) {
            map.put(key, rsJsons.getJSONObject(key).getLong("value"));
        }
        //解析结果
        return map;
    }

    /**
     * 二次聚合(返回结果较慢，不建议用)
     * 此方法可以针对索引中某项数据分组后 ,再对子项数据进行分组统计（类似于数据库两个字段的count(*) 与 group by结合）并返回统计结果<br>
     * 类似sql：select a.eid,COUNT(0)  FROM (  SELECT eid,qid FROM `content`  GROUP BY eid,qid) a GROUP BY a.eid  ;<br>
     * 该方法返回的列表中按照统计数由大到小排序<br>
     * 统计的字段包括pubdate、pubtime、eid、ip、user_id、group_id 等。 假设针对不同eid下的user_id进行统计，<br>
     * 会针对索引中每个不同eid，不同user_id进行统计<br>
     * ElasticSearch 利用Buketing 中的 terms aggregation 方式 默认分别返回1000000000,以便增加精确度。
     *
     * @param field      统计分组的父字段
     * @param childField 统计分组的子字段
     * @param topN       要求返回的结果数 ,topN 等于0 时，将返回所有的统计结果
     * @param childbool  结果是否返回子字段详情，true or false
     * @param sort       子字段统计排序
     * @return 前topN个结果的list ， 每一项为一个数组，数组 【0】为统计父字段，【1】为父字段检索文档数，,【2】为父字段下子字段聚合的统计数，【3】子字段统计返回结果详情
     */
    @Deprecated
    public List<String[]> facetTwoCountQueryOrderByCount(String field, String childField, int topN, boolean childbool, SortOrder sort) {
        if (!childbool) {
            List<String[]> rs = facetTwoCountQueryOrderByCount(field, childField, topN, sort);
            return rs;
        }
        //添加查询
        setRow(0);
        getQueryString(null);

        //添加分组字段（第一个字段）
        JSONObject aggregationJsonObject = new JSONObject();
        JSONObject facetJsonObject = new JSONObject();

        JSONObject termsJson = new JSONObject();
        termsJson.put("shard_size", this.shard_size);
        termsJson.put("field", field);
        termsJson.put("size", topN <= 0 ? 100000 : topN);
        JSONObject order = new JSONObject();
        order.put("_count", sort);
        termsJson.put("order", order);
        facetJsonObject.put("terms", termsJson);

        //添加分组字段（第二个字段）
        JSONObject childFieldJsonObject = new JSONObject();
        JSONObject childfacetJsonObject = new JSONObject();

        JSONObject childtermsJson = new JSONObject();
        childtermsJson.put("shard_size", this.shard_size);
        childtermsJson.put("field", childField);
        childtermsJson.put("size", 100000);
        JSONObject order_ = new JSONObject();
        order_.put("_count", sort);
        childtermsJson.put("order", order_);
        childfacetJsonObject.put("terms", childtermsJson);
        childFieldJsonObject.put(childField, childfacetJsonObject);
        facetJsonObject.put("aggs", childFieldJsonObject);

        aggregationJsonObject.put(field, facetJsonObject);

        this.queryJson.put("aggs", aggregationJsonObject);

        String queryStr = this.queryJson.toString();
        String queryResult = request.httpPost(ClientUtils.referenceUrl(this.queryUrl), queryStr);

        if (queryResult != null)
            this.queryJsonResult = JSONObject.parseObject(queryResult);

        if (this.debug) {
            logger.info(this.queryUrl + " -d " + queryStr);
            logger.info(this.queryJsonResult);
        }
        //解析结果
        List<String[]> list = new LinkedList<String[]>();

        if (this.queryJsonResult == null || this.queryJsonResult.size() == 0 || !this.queryJsonResult.containsKey("hits")) {
            this.countTotle = 0;
            return list;
        }

        JSONArray bucketJsons = this.queryJsonResult.getJSONObject("aggregations").getJSONObject(field).getJSONArray("buckets");

        for (int index = 0; index < bucketJsons.size(); index++) {
            JSONObject bucketJson = bucketJsons.getJSONObject(index);
            JSONArray childbucketJsons = bucketJson.getJSONObject(childField).getJSONArray("buckets");
            list.add(new String[]{bucketJson.getString("key"), bucketJson.getString("doc_count"), childbucketJsons.size() + "", childbool ? childbucketJsons.toString() : null});
        }
        return list;
    }

    /**
     * 二次聚合(返回结果较慢，不建议用)
     * 此方法可以针对索引中某项数据分组后 ,再对子项数据进行分组统计（类似于数据库两个字段的count(*) 与 group by结合）并返回统计结果<br>
     * 类似sql：select a.eid,COUNT(0)  FROM (  SELECT eid,qid FROM `content`  GROUP BY eid,qid) a GROUP BY a.eid  ;<br>
     * 该方法返回的列表中按照统计数由大到小排序<br>
     * 统计的字段包括pubdate、pubtime、eid、ip、user_id、group_id 等。 假设针对不同eid下的user_id进行统计，<br>
     * 会针对索引中每个不同eid，不同user_id进行统计<br>
     * ElasticSearch 利用Buketing 中的 terms aggregation 方式 默认分别返回1000000000,以便增加精确度。
     *
     * @param field       统计分组的父字段
     * @param childFields 统计分组的子字段
     * @param topN        要求返回的结果数 ,topN 等于0 时，将返回所有的统计结果
     * @param childbool   结果是否返回子字段详情，true or false
     * @param sort        子字段统计排序
     * @return 前topN个结果的list ， 每一项为一个数组，数组 【0】为统计父字段，【1】为父字段检索文档数，,【2】为父字段下子字段聚合的统计数，【3】子字段统计返回结果详情
     */
    @Deprecated
    public List<String[]> facetTwoCountQueryOrderByCount(String field, String[] childFields, int topN, boolean childbool, SortOrder sort) {
        if (!childbool) {
            List<String[]> rs = facetTwoCountQueryOrderByCount(field, childFields, topN, sort);
            return rs;
        }
        //添加查询
        setRow(0);
        getQueryString(null);

        //添加分组字段（第一个字段）
        JSONObject aggregationJsonObject = new JSONObject();
        JSONObject facetJsonObject = new JSONObject();

        JSONObject termsJson = new JSONObject();
        termsJson.put("shard_size", this.shard_size);
        termsJson.put("field", field);
        termsJson.put("size", topN <= 0 ? 100000 : topN);
        JSONObject order = new JSONObject();
        order.put("_count", sort);
        termsJson.put("order", order);
        facetJsonObject.put("terms", termsJson);

        //添加分组字段（第二个字段）
        JSONObject childFieldJsonObject = new JSONObject();
        for (int i = 0; i < childFields.length; i++) {
            String childField = childFields[i];
            JSONObject childfacetJsonObject = new JSONObject();
            JSONObject childtermsJson = new JSONObject();
            childtermsJson.put("shard_size", this.shard_size);
            childtermsJson.put("field", childField);
            childtermsJson.put("size", 100000);
            JSONObject order_ = new JSONObject();
            order_.put("_count", sort);
            childtermsJson.put("order", order_);
            childfacetJsonObject.put("terms", childtermsJson);
            childFieldJsonObject.put(childField, childfacetJsonObject);
        }
        facetJsonObject.put("aggs", childFieldJsonObject);

        aggregationJsonObject.put(field, facetJsonObject);

        this.queryJson.put("aggs", aggregationJsonObject);

        String queryStr = this.queryJson.toString();
        String queryResult = request.httpPost(ClientUtils.referenceUrl(this.queryUrl), queryStr);

        if (queryResult != null)
            this.queryJsonResult = JSONObject.parseObject(queryResult);

        if (this.debug) {
            logger.info(this.queryUrl + " -d " + queryStr);
            logger.info(this.queryJsonResult);
        }
        //解析结果
        List<String[]> list = new LinkedList<String[]>();

        if (this.queryJsonResult == null || this.queryJsonResult.size() == 0 || !this.queryJsonResult.containsKey("hits")) {
            this.countTotle = 0;
            return list;
        }

        JSONArray bucketJsons = this.queryJsonResult.getJSONObject("aggregations").getJSONObject(field).getJSONArray("buckets");

        for (int index = 0; index < bucketJsons.size(); index++) {
            JSONObject bucketJson = bucketJsons.getJSONObject(index);
            for (int i = 0; i < childFields.length; i++) {
                String childFieldSplit = childFields[i];
                JSONArray childbucketJsons = bucketJson.getJSONObject(childFieldSplit).getJSONArray("buckets");
                list.add(new String[]{bucketJson.getString("key"), bucketJson.getString("doc_count"), childbucketJsons.size() + "", childbool ? childbucketJsons.toString() : null});
            }
        }
        return list;
    }

    /**
     * 二次聚合
     * 此方法可以针对索引中某项数据分组后 ,再对子项数据进行分组统计（类似于数据库两个字段的count(*) 与 group by结合）并返回统计结果<br>
     * 类似sql：select a.eid,COUNT(0)  FROM (  SELECT eid,qid FROM `content`  GROUP BY eid,qid) a GROUP BY a.eid  ;<br>
     * 该方法返回的列表中按照统计数由大到小排序<br>
     * 统计的字段包括pubdate、pubtime、eid、ip、user_id、group_id 等。 假设针对不同eid下的user_id进行统计，<br>
     * 会针对索引中每个不同eid，不同user_id进行统计<br>
     * ElasticSearch 利用Buketing 中的 terms aggregation 方式 默认分别返回1000000000,以便增加精确度。
     *
     * @param field      统计分组的父字段
     * @param childField 统计分组的子字段
     * @param topN       要求返回的结果数 ,topN 等于0 时，将返回所有的统计结果
     * @param sort       子字段统计排序
     * @return 前topN个结果的list ， 每一项为一个数组，数组 【0】为统计父字段，【1】为父子段所查询文档的数量，【2】为子字段聚合的统计数
     * * 	注意 ： 执行 getTotal()方法               为条件过滤后未执行聚合分组的文档总量；
     * 执行 getCountTotal()方法               为条件过滤后执行聚合总量；
     */
    public List<String[]> facetTwoCountQueryOrderByCount(String field, String childField, int topN, SortOrder sort) {

        //添加查询
        setRow(0);
        getQueryString(null);

        //添加分组字段（第一个字段）
        JSONObject aggregationJsonObject = new JSONObject();
        JSONObject facetJsonObject = new JSONObject();

        JSONObject termsJson = new JSONObject();
        termsJson.put("shard_size", this.shard_size);
        termsJson.put("field", field);
        termsJson.put("size", topN <= 0 ? 100000 : topN);
        JSONObject order = new JSONObject();
        order.put(childField + ".value", sort);
        termsJson.put("order", order);
        facetJsonObject.put("terms", termsJson);

        //添加分组字段（第二个字段）
        JSONObject childFieldJsonObject = new JSONObject();
        JSONObject childfacetJsonObject = new JSONObject();

        JSONObject childCardinalityJson = new JSONObject();
        childCardinalityJson.put("field", childField);
        childfacetJsonObject.put("cardinality", childCardinalityJson);
        childFieldJsonObject.put(childField, childfacetJsonObject);
        facetJsonObject.put("aggs", childFieldJsonObject);
        aggregationJsonObject.put(field, facetJsonObject);

        //count
        JSONObject cardinalityJsonObject = new JSONObject();
        cardinalityJsonObject.put("field", field);
        cardinalityJsonObject.put("precision_threshold", "100000");
        JSONObject countJsonObject = new JSONObject();
        countJsonObject.put("cardinality", cardinalityJsonObject);
        aggregationJsonObject.put("field_count", countJsonObject);

        this.queryJson.put("aggs", aggregationJsonObject);

        String queryStr = this.queryJson.toString();
        String queryResult = request.httpPost(ClientUtils.referenceUrl(this.queryUrl), queryStr);

        if (queryResult != null)
            this.queryJsonResult = JSONObject.parseObject(queryResult);

        if (this.debug) {
            logger.info(this.queryUrl + " -d " + queryStr);
            logger.info(this.queryJsonResult);
        }
        //解析结果
        List<String[]> list = new LinkedList<String[]>();

        if (this.queryJsonResult == null || this.queryJsonResult.size() == 0 || !this.queryJsonResult.containsKey("hits")) {
            return list;
        }
        JSONArray bucketJsons = this.queryJsonResult.getJSONObject("aggregations").getJSONObject(field).getJSONArray("buckets");
        for (int index = 0; index < bucketJsons.size(); index++) {
            JSONObject bucketJson = bucketJsons.getJSONObject(index);
            list.add(new String[]{bucketJson.getString("key"), bucketJson.getString("doc_count"), bucketJson.getJSONObject(childField).getString("value")});
        }

        return list;
    }

    /**
     * 二次聚合
     * 此方法可以针对索引中某项数据分组后 ,再对子项数据进行分组统计（类似于数据库两个字段的count(*) 与 group by结合）并返回统计结果<br>
     * 类似sql：select a.eid,COUNT(0)  FROM (  SELECT eid,qid FROM `content`  GROUP BY eid,qid) a GROUP BY a.eid  ;<br>
     * 该方法返回的列表中按照统计数由大到小排序<br>
     * 统计的字段包括pubdate、pubtime、eid、ip、user_id、group_id 等。 假设针对不同eid下的user_id进行统计，<br>
     * 会针对索引中每个不同eid，不同user_id进行统计<br>
     * ElasticSearch 利用Buketing 中的 terms aggregation 方式 默认分别返回1000000000,以便增加精确度。
     *
     * @param field       统计分组的父字段
     * @param childFields 统计分组的子字段
     * @param topN        要求返回的结果数 ,topN 等于0 时，将返回所有的统计结果
     * @param sort        子字段统计排序
     * @return 前topN个结果的list ， 每一项为一个数组，数组 【0】为统计父字段，【1】为父子段所查询文档的数量，【2】为子字段聚合的统计数
     * * 	注意 ： 执行 getTotal()方法               为条件过滤后未执行聚合分组的文档总量；
     * 执行 getCountTotal()方法               为条件过滤后执行聚合总量；
     */
    public List<String[]> facetTwoCountQueryOrderByCount(String field, String[] childFields, int topN, SortOrder sort) {

        //添加查询
        setRow(0);
        getQueryString(null);

        //添加分组字段（第一个字段）
        JSONObject aggregationJsonObject = new JSONObject();
        JSONObject facetJsonObject = new JSONObject();

        JSONObject termsJson = new JSONObject();
        termsJson.put("shard_size", this.shard_size);
        termsJson.put("field", field);
        termsJson.put("size", topN <= 0 ? 100000 : topN);
        JSONObject order = new JSONObject();
        order.put(childFields[0] + ".value", sort);
        termsJson.put("order", order);
        facetJsonObject.put("terms", termsJson);

        //添加分组字段（第二个字段）
        JSONObject childFieldJsonObject = new JSONObject();
        for (int i = 0; i < childFields.length; i++) {
            String childField = childFields[i];
            JSONObject childfacetJsonObject = new JSONObject();
            JSONObject childCardinalityJson = new JSONObject();
            childCardinalityJson.put("field", childField);
            childfacetJsonObject.put("cardinality", childCardinalityJson);
            childFieldJsonObject.put(childField, childfacetJsonObject);
        }
        facetJsonObject.put("aggs", childFieldJsonObject);
        aggregationJsonObject.put(field, facetJsonObject);

        //count
        JSONObject cardinalityJsonObject = new JSONObject();
        cardinalityJsonObject.put("field", field);
        cardinalityJsonObject.put("precision_threshold", "100000");
        JSONObject countJsonObject = new JSONObject();
        countJsonObject.put("cardinality", cardinalityJsonObject);
        aggregationJsonObject.put("field_count", countJsonObject);

        this.queryJson.put("aggs", aggregationJsonObject);

        String queryStr = this.queryJson.toString();
        String queryResult = request.httpPost(ClientUtils.referenceUrl(this.queryUrl), queryStr);

        if (queryResult != null)
            this.queryJsonResult = JSONObject.parseObject(queryResult);

        if (this.debug) {
            logger.info(this.queryUrl + " -d " + queryStr);
            logger.info(this.queryJsonResult);
        }
        //解析结果
        List<String[]> list = new LinkedList<String[]>();

        if (this.queryJsonResult == null || this.queryJsonResult.size() == 0 || !this.queryJsonResult.containsKey("hits")) {
            return list;
        }
        JSONArray bucketJsons = this.queryJsonResult.getJSONObject("aggregations").getJSONObject(field).getJSONArray("buckets");
        for (int index = 0; index < bucketJsons.size(); index++) {
            JSONObject bucketJson = bucketJsons.getJSONObject(index);
            for (int i = 0; i < childFields.length; i++) {
                String childFieldSplit = childFields[i];
                list.add(new String[]{bucketJson.getString("key"), bucketJson.getString("doc_count"), bucketJson.getJSONObject(childFieldSplit).getString("value")});
            }
        }

        return list;
    }

    /**
     * 多次聚合
     * 此方法可以针对索引中某项数据分组后 ,再对子项数据进行分组统计（类似于数据库两个字段的count(*) 与 group by结合）并返回统计结果<br>
     * 类似sql：select a.eid,COUNT(0)  FROM (  SELECT eid,qid FROM `content`  GROUP BY eid,qid) a GROUP BY a.eid  ;<br>
     * 该方法返回的列表中按照统计数由大到小排序<br>
     * 统计的字段包括pubdate、pubtime、eid、ip、user_id、group_id 等。 假设针对不同eid下的user_id进行统计，<br>
     * 会针对索引中每个不同eid，不同user_id进行统计<br>
     * ElasticSearch 利用Buketing 中的 terms aggregation 方式 默认分别返回1000000000,以便增加精确度。
     *
     * @param field       统计分组的父字段
     * @param childFields 统计分组的子字段,数组
     * @param topN        要求返回的结果数 ,topN 等于0 时，将返回所有的统计结果
     * @return 前topN个结果的list ， 每一项为一个数组，数组 【0】为统计父字段，【1】为父子段所查询文档的数量，【N】为子字段聚合的统计数
     * 默认排序为父字段的文档数
     * * 	注意 ： 执行 getTotal()方法               为条件过滤后未执行聚合分组的文档总量；
     * 执行 getCountTotal()方法               为条件过滤后执行聚合总量；
     */
    public List<String[]> facetMultipleCountQueryOrderByCount(String field, String[] childFields, int topN) {
        List<String[]> list = facetMultipleCountQueryOrderByCount(field, childFields, topN, null, null);
        return list;
    }

    /**
     * 多次聚合
     * 此方法可以针对索引中某项数据分组后 ,再对子项数据进行分组统计（类似于数据库两个字段的count(*) 与 group by结合）并返回统计结果<br>
     * 类似sql：select a.eid,COUNT(0)  FROM (  SELECT eid,qid FROM `content`  GROUP BY eid,qid) a GROUP BY a.eid  ;<br>
     * 该方法返回的列表中按照统计数由大到小排序<br>
     * 统计的字段包括pubdate、pubtime、eid、ip、user_id、group_id 等。 假设针对不同eid下的user_id进行统计，<br>
     * 会针对索引中每个不同eid，不同user_id进行统计<br>
     * ElasticSearch 利用Buketing 中的 terms aggregation 方式 默认分别返回1000000000,以便增加精确度。
     *
     * @param field       统计分组的父字段
     * @param childFields 统计分组的子字段,数组
     * @param topN        要求返回的结果数 ,topN 等于0 时，将返回所有的统计结果
     * @param orderFiled  排序的字段，为空时， 默认排序为父字段的文档数
     * @param sort        排序
     * @return 前topN个结果的list ， 每一项为一个数组，数组 【0】为统计父字段，【1】为父子段所查询文档的数量，【N】为子字段聚合的统计数
     * * 	注意 ： 执行 getTotal()方法               为条件过滤后未执行聚合分组的文档总量；
     * 执行 getCountTotal()方法               为条件过滤后执行聚合总量；
     */
    public List<String[]> facetMultipleCountQueryOrderByCount(String field, String[] childFields, int topN, String orderFiled, SortOrder sort) {

        //添加查询
        setRow(0);
        getQueryString(null);

        //添加分组字段（第一个字段）
        JSONObject aggregationJsonObject = new JSONObject();
        JSONObject facetJsonObject = new JSONObject();

        JSONObject termsJson = new JSONObject();
        termsJson.put("shard_size", this.shard_size);
        termsJson.put("field", field);
        termsJson.put("size", topN <= 0 ? 100000 : topN);
        if (orderFiled != null) {
            JSONObject order = new JSONObject();
            if (orderFiled.equals(field)) {
                order.put("_count", sort);
            } else {
                order.put(orderFiled + ".value", sort);
            }
            termsJson.put("order", order);
        }
        facetJsonObject.put("terms", termsJson);

        //添加分组字段（第二个字段）
        JSONObject childFieldJsonObject = new JSONObject();
        for (String childField : childFields) {
            JSONObject childfacetJsonObject = new JSONObject();
            JSONObject childCardinalityJson = new JSONObject();
            childCardinalityJson.put("field", childField);
            childfacetJsonObject.put("cardinality", childCardinalityJson);
            childFieldJsonObject.put(childField, childfacetJsonObject);
        }
        facetJsonObject.put("aggs", childFieldJsonObject);
        aggregationJsonObject.put(field, facetJsonObject);


        //count
        JSONObject cardinalityJsonObject = new JSONObject();
        cardinalityJsonObject.put("field", field);
        cardinalityJsonObject.put("precision_threshold", "100000");
        JSONObject countJsonObject = new JSONObject();
        countJsonObject.put("cardinality", cardinalityJsonObject);
        aggregationJsonObject.put("field_count", countJsonObject);

        this.queryJson.put("aggs", aggregationJsonObject);
        String queryStr = this.queryJson.toString();
        String queryResult = request.httpPost(ClientUtils.referenceUrl(this.queryUrl), queryStr);
        if (queryResult != null)
            this.queryJsonResult = JSONObject.parseObject(queryResult);
        if (this.debug) {
            logger.info(this.queryUrl + "\" -d \"" + queryStr);
            logger.info(this.queryJsonResult);
        }
        //解析结果
        List<String[]> list = new LinkedList<String[]>();

        if (this.queryJsonResult == null || this.queryJsonResult.size() == 0 || !this.queryJsonResult.containsKey("hits")) {
            return list;
        }
        JSONArray bucketJsons = this.queryJsonResult.getJSONObject("aggregations").getJSONObject(field).getJSONArray("buckets");
        for (int index = 0; index < bucketJsons.size(); index++) {
            JSONObject bucketJson = bucketJsons.getJSONObject(index);
            String[] rs = new String[2 + childFields.length];
            rs[0] = bucketJson.getString("key");
            rs[1] = bucketJson.getString("doc_count");
            int inn = 2;
            for (String childField : childFields) {
                rs[inn] = bucketJson.getJSONObject(childField).getString("value");
                inn += 1;
            }
            list.add(rs);
        }
        return list;
    }

    /**
     * 根据时间粒度统计数量
     *
     * @param field         查询的时间字段
     * @param format        时间格式 例如：yyyy-MM-dd
     * @param interval      粒度 (1M代表每月，1d代表每日，1h代表每小时)
     * @param startTime     开始时间
     * @param endTime       结束时间
     * @param min_doc_count 最小返回值
     * @return
     */
    public List<String[]> facetDate(String field, String format, String interval, String startTime, String endTime, int min_doc_count) {

//		addRangeTerms(field, startTime, endTime);
        setRow(0);
        //添加查询
        getQueryString(null);

        JSONObject bounds_json = new JSONObject();
        if (startTime != null) {
            bounds_json.put("min", startTime);
        }
        if (endTime != null) {
            bounds_json.put("max", endTime);
        }

        JSONObject histog_json = new JSONObject();
        histog_json.put("field", field);
        histog_json.put("format", format);
        histog_json.put("interval", interval);
        histog_json.put("min_doc_count", min_doc_count <= 0 ? 0 : min_doc_count);
        if (bounds_json.size() != 0) {
            histog_json.put("extended_bounds", bounds_json);
        }

        JSONObject sales_json = new JSONObject();
        sales_json.put("date_histogram", histog_json);

        JSONObject aggs_json = new JSONObject();
        aggs_json.put(field, sales_json);

        this.queryJson.put("aggs", aggs_json);
        String queryStr = this.queryJson.toString();
        if (this.debug) {
            logger.info(this.queryUrl + " -d " + queryStr);
        }
        String queryResult = request.httpPost(ClientUtils.referenceUrl(this.queryUrl), queryStr);
        if (queryResult != null)
            this.queryJsonResult = JSONObject.parseObject(queryResult);
        if (this.debug) {
            logger.info(this.queryJsonResult);
        }

        //解析结果
        List<String[]> list = new LinkedList<String[]>();

        if (this.queryJsonResult == null || this.queryJsonResult.size() == 0 || !this.queryJsonResult.containsKey("hits")) {
            return list;
        }
        JSONArray bucketJsons = this.queryJsonResult.getJSONObject("aggregations").getJSONObject(field).getJSONArray("buckets");
        this.countTotle = bucketJsons.size();
        for (int index = 0; index < bucketJsons.size(); index++) {
            JSONObject bucketJson = bucketJsons.getJSONObject(index);
            String date = bucketJson.getString("key_as_string");
            String doc_count = bucketJson.getString("doc_count");
            list.add(new String[]{date, doc_count});
        }
        return list;
    }

    /**
     * 根据时间粒度统计数量
     *
     * @param field     查询的时间字段
     * @param format    时间格式 例如：yyyy-MM-dd
     * @param interval  粒度 (1M代表每月，1d代表每日，1h代表每小时)
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return
     */
    public List<String[]> facetDate(String field, String format, String interval, String startTime, String endTime) {
        return facetDate(field, format, interval, startTime, endTime, 0);
    }

    /**
     * 根据时间粒度统计数量
     *
     * @param field  查询的时间字段
     * @param format 时间格式 例如：yyyy-MM-dd HH
     * @param size   返回数量，小于等于0时返回10000条
     * @return
     */
    public List<String[]> facetDate(String field, String format, int size) {
        //添加查询
        setRow(0);
        getQueryString(null);

        JSONObject terms_json = new JSONObject();
        String script = String.format(Scripting.facetime, new String[]{field, field, format});
        terms_json.put("script", script);
        terms_json.put("size", size <= 0 ? 10000 : size);

        JSONObject field_json = new JSONObject();
        field_json.put("terms", terms_json);

        JSONObject aggs_json = new JSONObject();
        aggs_json.put(field, field_json);

        JSONObject cardinality_json = new JSONObject();
        cardinality_json.put("field", field);
        cardinality_json.put("precision_threshold", "100000");
        JSONObject field_count_json = new JSONObject();
        field_count_json.put("cardinality", cardinality_json);
        aggs_json.put("field_count", field_count_json);

        this.queryJson.put("aggs", aggs_json);
        String queryStr = this.queryJson.toString();
        if (this.debug) {
            logger.info(this.queryUrl + " -d " + queryStr);
        }
        String queryResult = request.httpPost(ClientUtils.referenceUrl(this.queryUrl), queryStr);
        if (queryResult != null)
            this.queryJsonResult = JSONObject.parseObject(queryResult);
        if (this.debug) {
            logger.info(this.queryJsonResult);
        }

        //解析结果
        List<String[]> list = new LinkedList<String[]>();

        if (this.queryJsonResult == null || this.queryJsonResult.size() == 0 || !this.queryJsonResult.containsKey("hits")) {
            return list;
        }
        JSONArray bucketJsons = this.queryJsonResult.getJSONObject("aggregations").getJSONObject(field).getJSONArray("buckets");
        this.countTotle = bucketJsons.size();
        for (int index = 0; index < bucketJsons.size(); index++) {
            JSONObject bucketJson = bucketJsons.getJSONObject(index);
            String date = bucketJson.getString("key");
            String doc_count = bucketJson.getString("doc_count");
            list.add(new String[]{date, doc_count});
        }

        return list;
    }

    /**
     * 根据时间粒度统计数量
     *
     * @param field    查询的时间字段
     * @param format   时间格式 例如：yyyy-MM-dd
     * @param interval 粒度 (1M代表每月，1d代表每日，1h代表每小时)
     * @return
     */
    public List<String[]> facetDate(String field, String format, String interval) {

        //添加查询
        setRow(0);
        getQueryString(null);

        JSONObject histog_json = new JSONObject();
        histog_json.put("field", field);
        histog_json.put("format", format);
        histog_json.put("interval", interval);
        histog_json.put("min_doc_count", 0);

        JSONObject sales_json = new JSONObject();
        sales_json.put("date_histogram", histog_json);

        JSONObject aggs_json = new JSONObject();
        aggs_json.put(field, sales_json);

        this.queryJson.put("aggs", aggs_json);
        String queryStr = this.queryJson.toString();
        if (this.debug) {
            logger.info(this.queryUrl + " -d " + queryStr);
        }
        String queryResult = request.httpPost(ClientUtils.referenceUrl(this.queryUrl), queryStr);
        if (queryResult != null)
            this.queryJsonResult = JSONObject.parseObject(queryResult);
        if (this.debug) {
            logger.info(this.queryJsonResult);
        }

        //解析结果
        List<String[]> list = new LinkedList<String[]>();

        if (this.queryJsonResult == null || this.queryJsonResult.size() == 0 || !this.queryJsonResult.containsKey("hits")) {
            return list;
        }
        JSONArray bucketJsons = this.queryJsonResult.getJSONObject("aggregations").getJSONObject(field).getJSONArray("buckets");
        this.countTotle = bucketJsons.size();
        for (int index = 0; index < bucketJsons.size(); index++) {
            JSONObject bucketJson = bucketJsons.getJSONObject(index);
            String date = bucketJson.getString("key_as_string");
            String doc_count = bucketJson.getString("doc_count");
            list.add(new String[]{date, doc_count});
        }

        return list;
    }

    /**
     * 根据时间粒度统计 聚合数量
     * 类似统计每一天有多少个用户；
     *
     * @param TimeField  查询的时间字段
     * @param format     时间格式 例如：yyyy-MM-dd
     * @param interval   粒度 (1M代表每月，1d代表每日，1H代表每小时)
     * @param CountField 要聚合的字段
     * @return
     */
    public List<String[]> facetDateByCount(String TimeField, String format, String interval, String startTime, String endTime, String CountField) {

        //添加查询
        setRow(0);
        getQueryString(null);

        JSONObject aggs_json = new JSONObject();

        JSONObject bounds_json = new JSONObject();
        if (startTime != null) {
            bounds_json.put("min", startTime);
        }
        if (endTime != null) {
            bounds_json.put("max", endTime);
        }

        JSONObject histog_json = new JSONObject();
        histog_json.put("field", TimeField);
        histog_json.put("format", format);
        histog_json.put("interval", interval);
        histog_json.put("min_doc_count", 0);
        if (bounds_json.size() != 0) {
            histog_json.put("extended_bounds", bounds_json);
        }
        JSONObject sales_json = new JSONObject();
        sales_json.put("date_histogram", histog_json);

        //添加统计字段
        JSONObject aggregationJsonObject = new JSONObject();
        JSONObject cardinalityJsonObject = new JSONObject();
        cardinalityJsonObject.put("field", CountField);
        cardinalityJsonObject.put("precision_threshold", "100000");
        JSONObject countJsonObject = new JSONObject();
        countJsonObject.put("cardinality", cardinalityJsonObject);
        aggregationJsonObject.put(CountField, countJsonObject);

        sales_json.put("aggs", aggregationJsonObject);
        aggs_json.put(TimeField, sales_json);

        this.queryJson.put("aggs", aggs_json);
        String queryStr = this.queryJson.toString();
        if (this.debug) {
            logger.info(this.queryUrl + " -d " + queryStr);
        }
        String queryResult = request.httpPost(ClientUtils.referenceUrl(this.queryUrl), queryStr);
        if (queryResult != null)
            this.queryJsonResult = JSONObject.parseObject(queryResult);
        if (this.debug) {
            logger.info(this.queryJsonResult);
        }

        //解析结果
        List<String[]> list = new LinkedList<String[]>();

        if (this.queryJsonResult == null || this.queryJsonResult.size() == 0 || !this.queryJsonResult.containsKey("hits")) {
            return list;
        }
        JSONArray bucketJsons = this.queryJsonResult.getJSONObject("aggregations").getJSONObject(TimeField).getJSONArray("buckets");
        this.countTotle = bucketJsons.size();
        for (int index = 0; index < bucketJsons.size(); index++) {
            JSONObject bucketJson = bucketJsons.getJSONObject(index);
            String date = bucketJson.getString("key_as_string");
            String doc_count = bucketJson.getString("doc_count");
            String childField_Count = bucketJson.getJSONObject(CountField).getString("value");
            list.add(new String[]{date, doc_count, childField_Count});
        }

        return list;
    }

    /**
     * 根据时间粒度统计 聚合数量
     * 类似统计每一天有多少个用户；
     *
     * @param TimeField   查询的时间字段
     * @param format      时间格式 例如：yyyy-MM-dd
     * @param interval    粒度 (1M代表每月，1d代表每日，1H代表每小时)
     * @param CountFields 要聚合的字段s
     * @return
     */
    public List<String[]> facetDateByCount(String TimeField, String format, String interval, String[] CountFields) {

        //添加查询
        setRow(0);
        getQueryString(null);

        JSONObject aggs_json = new JSONObject();

        JSONObject histog_json = new JSONObject();
        histog_json.put("field", TimeField);
        histog_json.put("format", format);
        histog_json.put("interval", interval);
        histog_json.put("min_doc_count", 0);
        JSONObject sales_json = new JSONObject();
        sales_json.put("date_histogram", histog_json);

        //添加统计字段
        JSONObject aggregationJsonObject = new JSONObject();
        for (String field : CountFields) {
            JSONObject cardinalityJsonObject = new JSONObject();
            cardinalityJsonObject.put("field", field);
            cardinalityJsonObject.put("precision_threshold", "100000");
            JSONObject countJsonObject = new JSONObject();
            countJsonObject.put("cardinality", cardinalityJsonObject);
            aggregationJsonObject.put(field, countJsonObject);
        }

        sales_json.put("aggs", aggregationJsonObject);
        aggs_json.put(TimeField, sales_json);

        this.queryJson.put("aggs", aggs_json);
        String queryStr = this.queryJson.toString();
        if (this.debug) {
            logger.info(this.queryUrl + " -d " + queryStr);
        }
        String queryResult = request.httpPost(ClientUtils.referenceUrl(this.queryUrl), queryStr);
        if (queryResult != null)
            this.queryJsonResult = JSONObject.parseObject(queryResult);
        if (this.debug) {
            logger.info(this.queryJsonResult);
        }

        //解析结果
        List<String[]> list = new LinkedList<String[]>();

        if (this.queryJsonResult == null || this.queryJsonResult.size() == 0 || !this.queryJsonResult.containsKey("hits")) {
            return list;
        }
        JSONArray bucketJsons = this.queryJsonResult.getJSONObject("aggregations").getJSONObject(TimeField).getJSONArray("buckets");
        this.countTotle = bucketJsons.size();
        for (int index = 0; index < bucketJsons.size(); index++) {
            JSONObject bucketJson = bucketJsons.getJSONObject(index);
            String[] rs = new String[2 + CountFields.length];
            rs[0] = bucketJson.getString("key_as_string");
            rs[1] = bucketJson.getString("doc_count");
            int inn = 2;
            for (String childField : CountFields) {
                rs[inn] = bucketJson.getJSONObject(childField).getString("value");
                inn += 1;
            }
            list.add(rs);
        }
        return list;
    }

    /**
     * 根据时间粒度统计 聚合不同类型数量
     * 类似统计每一天有多少个用户；
     *
     * @param TimeField  查询的时间字段
     * @param format     时间格式 例如：yyyy-MM-dd
     * @param interval   粒度 (1M代表每月，1d代表每日，1H代表每小时)
     * @param CountField 要聚合的字段
     * @return
     */
    public List<String[]> facetDateByTypeCount(String TimeField, String format, String interval, String CountField) {
        return facetDateByTypeCount(TimeField, format, interval, null, null, CountField);
    }

    /**
     * 根据时间粒度统计 聚合不同类型数量
     * 类似统计每一天有多少个用户；
     *
     * @param TimeField  查询的时间字段
     * @param format     时间格式 例如：yyyy-MM-dd
     * @param interval   粒度 (1M代表每月，1d代表每日，1H代表每小时)
     * @param CountField 要聚合的字段
     * @return
     */
    public List<String[]> facetDateByTypeCount(String TimeField, String format, String interval, String startTime, String endTime, String CountField) {

        //添加查询
        setRow(0);
        getQueryString(null);

        JSONObject aggs_json = new JSONObject();

        JSONObject bounds_json = new JSONObject();
        if (startTime != null) {
            bounds_json.put("min", startTime);
        }
        if (endTime != null) {
            bounds_json.put("max", endTime);
        }

        JSONObject histog_json = new JSONObject();
        histog_json.put("field", TimeField);
        histog_json.put("format", format);
        histog_json.put("interval", interval);
        histog_json.put("min_doc_count", 0);
        if (bounds_json.size() != 0) {
            histog_json.put("extended_bounds", bounds_json);
        }
        JSONObject sales_json = new JSONObject();
        sales_json.put("date_histogram", histog_json);

        //添加统计字段
        JSONObject aggregationJsonObject = new JSONObject();
        JSONObject fieldJsonObject = new JSONObject();
        fieldJsonObject.put("field", CountField);
        fieldJsonObject.put("shard_size", 100000);
        fieldJsonObject.put("size", 100000);
        JSONObject termsJsonObject = new JSONObject();
        termsJsonObject.put("terms", fieldJsonObject);
        aggregationJsonObject.put(CountField, termsJsonObject);

        sales_json.put("aggs", aggregationJsonObject);
        aggs_json.put(TimeField, sales_json);

        this.queryJson.put("aggs", aggs_json);
        String queryStr = this.queryJson.toString();
        if (this.debug) {
            logger.info(this.queryUrl + " -d " + queryStr);
        }
        String queryResult = request.httpPost(ClientUtils.referenceUrl(this.queryUrl), queryStr);
        if (queryResult != null)
            this.queryJsonResult = JSONObject.parseObject(queryResult);
        if (this.debug) {
            logger.info(this.queryJsonResult);
        }

        //解析结果
        List<String[]> list = new LinkedList<String[]>();

        if (this.queryJsonResult == null || this.queryJsonResult.size() == 0 || !this.queryJsonResult.containsKey("hits")) {
            return list;
        }
        JSONArray bucketJsons = this.queryJsonResult.getJSONObject("aggregations").getJSONObject(TimeField).getJSONArray("buckets");
        this.countTotle = bucketJsons.size();
        for (int index = 0; index < bucketJsons.size(); index++) {
            JSONObject bucketJson = bucketJsons.getJSONObject(index);
            String date = bucketJson.getString("key_as_string");
            String doc_count = bucketJson.getString("doc_count");
            String childField_Count = bucketJson.getJSONObject(CountField).getString("buckets");
            list.add(new String[]{date, doc_count, childField_Count});
        }

        return list;
    }

    /**
     * 根据时间粒度，根据排序 筛选返回每个时间段内前N条数据
     *
     * @param TimeField 查询的时间字段
     * @param format    时间格式 例如：yyyy-MM-dd
     * @param interval  粒度 (1M代表每月，1d代表每日，1H代表每小时)
     * @param startTime 起始时间
     * @param endTime   结束时间
     * @param size      时间段内返回数据条数
     * @return
     */
    public List<String[]> facetDateAggsTophits(String TimeField, String format, String interval, String startTime, String endTime, int size) {
        return facetDateAggsTophits(TimeField, format, interval, startTime, endTime, null, null, null, size);
    }

    /**
     * 根据时间粒度，根据排序 筛选返回每个时间段内前N条数据
     *
     * @param TimeField    查询的时间字段
     * @param format       时间格式 例如：yyyy-MM-dd
     * @param interval     粒度 (1M代表每月，1d代表每日，1H代表每小时)
     * @param startTime    起始时间
     * @param endTime      结束时间
     * @param sortField    时间段内数据排序字段，为null时将以相关性自动排序
     * @param sortOrder    时间段内数据排序方式
     * @param returnFields 时间段内数据返回字段，为null时字段全部返回
     * @param size         时间段内返回数据条数
     * @return
     */
    public List<String[]> facetDateAggsTophits(String TimeField, String format, String interval, String startTime, String endTime, String sortField, SortOrder sortOrder, String[] returnFields, int size) {

        //添加查询
        setRow(0);
        getQueryString(null);

        JSONObject aggs_json = new JSONObject();

        JSONObject bounds_json = new JSONObject();
        if (startTime != null) {
            bounds_json.put("min", startTime);
        }
        if (endTime != null) {
            bounds_json.put("max", endTime);
        }

        JSONObject histog_json = new JSONObject();
        histog_json.put("field", TimeField);
        histog_json.put("format", format);
        histog_json.put("interval", interval);
        histog_json.put("min_doc_count", 0);
        if (bounds_json.size() != 0) {
            histog_json.put("extended_bounds", bounds_json);
        }
        JSONObject sales_json = new JSONObject();
        sales_json.put("date_histogram", histog_json);

        //添加统计字段
        JSONObject aggregationJsonObject = new JSONObject();
        JSONObject fieldJsonObject = new JSONObject();

        //排序字段
        if (Validator.check(sortField)) {
            JSONArray sort = new JSONArray();
            JSONObject order = new JSONObject();
            order.put("order", sortOrder);
            JSONObject sortfildj = new JSONObject();
            sortfildj.put(sortField, order);
            sort.add(sortfildj);
            fieldJsonObject.put("sort", sort);
        }

        //返回字段
        if (Validator.check(returnFields)) {
            JSONArray jsonArray = JSON.parseArray(JSON.toJSONString(returnFields));
            JSONObject source = new JSONObject();
            source.put("includes", jsonArray);
            fieldJsonObject.put("_source", source);
        }

        fieldJsonObject.put("size", size);
        JSONObject termsJsonObject = new JSONObject();
        termsJsonObject.put("top_hits", fieldJsonObject);
        aggregationJsonObject.put("topHitsData", termsJsonObject);

        sales_json.put("aggs", aggregationJsonObject);
        aggs_json.put(TimeField, sales_json);

        this.queryJson.put("aggs", aggs_json);
        String queryStr = this.queryJson.toString();
        if (this.debug) {
            logger.info(this.queryUrl + " -d " + queryStr);
        }
        String queryResult = request.httpPost(ClientUtils.referenceUrl(this.queryUrl), queryStr);
        if (queryResult != null)
            this.queryJsonResult = JSONObject.parseObject(queryResult);
        if (this.debug) {
            logger.info(this.queryJsonResult);
        }

        //解析结果
        List<String[]> list = new LinkedList<String[]>();

        if (this.queryJsonResult == null || this.queryJsonResult.size() == 0 || !this.queryJsonResult.containsKey("hits")) {
            return list;
        }
        JSONArray bucketJsons = this.queryJsonResult.getJSONObject("aggregations").getJSONObject(TimeField).getJSONArray("buckets");
        this.countTotle = bucketJsons.size();
        for (int index = 0; index < bucketJsons.size(); index++) {
            JSONObject bucketJson = bucketJsons.getJSONObject(index);
            String date = bucketJson.getString("key_as_string");
            String doc_count = bucketJson.getString("doc_count");
            JSONArray hitss = bucketJson.getJSONObject("topHitsData").getJSONObject("hits").getJSONArray("hits");

            JSONArray rs = new JSONArray();
            if (Validator.check(hitss)) {
                for (int j = 0; j < hitss.size(); j++) {
                    JSONObject jsonObject = hitss.getJSONObject(j).getJSONObject("_source");
                    if (Validator.check(jsonObject)) {
                        rs.add(jsonObject);
                    }
                }
            }
            list.add(new String[]{date, doc_count, rs.toJSONString()});
        }

        return list;
    }

    /**
     * 返回检索结果，返回的检索字段,分组聚合返回总量专用
     *
     * @return 检索的结果列表
     */
    private List<String[]> getFacetgroupResult(String field, int topN) {

        List<String[]> list = new LinkedList<String[]>();

        if (this.queryJsonResult == null || this.queryJsonResult.size() == 0 || !this.queryJsonResult.containsKey("hits")) {
            this.countTotle = 0;
            return list;
        }

        JSONArray bucketJsons = this.queryJsonResult.getJSONObject("aggregations").getJSONObject(field).getJSONArray("buckets");
        this.countTotle = bucketJsons.size();

        for (int index = 0; index < bucketJsons.size(); index++) {
            if (topN >= 1 && topN <= index) {
                return list;
            }
            JSONObject bucketJson = bucketJsons.getJSONObject(index);
            list.add(new String[]{bucketJson.getString("key"), bucketJson.getLong("doc_count") + ""});
        }

        return list;
    }

    /**
     * 返回检索结果，返回的检索字段以及字段顺序由{@link #execute(String[])} 方法中的参数fields指定
     *
     * @return 检索的结果列表
     */
    private List<String[]> getFacetResult(String field) {

        List<String[]> list = new LinkedList<String[]>();

        if (this.queryJsonResult == null || this.queryJsonResult.size() == 0 || !this.queryJsonResult.containsKey("hits"))
            return list;

        JSONArray bucketJsons = this.queryJsonResult.getJSONObject("aggregations").getJSONObject(field).getJSONArray("buckets");

        for (int index = 0; index < bucketJsons.size(); index++) {

            JSONObject bucketJson = bucketJsons.getJSONObject(index);

            list.add(new String[]{bucketJson.getString("key"), bucketJson.getLong("doc_count") + ""});
        }

        return list;
    }

    /**
     * 自定义查询
     *
     * @param QuerString 为 luncene语法，例如： +(content:"北京" AND "上海") OR +(title:"北京" AND "上海")
     *                   意思为：只要 content 包含 北京与上海  或者  title 包含 北京与上海   都会返回结果；
     *                   其中 + 是必须存在， - 是必须不存在。 OR AND 都必须大写。
     */
    public void addQueryCondition(String QuerString) {
        keywordString = keywordString + BLANK + QuerString;
    }

    /**
     * 自定义查询
     *
     * @param QuerString 为 全部luncene语法，不与其它条件函数共用
     * @param QuerString
     * @return(结果json)
     */
    public String addQueryConditionBylucene(String QuerString) {
        String queryResult = request.httpPost(this.queryUrl, QuerString);
        return queryResult;
    }

    /**
     * @param
     * @return
     * @Description: TODO(重置HTTP模块 - 将上一次注册的地址移除 ， 并加入新的集群地址)
     */
    public void removeLastHttpsAddNewAddress(String ipPorts) {
        boolean status;
        do {
            status = HttpDiscoverRegister.discover(ipPorts);
        } while (!status);
    }

}



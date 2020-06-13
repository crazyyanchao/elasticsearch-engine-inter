package data.lab.elasticsearch.operation.search;


import java.io.IOException;
import java.io.StringReader;
import java.util.*;

import data.lab.elasticsearch.common.*;
import data.lab.elasticsearch.common.condition.Condition;
import data.lab.elasticsearch.common.condition.Must;
import data.lab.elasticsearch.common.condition.MustNot;
import data.lab.elasticsearch.common.condition.Should;
import data.lab.elasticsearch.model.BoundBox;
import data.lab.elasticsearch.model.BoundPoint;
import data.lab.elasticsearch.model.Circle;
import data.lab.elasticsearch.model.Shape;
import data.lab.elasticsearch.operation.http.HttpSymbol;
import data.lab.elasticsearch.operation.search.aircraft.ConfigTask;
import data.lab.elasticsearch.util.ClientUtils;
import data.lab.elasticsearch.util.Validator;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import data.lab.elasticsearch.util.RegexUtil;
import data.lab.elasticsearch.util.StringUtil;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import com.alibaba.fastjson.JSONArray;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

/**
 * ElasticSearch的索引查询接口(Http方式)
 *
 * @author
 * @version elasticsearch
 */
public class EsIndexSearch extends EsIndexSearchImp {

    @Deprecated
    public EsIndexSearch() {
        super();
    }

    public EsIndexSearch(String IPADRESS, String indexName, String typeName) {
        super(IPADRESS, indexName, typeName);
    }

    @Deprecated
    public EsIndexSearch(String IP, int Port, String indexName, String typeName) {
        super(IP, Port, indexName, typeName);
    }

    public EsIndexSearch(HttpSymbol httpPoolName, String ipPorts, String indexName, String typeName) {
        super(httpPoolName,ipPorts,indexName,typeName);
    }

    /**
     * 工具类：对传入的字符串进行分词
     *
     * @param //keywords 要分词的字符串
     * @param size       返回数量,负数时返回全部
     * @return 返回一个{@code Set<String>}对象，存放分词后的关键词
     */
    public static List<String> extractKeywords(String text, int size) {

        Map<String, Integer> map = new HashMap<>();
        StringReader stringReader = new StringReader(text);
        TokenStream ts = analyzer.tokenStream("", stringReader);
        try {
            ts.reset();
            if (ts == null)
                return null;
            CharTermAttribute attribute = ts.getAttribute(CharTermAttribute.class);
            // 分词，将分词得到的词加到查询中
            while (ts.incrementToken()) {
                String word = new String(attribute.buffer(), 0, attribute.length());
                if (word.length() < 2)
                    continue;
                Integer count = map.get(word);
                if (count == null || count == 0)
                    count = 1;
                else
                    count = count + 1;
                map.put(word, count);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                ts.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        stringReader.close();
        //排序，获取前size个
        List<Map.Entry<String, Integer>> rt = StringUtil.sort(map);
        int rtSize = rt.size();
        if (size == -1 || size > rtSize) {
            size = rtSize;
        }
        //將key值返回
        List<String> rtList = new ArrayList<String>();
        for (int i = 0; i < size; i++) {
            rtList.add(rt.get(i).getKey());
        }
        return rtList;
    }

    /**
     * 工具类：对数组类型的字符串进行转换为数组
     *
     * @param //keywords 要转换的字符串
     * @return 返回一个{@code String[]}对象
     */
    @SuppressWarnings("static-access")
    public static String[] extractStringGroup2(String text) {
        JSONArray jsona = new JSONArray().parseArray(text);
        String[] s = new String[jsona.size()];
        int index = 0;
        for (Object object : jsona) {
            s[index] = object.toString();
        }
        return s;
    }

    /**
     * 工具类：对数组类型的字符串进行转换为以分号间隔的字符串
     * 例如：["a","b","c"]  转换为    a;b;c;
     *
     * @param //keywords 要转换的字符串
     * @return 返回一个{@code String[]}对象
     */
    public static String extractStringGroup(String text) {
        StringBuffer sb = new StringBuffer();
        //判断是否为数组格式
        if (RegexUtil.match(text, "^(\\[)(.*?)(\\])$") != null) {
            JSONArray jsona = new JSONArray().parseArray(text);
            for (Object object : jsona) {
                sb.append(object).append(";");
            }
        } else {
            return text;
        }
        return sb.toString();
    }

//    /**
//     * 添加查询条件，查询条件必须满足lucene的查询语法
//     *
//     * @param query_string
//     */
//    public void addQueryString(String query_string, FieldOccurs occurs) {
//
//
//        if (query_string == null || "".equals(query_string))
//            return;
//
//        String queryCondition = null;
//
//        if (this.queryJson.containsKey("query")) {
//            queryCondition = this.queryJson.getString("query") + BLANK + occurs.getSymbolValue() + "(" + query_string + ")";
//        } else {
//            queryCondition = occurs.getSymbolValue() + "(" + query_string + ")";
//        }
//
//        this.queryJson.put("query", queryCondition);
//    }

    /**
     * 分词
     *
     * @param keywords   输入的字符串
     * @param isUsingMax 是否用最大分词算法
     * @param minLen     分词的最小长度，如2则表示分词后只返回长度大于等于2的分词结果
     * @return
     */
    public static Set<String> analysis(String keywords, boolean isUsingMax, int minLen) {
        Set<String> set = new LinkedHashSet<String>();
        StringReader stringReader = new StringReader(keywords);

        IKSegmenter ik = new IKSegmenter(stringReader, isUsingMax);
        Lexeme lex;
        try {
            while ((lex = ik.next()) != null) {
                String keyword = lex.getLexemeText();
                if (keyword.length() >= minLen)
                    set.add(keyword);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        stringReader.close();
        return set;
    }

    /**
     * 此方法用于对一些不可分词的数据进行检索，例如int,long型数据的genus，alarm等字段 <br>
     * 本方法也可以对分词的数据进行检索，但传入的内容不能有任何的标点符号以及空格，否则检索结果会出现异常<br>
     * 该方法用于int,long,string等类型的匹配，对于string类型，支持通配符(*)<br>
     *
     * @param field                 字段
     * @param terms                 字段对应的值，只能转入一个，且不能有有空格.如果传入的值为负值，请用双引号包起来，例如：<br>
     *                              client.addPrimitveTermQuery("eid", "\"-1\"",FieldOccurs.MUST); 段对应的多值，值之间是或的关系
     * @param combine:多值之间的查询关系，AND OR
     * @param occurs                是否必须作为过滤条件
     */
    public void addPrimitiveTermFilter(String field, String[] terms, KeywordsCombine combine, FieldOccurs occurs) {
        if (terms == null || terms.length == 0)
            return;
        super.keywordString = super.keywordString + BLANK + occurs.getSymbolValue() + field
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
                super.keywordString = super.keywordString + " " + combine + " ";
            }
            super.keywordString = super.keywordString + term;
        }
        super.keywordString = super.keywordString + ")";
    }

    /**
     * @param
     * @return
     * @Description: TODO(输出索引查询语句)
     */
    public JSONObject outputQueryJson() {
        return super.queryJson;
    }

    /**
     * @param
     * @return
     * @Description: TODO(输出索引查询结果)
     */
    public JSONObject outputResult() {
        return super.queryJsonResult;
    }

    /**
     * @param resultList:索引的查询结果 - 包含具体字段名
     * @return
     * @Description: TODO(输出索引查询结果)
     */
    public void outputResult(List<String[]> resultList) {
        // OUTPUT
        int i = 0;
        for (String[] infos : resultList) {
            System.out.print(i++ + ":");
            for (String info : infos)
                System.out.print(info + "\t");
            System.out.println("");
        }
    }

    /**
     * @param result:索引的查询结果 - 包含具体字段名
     * @return
     * @Description: TODO(输出索引查询结果)
     */
    public void outputResult(Map<String, Long> result) {
        // OUTPUT
        for (Map.Entry entry : result.entrySet()) {
            System.out.println("key:" + entry.getKey() + " value:" + entry.getValue());
        }
    }

    /**
     * @param field：统计的字段
     * @param topN：要求返回的结果数 ,topN 等于 0 时，将返回所有的统计结果 。topN 小于0时，不返回结果。
     * @param sort：可选的排序方式
     * @return
     * @Description: TODO(支持数组字段内的聚合)
     */
    public Map<String, Long> facetCountQueryOrderByCountToMap(String field, int topN, SortOrder sort) {
        List<String[]> result = facetCountQueryOrderByCount(field, topN, sort);
        return transferResultListToMap(result);
    }

    /**
     * @param
     * @return
     * @Description: TODO(统计接口的统计结果转换为MAP返回)
     */
    private Map<String, Long> transferResultListToMap(List<String[]> result) {
        Map<String, Long> map = new HashMap<>();
        for (int i = 0; i < result.size(); i++) {
            String[] strings = result.get(i);
            map.put(strings[0], Long.valueOf(String.valueOf(strings[1])));
        }
        return map;
    }

    /**
     * @param field:被查询的字段
     * @param sentence:字段值
     * @return
     * @Description: TODO(相似性查询)
     */
    public void addMoreLikeThisQuery(String field, String sentence) {
        if ((field == null) || ("".equals(field.trim())))
            return;
        sentence = sentence.replace("\\pP|\\pS\\b", " ");
        super.addKeywordsQuery(field, sentence, FieldOccurs.MUST, KeywordsCombine.OR);
    }

    /**
     * @param field:被查询的字段
     * @param sentence:字段值
     * @param keywordSize:控制分词数量(负数时返回所有分词结果)
     * @return
     * @Description: TODO(相似性查询 - 控制分词)
     */
    public void addMoreLikeThisQuery(String field, String sentence, int keywordSize) {
        if ((field == null) || ("".equals(field.trim())))
            return;
        List list = extractKeywords(sentence, keywordSize);
        super.addKeywordsQuery(field, list, FieldOccurs.MUST);
    }

    /**
     * 筛选某区间内的数据，筛选的字段必须为数字形式。 如时间、 id、 评论数等
     *
     * @param field       筛选的字段
     * @param value       区间结束值
     * @param occurs      是否必须作为过滤条件 一般为must
     * @param rangeOccurs 选择过滤方式（大于/大于等于/小于/小于等于）
     */
    public void addRangeTerms(String field, String value, FieldOccurs occurs, RangeOccurs rangeOccurs) {
        if (!Validator.check(value) && !Validator.check(value)) {
            return;
        }
        JSONObject fieldJson = new JSONObject();
        if (Validator.check(value)) {
            //大于等于
            fieldJson.put(rangeOccurs.getSymbolValue(), value);
        }
        JSONObject json = new JSONObject();
        JSONObject rangejson = new JSONObject();
        json.put(field, fieldJson);
        rangejson.put("range", json);
        if (occurs.equals(FieldOccurs.MUST)) {
            super.queryFilterMustJarr.add(rangejson);
        } else if (occurs.equals(FieldOccurs.MUST_NOT)) {
            super.queryFilterMustNotJarr.add(rangejson);
        }
    }

    /**
     * 筛选某区间内的数据，筛选的字段必须为数字形式。 如时间、 id、 评论数等
     *
     * @param field           筛选的字段
     * @param startTerm       区间开始值
     * @param startRangeOccur 指定开始值的开闭区间
     * @param endTerm         区间结束值
     * @param stopRangeOccur  指定结束值的开闭区间
     * @param occurs          是否必须作为过滤条件 一般为must
     */
    public void addRangeTerms(String field, String startTerm, RangeOccurs startRangeOccur, String endTerm, RangeOccurs stopRangeOccur, FieldOccurs occurs) {
        if (!Validator.check(field)) {
            return;
        }
        if ((!Validator.check(startTerm)) && (!Validator.check(endTerm))) {
            return;
        }
        JSONObject fieldJson = new JSONObject();
        if (Validator.check(startTerm)) {
            fieldJson.put(startRangeOccur.getSymbolValue(), startTerm);
        }
        if (Validator.check(endTerm)) {
            fieldJson.put(stopRangeOccur.getSymbolValue(), endTerm);
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
     * 根据时间粒度统计 聚合数量
     * 类似统计每一天it字段下各个类型数据量
     *
     * @param TimeField   查询的时间字段
     * @param format      时间格式 例如：yyyy-MM-dd
     * @param interval    粒度 (1M代表每月，1d代表每日，1H代表每小时)
     * @param secondField 要聚合的字段s
     * @return
     * @Description: TODO(根据时间粒度统计)
     */
    public List<String[]> facetDateBySecondFieldValueCount(String TimeField, String format, String interval, String secondField) {
        //添加查询
        //添加查询
        setRow(0);
        super.getQueryString(null);

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

        JSONObject cardinalityJsonObject = new JSONObject();
        cardinalityJsonObject.put("field", secondField);
        JSONObject countJsonObject = new JSONObject();
        countJsonObject.put("terms", cardinalityJsonObject);
        aggregationJsonObject.put(secondField, countJsonObject);

        sales_json.put("aggs", aggregationJsonObject);
        aggs_json.put(TimeField, sales_json);

        super.queryJson.put("aggs", aggs_json);
        String queryStr = super.queryJson.toString();
        if (super.debug) {
            super.logger.info(super.queryUrl + " -d " + queryStr);
        }
        String queryResult = super.request.httpPost(super.queryUrl, queryStr);
//        String queryResult = HttpProxyRequest.httpProxySendJsonBody(ClientUtils.referenceUrl(this.queryUrl), queryStr);
        if (queryResult != null)
            super.queryJsonResult = JSONObject.parseObject(queryResult);
        if (super.debug) {
            super.logger.info(super.queryJsonResult);
        }

        //解析结果
        List<String[]> list = new LinkedList<String[]>();

        if (super.queryJsonResult == null || super.queryJsonResult.size() == 0 || !super.queryJsonResult.containsKey("hits")) {
            return list;
        }
        JSONArray bucketJsons = super.queryJsonResult.getJSONObject("aggregations").getJSONObject(TimeField).getJSONArray("buckets");
        super.countTotle = bucketJsons.size();
        for (int index = 0; index < bucketJsons.size(); index++) {
            JSONObject bucketJson = bucketJsons.getJSONObject(index);
            JSONArray secondbucketJsons = bucketJson.getJSONObject(secondField).getJSONArray("buckets");
            String[] rs = new String[2 + 2 * secondbucketJsons.size()];

            rs[0] = bucketJson.getString("key_as_string");
            rs[1] = bucketJson.getString("doc_count");
            int inn = 2;
            for (int i = 0; i < secondbucketJsons.size(); i++) {
                JSONObject bucket = secondbucketJsons.getJSONObject(i);
                rs[inn] = bucket.getString("key");
                inn += 1;
                rs[inn] = bucket.getString("doc_count");
                inn += 1;
            }

            list.add(rs);
        }
        return list;
    }

    /**
     * @param
     * @return
     * @Description: TODO(统计事件的评论数 - 度量聚合从不同文档的分组中提取统计数据 ， 这些统计数据通常来自数值型字段)
     */
    public Map<String, Double> facetStatsCount(String eid, String commentField) {
        String result = super.executeDSL(new StringBuilder()
                .append("{")
                .append("\"query\": {\n" +
                        "        \"term\": {\n" +
                        "            \"eid\": \"" + eid + "\"\n" +
                        "        }\n" +
                        "    },")
                .append("\"aggs\": {\n" +
                        "        \"commtcount_event\": {\n" +
                        "            \"stats\": {\n" +
                        "                    \"field\": \"" + commentField + "\"\n" +
                        "                \n" +
                        "            }\n" +
                        "        }\n" +
                        "    },")
                .append("\"size\":0")
                .append("}").toString());
        JSONObject object = JSONObject.parseObject(result);
        JSONObject queryResult = object.getJSONObject("aggregations");
        JSONObject stats = queryResult.getJSONObject("commtcount_event");
        Map<String, Double> map = new HashMap<>();
        map.put("sum", stats.getDouble("sum"));
        map.put("avg", stats.getDouble("avg"));
        map.put("max", stats.getDouble("max"));
        map.put("min", stats.getDouble("min"));
        map.put("count", stats.getDouble("count"));
        return map;
    }

    /**
     * @param field:字段名（例如精确查询多个URL）
     * @param terms:多值字段值
     * @return
     * @Description: TODO(多值精确查询)
     */
    public void addPrimitiveTermsFilter(String field, String[] terms, FieldOccurs occur) {
        if (terms == null || terms.length == 0) {
            return;
        }
        if (occur.getSymbolValue().equals("-")) {
            this.addPrimitiveTermsFilter(super.queryMustNotJarr, field, terms);
        } else if (occur.getSymbolValue().equals("+")) {
            this.addPrimitiveTermsFilter(super.queryMustJarr, field, terms);
        }
    }

    private void addPrimitiveTermsFilter(JSONArray queryMustOrNotJarr, String arrayFieldName, String[] terms) {
        JSONObject termValues = new JSONObject();
        termValues.put(arrayFieldName, JSONArray.parseArray(JSON.toJSONString(terms)));
        JSONObject termsObject = new JSONObject();
        termsObject.put("terms", termValues);
        queryMustOrNotJarr.add(termsObject);
    }

    /**
     * @param field:字段名（例如精确查询多个URL）
     * @param terms:多值字段值
     * @return
     * @Description: TODO(多值精确查询)
     */
    public void addPrimitiveTermsFilter(String field, Set<Object> terms, FieldOccurs occur) {
        if (terms == null || terms.size() == 0) {
            return;
        }
        if (occur.getSymbolValue().equals("-")) {
            this.addPrimitiveTermsFilter(super.queryMustNotJarr, field, terms);
        } else if (occur.getSymbolValue().equals("+")) {
            this.addPrimitiveTermsFilter(super.queryMustJarr, field, terms);
        }
    }

    private void addPrimitiveTermsFilter(JSONArray queryMustOrNotJarr, String arrayFieldName, Set<Object> terms) {
        JSONObject termValues = new JSONObject();
        termValues.put(arrayFieldName, JSONArray.parseArray(JSON.toJSONString(terms)));
        JSONObject termsObject = new JSONObject();
        termsObject.put("terms", termValues);
        queryMustOrNotJarr.add(termsObject);
    }

    /**
     * @param locPointField:字段名-geo类型数据的字段名
     * @param firstBoundPoint:设置矩形框第一个点
     * @param nextBoundPoint:设置矩形框第二个点
     * @param occurs:必须满足/必须不满足
     * @return
     * @Description: TODO(geo - 盒模型过滤器 - 指定矩形框的两个对角)
     */
    public void addGeoBoundingBox(String locPointField, BoundPoint firstBoundPoint, BoundPoint nextBoundPoint, FieldOccurs occurs) {

        if (firstBoundPoint.getLocBoundMark() == null || nextBoundPoint.getLocBoundMark() == null) {
            super.logger.info("Set geo bounding box parameter error!", new IllegalArgumentException());
        }

        JSONObject geoBoundCondition = new JSONObject();

        JSONObject firstPoint = JSONObject.parseObject(JSON.toJSONString(firstBoundPoint));
        firstPoint.remove("locBoundMark");
        geoBoundCondition.put(firstBoundPoint.getLocBoundMark().getSymbolValue(), firstPoint);

        JSONObject nextPoint = JSONObject.parseObject(JSON.toJSONString(nextBoundPoint));
        nextPoint.remove("locBoundMark");
        geoBoundCondition.put(nextBoundPoint.getLocBoundMark().getSymbolValue(), nextPoint);

        JSONObject location = new JSONObject();
        location.put(locPointField, geoBoundCondition);

        JSONObject geo_bounding = new JSONObject();
        location.put("type", "indexed");
        geo_bounding.put("geo_bounding_box", location);

        if (FieldOccurs.MUST.equals(occurs) && !super.queryFilterMustJarr.contains(geo_bounding)) {
            super.queryFilterMustJarr.add(geo_bounding);
        } else if (FieldOccurs.MUST_NOT.equals(occurs) && !super.queryFilterMustNotJarr.contains(geo_bounding)) {
            super.queryFilterMustNotJarr.add(geo_bounding);
        } else if (FieldOccurs.SHOULD.equals(occurs)) {
            JSONObject boolShould = packBoolShould(geo_bounding);
            if (!super.queryFilterMustJarr.contains(boolShould)) {
                super.queryFilterMustJarr.add(boolShould);
            }
        }
    }

    /**
     * @param
     * @return
     * @Description: TODO(DSL实现OR查询 - 嵌套BOOL查询)
     */
    private JSONObject packBoolShould(JSONObject condition) {
        JSONObject bool = new JSONObject();
        JSONObject should = new JSONObject();
        JSONArray conditions = new JSONArray();
        conditions.add(condition);
        should.put("should", conditions);
        bool.put("bool", should);
        return bool;
    }

    /**
     * @param
     * @return
     * @Description: TODO(DSL实现OR查询 - 嵌套BOOL查询)
     */
    private JSONObject packBoolShould(JSONArray conditions) {
        if (conditions != null && !conditions.isEmpty()) {
            JSONObject bool = new JSONObject();
            JSONObject should = new JSONObject();
            should.put("should", conditions);
            bool.put("bool", should);
            return bool;
        } else {
            return new JSONObject();
        }
    }

    /**
     * @param locPointField:字段名-geo类型数据的字段名
     * @param boundBoxList:设多个矩形框
     * @param occurs:必须满足/必须不满足
     * @return
     * @Description: TODO(geo - 盒模型过滤器 - 指定多个矩形框)
     */
    @Deprecated
    public void addGeoBoundingMultiBox(String locPointField, List<BoundBox> boundBoxList, FieldOccurs occurs) {

        if (boundBoxList == null || boundBoxList.isEmpty()) {
            super.logger.info("Set geo bounding box parameter error!", new IllegalArgumentException());
        }
        JSONArray conditions = new JSONArray();
        for (int i = 0; i < boundBoxList.size(); i++) {
            BoundBox boundBox = boundBoxList.get(i);
            JSONObject geo_bounding = packBoundBoxCondition(boundBox, locPointField);
            if (FieldOccurs.MUST.equals(occurs) && !super.queryFilterMustJarr.contains(geo_bounding)) {
                super.queryFilterMustJarr.add(geo_bounding);
            } else if (FieldOccurs.MUST_NOT.equals(occurs) && !super.queryFilterMustNotJarr.contains(geo_bounding)) {
                super.queryFilterMustNotJarr.add(geo_bounding);
            } else if (FieldOccurs.SHOULD.equals(occurs)) {
                conditions.add(geo_bounding);
            }
        }
        JSONObject boolShould = packBoolShould(conditions);
        if (!super.queryFilterMustJarr.contains(boolShould)) {
            super.queryFilterMustJarr.add(boolShould);
        }
    }

    /**
     * @param locPointField:字段名-geo类型数据的字段名
     * @param lat:维度
     * @param lon:经度
     * @param distance:距离
     * @param distanceUnit:指定距离单位(1km/1mi/)-传入NULL则默认单位是米
     * @param occur:距离计算算法选择
     * @return
     * @Description: TODO(geo - 地理距离过滤器接口 - 查找指定距离范围内的数据)
     * <p>
     * The full list of units is listed below:
     * Mile-mi or miles
     * Yard - yd or yards
     * Feet - ft or feet
     * Inch - in or inch
     * Kilometer - km or kilometers
     * Meter - m or meters
     * Centimeter - cm or centimeters
     * Millimeter - mm or millimeters
     */
    public void addGeoDistance(String locPointField, double lat, double lon, int distance, DistanceUnit distanceUnit, GeoDistanceOccurs occur) {

        JSONObject location = packLocation(lat, lon);
        JSONObject geoDisConditon = new JSONObject();
        geoDisConditon.put("distance", distance + distanceUnit.getSymbolValue());
        geoDisConditon.put("distance_type", occur.getSymbolValue());
        geoDisConditon.put(locPointField, location);

        JSONObject geo_distance = new JSONObject();
        geo_distance.put("geo_distance", geoDisConditon);

        if (!super.queryFilterMustJarr.contains(geo_distance)) {
            super.queryFilterMustJarr.add(geo_distance);
        }
    }

    /**
     * @param locPointField:GEO类型的字段名
     * @param occur:距离计算算法选择
     * @param _conditions:（泛型参数）多形状条件组合
     * @return
     * @Description: TODO(多形状组合查询)-支持(MUST/MUST_NOT/SHOULD)任意组合-(目前支持两个形状圆形和矩形)
     */
    public void addGeoShape(String locPointField, GeoDistanceOccurs occur, Condition... _conditions) {
        if (_conditions == null || _conditions.length == 0)
            super.logger.info("Set geo _conditions parameter error!", new IllegalArgumentException());
        JSONObject bool = new JSONObject();
        for (int i = 0; i < _conditions.length; i++) {
            Condition condition = _conditions[i];
            JSONArray cond = wrapGeo(condition, locPointField, occur);
            if (condition instanceof Must) {
                putGeoBool("must", cond, bool);
            } else if (condition instanceof MustNot) {
                putGeoBool("must_not", cond, bool);
            } else if (condition instanceof Should) {
                putGeoBool("should", cond, bool);
            } else {
                super.logger.info("Set parameter error!Abstract classes cannot be instantiated!" + condition.getClass(), new IllegalArgumentException());
            }
        }
        JSONObject boolShould = packBoolShould(bool.getJSONArray("should"));
        JSONArray boolMust = bool.getJSONArray("must");
        JSONArray boolMustNot = bool.getJSONArray("must_not");

        if (boolShould != null && !boolShould.isEmpty() && !super.queryFilterMustJarr.contains(boolShould)) {
            super.queryFilterMustJarr.add(boolShould);
        }
        if (boolMust != null && !boolMust.isEmpty() && !super.queryFilterMustJarr.contains(boolMust)) {
            super.queryFilterMustJarr.add(boolMust);
        }
        if (boolMustNot != null && !boolMustNot.isEmpty() && !super.queryFilterMustNotJarr.contains(boolMustNot)) {
            super.queryFilterMustNotJarr.add(boolMustNot);
        }
    }

    private void putGeoBool(String markBoolConditionField, JSONArray condition, JSONObject bool) {
        if (bool.containsKey(markBoolConditionField)) {
            JSONArray oldCondition = bool.getJSONArray(markBoolConditionField);
            oldCondition.addAll(condition);
            bool.put(markBoolConditionField, oldCondition);
        } else {
            bool.put(markBoolConditionField, condition);
        }
    }

    private JSONArray wrapGeo(Condition condition, String locPointField, GeoDistanceOccurs occur) {
        List<Shape> shapeList = condition.getList();
        JSONArray cond = new JSONArray();
        for (int i = 0; i < shapeList.size(); i++) {
            Object object = shapeList.get(i);
            if (object instanceof BoundBox) {
                cond.add(packBoundBoxCondition((BoundBox) object, locPointField));
            } else if (object instanceof Circle) {
                cond.add(packCircleCondition((Circle) object, locPointField, occur));
            }
        }
        condition.clear();
        return cond;
    }

    /**
     * @param
     * @return
     * @Description: TODO(封装圆查询条件)
     */
    private JSONObject packCircleCondition(Circle circle, String locPointField, GeoDistanceOccurs occur) {
        JSONObject location = packLocation(circle.getCentre().getLat(), circle.getCentre().getLon());
        JSONObject geoDisConditon = new JSONObject();
        geoDisConditon.put("distance", circle.getDistance());
        geoDisConditon.put("distance_type", occur.getSymbolValue());
        geoDisConditon.put(locPointField, location);
        JSONObject geo_distance = new JSONObject();
        geo_distance.put("geo_distance", geoDisConditon);
        return geo_distance;
    }

    /**
     * @param
     * @return
     * @Description: TODO(封装矩形查询条件)
     */
    private JSONObject packBoundBoxCondition(BoundBox boundBox, String locPointField) {
        BoundPoint first = boundBox.getFirstBoundPoint();
        BoundPoint next = boundBox.getNextBoundPoint();
        JSONObject geoBoundCondition = new JSONObject();
        JSONObject firstPoint = JSONObject.parseObject(JSON.toJSONString(first));
        firstPoint.remove("locBoundMark");
        geoBoundCondition.put(first.getLocBoundMark().getSymbolValue(), firstPoint);
        JSONObject nextPoint = JSONObject.parseObject(JSON.toJSONString(next));
        nextPoint.remove("locBoundMark");
        geoBoundCondition.put(next.getLocBoundMark().getSymbolValue(), nextPoint);
        JSONObject location = new JSONObject();
        location.put(locPointField, geoBoundCondition);
        JSONObject geo_bounding = new JSONObject();
        location.put("type", "indexed");
        geo_bounding.put("geo_bounding_box", location);
        return geo_bounding;
    }


    /**
     * @param lat:维度
     * @param lon:经度
     * @return
     * @Description: TODO(封装经纬度数据)
     */
    private JSONObject packLocation(double lat, double lon) {
        JSONObject location = new JSONObject();
        location.put("lat", lat);
        location.put("lon", lon);
        return location;
    }

    /**
     * @param
     * @return
     * @Description: TODO(geo - 多边形过滤器接口 - 根据给定的多个点组成的多边形 ， 查询范围内的点)
     */
    public void addGeoPolygon() {
//        {
//            "query": {
//            "geo_polygon": {
//                "location": {
//                    "points": [
//                    {
//                        "lat": 118.296963,
//                            "lon": 32.818034
//                    },
//                    {
//                        "lat": 117.296963,
//                            "lon": 31.818034
//                    },
//                    {
//                        "lat": 116.296963,
//                            "lon": 30.818034
//                    },
//                    {
//                        "lat": 115.296963,
//                            "lon": 29.818034
//                    }
//        ]
//                }
//            }
//        }
//        }
    }

    /**
     * @param locPointField:字段名-geo类型数据的字段名
     * @param lat:维度
     * @param lon:经度
     * @param distanceUnit:指定距离单位(km/mi/...) - 将距离以...为单位写入到每个返回结果的 sort 键中
     * @param occur:距离计算算法选择
     * @return
     * @Description: TODO(geo - 按照距离排序的接口 - 扩展addSortField接口)
     * <p>
     * The full list of units is listed below:
     * Mile-mi or miles
     * Yard - yd or yards
     * Feet - ft or feet
     * Inch - in or inch
     * Kilometer - km or kilometers
     * Meter - m or meters
     * Centimeter - cm or centimeters
     * Millimeter - mm or millimeters
     */
    public void addSortField(String locPointField, double lat, double lon, DistanceUnit distanceUnit, GeoDistanceOccurs occur, SortOrder order) {
        if (Validator.check(locPointField)) {

            if (!super.queryJson.containsKey("sort")) {
                super.queryJson.put("sort", new JSONArray());
            }
            JSONObject geoCondition = new JSONObject();
            geoCondition.put("order", order.getSymbolValue());
            geoCondition.put("unit", distanceUnit.getSymbolValue());
            geoCondition.put("distance_type", occur.getSymbolValue());
            geoCondition.put(locPointField, packLocation(lat, lon));
            JSONObject sortJson = new JSONObject();
            sortJson.put("_geo_distance", geoCondition);
            super.queryJson.getJSONArray("sort").add(sortJson);
        }
    }

    /**
     * @param
     * @return
     * @Description: TODO(距离范围区间内统计接口 - 查询距离范围区间内的点的数量)
     */
    public List<String[]> facetGeoDistanceRange() {
//        {
//            "size": 0,
//                "aggs": {
//            "myaggs": {
//                "geo_distance": {
//                    "field": "location",
//                            "origin": {
//                        "lat": 65.4144,
//                                "lon": -26.5334
//                    },
//                    "unit": "km",
//                            "ranges": [
//                    {
//                        "from": 50,
//                            "to": 500
//                    }
//        ]
//                }
//            }
//        }
//        }
        return null;
    }

    /**
     * @param field:用来统计的字段
     * @param sortStatsField:父聚集桶的排序方式
     * @param _source:需要返回的字段
     * @param size:每个子聚集桶内返回的数据量(-1返回全部)
     * @param childSortTimeField:子聚集桶内时间字段
     * @param childSortTimeOrder:子聚集桶内时间字段的排序方式
     * @param childSize:子聚集桶内返回的数据量
     * @return
     * @Description: TODO(嵌套聚集获得结果分组排序过滤)
     */
    public List<String[]> facetStatsTermsAggsTophits(String field, SortOrder sortStatsField, String[] _source, int size, String childSortTimeField, SortOrder childSortTimeOrder, int childSize) {
        StringBuilder builder = new StringBuilder();
        if (size == -1) size = 1000_000;
        // track_aggs recent_track
        builder.append("{\n" +
                "    \"aggs\": {\n" +
                "        \"track_aggs\": {\n" +
                "            \"terms\": {\n" +
                "                \"field\": \"" + field + "\",\n" +
                "                \"order\": {\n" +
                "                    \"_count\": \"" + sortStatsField.getSymbolValue() + "\"\n" +
                "                },\n" +
                "                \"size\": " + size + "\n" +
                "            },\n" +
                "            \"aggs\": {\n" +
                "                \"recent_track\": {\n" +
                "                    \"top_hits\": {\n" +
                "                        \"sort\": [\n" +
                "                            {\n" +
                "                                \"" + childSortTimeField + "\": {\n" +
                "                                    \"order\": \"" + childSortTimeOrder.getSymbolValue() + "\"\n" +
                "                                }\n" +
                "                            }\n" +
                "                        ],\n" +
                "                        \"_source\": {\n" +
                "                            \"includes\": " + JSONArray.parseArray(JSON.toJSONString(_source)).toJSONString() + "\n" +
                "                        },\n" +
                "                        \"size\": " + childSize + "\n" +
                "                    }\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "    },\n" +
                "    \"size\": 0\n" +
                "}");
        String queryStr = getQueryString(_source);

        JSONObject queryStrObj = JSONObject.parseObject(queryStr);
        JSONObject builderObj = JSONObject.parseObject(builder.toString());
        builderObj.putAll(queryStrObj);

        String queryResult = request.httpPost(ClientUtils.referenceUrl(this.queryUrl), builderObj.toJSONString());
        if (queryResult != null)
            this.queryJsonResult = JSONObject.parseObject(queryResult);
        if (this.debug) {
            logger.info(this.queryJsonResult);
        }
        //解析结果
        List<String[]> list = new LinkedList<>();
        if (this.queryJsonResult == null || this.queryJsonResult.size() == 0 || !this.queryJsonResult.containsKey("hits")) {
            return list;
        }
        JSONArray bucketJsons = this.queryJsonResult.getJSONObject("aggregations").getJSONObject("track_aggs").getJSONArray("buckets");
        this.countTotle = bucketJsons.size();
        for (int index = 0; index < bucketJsons.size(); index++) {
            JSONObject bucketJson = bucketJsons.getJSONObject(index);
            String date = bucketJson.getString("key");
            String doc_count = bucketJson.getString("doc_count");
            JSONArray hitss = bucketJson.getJSONObject("recent_track").getJSONObject("hits").getJSONArray("hits");

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
     * @param configTasks:多个查询任务
     * @param from:分页开始
     * @param size:分页结束
     * @param sort:排序方式-使用发布时间排序
     * @param sortFieldName:排序字段
     * @param _source:返回的字段名称
     * @param mapField:字段映射-拼接查询时使用的字段名称
     * @return
     * @Description: TODO(多个查询任务拼接成查询条件)
     */
//    public JSONObject toQueryCraftDSL(List<ConfigTask> configTasks, int from, int size, SortOrder sort, String sortFieldName, String[] _source, Map<String, String> mapField) {
//
//        // 拼接查询条件
//        // 区域
//        List<Shape> areas = new ArrayList<>();
//        // 国家
//        List<String> countries = new ArrayList<>();
//        // 种类
//        List<String> species = new ArrayList<>();
//        // 识别码
//        List<String> identificationCodes = new ArrayList<>();
//
//        // --飞机--
//        // s模式
//        List<String> modeSs = new ArrayList<>();
//        // 注册号
//        List<String> registrationNums = new ArrayList<>();
//
//        for (ConfigTask configTask : configTasks) {
//            areas.addAll(configTask.getAreas());
//            countries.add(configTask.getCountry());
//            species.add(configTask.getSpecies());
//            identificationCodes.add(configTask.getIdentificationCode());
//            modeSs.add(configTask.getModeS());
//            registrationNums.add(configTask.getRegistrationNum());
//        }
//
//        // 添加排序
//        addSortField(sortFieldName, sort);
//
//        // 分页参数
//        setStart(from);
//        setRow(size);
//
//        // 多个任务字段条件过滤-字段内是或的关系
//        addGeoShape(mapField.get("areas"), GeoDistanceOccurs.PLANE, Should.init().addMulti(areas));
//        addQueryCondition(new StringBuilder()
//                .append(packLuceneQuery(mapField.get("country"), countries, KeywordsCombine.OR))
//                .append(" OR ")
//                .append(packLuceneQuery(mapField.get("species"), species, KeywordsCombine.OR))
//                .append(" OR ")
//                .append(packLuceneQuery(mapField.get("identificationCode"), identificationCodes, KeywordsCombine.OR))
//                .append(" OR ")
//                .append(packLuceneQuery(mapField.get("modeS"), modeSs, KeywordsCombine.OR))
//                .append(" OR ")
//                .append(packLuceneQuery(mapField.get("registrationNum"), registrationNums, KeywordsCombine.OR))
//                .toString());
//
//        JSONObject query = JSONObject.parseObject(getQueryString(_source));
//        return query;
//    }


    /**
     * @param configTasks:多个查询任务
     * @param from:分页开始
     * @param size:分页结束
     * @param sort:排序方式-使用发布时间排序
     * @param sortFieldName:排序字段
     * @param _source:返回的字段名称
     * @param mapField:字段映射-拼接查询时使用的字段名称
     * @return
     * @Description: TODO(多个查询任务拼接成查询条件)
     */
    public JSONObject toQueryCraftDSL(List<ConfigTask> configTasks, int from, int size, SortOrder sort, String sortFieldName, String[] _source, Map<String, String> mapField) {

        JSONObject dsl = new JSONObject();
        dsl.put("_source", JSONArray.parseArray(JSON.toJSONString(_source)));
        JSONArray sortArray = new JSONArray();
        JSONObject sortObj = new JSONObject();
        sortObj.put(sortFieldName, sort.getSymbolValue());
        sortArray.add(sortObj);
        dsl.put("sort", sortArray);
        dsl.put("from", from);
        dsl.put("size", size);

        // 拼接查询条件
        JSONObject bool1 = new JSONObject();
        JSONObject should1 = new JSONObject();
        JSONArray taskArray = new JSONArray();

        for (ConfigTask configTask : configTasks) {
            taskArray.add(packLuceneQuery(configTask, mapField));
        }

        should1.put("should", taskArray);
        bool1.put("bool", should1);
        dsl.put("query", bool1);
        return dsl;
    }

    public JSONObject packLuceneQuery(ConfigTask configTask, Map<String, String> mapField) {
        JSONObject bool = new JSONObject();
        JSONObject insert = new JSONObject();

        JSONArray mustArray = new JSONArray();
        JSONObject queryStr = new JSONObject();
        JSONObject luceneQuery = new JSONObject();

        luceneQuery.put("query", packLuceneQueryString(configTask, mapField));

        queryStr.put("query_string", luceneQuery);
        mustArray.add(queryStr);

        List<Shape> shapeList = configTask.getAreas();
        JSONArray shouldArray = wrapGeo(Should.init().addMulti(shapeList), mapField.get("areas"), GeoDistanceOccurs.PLANE);

        insert.put("must", mustArray);
        insert.put("should", shouldArray);
        bool.put("bool", insert);

        return bool;
    }

    // 字段之间是AND，字段内部是OR
    private Object packLuceneQueryString(ConfigTask configTask, Map<String, String> mapField) {

        String country = packLuceneQuery(mapField.get("country"), configTask.getCountry(), KeywordsCombine.OR);
        String species = packLuceneQuery(mapField.get("species"), configTask.getSpecies(), KeywordsCombine.OR);
        String identificationCode = packLuceneQuery(mapField.get("identificationCode"), configTask.getIdentificationCode(), KeywordsCombine.OR);
        String modeS = packLuceneQuery(mapField.get("modeS"), configTask.getModeS(), KeywordsCombine.OR);
        String registrationNum = packLuceneQuery(mapField.get("registrationNum"), configTask.getRegistrationNum(), KeywordsCombine.OR);
        StringBuilder builder = new StringBuilder();
        builder.append(!"".equals(country) ? country + " AND " : "")
                .append(!"".equals(species) ? species + " AND " : "")
                .append(!"".equals(identificationCode) ? identificationCode + " AND " : "")
                .append(!"".equals(modeS) ? modeS + " AND " : "")
                .append(!"".equals(registrationNum) ? registrationNum + " AND " : "");
        return builder.substring(0, builder.length() - 4);

    }


    public String packLuceneQuery(String filed, List<String> values, KeywordsCombine combine) {
        if (values == null || values.isEmpty()) return "";

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < values.size(); i++) {
            String value = values.get(i);
            if (value != null && !"".equals(value)) {
                if (combine == KeywordsCombine.OR) {
                    builder.append("\"");
                    builder.append(value);
                    builder.append("\"");
                    builder.append(" OR  ");
                } else {
                    builder.append("\"");
                    builder.append(value);
                    builder.append("\"");
                    builder.append(" AND ");
                }
            }
        }
        return builder.length() > 5 ? "+(" + filed + ":" + builder.substring(0, builder.length() - 5) + ")" : "";
    }

}




package casia.isi.elasticsearch.operation.search;


import java.io.IOException;
import java.io.StringReader;
import java.util.*;

import casia.isi.elasticsearch.common.FieldOccurs;
import casia.isi.elasticsearch.common.SortOrder;
import casia.isi.elasticsearch.util.Validator;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import com.alibaba.fastjson.JSONArray;

import casia.isi.elasticsearch.util.RegexUtil;
import casia.isi.elasticsearch.util.StringUtil;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

/**
 * ElasticSearch的索引查询接口(Http方式)
 *
 * @author wzy
 * @version elasticsearch - 5.6.3
 */
public class EsIndexSearch extends EsIndexSearchImp {

    public EsIndexSearch() {
        super();
    }

    public EsIndexSearch(String IPADRESS, String indexName, String typeName) {
        super(IPADRESS, indexName, typeName);
    }

    public EsIndexSearch(String IP, int Port, String indexName, String typeName) {
        super(IP, Port, indexName, typeName);
    }

    /**
     * 工具类：对传入的字符串进行分词
     *
     * @param //keywords 要分词的字符串
     * @param size       返回数量,负数时返回全部
     * @return 返回一个{@code Set<String>}对象，存放分词后的关键词
     */
    public static List<String> extractKeywords(String text, int size) {

        Map<String, Integer> map = new HashMap<String, Integer>();
        //Set<String> set = new LinkedHashSet<String>();
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

    /**
     * 添加查询条件，查询条件必须满足lucene的查询语法
     *
     * @param query_string
     */
    public void addQueryString(String query_string, FieldOccurs occurs) {


        if (query_string == null || "".equals(query_string))
            return;

        String queryCondition = null;

        if (this.queryJson.containsKey("query")) {
            queryCondition = this.queryJson.getString("query") + BLANK + occurs.getSymbolValue() + "(" + query_string + ")";
        } else {
            queryCondition = occurs.getSymbolValue() + "(" + query_string + ")";
        }

        this.queryJson.put("query", queryCondition);

    }

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
     * @param arrayFieldName:数组字段名称
     * @param terms:数组值-多值必须都满足才返回
     * @param occur:定义此过滤条件与其它过滤条件的的组合方式
     * @return
     * @Description: TODO(添加过滤条件 - 过滤数组类型的字段)
     */
    public void addArrayTypeTermsQuery(String arrayFieldName, String[] terms, FieldOccurs occur) {
        if (terms == null || terms.length == 0)
            return;

        if (occur.getSymbolValue().equals("-")) {
            addArrayTypeTerms(super.queryMustNotJarr, arrayFieldName, terms);
        } else if (occur.getSymbolValue().equals("+")) {
            addArrayTypeTerms(super.queryMustJarr, arrayFieldName, terms);
        }
    }

    /**
     * @param queryMustOrNotJarr:boolean查询数组
     * @return
     * @Description: TODO(添加数组的过滤条件)
     */
    private void addArrayTypeTerms(JSONArray queryMustOrNotJarr, String arrayFieldName, String[] terms) {
        JSONObject termValues = new JSONObject();
        termValues.put(arrayFieldName, JSONArray.parseArray(JSON.toJSONString(terms)));
        JSONObject termsObject = new JSONObject();
        termsObject.put("terms", termValues);
        queryMustOrNotJarr.add(termsObject);
    }

    /**
     * @param
     * @return
     * @Description: TODO(输出索引查询结果)
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

}



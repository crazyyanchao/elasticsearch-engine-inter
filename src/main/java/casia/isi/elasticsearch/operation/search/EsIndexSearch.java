package casia.isi.elasticsearch.operation.search;


import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import com.alibaba.fastjson.JSONArray;

import casia.isi.elasticsearch.util.RegexUtil;
import casia.isi.elasticsearch.util.StringUtil;

/**
 * ElasticSearch的索引查询接口(Http方式)
 * @author wzy
 * @version elasticsearch - 5.6.3
 */
public class EsIndexSearch extends EsIndexSearchImp{
	public EsIndexSearch() {
		super();
	}
	public EsIndexSearch(String IPADRESS, String indexName, String typeName) {
		super(IPADRESS, indexName, typeName);
	}
	public EsIndexSearch(String IP, int Port , String indexName,String typeName){
		super( IP , Port , indexName , typeName );
	}
	/**
	 * 工具类：对传入的字符串进行分词
	 * 
	 * @param keywords
	 *            要分词的字符串
	 * @param size
	 *            返回数量,负数时返回全部
	 * @return 返回一个{@code Set<String>}对象，存放分词后的关键词
	 */
	public static List<String> extractKeywords(String text,int size) {
		
		Map<String,Integer> map = new HashMap<String,Integer>();
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
				if(word.length() < 2)
					continue;
				Integer count = map.get(word);
				if(count == null || count == 0)
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
		if(size == -1 || size > rtSize){
			size = rtSize;
		}
		//將key值返回
		List<String> rtList = new ArrayList<String>();
		for(int i = 0; i < size; i ++){
			rtList.add(rt.get(i).getKey());
		}
		return rtList;
	}
	/**
	 * 工具类：对数组类型的字符串进行转换为数组
	 * @param keywords
	 *            要转换的字符串
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
	 * @param keywords
	 *            要转换的字符串
	 * @return 返回一个{@code String[]}对象
	 */
	public static String extractStringGroup(String text) {
		StringBuffer sb = new StringBuffer();
		//判断是否为数组格式
		if( RegexUtil.match(text, "^(\\[)(.*?)(\\])$") != null ) {
			JSONArray jsona = new JSONArray().parseArray(text);
			for (Object object : jsona) {
				sb.append(object).append(";");
			}
		}else{
			return text;
		}
		return sb.toString();
	}
}

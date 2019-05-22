package casia.isi.elasticsearch.operation.sql;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import casia.isi.elasticsearch.common.Symbol;
import casia.isi.elasticsearch.operation.http.HttpRequest;
import casia.isi.elasticsearch.util.StringUtil;
import casia.isi.elasticsearch.util.Validator;

public class EsIndexSqlImp {
	/**
	 * 日志对象
	 */
	private static Logger logger = Logger.getLogger(EsIndexSqlImp.class);
	/**
	 * http访问对象
	 */
	private HttpRequest request = null;
	/**
	 * 查询索引的url
	 */
	private String queryUrl;
	/**
	 * 是否开启debug模式，debug模式下过程语句将会输出
	 */
	public static boolean debug = false;
	/**
	 * 设置日志输出对象
	 * @param LOGGER  log4j对象
	 */
	public void setLogger(Logger logger){
		EsIndexSqlImp.logger = logger;
	}
	/**
	 * 查询的返回值
	 */
	private JSONObject queryJsonResult = null;
	/**
	 * 重置搜索条件
	 */
	public void reset() {
		
		try{
			this.queryJsonResult = null;
		}catch(Exception e){}
	}
	public EsIndexSqlImp(){}
	/**
	 * 构造函数，传入索引地址，索引名和类型名
	 * @param IPADRESS
	 * 			索引的ip和端口，格式 ip:port 多个以&隔开
	 */
	public EsIndexSqlImp(String IPADRESS) {
		if(IPADRESS == null ){
			logger.error("ip must not be null");
		}
		String[] servers = IPADRESS.split(Symbol.SPACE_CHARACTER.toString());
		//构造查询url
		this.queryUrl = "http://" + servers[new Random().nextInt(servers.length)];
		this.queryUrl = this.queryUrl + "/_sql";
		this.request = new HttpRequest();
	}
	/**
	 * 构造函数，传入索引地址，索引名和类型名
	 * @param IP
	 * 			索引的ip
	 * @param Port
	 * 			索引的ip端口
	 */
	public EsIndexSqlImp(String IP, int Port) {
		if(IP == null || Port == 0){
			logger.error("ip must not be null");
		}
		//构造查询url
		this.queryUrl = "http://" + IP +":"+Port;
		this.queryUrl = this.queryUrl + "/_sql";
		this.request = new HttpRequest();
	}
	/**
	 * 通过sql语法查询es索引
	 * 例如：select * from indexName limit 10;
	 * 		查询indexName索引所有数据前十条
	 * @param sql
	 */
	public void queryBySql(String sql){
		if( !Validator.check(sql) ){
			logger.error("parameter sql cannot be empty!");
			return;
		}
		if( debug ){
			logger.info("query -d "+this.queryUrl+"; sql:"+sql);
		}
		String query_url = this.queryUrl+"?sql="+sql;
		System.out.println(StringUtil.urlEscape(query_url));
		
		String queryResult = request.httpGet( StringUtil.urlEscape(query_url) );
		if( debug ){
			logger.info("queryResult: -d "+queryResult);
		}
		if(queryResult != null)
			this.queryJsonResult = JSONObject.parseObject(queryResult);
	}
	/**
	 * 返回检索结果，返回的检索字段以及字段顺序由{@link #execute(String[])} 方法中的参数fields指定
	 * 
	 * @return
	 * 		检索的结果列表
	 */
	public List<Map<String, String>> getResults() {
		List<Map<String, String>> list = new LinkedList<Map<String, String>>();
		if(this.queryJsonResult == null || this.queryJsonResult.size() == 0 || !this.queryJsonResult.containsKey("hits"))
			return list;
		JSONArray hitJsons = this.queryJsonResult.getJSONObject("hits").getJSONArray("hits");
		for (int index = 0; index < hitJsons.size(); index ++) {
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

package casia.isi.elasticsearch;

import java.io.File;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import casia.isi.elasticsearch.operation.index.EsIndexCreat;

public class CreatTest {

	private Logger logger = Logger.getLogger(CreatTest.class);
	
	public static void main(String[] args) throws SQLException, NumberFormatException, ParseException {
		PropertyConfigurator.configureAndWatch("config"+File.separator+"log4j.properties");

		EsIndexCreat indexer = new EsIndexCreat("106.75.177.129",61233,"all_data_q-201808","analysis_data");
		
		//创建新字段
		Map<String, String> map = new HashMap<String, String>();
		map.put("type", "keyword");
		map.put("index", "not_analyzed");
		boolean boo = indexer.insertField("zdr_clue", map);
		System.out.println(boo);
		
		//创建新数据
		/*List<JSONObject> list = new ArrayList<JSONObject>();
		for (int i = 1; i < 3; i++) {
			JSONObject json = new JSONObject();
			json.put("id", i);
			json.put("gid", i+"我的宝时捷马力也不错");
			list.add(json);
		}
		indexer.index(list, "id");*/
		
		//判断索引是否存在
		/*boolean rs= indexer.isIndexName();
		System.out.println(rs);*/
		
		//查询所有索引名
		/*List<String> rs= indexer.searchIndexNames();
		for (String string : rs) {
			System.out.println(string);
		}*/
	}

}


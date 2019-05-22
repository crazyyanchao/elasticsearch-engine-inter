package casia.isi.elasticsearch;


import java.util.HashMap;
import java.util.Map;

import casia.isi.elasticsearch.operation.update.EsIndexUpdate;

public class UpdateTest {
	public static void main(String[] args) {
		EsIndexUpdate es = new EsIndexUpdate("106.75.177.129",61233,"event_data_extract_result_v-201808","analysis_data");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("zdr_clue", new String[]{"湘西","公安局"});
		boolean boo = es.UpdateParameterById(map, "26f989e085b3a53a9ae5e753952b76eb");
		System.out.println(boo);
	}
}

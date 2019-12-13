package casia.isi.elasticsearch;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import casia.isi.elasticsearch.common.SortOrder;
import casia.isi.elasticsearch.operation.search.EsIndexSearch;
import casia.isi.elasticsearch.operation.update.EsIndexUpdate;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;

public class UpdateTest {

    @Test
    public void test1() {
        EsIndexUpdate es = new EsIndexUpdate("106.75.177.129", 61233, "event_data_extract_result_v-201808", "analysis_data");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("zdr_clue", new String[]{"湘西", "公安局"});
        boolean boo = es.updateParameterById(map, "26f989e085b3a53a9ae5e753952b76eb");
        System.out.println(boo);
    }

    @Test
    public void test2() {
        //新增字段
//		EsIndexCreat indexer = new EsIndexCreat("106.75.177.129",61233,"event_data_extract_result_v-201808","analysis_data");
//		Map<String, String> map = new HashMap<>();
//		map.put("type", "keyword");
//		map.put("index", "not_analyzed");
//		boolean a= indexer.insertField("con_md5", map);
//		System.out.println(a);

        EsIndexSearch searchClient = new EsIndexSearch("106.75.177.129:61233","all_data_q-201808","analysis_data");
        EsIndexUpdate es = new EsIndexUpdate("106.75.177.129:61233", "all_data_q-201808", "analysis_data");

        String a = "0";
        while (true) {
            searchClient.reset();
            searchClient.addRangeTerms("pid", a+"", null );
            searchClient.addSortField("pid", SortOrder.ASC);
//			searchClient.addPhraseQuery("eid","3", FieldOccurs.MUST);
            searchClient.setStart(0);
            searchClient.setRow(1000);
            searchClient.execute(new String[]{"_id","pid","content","_index","_type"});
            List<String[]> list= searchClient.getResults();
            System.out.println(searchClient.getTotal());
//			if(list.size()==0 ||list.size()==1 ){break;}
            for (String[] strings : list) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("con_md5", DigestUtils.md5Hex(strings[2]) );
                es.updateParameterById(map,strings[0]);
                a = strings[1];
//				System.out.println(a);
            }
        }


//		EsIndexUpdate es = new EsIndexUpdate("106.75.137.175:61233", "event_data_extract_result_v", "analysis_data");
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("is_spam", "1");
////		map.put("province", "广东");
////		map.put("pubtime", "2018-06-25 11:23:12");
//		map.put("viewport", new String[]{"明天举着横幅到杭州市委门口，抗议"});
////		map.put("lal", "22.548766,114.043955");
//		boolean boo = es.UpdateParameterById(map, "103825");
//		System.out.println(boo);
    }


}



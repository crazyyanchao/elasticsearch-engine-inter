package casia.isi.elasticsearch;

import java.io.File;
import java.util.List;

import org.apache.log4j.PropertyConfigurator;

import casia.isi.elasticsearch.common.SortOrder;
import casia.isi.elasticsearch.operation.search.EsIndexSearch;
import org.junit.Test;

public class SearchTest {

    @Test
    public void test1() {
        PropertyConfigurator.configureAndWatch("config" + File.separator + "log4j.properties");

        long a = System.currentTimeMillis();
        EsIndexSearch searchClient = new EsIndexSearch("106.75.177.129:61233", "all_data_v-201808", "analysis_data");
//		EsIndexSearch searchClient =new EsIndexSearch("106.75.177.129:61233","all_data*","analysis_data");
//		
		/*searchClient.reset();//重置
		searchClient.debug = true;
		
//		List<String> list=  new ArrayList<String>();
//		list.add("北京 深圳");
//		list.add("上海 暴恐");
		String list = "Joe William Cañas ABR/CNE/PMP/ITIL";
		searchClient.addPhraseQuery("uid",list,FieldOccurs.MUST,KeywordsCombine.OR);
//		searchClient.addPhraseQuery(new String[]{"uid","gid"}, list, FieldOccurs.MUST,KeywordsCombine.AND,FieldCombine.OR);//content必须包含完整的北京大学才能匹配到
//		searchClient.addPrimitiveTermQuery("_id", "554491",  FieldOccurs.MUST );//不可分割数据检索，例如int,long型数据的genus，alarm等字段
//		searchClient.addExistsFilter( "content" );//非空
//		searchClient.addMissingFilter( "content" );//为空
		searchClient.setStart(0);
		searchClient.setRow(20);
		String[] fieldss = {"uid","gid"};
		searchClient.execute(fieldss);
		System.out.println("总量："+searchClient.getTotal());
		List<String[]> resultLists = searchClient.getResults();
		for (String[] strings : resultLists) {
			for (String string : strings) {
				System.out.print(searchClient.extractStringGroup(string)+"\t");
			}
			System.out.println("");
		}*/


        searchClient.reset();//重置
        searchClient.debug = true;
//		searchClient.addPhraseQuery("eid","1",FieldOccurs.MUST);
//		searchClient.addRangeTerms("pubtime", "2017-05-01 00:00:00", "2017-05-31 00:00:00");
        List<String[]> list7 = searchClient.facetCountQueryOrderByCount("uid", 10, SortOrder.DESC, new String[]{"pubtime"}, "pubtime", SortOrder.DESC, 1);

        //返回结果list数组，每个String[]的第0位为时间区间，第1位为该区间的文档数,第2位为该区间内聚合的uid字段值；
        for (String[] infos : list7) {
            for (String info : infos)
                System.out.print(info + "\t");
            System.out.println("");
        }

    }

}

package data.lab.elasticsearch;

import data.lab.elasticsearch.operation.delete.EsIndexDelete;

public class DeleteTest {
	public static void main(String[] args) {
		EsIndexDelete es = new EsIndexDelete("106.75.177.179:61233","test1","test_data");
		
		/*System.out.println("\r\n---------------------------1.按主键删除------------------------\r\n");
		elasticsearch.reset();
		boolean rs = elasticsearch.deleteById("11");
		long del_number = elasticsearch.getDeleteTotal();
		System.out.println(rs+"\t"+del_number);*/
		
		/*System.out.println("\r\n---------------------------2.按主键删除(弃用)------------------------\r\n");
		elasticsearch.reset();
		List<String> list = new ArrayList<String>();
		list.add("1");
		list.add("2");
		boolean rs = elasticsearch.deleteByIds(list);
		long del_number = elasticsearch.getDeleteTotal();
		System.out.println(rs+"\t"+del_number);*/
		
		
		/*System.out.println("\r\n---------------------------3.按短语匹配(must)删除------------------------\r\n");
		elasticsearch.reset();
		boolean rs = elasticsearch.deleteByQueryRun("eid", "12");
		long del_number = elasticsearch.getDeleteTotal();
		System.out.println(rs+"\t"+del_number);*/
		
		
		/*System.out.println("\r\n---------------------------4.按短语匹配(must_not)删除------------------------\r\n");
		elasticsearch.reset();
		boolean rs = elasticsearch.deleteByNotQueryRun("id", "0");
		long del_number = elasticsearch.getDeleteTotal();
		System.out.println(rs+"\t"+del_number);*/
		
		/*System.out.println("\r\n---------------------------5.按区间范围(must)删除------------------------\r\n");
		elasticsearch.reset();
		boolean rs = elasticsearch.deleteByRangeRun("id", "2", "5");
		long del_number = elasticsearch.getDeleteTotal();
		System.out.println(rs+"\t"+del_number);*/
		
		/*System.out.println("\r\n---------------------------6.按区间范围(must_not)删除------------------------\r\n");
		elasticsearch.reset();
		boolean rs = elasticsearch.deleteByNotRangeRun("id", "2", "5");
		long del_number = elasticsearch.getDeleteTotal();
		System.out.println(rs+"\t"+del_number);*/
		
		/*System.out.println("\r\n---------------------------7.自定义条件删除------------------------\r\n");
		elasticsearch.reset();
		elasticsearch.addExistsFilter("eid");//字段不为空
		elasticsearch.addMissingFilter("id");//字段为空
		elasticsearch.addPhraseQuery("content", "7" , FieldOccurs.MUST, KeywordsCombine.OR);//短语匹配
		elasticsearch.addPrimitiveTermFilter("id", "3" , FieldOccurs.MUST);//整数型匹配
		elasticsearch.addRangeTerms("id", "2", "9", FieldOccurs.MUST);//范围
		elasticsearch.execute();//执行
		long del_number = elasticsearch.getDeleteTotal();
		System.out.println(del_number);*/
		
		System.out.println("\r\n---------------------------8.清空索引数据------------------------\r\n");
		es.reset();
		es.debug = true;
		boolean rs = es.deleteByIndexNameRun();
		long del_number = es.getDeleteTotal();
		System.out.println(rs+"\t"+del_number);
		
		/*System.out.println("\r\n---------------------------9.删除索引------------------------\r\n");
		elasticsearch.reset();
		boolean rs = elasticsearch.deleteIndexNameRun();
		System.out.println(rs);*/
	}
}

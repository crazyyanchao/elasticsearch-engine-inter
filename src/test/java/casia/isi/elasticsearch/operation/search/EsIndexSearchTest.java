package casia.isi.elasticsearch.operation.search;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.Set;

/**
 * 　　　　　　　 ┏┓       ┏┓+ +
 * 　　　　　　　┏┛┻━━━━━━━┛┻┓ + +
 * 　　　　　　　┃　　　　　　 ┃
 * 　　　　　　　┃　　　━　　　┃ ++ + + +
 * 　　　　　　 █████━█████  ┃+
 * 　　　　　　　┃　　　　　　 ┃ +
 * 　　　　　　　┃　　　┻　　　┃
 * 　　　　　　　┃　　　　　　 ┃ + +
 * 　　　　　　　┗━━┓　　　 ┏━┛
 * ┃　　  ┃
 * 　　　　　　　　　┃　　  ┃ + + + +
 * 　　　　　　　　　┃　　　┃　Code is far away from     bug with the animal protecting
 * 　　　　　　　　　┃　　　┃ +
 * 　　　　　　　　　┃　　　┃
 * 　　　　　　　　　┃　　　┃　　+
 * 　　　　　　　　　┃　 　 ┗━━━┓ + +
 * 　　　　　　　　　┃ 　　　　　┣┓
 * 　　　　　　　　　┃ 　　　　　┏┛
 * 　　　　　　　　　┗┓┓┏━━━┳┓┏┛ + + + +
 * 　　　　　　　　　 ┃┫┫　 ┃┫┫
 * 　　　　　　　　　 ┗┻┛　 ┗┻┛+ + + +
 */

/**
 * @author YanchaoMa yanchaoma@foxmail.com
 * @PACKAGE_NAME: casia.isi.elasticsearch.operation.search
 * @Description: TODO(检索测试)
 * @date 2019/5/22 11:24
 */
public class EsIndexSearchTest {

    @Test
    public void search() {
        PropertyConfigurator.configureAndWatch("config" + File.separator + "log4j.properties");

//		EsIndexSearch searchClient = new EsIndexSearch("106.75.136.149:61230&106.75.136.149:61231","all_data","analysis_data");
//		EsIndexSearch searchClient = new EsIndexSearch("106.75.177.129:61233","test2","wechat_new");
        EsIndexSearch searchClient = new EsIndexSearch("192.168.12.110:9200", "aircraft_info", "graph");
//		EsIndexUpdate es = new EsIndexUpdate("106.75.137.175:61233", "event_data_extract_result_v*,event_data_extract_result_q*", "analysis_data");

        System.out.println("\r\n---------------------------1.关键词、范围、分页、排序------------------------\r\n");
//		searchClient.addKeywordsQuery( "content" , "北京大学", FieldOccurs.MUST , KeywordsCombine.OR);//针对分词字段检索，检索内容也会分词，例如检索北京大学，返回结果可能存在北京理工大学
//		searchClient.addRangeTerms("id", "0", "19741");//数字类型范围
//		searchClient.addRangeTerms("pubtime", "2017-04-01 00:16:27", null);//日期类型范围
        searchClient.setStart(0);//分页
        searchClient.setRow(4);//分页
//		searchClient.addSortField("pubtime", SortOrder.ASC);//排序
        String[] fields = {"_id", "pubtime"};//返回字段
        searchClient.execute(fields);//执行查询
        System.out.println("总量：" + searchClient.getTotal());//获取结果总量
        List<String[]> resultList = searchClient.getResults();//获取结果
        for (String[] strings : resultList) {
            for (String string : strings) {
                System.out.print(string + "\t");
            }
            System.out.println("");
        }

		/*System.out.println("\r\n---------------------------2.原子、短语、非空------------------------\r\n");
		searchClient.reset();//重置
		searchClient.debug = true;
		List<String> list = new ArrayList<String>();
		list.add("北京 深圳");
		list.add("上海 深圳");
		searchClient.addPhraseQuery("content",list, FieldOccurs.MUST,KeywordsCombine.OR,KeywordsCombine.AND);
//		searchClient.addPhraseQuery("uid", "b966ac68db", FieldOccurs.MUST,KeywordsCombine.AND);//content必须包含完整的北京大学才能匹配到
		searchClient.addPhraseQuery("province", "北京", FieldOccurs.MUST,KeywordsCombine.OR);
		searchClient.addPhraseQuery("city", "北京市", FieldOccurs.MUST,KeywordsCombine.OR);
		searchClient.addPhraseQuery("prefecture", "怀柔区", FieldOccurs.MUST,KeywordsCombine.OR);//content必须包含完整的北京大学才能匹配到
//		searchClient.addPrimitiveTermQuery("_id", "554491",  FieldOccurs.MUST );//不可分割数据检索，例如int,long型数据的genus，alarm等字段
//		searchClient.addExistsFilter( "content" );//非空
//		searchClient.addMissingFilter( "content" );//为空
		searchClient.addSortField("pubtime", SortOrder.DESC);
		searchClient.setStart(0);
		searchClient.setRow(10);
		String[] fieldss = {"_id","country","province","city","prefecture"};
		searchClient.execute(fieldss);
		System.out.println("总量："+searchClient.getTotal());
		List<String[]> resultLists = searchClient.getResults();
		for (String[] strings : resultLists) {
			for (String string : strings) {
				System.out.print(searchClient.extractStringGroup(string)+"\t");
			}
			System.out.println("");
		}
		Date a = new Date();
		a.setMinutes(0);
		a.setSeconds(0);
		System.out.println(new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(a));*/

		/*System.out.println("\r\n---------------------------3.获取数据量------------------------\r\n");
		searchClient.reset();//重置
//		searchClient.addPhraseQuery("_id", "9bae4d74920644e33d3197bbb55ed5a9", FieldOccurs.MUST,KeywordsCombine.AND);//content必须包含完整的北京大学才能匹配到
		searchClient.addRangeTerms("insertime", "2018-07-03 13:50:00", "2018-07-03 14:10:00");
		long total = searchClient.searchTotal();
		System.out.println(total);*/

		/*System.out.println("\r\n----------------------------4.单字段聚合-----------------------\r\n");
		searchClient.reset();//重置

		//针对uid字段进行聚合，按uid聚合的文档数倒序，返回前5条(返回参数小于0时返回0条，参数等于0时返回全部，参数大于0时返回参数指定数)
//		searchClient.addPhraseQuery("gid", "zx858x0004 y44yz3xyx ywz83500xz x303yx434y xx9z58y9wx w9055508xw xz389x8z03 80wzxz0340 8yz3334yx5 x85x3x04w0 y358xzx3z4 8yx054x083 8yy0wy8935 w849zxxx58 ww80340yw5 wz305y9904 x3540y5095 x8wwyx5xxw xw8w3w8yyx 4z9x0yxww 8y54w8983x 8yyw5w9099 w04550x304 w085993449 w09wyz4y40 w30y93z58z w583095y8w w5ww5z00wy w5x0xz08x3 w80w54ww3y w8550w439w w8x080x888 ww0z339098 wx90448xzy wyw5y5z90x wyyx4xz358 wzxx5y5y39 x3zw0y535y x438zwxxwz x498y5yyxx y4zz384w85 53yx0ywy5y 805x5440x8 80z4w50090 8y30wy508w 8y44339ww4 8y8w5z4zx4 8y93zyzy43 8yy35xw854 8yzy5x9430 8z54xz900z 8z55389z53 8zx0wyx8z0 8zzx5x9334 9y4wz55xw w0yw3w9w3z w3wy3y8w3z w485zy4544 w59454458y w59843x8z5 w5x9334w40 w5yyx3x5z4 w8943934xz w90454y0x3 w9w0390x49 w9z05095w3 w9zww4x45z ww0y80xwx3 ww39409353 wwyy593543 wx35w4405y wy8w3w8z0z wy9zy5x40w wyyy4y3y8y wzx95yxz30 x338y50908 x35y5zx4y0 x443385543 x53w39z503 x55xyz4z35 x5x8z985yw x80z3ww094 x894380z4y x898zwy5z0 x9y94534x5 xw4330y4x3 xw8x3w8y3z xw935z5384 xx50504y0w xx5z0y5354 xyxy08z33 y850354wz8 yy83yy0w98 z3x09yxw9w z909z004y5 308y050080 3z3x8980w 58zwyz59w 8033389xz0 8054390y44 80585yy8z5 8080x3yyzz 809w99y09z 80xyw434zw 80yxx5y33x 8x094y8w5 8y3x339z50 8yx459535z 8yy5044zxx 8z4w544953 8z55w58y48 8z84408400 8zw03z5zwy 8zw9x3xyxx 8zx53893xy 8zx8w4w30y 8zxx548z5w 8zyy339388 w08xy5y303 w0x0zy3w33 w0x4333x8x w0xwy5z9wy w0xz5y4339 w0y5y5z888 w0yx3x859y w308wx3385 w39xwxyx35 w3xw405w84 w3yx3ww954 w44z893090 w45z405zz3 w48xx9x0zw w498339390 w5095w58xz w53w0939xy w545y0w9x4 w555y35393 w583x0y0w4 w59xx550zw w5w8w89995 w5y445ww98 w5z0x598wz w830wx4385 w840w3z8xx w843333wz3 w84939044w w84953y545 w84w340535 w84xz0xy8w w8943900wy w8ww3y55xw w8xy340zx5 w8yy3893zw w8z9339z03 w8zyy4z900 w94z5wx5x0 w95wwz4403 w95x08y3x3 w9858yww95 w985xw59x9 w99434wxzz w99yz0y4xz w9w9389350 ww895809xw ww8x543yxw www438895w wwy4x4yyy0 wwz854098w wx3w355y0x wx3xwzyyzy wx59w983y9 wx84339004 wxx3w48w54 wxxw0894zx wxz53898z5 wy30y0x48x wy3x349z5y wy853wz449 wy933wz33y wy99w8w404 wyw9y53y9z wyy0458zx9 wyyw39y49y wyyx0x583x wyyxyz3984 wyz5z3wy3x wz0w330y39 wz384w8995 wz40y5y4x9 wz4zy5y4yx x38zyz08zx x39308zy45 x39wy5y539 x3w05z4940 x3w80x053y x3x4yz459y x4093w005w x440y54449 x4z95z9y4y x5033zy483 x5xw3493z3 x5y3zz0509 x8393x9w4z x85z38533z x8939wyy4x x895yz3885 x89y40x989 x89yy03x0w x89yyz3983 x8x94zw949 x8xx30wx3y x8zz5z0998 x90z4z890y x94xyz4y49 x99x08x040 x9w3yz4330 x9yy340yxx xw05y08594 xw34z3x38w xw850y4844 xwy4yz045w xwyx0y48z3 xx054393w3 xx05yz4zw9 xx394848x4 xx44y5z8w8 xx8yy098w5 xx90y54ywy xx93y5y4ww xxyy3zxz3y y33x5yw840 y3838yx80x y394xyyw08 y409yxx945 y5000038y9 y5444w99z4 y80yzyx8x9 y94xxz45x0 yw003w5898 yw0w33wx ywx00583y3 ywy5094z4w z0x8w4z094 z30y48xxy8 z3yzw5w00y z4430449wx z4y3yx8w9y z543834z95 z94933xxw8 z94xz8y5z9 zw983yx533 zw9xzxwx53 zz80995434 30wzx8z895 33583504z 34y595xywy 394wx4xxy 3wxyz8x03 3yx3w4483y 533w998485 540y9wz305 585y9x95x0 5ww598y99y 5ywwz4939 5z559w9z4 65111 800055583y 8005544x3y 8033393z05 8083x4x8yy 808w39zw9w 808zy35489 809x590y8z 80w35y84yw 80w3xy8zx8 80wz4y040x 80wz5z339z 80wzx00054 80x55444x4 80x939z003 80xx339x8x 80yy389xyz 80zy4349y3 84y38w4y 8y044zz9x4 8y0830xz0z 8y0wwzw5yx 8y0y54w85y 8y3w5w0z95 8y3x4wyxz0 8y50385wy4 8y55598xw4 8y555x5089 8y90w849xy 8yw0548489 8yxyx34w0z 8yy339z4w5 8yz353wxz4 8yz8w9ww59 8yzw54wx90 8z03w9wz9z 8z38w99y3w 8z38xx05xw 8z39y98w4w 8z4w509zz3 8z54340zy4 8z80wy339x 8z8339wx53 8z8y39zzw4 8z9y333yxy 8zwxxw3083 8zwz550w89 8zxzx34y50 8zy4xzy00x 8zyz5949xy w009y5yx50 w00yy58959 w00z4w385x w03w3w84z4 w03w493zy4 w04y4x90z9 w059yz4yx4 w08308xyxw w0833wz5wz w08y4y34x8 w09345zx3z w095y80305 w099y5x9w8 w0x94x90zx w0y53zyyxz w0y5404wzy w0yz40xyw8 w30895533z w3599xz5xy w393083zy9 w3w9wz4z98 w4090498w9 w49xw85w5w w4w94z90yw w50y054403 w53354398z w539339xww w53w390zz8 w53x335w8x w53xxwx55w w54xw08804 w54y58ww3x w558593w59 w55x4y5zy4 w55x5x4855 w55z54905w w594544855 w599389990 w59x3w0w93 w59x5w3zz0 w5w83wzy04 w5x4w4w34x w5y439z0z3 w5y4yzz59z w5y533955z w5yz38949w w5z3389wx0 w5z9x00w8x w5zw5w8xz3 w80454xz0z w804w95ww3 w80859y844 w83439z4xz w8344zw330 w8483z4y88 w854xy8z94 w85953xz90 w88054zw38 w8845x90x8 w884xzzwx5 w89z395y00 w8w0xy9xx4 w8x0yy9z59 w8xy80y3zy w8y5x3xx30 w8yx4800y3 w8zz344yy4 w9354500z3 w9383w05zz w93y389xx5 w9485w9094 w95354wxyy w954xz3zw3 w955588954 w958538y59 w958w90x58 w98x43y80w w990x38x30 w994y3x43z w99wy84889 w99y588w45 w99yw0y085 w9w0390y35 w9w0w4wxx0 w9wxwzyy05 w9wxx3z9z4 w9wy3888z5 w9x0zw4xx3 w9x338wxxz w9x455w30y w9x9404y05 w9y0099z4y w9y0x4w450 w9z3390yxz w9z8z40yy3 w9zz3333x9 ww03x5xz5 ww055xx933 ww09wz43x4 ww0w48zz00 ww0y44w8yx ww0z5wxx83 ww3y54wx4x ww3yxww880 ww3zzw805x ww44z8wyy9 ww45x8zx98 ww485xzx45 ww503zyz88 ww533395y9 ww5w5y40w5 ww5yzwwx0x ww5zx5w8x3 ww8w0558w0 ww8z558449 ww94390xw9 ww9w5z8353 ww9x5y8948 wwx45x5853 wwx4x3w5y8 wwx8353zz9 wwy0x94w45 wwy45583w8 wx0x53z5yx wx3455zy3x wx48040909 wx53w8394x wx8zxy35yz wx9w39zyzw wx9yx88899 wxw9558x83 wxy85yzwy5 wxz9zywww5 wy03484w4y wy0y485w4y wy34434554 wy3538xx09 wy44y93440 wy49433304 wyw9548zxy wyx0545wy0 wyx839z9y9 wyy3yz3wz0 wz0345x498 wz043x8xy8 wz3509305w wz3y3xx8y0 wz3y5x0yw5 wz580y50wx wz5zy5y3w8 wz8y3x93w5 wz9854xzyw wz9w3x8003 wz9y4y80w0 wzw90049yw wzw9x0009y wzwyyw9zx5 wzy04w350x wzy0x3xy43 wzy43ywyz5 wzy93wy0z3 wzyxy59933 wzzw38430y x30909y44x x3335z304 x33w5yw8zy x343y5z9xx x34x3z85xy x353yz00z0 x353yz535w x355449y0w x358y5y4z0 x38wy8zx93 x390yz5y34 x3935880wx x393zy09ww x39z5y8w8z x3w8y54z9x x3wzy5yxw4 x3yw49598w x3yy3wz449 x403y5y958 x433y48zyy x4344x9495 x4444z95xx ",FieldOccurs.MUST, KeywordsCombine.OR);
		searchClient.addPhraseQuery("eid", "3",FieldOccurs.MUST, KeywordsCombine.OR);
//		searchClient.addExistsFilter("action");
		List<String[]> s = searchClient.facetCountQueryOrderByCount("action", 50, SortOrder.DESC );
		//返回结果s为list，其中每个String[]的 第0位为uid字段值， 第1位为该uid字段匹配到的文档数。
		System.out.println("文档总量："+searchClient.getTotal());//检索的文档总数
		System.out.println("结果总量："+searchClient.getCountTotal());//结果文档总数
		String a ="";
		for(String[] infos: s){
			for(String info:infos)
				System.out.print(info + "\t");
			System.out.println("");
			a=a+infos[0]+" ";
		}
		System.out.println(a);*/

	/*	System.out.println("\r\n----------------------------5.多字段聚合统计-----------------------\r\n");
		searchClient.reset();//重置
		//针对uid，gid字段分别聚合统计文档数
		Map<String, Long> s = searchClient.facetCountQuerysOrderByCount(new String[]{"uid","gid"} );
		//返回结果map类型，key值为聚合的字段，value为该字段聚合的文档数
		System.out.println("文档总量："+searchClient.getTotal());
		Set<String> keys = s.keySet();
		for (String key : keys) {
			System.out.println(key+"\t"+s.get(key));
		}*/

		/*System.out.println("\r\n----------------------------6.二次聚合-----------------------\r\n");
		searchClient.reset();//重置
		searchClient.debug=true;
		searchClient.addPhraseQuery("eid","1",FieldOccurs.MUST);
//		searchClient.addExistsFilter("action");
		//返回结果较慢，不建议用,针对第一个字段eid聚合情况下，再聚合info_type
		//参数5为限制返回结果5条；参数boolean为true时结果返回info_type详细数据；参数SortOrder为排序
		List<String[]> ss = searchClient.facetTwoCountQueryOrderByCount("uid","info_type",5, true, SortOrder.DESC );
		//结果ss为list数组，其中每个Sting[]，第0位为eid的值，第1位为该eid匹配到的文档数，第2位为info_type字段的聚合数，第3位为info_type字段的聚合的详细信息
		System.out.println("文档总量："+searchClient.getTotal());
		for(String[] infos: ss){
			for(String info:infos)
				System.out.print(info + "\t");
			System.out.println("");
		}*/


	/*	System.out.println("\r\n----------------------------7.多次聚合-----------------------\r\n");
		searchClient.reset();//重置

		searchClient.addPhraseQuery("uid","5zyz0y8x 64168 26022 24106",FieldOccurs.MUST,KeywordsCombine.OR);
		// 1、field 统计分组的父字段；2、 childFields 统计分组的子字段,数组格式  ；3、topN 要求返回的结果数 ,topN 等于0 时，将返回所有的统计结果；4、orderFiled 排序的字段，为空时， 默认排序为父字段的文档数； 5、sort 排序
		List<String[]> ss = searchClient.facetMultipleCountQueryOrderByCount("ip", new String[]{"uid"}, 5, "ip", SortOrder.DESC );
		// 返回结果 前topN个结果的list ， 每一项为一个数组，数组 【0】为统计父字段值，【1】为父子段所查询文档的数量，【N】为子字段聚合的统计数(顺序与设置childFields字段顺序一致)
		System.out.println("文档总量："+searchClient.getTotal());
		System.out.println("结果总量："+searchClient.getCountTotal());
		for(String[] infos: ss){
			for(String info:infos)
				System.out.print(info + "\t");
			System.out.println("");
		}*/


		/*System.out.println("\r\n----------------------------8.按时间粒度统计文档数-----------------------\r\n");
		searchClient.reset();//重置
		searchClient.addPhraseQuery("eid","1",FieldOccurs.MUST);
//		searchClient.addRangeTerms("pubtime", "2017-04-16 00:00:00", "2017-04-19 00:00:00");
		//针对时间类型字段进行时间间隔文档数量统计；参数：1、field为时间类型字段；2、format为时间类型格式；3、interval为间隔粒度；4、startTime为开始时间；5、endTime为结束时间；6、每个时间段返回最小文档数
		List<String[]> list = searchClient.facetDate("pubtime", "yyyy-MM-dd", "1d","2017-04-16","2017-04-19",0);
		//返回结果list数组，每个String[]的第0位为时间区间，第1位为该区间的文档数；
		for(String[] infos: list){
			for(String info:infos)
				System.out.print(info + "\t");
			System.out.println("");
		}*/

		/*System.out.println("\r\n----------------------------9.按时间粒度统计聚合字段-----------------------\r\n");
		searchClient.reset();//重置

		searchClient.addPhraseQuery("eid","1",FieldOccurs.MUST);
		searchClient.addRangeTerms("pubtime", "2017-04-13 00:00:00", "2017-04-19 00:00:00");
		//针对时间类型字段进行时间间隔文档数量统计，并且进一步聚合统计每个时间区间的uid字段数量；参数：1、field为时间类型字段；2、format为时间类型格式；3、interval为间隔粒度；4、startTime为开始时间；5、endTime为结束时间；6、每个时间段返回最小文档数；7、CountField为聚合的字段
		List<String[]> list7 = searchClient.facetDateByCount("pubtime", "yyyy-MM-dd", "1d","2017-04-13","2017-04-19", "uid");
		//返回结果list数组，每个String[]的第0位为时间区间，第1位为该区间的文档数,第2位为该区间内聚合的uid字段值；
		for(String[] infos: list7){
			for(String info:infos)
				System.out.print(info + "\t");
			System.out.println("");
		}*/

		/*System.out.println("\r\n----------------------------10.按时间粒度统计聚合字段----------------------\r\n");
		searchClient.reset();//重置

		searchClient.addPhraseQuery("eid","3",FieldOccurs.MUST);
		searchClient.addRangeTerms("pubtime", "2017-05-13 00:00:00", "2017-05-19 00:00:00");
		//针对时间类型字段进行时间间隔文档数量统计；参数：1、field为时间类型字段；2、format为时间类型格式；3、interval为间隔粒度；4、CountField为聚合的字段
		List<String[]> list = searchClient.facetDateByCount("pubtime", "HH", "1H",new String[]{"warning_level"});
		//返回结果list数组，每个String[]的第0位为时间区间，第1位为该区间的文档数；
		for(String[] infos: list){
			for(String info:infos)
				System.out.print(info + "\t");
			System.out.println("");
		}*/

		/*System.out.println("\r\n----------------------------11.按时间粒度统计聚合字段-----------------------\r\n");
		searchClient.reset();//重置

		searchClient.addPhraseQuery("eid","1",FieldOccurs.MUST);
//		searchClient.addRangeTerms("pubtime", "2018-04-13 00:00:00", "2018-04-19 00:00:00");
		List<String[]> list = searchClient.facetDate("pubtime", "HH",0);//可用于小时粒度统计
		for(String[] infos: list){
			for(String info:infos)
				System.out.print(info + "\t");
			System.out.println("");
		}*/

		/*System.out.println("\r\n----------------------------12.按时间粒度统计聚合字段各类型数据量----------------------\r\n");
		searchClient.reset();//重置
		searchClient.debug=true;
//		searchClient.addPhraseQuery("eid","1",FieldOccurs.MUST);
		searchClient.addRangeTerms("pubtime", "2017-05-13 00:00:00", "2017-05-19 00:00:00");
		//针对时间类型字段进行时间间隔文档数量统计；参数：1、field为时间类型字段；2、format为时间类型格式；3、interval为间隔粒度；4、CountField为聚合的字段
		List<String[]> list = searchClient.facetDateByTypeCount("pubtime","yyyy-MM-dd HH","1H","2017-05-13 00","2017-05-19 00","msg_type");
		//返回结果list数组，每个String[]的第0位为时间区间，第1位为该区间的文档数；第2位为聚合字段各类型是数据量；
		//结果数组样例样例 ：2017-04-10	3	[{"doc_count":2,"key":3},{"doc_count":1,"key":4}]
		for(String[] infos: list){
			for(String info:infos)
				System.out.print(info + "\t");
			System.out.println("");
		}*/

		/*System.out.println("\r\n---------------------------13.自定义查询------------------------\r\n");
		searchClient.reset();//重置
		searchClient.debug=true;
		searchClient.addPhraseQuery("eid", "1", FieldOccurs.MUST,KeywordsCombine.AND);
		String querstring = "+( (content:\"北京\" AND \"上访\") OR (content:\"长春\" OR \"呼吁给孩子无条件接受教育的权利\") )";
		searchClient.addQueryCondition(querstring);//自定义
		String [] fieldsss = {"id","content"};
		searchClient.execute(fieldsss);
		System.out.println("总量："+searchClient.getTotal());
		List<String[]> resultLists2 = searchClient.getResults();
		for (String[] strings : resultLists2) {
			for (String string : strings) {
				System.out.print(string+"\t");
			}
			System.out.println("");
		}*/


		/*System.out.println("\r\n---------------------------14.自定义查询------------------------\r\n");
		searchClient.reset();//重置
		String lucene = "{\"query\":{\"match\":{\"eid\":1}}}";
		String json = searchClient.addQueryConditionBylucene(lucene);
		System.out.println(json);*/

		/*System.out.println("\r\n----------------------------15.分词工具-----------------------\r\n");
		String content = "中国科学院自动化研究所";
		System.out.println("语料："+content);
		List<String> li = searchClient.extractKeywords(content, -1);
		for (String string : li) {
			System.out.print(string+"|");
		}*/
    }

    @Test
    public void analysis(){
        String text = "原标题形势严峻这个地方书记市长纪委书记为何连续空降市委书记市长市委副书记接连落马的广东江门市政治生态修复从补齐关键岗位开始在连续迎来空降市委书记市长候选人后江门新一任纪委书记近日也到岗了值得关注的是他也是从省里空降的还是纪检部门这位新纪委书记叫项天保任职省纪委年在案例管理派驻机构巡视部门等关键岗位都工作过经验十分丰富去年底江门成立市委巡察工作机构时任省委巡视办副主任的项天保亲赴江门参加了启动仪式长安街知事此前曾介绍过江门是腐败的重灾区市委书记毛荣楷市长邓伟根市委副书记政法委书记邹家军市委常委王积俊市人大常委会副主任聂党权曾任市委副书记落马班子塌方全国罕见中央派来了一个沙瑞金省委书记又派来了一个田国富纪委书记这是人民的名义里的一个情节以此说明推动从严治党的迫切性江门的情况与此类似市委书记林应武市长候选人刘毅都是从省委组织部副部长任上调来江门的补位落马前任如今新纪委书记又从省级纪检部门调来从一个侧面也反映出地方反腐形势的严峻性就在项天保就任的会议上前任纪委书记胡钛也以新身份亮相他已经出任市委副书记政法委书记也就是说现在江门市委常委班子中有两名来自纪检系统的领导胡钛是军转干部年底刚刚调任江门市纪委书记他有两次救火经历一次是梅州一次是江门年梅州市委书记朱泽君和纪委书记李纯德相继被调离此后又相继被查媒体对两人内斗多有报道胡钛正是接替了李的梅州纪委书记职务而去年赴江门履新正是该市市委书记毛荣楷和市委副书记邹家军落马之后胡钛之前的江门市纪委书记周伟万也是一名老纪检在纪检政法战线工作了年今年初当选市政协主席面对从严治党的新形势和班子塌方的旧局面接力反腐任重道远近日召开的江门全市领导干部大会上广东省委常委组织部长邹铭根据省委书记胡春华同志的指示对全市领导干部提出三点要求其中特别指出要进一步严明政治纪律和政治规矩营造良好的政治生态要保持干部队伍思想稳定和改革发展大局稳定积极引导广大干部群众把违纪违法的个人问题与江门整体工作区分开来不因人废事不因案划线不因此否定江门的工作影响江门的发展稳定营造良好的政治生态更好地推动发展无疑是江门工作当下的重中之重来源长安街知事责任编辑初晓慧文章关键词纪委书记市长纪检我要反馈保存网页";
        Set<String> set = EsIndexSearch.analysis(text,true,2);
        for (String word: set) {
            System.out.println(word);
        }
    }

}


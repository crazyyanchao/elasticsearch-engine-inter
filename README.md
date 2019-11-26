# ELASTICSEARCH接口程序

## 一、创建映射到ES
### 1、index的三个选项
>analyzed:默认选项，以标准的全文索引方式，分析字符串，完成索引。
>not_analyzed:精确索引，不对字符串做分析，直接索引字段数据的精确内容。
>no：不索引该字段。
### 2、空值字段不会被索引
>"empty_string" : "",
 "null_value" : null,
 "empty_array" : [],
 "array_with_null_value" : [ null ]
 
### 3、Mapping数据类型参考
[Mapping数据类型参考](https://blog.csdn.net/chengyuqiang/article/details/79048800)

[Mapping模板](https://blog.csdn.net/laoyang360/article/details/78396928)

[Elaticsearch-5.6.X官方技术文档](https://www.elastic.co/guide/en/elasticsearch/plugins/current/index.html)

## 二、数据转储到ES

## 三、ES数据检索

## 四、ES数据更新

## 五、ES数据删除

# ELASTICSEARCH ENGINE INTERFACE UPDATE LOGS

# elasticsearch-engine-inter-1.5.0.jar接口包更新说明
1、version-1.5.0接口包新增HTTP容灾处理模块（解决单点访问故障）

2、【注意点】使用增删改查接口程序时，标记有Deprecated注解的构造函数尽量避免使用。

3、所里本地elasticsearch集群所有地址：
   private String ipPort = "" +
            "192.168.12.107:9210,192.168.12.107:9211,192.168.12.114:9210," +
            "192.168.12.109:9211,192.168.12.112:9211,192.168.12.109:9210," +
            "192.168.12.114:9211,192.168.12.114:9210,192.168.12.110:9210," +
            "192.168.12.111:9210";
            
4、增删改查接口构造函数的使用样例【其它测试请查看TEST CLASS】
xxxer = new EsIndexXXX(ipPort, "indexName1,indexName2,...", "indexType1,indexType1,...");

5、【升级程序注意事项】已有程序需要修改的地方是只是在构造函数中传入多个用逗号分隔IP:PORT地址，其它不需要调整。

6、【暂时未做升级 - 双数据中心可以进行相关的路由配置】

# elasticsearch-engine-inter-1.6.0.jar接口包更新说明

1、【增加故障节点的访问链接自动恢复功能（HTTP功能模块升级）】（守护进程执行恢复-API功能）

2、新增es集群检索对象构造函数使用的测试案例（EsIndexSearchTest包含如何优化检索对象构造函数）

3、新增二次检索测试用例（EsIndexSearchTest查看测试）

4、修复服务发现类的线程安全BUG - 增加服务发现功能测试用例（EsIndexSearchTest查看测试）

5、新增一个GEO类型距离范围数据检索接口，扩展GEO类型的排序接口（EsIndexSearchImpTest查看测试）

# elasticsearch-engine-inter-1.6.5.jar接口包更新说明

1、新增TERMS精确查询接口，测试查看（EsIndexSearchTest）

2、新增GEO类型矩形范围检索接口（EsIndexSearchImpTest查看测试）

# elasticsearch-engine-inter-1.6.6.jar接口包更新说明

1、增加二元操作OR组合多个子句FieldOccurs.SHOULD(类似于：query1 or query2 or query3)

# elasticsearch-engine-inter-1.6.7.jar接口包更新说明

1、增加航段分析方法

# elasticsearch-engine-inter-1.6.8.jar接口包更新说明

1、增加地球距离计算算法ARC/PLANE
2、补充航线分析方法

# elasticsearch-engine-inter-1.6.9.jar接口包更新说明

1、增加度量统计接口（例如-[facetStatsCount]统计事件的评论量）

# elasticsearch-engine-inter-1.7.0.jar接口包更新说明

1、新增addGeoShape接口-(多形状组合查询)-支持(MUST/MUST_NOT/SHOULD)任意组合-(目前支持两个形状圆形和矩形)

2、addGeoBoundingMultiBox多矩形区域检索接口标记为Deprecated，可以使用addGeoShape代替

3、修复航段分析方法的BUG

4、增加一个度量统计接口facetStatsTermsAggsTophits

# elasticsearch-engine-inter-1.7.1.jar接口包更新说明

1、二次分组统计-多个child字段（例如分组统计ZDR的各个类型的发帖回复总数据量）

2、addPrimitiveTermFilter接口拼接LUCENE查询，限制数组传入大小为500（不限制大小导致集群StackOverflowError异常）

3、二次分组统计-多个child字段（调整统计接口的条件添加方式）

# elasticsearch-engine-inter-1.7.4.jar接口包更新说明
1、增加多任务合并查询

# elasticsearch-engine-inter-1.7.5.jar接口包更新说明
1、重置HTTP模块-将上一次注册的地址全部移除，并加入新的集群地址【传入新的集群地址即可】
2、两种使用方法：<1>使用检索对象直接调用；<2>使用发现模块直接重置


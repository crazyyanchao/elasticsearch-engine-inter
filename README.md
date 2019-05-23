# ELASTICSEARCH-5.6.X接口程序

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

[Elaticsearch-5.6.X官方技术文档](https://www.elastic.co/guide/en/elasticsearch/plugins/current/index.html)

## 二、数据转储到ES

## 三、ES数据检索

## 四、ES数据更新

## 五、ES数据删除


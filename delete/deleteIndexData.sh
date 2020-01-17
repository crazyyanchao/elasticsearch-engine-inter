##!/usr/bin/env bash
#
#myJarPath=./lib/dx_monitor_caiji_es_indexer.jar
#
## ---------------------------启动小索引数据删除进程---------------------------
#
## 索引类型
#indexType="caiji_small"
#
## 索引名称-多个索引名称使用逗号分隔
#indexName="index_mall,index_all"
#
## IP和端口-使用冒号分隔
#ipPort="192.168.12.109:9210"
#
## 索引mapping中的时间字段 根据发布时间或者插入时间删除
#timeField="pubtime"
#
## 每隔delayTime执行一次删除数据操作 - 延时执行-支持按天/小时/分钟（格式数字加d/h/m：1d/24h/60m/60s）
#delayTime="30m"
#
## 删除beforeDataTime以前的数据 - 行一次时删除多久以前的数据-支持按天/小时/分钟（格式数字加d/h/m：1d/24h/60m/60s）
#beforeDataTime="4d"
#
## 是否启动DEBUG模式
#debug="true"
#
##*****************************************************************
## 是否启用force merge（释放磁盘空间 - cpu/io消耗增加，缓存失效）
## 1、对于不再生成新分段的索引，建议打开此配置；2、如果索引在不断的产生新分段建议关闭此配置-通过修改集群段合并策略优化
##*****************************************************************
#isForceMerge="false"
#
#nohup java -Xmx512m -cp ${myJarPath} casia.isi.delete.DeleteIndexData ${indexType} ${indexName} ${ipPort} ${timeField} ${delayTime} ${beforeDataTime} ${debug} ${isForceMerge} >>logs/delete.DeleteIndexData.log 2>&1 &
#

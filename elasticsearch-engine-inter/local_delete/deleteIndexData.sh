##!/usr/bin/env bash
#
#myJarPath=./lib/elasticsearch-engine-inter.jar
#
#
## IP和端口-使用冒号分隔
#ipPort="192.168.12.109:9210"
#
## 每隔delayTime执行一次删除数据操作 - 延时执行-支持按天/小时/分钟（格式数字加d/h/m：1d/24h/60m/60s）
#delayTime="30m"
#
## 是否启动DEBUG模式
#debug="true"
#
#
##*****************************************************************
## 是否启用force merge（释放磁盘空间 - cpu/io消耗增加，缓存失效）
## 1、对于不再生成新分段的索引，建议打开此配置；2、如果索引在不断的产生新分段建议关闭此配置-通过修改集群段合并策略优化
##*****************************************************************
#isForceMerge="false"
#
#
## -----------------------------------飞机卫星数据删除TASK-----------------------------------
#
## 索引类型
#indexTypeAirState="graph"
#
## 索引名称-多个索引名称使用逗号分隔
#indexNameAirState="statellite_info,aircraft_info"
#
## 索引mapping中的时间字段 根据发布时间或者插入时间删除
#timeFieldAirState="pubtime"
#
## 删除beforeDataTime以前的数据 - 行一次时删除多久以前的数据-支持按天/小时/分钟（格式数字加d/h/m：1d/24h/60m/60s）
#beforeDataTimeAirState="7d"
#
#nohup java -Xmx512m -cp ${myJarPath} casia.isi.elasticsearch.operation.delete.shell.DeleteDataByShell ${indexTypeAirState} ${indexNameAirState} ${ipPort} ${timeFieldAirState} ${delayTime} ${beforeDataTimeAirState} ${debug} ${isForceMerge} >>logs/delete.DeleteDataByShell.log 2>&1 &
#
#
## -----------------------------------采集使用的索引数据删除TASK-----------------------------------
#
#timeField_caiji="index_time"
#beforeDataTime_caiji="3d"
#
#indexType_wechat_new="wechat_new"
#indexName_wechat_new="wechat_new"
#nohup java -Xmx512m -cp ${myJarPath} casia.isi.elasticsearch.operation.delete.shell.DeleteDataByShell ${indexType_wechat_new} ${indexName_wechat_new} ${ipPort} ${timeField_caiji} ${delayTime} ${beforeDataTime_caiji} ${debug} ${isForceMerge} >>logs/delete.DeleteDataByShell.log 2>&1 &
#
#indexType_wechat="wechat"
#indexName_wechat="wechat"
#nohup java -Xmx512m -cp ${myJarPath} casia.isi.elasticsearch.operation.delete.shell.DeleteDataByShell ${indexType_wechat} ${indexName_wechat} ${ipPort} ${timeField_caiji} ${delayTime} ${beforeDataTime_caiji} ${debug} ${isForceMerge} >>logs/delete.DeleteDataByShell.log 2>&1 &
#
#indexType_video="video"
#indexName_video="video"
#nohup java -Xmx512m -cp ${myJarPath} casia.isi.elasticsearch.operation.delete.shell.DeleteDataByShell ${indexType_video} ${indexName_video} ${ipPort} ${timeField_caiji} ${delayTime} ${beforeDataTime_caiji} ${debug} ${isForceMerge} >>logs/delete.DeleteDataByShell.log 2>&1 &
#
#indexType_think_tank="think_tank"
#indexName_think_tank="think_tank"
#nohup java -Xmx512m -cp ${myJarPath} casia.isi.elasticsearch.operation.delete.shell.DeleteDataByShell ${indexType_think_tank} ${indexName_think_tank} ${ipPort} ${timeField_caiji} ${delayTime} ${beforeDataTime_caiji} ${debug} ${isForceMerge} >>logs/delete.DeleteDataByShell.log 2>&1 &
#
#indexType_statis_in="statis_in"
#indexName_statis_in="statis_in"
#nohup java -Xmx512m -cp ${myJarPath} casia.isi.elasticsearch.operation.delete.shell.DeleteDataByShell ${indexType_statis_in} ${indexName_statis_in} ${ipPort} ${timeField_caiji} ${delayTime} ${beforeDataTime_caiji} ${debug} ${isForceMerge} >>logs/delete.DeleteDataByShell.log 2>&1 &
#
#indexType_statis_daily="statis_daily"
#indexName_statis_daily="statis_daily"
#nohup java -Xmx512m -cp ${myJarPath} casia.isi.elasticsearch.operation.delete.shell.DeleteDataByShell ${indexType_statis_daily} ${indexName_statis_daily} ${ipPort} ${timeField_caiji} ${delayTime} ${beforeDataTime_caiji} ${debug} ${isForceMerge} >>logs/delete.DeleteDataByShell.log 2>&1 &
#
#indexType_news="news"
#indexName_news="news"
#nohup java -Xmx512m -cp ${myJarPath} casia.isi.elasticsearch.operation.delete.shell.DeleteDataByShell ${indexType_news} ${indexName_news} ${ipPort} ${timeField_caiji} ${delayTime} ${beforeDataTime_caiji} ${debug} ${isForceMerge} >>logs/delete.DeleteDataByShell.log 2>&1 &
#
#indexType_monitor="monitor"
#indexName_monitor="monitor"
#nohup java -Xmx512m -cp ${myJarPath} casia.isi.elasticsearch.operation.delete.shell.DeleteDataByShell ${indexType_monitor} ${indexName_monitor} ${ipPort} ${timeField_caiji} ${delayTime} ${beforeDataTime_caiji} ${debug} ${isForceMerge} >>logs/delete.DeleteDataByShell.log 2>&1 &
#
#indexType_mblog="mblog"
#indexName_mblog="mblog"
#nohup java -Xmx512m -cp ${myJarPath} casia.isi.elasticsearch.operation.delete.shell.DeleteDataByShell ${indexType_mblog} ${indexName_mblog} ${ipPort} ${timeField_caiji} ${delayTime} ${beforeDataTime_caiji} ${debug} ${isForceMerge} >>logs/delete.DeleteDataByShell.log 2>&1 &
#
#indexType_forum="forum"
#indexName_forum="forum"
#nohup java -Xmx512m -cp ${myJarPath} casia.isi.elasticsearch.operation.delete.shell.DeleteDataByShell ${indexType_forum} ${indexName_forum} ${ipPort} ${timeField_caiji} ${delayTime} ${beforeDataTime_caiji} ${debug} ${isForceMerge} >>logs/delete.DeleteDataByShell.log 2>&1 &
#
#indexType_facebook="facebook"
#indexName_facebook="facebook"
#nohup java -Xmx512m -cp ${myJarPath} casia.isi.elasticsearch.operation.delete.shell.DeleteDataByShell ${indexType_forum} ${indexName_forum} ${ipPort} ${timeField_caiji} ${delayTime} ${beforeDataTime_caiji} ${debug} ${isForceMerge} >>logs/delete.DeleteDataByShell.log 2>&1 &
#
#indexType_blog="blog"
#indexName_blog="blog"
##nohup java -Xmx512m -cp ${myJarPath} casia.isi.elasticsearch.operation.delete.shell.DeleteDataByShell ${indexType_blog} ${indexName_blog} ${ipPort} ${timeField_caiji} ${delayTime} ${beforeDataTime_caiji} ${debug} ${isForceMerge} >>logs/delete.DeleteDataByShell.log 2>&1 &
#
#indexType_app="app"
#indexName_app="app"
#nohup java -Xmx512m -cp ${myJarPath} casia.isi.elasticsearch.operation.delete.shell.DeleteDataByShell ${indexType_app} ${indexName_app} ${ipPort} ${timeField_caiji} ${delayTime} ${beforeDataTime_caiji} ${debug} ${isForceMerge} >>logs/delete.DeleteDataByShell.log 2>&1 &
#
#
## -----------------------------------新版大兴重点人索引数据删除TASK-暂不删除-----------------------------------
#
#
## -----------------------------------新版大兴预警索引数据删除TASK-----------------------------------
#indexType_monitor_data="monitor_data"
#indexName_monitor_data="event_news_ref_monitor,event_blog_ref_monitor,event_mblog_ref_monitor,event_threads_ref_monitor,event_video_ref_monitor,event_appdata_ref_monitor,event_weichat_ref_monitor"
#timeField_monitor_data="pubtime"
#beforeDataTime_monitor_data="30d"
#nohup java -Xmx512m -cp ${myJarPath} casia.isi.elasticsearch.operation.delete.shell.DeleteDataByShell ${indexType_monitor_data} ${indexName_monitor_data} ${ipPort} ${timeField_monitor_data} ${delayTime} ${beforeDataTime_monitor_data} ${debug} ${isForceMerge} >>logs/delete.DeleteDataByShell.log 2>&1 &
#
#indexName_monitor_data_site_event_data_monitor="site_event_data_monitor"
#timeField_monitor_data_site_event_data_monitor="es_insert_time"
#beforeDataTime_monitor_data_site_event_data_monitor="7d"
#nohup java -Xmx512m -cp ${myJarPath} casia.isi.elasticsearch.operation.delete.shell.DeleteDataByShell ${indexType_monitor_data} ${indexName_monitor_data_site_event_data_monitor} ${ipPort} ${timeField_monitor_data_site_event_data_monitor} ${delayTime} ${beforeDataTime_monitor_data_site_event_data_monitor} ${debug} ${isForceMerge} >>logs/delete.DeleteDataByShell.log 2>&1 &
#
#
## -----------------------------------新版大兴专题索引数据删除TASK-暂不删除-----------------------------------
#indexType_event_data="event_data"
#indexName_event_data="event_news_ref_event,event_blog_ref_event,event_mblog_ref_event,event_threads_ref_event,event_video_ref_event,event_appdata_ref_event,event_weichat_ref_event"
#timeField_event_data="pubtime"
#beforeDataTime_event_data="30d"
#
#nohup java -Xmx512m -cp ${myJarPath} casia.isi.elasticsearch.operation.delete.shell.DeleteDataByShell ${indexType_event_data} ${indexName_event_data} ${ipPort} ${timeField_event_data} ${delayTime} ${beforeDataTime_event_data} ${debug} ${isForceMerge} >>logs/delete.DeleteDataByShell.log 2>&1 &
#
#
## -----------------------------------吉林预警索引数据删除TASK-----------------------------------
#indexType_jilin_monitor_data="monitor_data"
#indexName_jilin_monitor_data="event_news_ref,event_blog_ref,event_mblog_ref,event_threads_ref,event_video_ref,event_appdata_ref,event_weichat_ref"
#timeField_jilin_monitor_data="pubtime"
#beforeDataTime_jilin_monitor_data="30d"
#
#nohup java -Xmx512m -cp ${myJarPath} casia.isi.elasticsearch.operation.delete.shell.DeleteDataByShell ${indexType_jilin_monitor_data} ${indexName_jilin_monitor_data} ${ipPort} ${timeField_jilin_monitor_data} ${delayTime} ${beforeDataTime_jilin_monitor_data} ${debug} ${isForceMerge} >>logs/delete.DeleteDataByShell.log 2>&1 &
#
#indexName_jilin_monitor_data_forewarnning_data="forewarnning_data"
#timeField_jilin_monitor_data_forewarnning_data="es_insert_time"
#beforeDataTime_jilin_monitor_data__forewarnning_data="7d"
#
#nohup java -Xmx512m -cp ${myJarPath} casia.isi.elasticsearch.operation.delete.shell.DeleteDataByShell ${indexType_jilin_monitor_data} ${indexName_jilin_monitor_data_forewarnning_data} ${ipPort} ${timeField_jilin_monitor_data_forewarnning_data} ${delayTime} ${beforeDataTime_jilin_monitor_data__forewarnning_data} ${debug} ${isForceMerge} >>logs/delete.DeleteDataByShell.log 2>&1 &
#
#

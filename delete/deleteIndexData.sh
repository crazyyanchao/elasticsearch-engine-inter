#!/usr/bin/env bash

myJarPath=./lib/xxx.jar

# ---------------------------启动小索引数据删除进程---------------------------

# 索引类型
indexType="monitor_caiji_small"

# 索引名称-多个索引名称使用逗号分隔
indexName="news_small,blog_small,forum_threads_small,mblog_info_small,video_brief_small,wechat_message_xigua_small,appdata_small,newspaper_info_small"

# IP和端口-使用冒号分隔
ipPort="localhost:9200"

# 索引mapping中的时间字段
timeField="pubtime"

# 每隔delayTime执行一次删除数据操作 - 延时执行-支持按天/小时/分钟（格式数字加d/h/m：1d/24h/60m/60s）
delayTime="5m"

# 删除beforeDataTime以前的数据 - 行一次时删除多久以前的数据-支持按天/小时/分钟（格式数字加d/h/m：1d/24h/60m/60s）
beforeDataTime="3d"

# 是否启动DEBUG模式
debug="true"

# 是否启用force merge（释放磁盘空间 - cpu/io消耗增加）
isForceMerge="true"

nohup java -Xmx512m -cp ${myJarPath} casia.isi.delete.XXXMAIN ${indexType} ${indexName} ${ipPort} ${timeField} ${delayTime} ${beforeDataTime} ${debug} ${isForceMerge} >>logs/delete.DeleteIndexData.log 2>&1 &



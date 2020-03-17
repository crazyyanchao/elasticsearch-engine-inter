#!/usr/bin/env bash

###################  log clean shell
###################  delay 1s/1m/1h/1d
DELAY=5s
echo clean log shell execute delay is:${DELAY}

################### log
LOGFILE=xget.log

###################  execute xget
while [ 1 ]
do
curl -XGET "http://192.168.1.50:9211/_cat/indices/aircraft_info_latest_status?v&h=docs.count,uuid&format=json&pretty" >> ${LOGFILE}

sleep ${DELAY}
done

################### background execution this shell

# START:
# nohup ./es-xget.sh > myout.file 2>&1 &

# STOP:
# kill -9 `ps -ef|grep es-xget.sh|grep -v grep|awk '{print $2}'`


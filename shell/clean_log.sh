#!/usr/bin/env bash

###################  log clean shell
###################  delay 1s/1m/1h/1d
DELAY=1d
echo clean log shell execute delay is:${DELAY}

################### config need to clean logs dir
BASEPATH=$(pwd)
LOGDIR=${BASEPATH}/logs/
echo logs absolute path:${LOGDIR}

###################  get logfile name array
function getLogFileArray(){
    cd ${LOGDIR}
    j=0
    for i in `ls`
    do
        folder_list[j]=${i}
        j=`expr ${j} + 1`
    done
}
###################  execute clean log
while [ 1 ]
do
getLogFileArray;
    for fileName in ${folder_list[@]}
        do
            echo clean log ... ${fileName}
            echo > ${fileName}
        done
echo clean log file time:$(date)
sleep ${DELAY}

done

################### background execution this log clean shell

# START_CLEAN_LOG_SERVER:
# nohup ./clean_log.sh > myout.file 2>&1 &

# STOP_CLEAN_LOG_SERVER:
# kill -9 `ps -ef|grep clean_log.sh|grep -v grep|awk '{print $2}'`


package casia.isi.elasticsearch.operation.delete.shell;
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

import casia.isi.elasticsearch.common.FieldOccurs;
import casia.isi.elasticsearch.common.RangeOccurs;
import casia.isi.elasticsearch.operation.delete.EsIndexDelete;
import casia.isi.elasticsearch.util.DateUtil;
import casia.isi.elasticsearch.util.StringUtil;
import com.alibaba.fastjson.JSONObject;

/**
 * @author YanchaoMa yanchaoma@foxmail.com
 * @PACKAGE_NAME: casia.isi.elasticsearch.operation.delete.shell
 * @Description: TODO(监控删除索引数据)
 * @date 2019/5/30 15:27
 */
public final class DeleteDataByShell {

    private static EsIndexDelete esIndexDataDelete;

    private static String indexType;
    private static String indexName;
    private static String ipPort;

    private static String timeField;
    private static String delayTime;
    private static String beforeDataTime;
    private static boolean isForceMerge = false;

    // DELETE WORK TASK ID
    private static String lastTaskId;

    public static boolean debug = false;

    /**
     * @param indexType：索引类型
     * @param indexName：索引名称-多个索引名称使用逗号分隔
     * @param ipPort：IP和端口-使用冒号分隔
     * @param timeField：索引mapping中的时间字段
     * @param delayTime：延时执行-支持按天/小时/分钟（格式数字加d/h/m：1d/24h/60m/60s）
     * @param beforeDataTime：执行一次时删除多久以前的数据-支持按天/小时/分钟（格式数字加d/h/m：1d/24h/60m/60s）
     * @param isForceMerge:true启用force-merge
     * @return
     * @Description: TODO(为监控程序创建一个索引数据删除对象)
     */
    public DeleteDataByShell(String indexType, String indexName, String ipPort, String timeField,
                             String delayTime, String beforeDataTime, boolean isForceMerge) {
        this.esIndexDataDelete = new EsIndexDelete(ipPort, indexName, indexType);

        this.indexType = indexType;
        this.indexName = indexName;
        this.ipPort = ipPort;

        this.timeField = timeField;
        this.delayTime = delayTime;
        this.beforeDataTime = beforeDataTime;

        this.isForceMerge = isForceMerge;
    }

    /**
     * @return
     * @Description: TODO(启动监控删除)
     */
    public void run() {

        boolean isExcute = check();
        while (isExcute) {
            try {

                // 执行删除
                executeDelete();

                // 延时执行
                sleep();

            } catch (Exception e) {
                System.out.println("Delete data exception,please check your parameters!");
                System.out.println("indexType:" + indexType);
                System.out.println("indexName:" + indexName);
                System.out.println("ipPort:" + ipPort);
                System.out.println("timeField:" + timeField);
                System.out.println("delayTime:" + delayTime);
                System.out.println("beforeDataTime:" + beforeDataTime);
                esIndexDataDelete.reset();
            }
        }
    }

    private boolean check() {
        if (this.timeField != null && this.delayTime != null && this.beforeDataTime != null) {
            return true;
        }
        return false;
    }

    private void sleep() throws InterruptedException {
        Thread.sleep(dhmToMill(delayTime));
    }

    private void outputResult() {
        System.out.println("Delay time:" + delayTime);
        System.out.println("Delete data from " + beforeDataTime + " ago.Current system time:" + DateUtil.millToTimeStr(System.currentTimeMillis()));
        if (debug) {
            System.out.println("Query url:" + esIndexDataDelete.getQueryUrl());
            System.out.println("Query json:" + esIndexDataDelete.getQueryString());
            System.out.println("Query result json:" + esIndexDataDelete.getQueryReslut());
        }
        lastTaskId = setTaskId(esIndexDataDelete.getQueryReslut());
    }

    /**
     * @param { "task": "EXlbuEGgRZK-IYKoOHmqWQ:xxxxxxx"
     *          }
     * @return
     * @Description: TODO(设置taskID)
     */
    private String setTaskId(String queryReslut) {
        JSONObject object = JSONObject.parseObject(queryReslut);
        return object.getString("task");
    }

    private void executeDelete() {

        // 输出上一个task的信息
        System.out.println("===========================================EXECUTE DELETE TASK===========================================");
        if (lastTaskId != null && !"".equals(lastTaskId)) {
            System.out.println(esIndexDataDelete.outputLastTaskInfo(lastTaskId));
        }

        String currentThreadTime = getCurrentThreadTime();

        esIndexDataDelete.addRangeTerms(timeField, currentThreadTime, FieldOccurs.MUST, RangeOccurs.LTE);
        esIndexDataDelete.setRefresh(true);
        esIndexDataDelete.setScrollSize(1000);
        esIndexDataDelete.conflictsProceed("proceed");
        esIndexDataDelete.setWaitForCompletion(false);
        esIndexDataDelete.execute();

        // 输出删除统计结果
        outputResult();

        // 释放磁盘空间（执行段合并操作）- CPU/IO消耗增加，缓存失效
        if (isForceMerge) {
            System.out.println(esIndexDataDelete.forceMerge());
        }

        esIndexDataDelete.reset();
    }

    private String getCurrentThreadTime() {
        long mill = System.currentTimeMillis() - dhmToMill(beforeDataTime);
        return DateUtil.millToTimeStr(mill);
    }

    private long dhmToMill(String dhmStr) {
        if (dhmStr != null && !"".equals(dhmStr)) {
            int number = Integer.valueOf(StringUtil.cutNumber(dhmStr));
            if (dhmStr.contains("d")) {
                return number * 86400000;
            } else if (dhmStr.contains("h")) {
                return number * 3600000;
            } else if (dhmStr.contains("m")) {
                return number * 60000;
            } else if (dhmStr.contains("s")) {
                return number * 1000;
            }
        }
        return 0;
    }

    /**
     * @param
     * @return
     * @Description: TODO(Delete thread main entrance)
     */
    public static void main(String[] args) {
//        PropertyConfigurator.configureAndWatch("config/log4j.properties");
        String indexType = args[0];
        String indexName = args[1];
        String ipPort = args[2];
        String timeField = args[3];
        String delayTime = args[4];
        String beforeDataTime = args[5];
        DeleteDataByShell.debug = Boolean.valueOf(args[6]);
        String isForceMerge = args[7];
        new DeleteDataByShell(indexType, indexName, ipPort, timeField, delayTime, beforeDataTime, Boolean.valueOf(isForceMerge)).run();
    }

}


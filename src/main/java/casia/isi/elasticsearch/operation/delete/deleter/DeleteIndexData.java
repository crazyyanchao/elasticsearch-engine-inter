package casia.isi.elasticsearch.operation.delete.deleter;

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
public final class DeleteIndexData {

    private static EsIndexDelete esIndexDataDelete;

    // DELETE WORK TASK ID
    private static String lastTaskId;

    public static boolean debug = true;

    /**
     * @return
     * @Description: TODO(为监控程序创建一个索引数据删除对象)
     */
    public DeleteIndexData() {
        this.esIndexDataDelete = new EsIndexDelete(SystemConstant.ipPort, SystemConstant.indexName, SystemConstant.indexType);
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
                System.out.println("indexType:" + SystemConstant.indexType);
                System.out.println("indexName:" + SystemConstant.indexName);
                System.out.println("ipPort:" + SystemConstant.ipPort);
                System.out.println("timeField:" + SystemConstant.timeField);
                System.out.println("delayTime:" + SystemConstant.delayTime);
                System.out.println("beforeDataTime:" + SystemConstant.beforeDataTime);
                esIndexDataDelete.reset();
            }
        }
    }

    private boolean check() {
        if (SystemConstant.timeField != null && SystemConstant.delayTime != null && SystemConstant.beforeDataTime != null) {
            return true;
        }
        return false;
    }

    private void sleep() throws InterruptedException {
        Thread.sleep(dhmToMill(SystemConstant.delayTime));
    }

    private void outputResult() {
        System.out.println("Delay time:" + SystemConstant.delayTime);
        System.out.println("Delete data from " + SystemConstant.beforeDataTime + " ago.Current system time:" + DateUtil.millToTimeStr(System.currentTimeMillis()));
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

        esIndexDataDelete.addRangeTerms(SystemConstant.timeField, currentThreadTime, FieldOccurs.MUST, RangeOccurs.LTE);
        esIndexDataDelete.setRefresh("wait_for");
        esIndexDataDelete.setScrollSize(1000);
        esIndexDataDelete.conflictsProceed("proceed");
        esIndexDataDelete.setWaitForCompletion(false);
        esIndexDataDelete.execute();

        // 输出删除统计结果
        outputResult();

        esIndexDataDelete.reset();
    }

    private String getCurrentThreadTime() {
        long mill = System.currentTimeMillis() - dhmToMill(SystemConstant.beforeDataTime);
        return DateUtil.millToTimeStr(mill);
    }

    private long dhmToMill(String dhmStr) {
        if (dhmStr != null && !"".equals(dhmStr)) {
            int number = Integer.valueOf(StringUtil.cutNumber(dhmStr));
            if (dhmStr.contains("d")) {
                return number * 86400000l;
            } else if (dhmStr.contains("h")) {
                return number * 3600000l;
            } else if (dhmStr.contains("m")) {
                return number * 60000l;
            } else if (dhmStr.contains("s")) {
                return number * 1000l;
            }
        }
        return 0l;
    }

    /**
     * @param
     * @return
     * @Description: TODO(Delete thread main entrance)
     */
    public static void main(String[] args) {

        new DeleteIndexData().run();
    }

}


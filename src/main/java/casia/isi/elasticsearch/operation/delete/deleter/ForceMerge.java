package casia.isi.elasticsearch.operation.delete.deleter;

import casia.isi.elasticsearch.common.SystemConstant;
import casia.isi.elasticsearch.operation.delete.EsIndexDelete;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author YanchaoMa yanchaoma@foxmail.com
 * @PACKAGE_NAME: casia.isi.elasticsearch.delete
 * @Description: TODO(lucene分段合并的处理)
 * @date 2019/6/25 17:44
 */
public final class ForceMerge {

    // force merge休眠时间
    private final static int SLEEP = 6_0_000 * 5;    // 5 * 60 * 1000

    // 任务管理map初始化大小
    private final static int initialCapacity = 10;

    // 管理force merge每日执行
    private static Map<String, Boolean> everydayExecuteManager = new HashMap(initialCapacity);

    /**
     * 使用forcemerge可以及时释放磁盘空间，但是会带来cpu/io消耗增加，缓存失效等问题。这种问题对查询性能带来影响。
     * 但是可以按照具体的使用场景来采取措施：
     * 1、对于不再生成新分段的索引（不再有数据被索引和更新），可以考虑人工启动分段merge操作（在一定接受范围内手动merge）；
     * 2、如果索引在不断的产生新分段（数据被索引），通过修改集群段合并策略优化。
     **/

    private static EsIndexDelete esIndexDataDelete;

    public ForceMerge() {
        Map<String, Object> configure = new HashMap<>();
        // 建立连接超时时间，单位：毫秒
        configure.put("http.timeoutConnection ", 1_00_000);
        // socket通讯超时时间，如果在通讯过程中出现sockertimeout异常，可以适当调整timeoutSocket参数值，单位：毫秒
        configure.put("http.timeoutSocket", 1_000 * 60 * 60 * 6);
        // 申请连接超时时间，设置为0不超时，单位：毫秒
        configure.put("http.connectionRequestTimeout", 0);

        this.esIndexDataDelete = new EsIndexDelete(SystemConstant.ipPort, SystemConstant.indexName, SystemConstant.indexType, configure);
    }

    private void run() {

        while (true) {

            // 释放磁盘空间（执行段合并操作）- CPU/IO消耗增加，缓存失效
            if (isForceMerge()) {
                System.out.println("===========================================EXECUTE FORCE MERGE TASK===========================================");
                // 防止出问题之后进行重新MERGE（默认循环MERGE三次）
                for (int i = 0; i < 3; i++) {
                    System.out.println(esIndexDataDelete.splitForceMerge());
                }
                addExecuteMark();
            }
            System.out.println("EXECUTE FORCE MERGE TASK HOUR ZONE:" + SystemConstant.forcemergeTime);
            System.out.println("CURRENT HOUR:" + getCurrentHour());
            System.out.println("SLEEP:" + SLEEP + "ms");
            System.out.println(JSONObject.parseObject(JSON.toJSONString(everydayExecuteManager)));
            try {
                Thread.sleep(SLEEP);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param
     * @return
     * @Description: TODO(记录执行 - 固定SIZE执行清理)
     */
    private void addExecuteMark() {
        if (everydayExecuteManager.size() < initialCapacity) {
            everydayExecuteManager.put(getCurrentDate(), true);
        } else {
            everydayExecuteManager.clear();
            everydayExecuteManager.put(getCurrentDate(), true);
        }
    }

    private boolean isForceMerge() {
        String timeSplit = SystemConstant.forcemergeTime;
        int startHour = tansferInt(timeSplit.split("~")[0].split(":")[0]);
        int stopHour = tansferInt(timeSplit.split("~")[1].split(":")[0]);

        int currentHour = getCurrentHour();

        // 是否满足时间段
        if (startHour <= currentHour && currentHour <= stopHour) {
            if (!todayIsExecute()) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param
     * @return
     * @Description: TODO(今天是否执行过force merge操作)
     */
    private boolean todayIsExecute() {
        if (everydayExecuteManager.containsKey(getCurrentDate())) {
            return everydayExecuteManager.get(getCurrentDate());
        } else {
            everydayExecuteManager.put(getCurrentDate(), false);
            return false;
        }
    }

    private int getCurrentHour() {
        Date d = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = sdf.format(d);
        return tansferInt(date.split(" ")[1].split(":")[0]);
    }

    private String getCurrentDate() {
        Date d = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(d);
    }

    private int tansferInt(String hour) {
        return Integer.valueOf(hour);
    }

    public static void main(String[] args) {
        new ForceMerge().run();
    }

}


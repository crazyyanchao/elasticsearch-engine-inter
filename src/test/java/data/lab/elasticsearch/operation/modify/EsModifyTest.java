package data.lab.elasticsearch.operation.modify;

import data.lab.elasticsearch.common.FieldOccurs;
import data.lab.elasticsearch.operation.index.EsIndexCreat;
import data.lab.elasticsearch.operation.search.EsIndexSearch;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: casia.isi.elasticsearch.operation.modify
 * @Description: TODO(Describe the role of this JAVA class)
 * @date 2019/12/7 17:35
 */
public class EsModifyTest {

    @Test
    public void executeAutoRefresh() {

        EsIndexCreat esIndexCreat = new EsIndexCreat("localhost:9200", "ship_info", "graph");

        // 修改索引的刷新间隔
        EsModify.modifyRefreshIntervalSecond("localhost:9200", "ship_info,.monitor_task_alarm", 60);

        EsIndexSearch esIndexSearch = new EsIndexSearch("localhost:9200", "ship_info,.monitor_task_alarm", "graph");

        for (int i = 650; i < 1000; i++) {
            String mmsi = "mmsi-test" + i;

            // 写入数据
            List<JSONObject> list = new ArrayList<>();
            JSONObject data = new JSONObject();
            data.put("mmsi", mmsi);
            list.add(data);

            esIndexCreat.index(list, "mmsi");

            // 查询一条数据
            esIndexSearch.addPrimitiveTermFilter("mmsi", mmsi, FieldOccurs.MUST);
            esIndexSearch.execute(new String[]{"mmsi"});
            esIndexSearch.outputResult(esIndexSearch.getResults());
            esIndexSearch.reset();

            // 手动执行一次索引刷新
            EsModify.executeAutoRefresh("localhost:9200", "ship_info");

            // 查询数据
            esIndexSearch.addPrimitiveTermFilter("mmsi", mmsi, FieldOccurs.MUST);
            esIndexSearch.execute(new String[]{"mmsi"});
            esIndexSearch.outputResult(esIndexSearch.getResults());
            esIndexSearch.reset();
        }
    }
}



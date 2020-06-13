package data.lab.elasticsearch.operation.modify;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import data.lab.elasticsearch.operation.http.HttpDiscoverRegister;
import data.lab.elasticsearch.operation.http.HttpPoolSym;
import data.lab.elasticsearch.operation.http.HttpProxyRegister;
import data.lab.elasticsearch.operation.http.HttpProxyRequest;
import com.alibaba.fastjson.JSONObject;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: casia.isi.elasticsearch.operation.modify
 * @Description: TODO(ES MODIFY)
 * @date 2019/12/7 17:25
 */
public class EsModify {

    /**
     * @param indexName：支持多个索引用逗号分隔
     * @return
     * @Description: TODO(修改索引刷新间隔)
     */
    public static JSONObject modifyRefreshIntervalSecond(String ipPorts, String indexName, int second) {

        HttpProxyRequest request = revertHttpModule(ipPorts);

        JSONObject query = new JSONObject();
        query.put("refresh_interval", second + "s");
        String url = "/" + indexName + "/_settings";
        String queryResultStr = request.httpPut(url, query.toJSONString());
        return JSONObject.parseObject(queryResultStr);
    }

    /**
     * @param indexName：支持多个索引用逗号分隔
     * @return
     * @Description: TODO(索引执行一次手动刷新-对于数据可见性有实时性要求的需要调用此方法)
     */
    public static JSONObject executeAutoRefresh(String ipPorts, String indexName) {

        HttpProxyRequest request = revertHttpModule(ipPorts);

        String url = "/" + indexName + "/_refresh";
        String queryResultStr = request.httpGet(url);
        return JSONObject.parseObject(queryResultStr);
    }

    /**
     * @param
     * @return
     * @Description: TODO(重置HTTP模块)
     */
    private static HttpProxyRequest revertHttpModule(String ipPorts) {
        HttpProxyRequest request = new HttpProxyRequest(HttpPoolSym.DEFAULT.getSymbolValue());
        HttpProxyRegister.register(ipPorts);
        removeLastHttpsAddNewAddress(ipPorts);
        return request;
    }

    /**
     * @param
     * @return
     * @Description: TODO(重置HTTP模块 - 将上一次注册的地址移除 ， 并加入新的集群地址)
     */
    public static void removeLastHttpsAddNewAddress(String ipPorts) {
        boolean status;
        do {
            status = HttpDiscoverRegister.discover(ipPorts);
        } while (!status);
    }

}


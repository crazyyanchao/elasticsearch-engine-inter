package data.lab.elasticsearch.common;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import casia.isi.component.http.ClientConfiguration;
import casia.isi.component.http.proxy.HttpAddress;
import casia.isi.component.http.proxy.HttpServiceHosts;
import data.lab.elasticsearch.operation.http.*;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: casia.isi.elasticsearch.common
 * @Description: TODO(索引构建器 / 增 / 删 / 改 / 查工具的父类)
 * @date 2019/11/28 10:50
 */
public abstract class EsAccessor {

    public Logger logger = Logger.getLogger(this.getClass());

    /**
     * 是否开启debug模式，debug模式下过程语句将会输出
     */
    public static boolean debug = false;

    /**
     * 是否等待接口响应
     */
    public static boolean isWaitResponse = true;

    public static String httpPoolName = HttpPoolSym.DEFAULT.getSymbolValue();

    /**
     * http访问对象 仅仅支持绝对地址接口访问
     */
//    public HttpRequest request =  new HttpRequest();

    /**
     * http访问对象 支持绝对接口地址和相对接口地址
     **/
    public HttpProxyRequest request = new HttpProxyRequest(httpPoolName);

    public EsAccessor() {
    }

    public EsAccessor(HttpSymbol httpPoolName, String ipPorts) {
        if (httpPoolName.name().equals(httpPoolName))
            logger.error(httpPoolName + " hold on...", new IllegalArgumentException());
        HttpProxyRegister.register(ipPorts);
        removeLastHttpsAddNewAddress(ipPorts);
    }

    /**
     * @param
     * @return
     * @Description: TODO(重置HTTP模块 - 将上一次注册的地址移除 ， 并加入新的集群地址)
     */
    public void removeLastHttpsAddNewAddress(String ipPorts) {
        // MODIFY STATUS
        ClientConfiguration config = ClientConfiguration.getClientConfiguration(HttpPoolSym.DEFAULT.getSymbolValue());
        HttpServiceHosts httpServiceHosts = config.getHttpServiceHosts();
        if (httpServiceHosts != null) {
            List<HttpAddress> httpAddressList = httpServiceHosts.getAddressList();
            for (HttpAddress address : httpAddressList) {
                address.setStatus(2);
            }
            // REGISTER
            boolean status;
            do {
                status = HttpDiscoverRegister.discover(ipPorts);
            } while (!status);
        }
    }

    public static boolean isDebug() {
        return debug;
    }

    public static void setDebug(boolean isDebug) {
        debug = isDebug;
    }

    /**
     * @param
     * @return
     * @Description: TODO(获取索引信息)
     */
    public String catIndicesInfo(String indicesName) {
        return request.httpGet("/_cat/indices/" + indicesName + "?v&format=json&pretty");
    }

    /**
     * @param
     * @return
     * @Description: TODO(获取索引信息 - 指定字段返回)
     */
    public String catIndicesInfo(String indicesName, String fields) {
        return request.httpGet("/_cat/indices/" + indicesName + "?v&h=" + fields + "&format=json&pretty");
    }
}


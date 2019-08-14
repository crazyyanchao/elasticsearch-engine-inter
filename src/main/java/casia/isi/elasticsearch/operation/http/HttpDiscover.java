package casia.isi.elasticsearch.operation.http;
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

import casia.isi.elasticsearch.common.Symbol;
import org.frameworkset.spi.assemble.GetProperties;
import org.frameworkset.spi.remote.http.ClientConfiguration;
import org.frameworkset.spi.remote.http.HttpHost;
import org.frameworkset.spi.remote.http.proxy.HttpHostDiscover;
import org.frameworkset.spi.remote.http.proxy.HttpProxyUtil;
import org.frameworkset.spi.remote.http.proxy.HttpServiceHostsConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * @author YanchaoMa yanchaoma@foxmail.com
 * @PACKAGE_NAME: casia.isi.elasticsearch.operation.http
 * @Description: TODO(服务地址发现功能)
 * @date 2019/6/27 14:38
 */
public class HttpDiscover extends HttpHostDiscover {

    private int count = 0;

    /**
     * @param
     * @return
     * @Description: TODO(主动发现服务地址)
     */
    @Override
    protected List<HttpHost> discover(HttpServiceHostsConfig httpServiceHostsConfig,
                                      ClientConfiguration configuration,
                                      GetProperties context) {

        List<HttpHost> hosts = new ArrayList<HttpHost>();

//        // https服务必须带https://协议头,例如https://192.168.12.109:9210
//        HttpHost host = new HttpHost("192.168.12.109:9210");
//        hosts.add(host);
//        if (count != 2) {
//            host = new HttpHost("192.168.12.107:9210");
//            hosts.add(host);
//        } else {
//            System.out.println("HttpDiscover--------");
//        }
//        host = new HttpHost("192.168.12.112:9210");
//        hosts.add(host);
//        count++;

        return hosts;
    }

    /**
     * 返回null或者false，忽略对返回的null或者空的hosts进行处理；
     * 返回true，要对null或者空的hosts进行处理，这样会导致所有的地址不可用
     *
     * @return 默认返回null
     */
    protected Boolean handleNullOrEmptyHostsByDiscovery() {
        return null;
    }

    /**
     * @param ipPorts:逗号分隔的IP:PORT地址
     * @return
     * @Description: TODO(被动发现服务地址)
     */
    public static void discover(String ipPorts) {
        List<HttpHost> httpHosts = new ArrayList<>();
        String[] servers = ipPorts
                .replace(" ", "")
                .split(Symbol.COMMA_CHARACTER.getSymbolValue());

        for (int i = 0; i < servers.length; i++) {
            String server = servers[i];
            httpHosts.add(new HttpHost(server));
        }

        /**
         * 被动发现模式：例如监听消息中间件等数据变化，适用于发布订阅模式
         *
         * **/
        HttpProxyUtil.handleDiscoverHosts(HttpPoolSym.DEFAULT.getSymbolValue(), httpHosts);
    }

}

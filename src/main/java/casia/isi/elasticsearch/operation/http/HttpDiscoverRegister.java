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
import casia.isi.elasticsearch.util.ClientUtils;
import org.frameworkset.spi.remote.http.ClientConfiguration;
import org.frameworkset.spi.remote.http.HttpHost;
import org.frameworkset.spi.remote.http.proxy.HttpAddress;
import org.frameworkset.spi.remote.http.proxy.HttpProxyUtil;
import org.frameworkset.spi.remote.http.proxy.HttpServiceHosts;

import java.util.ArrayList;
import java.util.List;

/**
 * @author YanchaoMa yanchaoma@foxmail.com
 * @PACKAGE_NAME: casia.isi.elasticsearch.operation.http
 * @Description: TODO(服务地址发现功能)
 * @date 2019/11/26 10:45
 */
public class HttpDiscoverRegister {
    /**
     * @param ipPorts:逗号分隔的IP:PORT地址
     * @return
     * @Description: TODO(被动发现服务地址)
     */
    public static boolean discover(String ipPorts) {

        List<HttpHost> httpHosts = packHosts(ipPorts);

        /**
         * 被动发现模式：例如监听消息中间件等数据变化，适用于发布订阅模式
         *
         * **/
        HttpProxyUtil.handleDiscoverHosts(HttpPoolSym.DEFAULT.getSymbolValue(), httpHosts);

        ClientConfiguration config = ClientConfiguration.getClientConfiguration(HttpPoolSym.DEFAULT.getSymbolValue());

        HttpAddress httpAddress = config.getHttpServiceHosts().getHttpAddress();
        int status = httpAddress.getStatus();

        String[] arrayChar = httpAddress.getAddress().split("|");
        StringBuilder builder = new StringBuilder();
        for (String cypher : arrayChar) {
            builder.append(cypher);
        }
        String ipPort = builder.toString().replace("http://", "").replace("https://", "");
        return status == 0 && httpHosts.contains(new HttpHost(ipPort));
    }

    /**
     * @param ipPorts:逗号分隔的IP:PORT地址
     * @return
     * @Description: TODO(封装HTTP HOSTS)
     */
    private static List<HttpHost> packHosts(String ipPorts) {
        List<HttpHost> httpHosts = new ArrayList<>();
        String[] servers = ipPorts
                .replace(" ", "")
                .split(Symbol.COMMA_CHARACTER.getSymbolValue());

        for (int i = 0; i < servers.length; i++) {
            String server = servers[i];
            httpHosts.add(new HttpHost(server));
        }
        return httpHosts;
    }

}


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

import org.frameworkset.spi.assemble.GetProperties;
import casia.isi.component.http.ClientConfiguration;
import casia.isi.component.http.HttpHost;
import casia.isi.component.http.proxy.HttpHostDiscover;
import casia.isi.component.http.proxy.HttpServiceHostsConfig;

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
     * 动态操作：在返回列表中将失效或者不需要的地址去掉即可
     **/

//    @Override
//    protected List<HttpHost> discover(HttpServiceHostsConfig httpServiceHostsConfig,
//                                      ClientConfiguration configuration,
//                                      GetProperties context) {
//        // 直接构造并返回三个服务地址的列表对象
//        List<HttpHost> hosts = new ArrayList<>();
//        // https服务必须带https://协议头,例如https://192.168.137.1:808
//        HttpHost host = new HttpHost("192.168.137.1:808");
//        hosts.add(host);
//        if (count != 2) {//模拟添加和去除节点
//            host = new HttpHost("192.168.137.1:809");
//            hosts.add(host);
//        } else {
//            System.out.println("aa");
//        }
//        host = new HttpHost("192.168.137.1:810");
//        hosts.add(host);
//        count++;
//        return hosts;
//    }
//
//    /**
//     * 返回null或者false，忽略对返回的null或者空的hosts进行处理；
//     * 返回true，要对null或者空的hosts进行处理，这样会导致所有的地址不可用
//     *
//     * @return 默认返回null
//     */
//    protected Boolean handleNullOrEmptyHostsByDiscovery() {
//        return null;
//    }

    private static List<String> ipPortList = new ArrayList<>();

    /**
     * @param
     * @return
     * @Description: TODO(主动发现服务地址)
     */
    @Override
    protected List<HttpHost> discover(HttpServiceHostsConfig httpServiceHostsConfig,
                                      ClientConfiguration configuration,
                                      GetProperties context) {

        List<HttpHost> hosts = new ArrayList<>();

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
        for (int i = 0; i < ipPortList.size(); i++) {
            String address = ipPortList.get(i);
            HttpHost host = new HttpHost(address);
            hosts.add(host);
        }

        count++;
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
     * @param
     * @return
     * @Description: TODO(传入使用逗号分隔的地址)
     */
    public static void addAddress(String ipPorts) {
        String[] ipPortArray = ipPorts.split(",");
        for (int i = 0; i < ipPortArray.length; i++) {
            String address = ipPortArray[i];
            ipPortList.add(address.trim());
        }
    }
}


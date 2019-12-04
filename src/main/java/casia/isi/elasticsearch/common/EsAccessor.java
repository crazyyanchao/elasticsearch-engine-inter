package casia.isi.elasticsearch.common;
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

import casia.isi.elasticsearch.operation.http.*;
import org.apache.log4j.Logger;

/**
 * @author YanchaoMa yanchaoma@foxmail.com
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
     * http访问对象 仅仅支持绝对地址接口访问
     */
//    public HttpRequest request =  new HttpRequest();

    /**
     * http访问对象 支持绝对接口地址和相对接口地址
     **/
    public HttpProxyRequest request = new HttpProxyRequest(HttpPoolSym.DEFAULT.getSymbolValue());

    public EsAccessor() {
    }

    public EsAccessor(HttpSymbol httpPoolName, String ipPorts) {
        if (httpPoolName.name().equals(HttpPoolSym.DEFAULT.getSymbolValue()))
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
        boolean status;
        do {
            status = HttpDiscoverRegister.discover(ipPorts);
        } while (!status);
    }

    public static boolean isDebug() {
        return debug;
    }

    public static void setDebug(boolean isDebug) {
        debug = isDebug;
    }

}


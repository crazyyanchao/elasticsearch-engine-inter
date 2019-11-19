package casia.isi.elasticsearch.operation.delete.deleter;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * @author YanchaoMa yanchaoma@foxmail.com
 * @PACKAGE_NAME: casia.isi.elasticsearch.common
 * @Description: TODO(配置加载)
 * @date 2019/6/25 17:48
 */
public final class SystemConstant {

    private final static String deletePropertyFile = "config" + File.separator + "delete.properties";

    // es配置
    public final static String ipPort = loadProperties("elasticsearch.address");

    // 执行删除接口的配置
    public final static String indexType = loadProperties("indexType");
    public final static String indexName = loadProperties("indexName");
    public final static String timeField = loadProperties("timeField");
    public final static String delayTime = loadProperties("delayTime");
    public final static String beforeDataTime = loadProperties("beforeDataTime");

    // lucene分段合并的时间
    public final static String forcemergeTime = loadProperties("forcemerge.time");


    /**
     * @param
     * @return
     * @Description: TODO(加载配置文件)
     */
    private static Properties getIndexProperties() {
        try {
            //获取服务器ip和端口
            FileInputStream inStream = new FileInputStream(new File(deletePropertyFile));
            Properties properties = new Properties();
            properties.load(inStream);
            return properties;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * @param proName
     * @return
     * @Description: TODO(通过配置名称获取配置)
     */
    public static String loadProperties(String proName) {
        Properties properties = getIndexProperties();
        return properties.getProperty(proName);
    }

}


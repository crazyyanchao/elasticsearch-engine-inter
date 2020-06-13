package data.lab.elasticsearch.operation.http;
/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: casia.isi.elasticsearch.operation.http
 * @Description: TODO(HTTP POOL SYMBOL)
 * @date 2019/7/1 12:00
 */
public enum HttpPoolSym implements HttpSymbol {

    /**
     * 多个集群时才需要新增指定HTTP连接池
     * **/

    /**
     * 默认连接池
     */
    DEFAULT("default"),

    REPORT("report");

    private String symbol;

    HttpPoolSym(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbolValue() {
        return this.symbol;
    }

}

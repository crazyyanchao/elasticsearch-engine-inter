package casia.isi.elasticsearch.common;

/**
 * 枚举类，字段出现情况
 *
 * @author
 */
public enum FieldOccurs {
    /**
     * 必须出现
     */
    MUST("+"),
    /**
     * 必须不能出现
     */
    MUST_NOT("-"),

    /**
     * 可以匹配也可以不匹配一篇文档，但是匹配数量至少要达到minimum_should_match参数所设置的数量
     * --minimum_should_match
     * （如果没有使用MUST那么默认是1，如果使用了MUST默认是0）类似于-(query1 or query2 or query3)
     */
    SHOULD("");

    private String symbol;

    FieldOccurs(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbolValue() {
        return this.symbol;
    }

}



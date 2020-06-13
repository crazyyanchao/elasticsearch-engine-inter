package data.lab.elasticsearch.common;
/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: casia.isi.elasticsearch.common
 * @Description: TODO(range查询参数)
 * @date 2019/5/31 14:18
 */
public enum RangeOccurs {

    /**
     * 搜索大于某值的字段，不包含该值本身
     **/
    GT("gt"),

    /**
     * 搜索大于某值的字段，包含该值本身
     **/
    GTE("gte"),

    /**
     * 搜索小于某值的字段，不包含该值本身
     **/
    LT("lt"),

    /**
     * 搜索小于某值的字段，包含该值本身
     **/
    LTE("lte");

    private String symbol;

    RangeOccurs(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbolValue() {
        return this.symbol;
    }

}

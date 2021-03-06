package data.lab.elasticsearch.common;
/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: casia.isi.elasticsearch.common
 * @Description: TODO(GEO类型检索时的设置)
 * @date 2019/7/15 11:00
 */
public enum GeoBoundOccurs {

    TOP_LEFT("top_left"),

    BOTTOM_RIGHT("bottom_right"),

    TOP_RIGHT("top_right"),

    BOTTOM_LEFT("bottom_left");

    private String symbol;

    GeoBoundOccurs(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbolValue() {
        return this.symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}


package data.lab.elasticsearch.common;
/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: casia.isi.elasticsearch.common
 * @Description: TODO(两点间的距离计算 ， 选择算法)
 * @date 2019/6/24 19:06
 */
public enum GeoDistanceOccurs {

    /**
     * 最慢但最精确的是 arc 计算方式，这种方式把世界当作球体来处理。不过这种方式的精度有限，因为这个世界并不是完全的球体。
     * **/
    ARC("arc"),

    /**
     * 更快但精度稍差 - plane 计算方式把地球当成是平坦的，这种方式快一些但是精度略逊。在赤道附近的位置精度最好，而靠近两极则变差。
     * **/
    PLANE("plane"),

    /**
     * 如此命名，是因为它使用了 Lucene 的 SloppyMath 类。这是一种用精度换取速度的计算方式， 它使用 Haversine formula 来计算距离。
     * 它比 arc 计算方式快 4 到 5 倍，并且距离精度达 99.9%。这也是默认的计算方式。
     * **/
    SLOPPY_ARC("sloppy_arc");

    private String symbol;

    GeoDistanceOccurs(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbolValue() {
        return this.symbol;
    }
}

package data.lab.elasticsearch.common;
/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: casia.isi.elasticsearch.common
 * @Description: TODO(距离单位)
 * @date 2019/7/8 18:03
 */
public enum DistanceUnit {

    /*
     * <p>
     * The full list of units is listed below:
     * Mile-mi or miles
     * Yard - yd or yards
     * Feet - ft or feet
     * Inch - in or inch
     * Kilometer - km or kilometers
     * Meter - m or meters
     * Centimeter - cm or centimeters
     * Millimeter - mm or millimeters
     */

    MILE("mi"),

    YARD("yd"),

    FEET("ft"),

    INCH("in"),

    KILOMETER("km"),

    METER("m"),

    CENTIMETER("cm"),

    MILLIMETER("mm");

    private String symbol;

    DistanceUnit(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbolValue() {
        return this.symbol;
    }
}

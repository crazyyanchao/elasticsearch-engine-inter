package data.lab.elasticsearch.model;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import data.lab.elasticsearch.common.GeoBoundOccurs;

import java.util.Objects;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: casia.isi.elasticsearch.model
 * @Description: TODO(位置点实体类)
 * @date 2019/7/15 11:14
 */
public class BoundPoint {

    // 纬度
    private double lat;

    // 经度
    private double lon;

    private GeoBoundOccurs locBoundMark;

    public BoundPoint(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public BoundPoint(double lat, double lon, GeoBoundOccurs locBoundMark) {
        this.lat = lat;
        this.lon = lon;
        this.locBoundMark = locBoundMark;
    }

    public GeoBoundOccurs getLocBoundMark() {
        return locBoundMark;
    }

    public void setLocBoundMark(GeoBoundOccurs locBoundMark) {
        this.locBoundMark = locBoundMark;
    }


    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    @Override
    public String toString() {
        return "Point{" +
                "lat=" + lat +
                ", lon=" + lon +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoundPoint point = (BoundPoint) o;
        return Double.compare(point.lat, lat) == 0 &&
                Double.compare(point.lon, lon) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lat, lon);
    }

}


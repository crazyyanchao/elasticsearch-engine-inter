package data.lab.elasticsearch.model;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import data.lab.elasticsearch.common.DistanceUnit;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: casia.isi.elasticsearch.model
 * @Description: TODO(圆形检索条件)
 * @date 2019/8/3 18:01
 */
public class Circle implements Shape {
    private Centre centre;
    private String distance;

    public Centre getCentre() {
        return centre;
    }

    public void setCentre(double lat, double lon) {
        this.centre = new Centre(lat, lon);
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(int distance, DistanceUnit distanceUnit) {
        this.distance = distance + distanceUnit.getSymbolValue();
    }

    public class Centre {
        private double lat;
        private double lon;

        public Centre(double lat, double lon) {
            this.lat = lat;
            this.lon = lon;
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
    }
}

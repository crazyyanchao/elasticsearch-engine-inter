package data.lab.elasticsearch.operation.search.analyzer;

import data.lab.elasticsearch.model.BoundPoint;
import org.junit.Test;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.*;
/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @PACKAGE_NAME: casia.isi.elasticsearch.operation.search.analyzer
 * @Description: TODO(Describe the role of this JAVA class)
 * @author Yc-Ma
 * @date 2019/7/25 11:54
 *
 *
 */
public class GeoDistanceTest {

    @Test
    public void calculate() {

        BoundPoint centre = new BoundPoint(48.8534100, 2.3488000);
        BoundPoint northernPoint = new BoundPoint(48.8801108681, 2.35152032666);
        BoundPoint westernPoint = new BoundPoint(48.85265, 2.308896);

        // With GeoDistance.ARC both the northern and western points are within the 4km range
//        assertThat(GeoDistance.ARC.calculate(centre.getLat(), centre.getLon(), northernPoint.getLat(),
//                northernPoint.getLon(), GeoDistanceUnit.KILOMETERS), lessThan(4D));
//        assertThat(GeoDistance.ARC.calculate(centre.getLat(), centre.getLon(), westernPoint.getLat(),
//                westernPoint.getLon(), GeoDistanceUnit.KILOMETERS), lessThan(4D));

        System.out.println(centre.toString()+" "+northernPoint.toString()+" ARC:"+
                GeoDistance.ARC.calculate(centre.getLat(), centre.getLon(), northernPoint.getLat(),
                northernPoint.getLon(), GeoDistanceUnit.KILOMETERS));

        System.out.println(centre.toString()+" "+westernPoint.toString()+" ARC:"+
                GeoDistance.ARC.calculate(centre.getLat(), centre.getLon(), westernPoint.getLat(),
                westernPoint.getLon(), GeoDistanceUnit.KILOMETERS));


        // With GeoDistance.PLANE, only the northern point is within the 4km range,
        // the western point is outside of the range due to the simple math it employs,
        // meaning results will appear elliptical
//        assertThat(GeoDistance.PLANE.calculate(centre.getLat(), centre.getLon(), northernPoint.getLat(),
//                northernPoint.getLon(), GeoDistanceUnit.KILOMETERS), lessThan(4D));
        assertThat(GeoDistance.PLANE.calculate(centre.getLat(), centre.getLon(), westernPoint.getLat(),
                westernPoint.getLon(), GeoDistanceUnit.KILOMETERS), greaterThan(4D));

        System.out.println(centre.toString()+" "+northernPoint.toString()+" PLANE:"+
                GeoDistance.PLANE.calculate(centre.getLat(), centre.getLon(), northernPoint.getLat(),
                        northernPoint.getLon(), GeoDistanceUnit.KILOMETERS));

        System.out.println(centre.toString()+" "+westernPoint.toString()+" PLANE:"+
                GeoDistance.PLANE.calculate(centre.getLat(), centre.getLon(), westernPoint.getLat(),
                        westernPoint.getLon(), GeoDistanceUnit.KILOMETERS));
    }

    @Test
    public void calculateTest() {
        // ZHONG GUAN CUN 95 HAO -> ZHI CHUN LU
        BoundPoint point1 = new BoundPoint(39.984857, 116.339618);
        BoundPoint point2 = new BoundPoint(39.982082, 116.348583);
        System.out.println(point1.toString()+" "+point2.toString()+" PLANE:"+
                GeoDistance.PLANE.calculate(point1.getLat(), point1.getLon(), point2.getLat(),
                        point2.getLon(), GeoDistanceUnit.KILOMETERS));
    }


    @Test
    public void calculateTest2() {
        // ZHONG GUAN CUN 95 HAO -> ZHI CHUN LU
        BoundPoint point1 = new BoundPoint(33.67507, -117.86925);
        BoundPoint point2 = new BoundPoint(38.69776, -121.59717);
        System.out.println(point1.toString()+" "+point2.toString()+" PLANE:"+
                GeoDistance.PLANE.calculate(point1.getLat(), point1.getLon(), point2.getLat(),
                        point2.getLon(), GeoDistanceUnit.KILOMETERS));
    }

    @Test
    public void calculateTest3() {
        // ZHONG GUAN CUN 95 HAO -> ZHI CHUN LU
        BoundPoint point1 = new BoundPoint(33.67507, -117.86925);
        BoundPoint point2 = new BoundPoint(38.66565, -121.56068);
        System.out.println(point1.toString()+" "+point2.toString()+" PLANE:"+
                GeoDistance.PLANE.calculate(point1.getLat(), point1.getLon(), point2.getLat(),
                        point2.getLon(), GeoDistanceUnit.KILOMETERS));
    }

    @Test
    public void calculateTest4() {
        BoundPoint origin = new BoundPoint(50.03333, 8.57056);

        BoundPoint point1 = new BoundPoint(49.0731, 2.688);

        BoundPoint point2 = new BoundPoint(49.0058, 2.7338);

        System.out.println("当前点："+origin.toString()+" "+point2.toString()+" PLANE:"+
                GeoDistance.PLANE.calculate(origin.getLat(), origin.getLon(), point2.getLat(),
                        point2.getLon(), GeoDistanceUnit.KILOMETERS));

        System.out.println("上一个点："+origin.toString()+" "+point1.toString()+" PLANE:"+
                GeoDistance.PLANE.calculate(origin.getLat(), origin.getLon(), point1.getLat(),
                        point1.getLon(), GeoDistanceUnit.KILOMETERS));
    }

}



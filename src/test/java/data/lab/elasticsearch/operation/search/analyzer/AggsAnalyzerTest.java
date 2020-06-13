package data.lab.elasticsearch.operation.search.analyzer;

import org.junit.Test;
/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: casia.isi.elasticsearch.operation.search.analyzer
 * @Description: TODO(Describe the role of this JAVA class)
 * @date 2019/7/25 15:28
 */
public class AggsAnalyzerTest {

    @Test
    public void flightCourseSegmentByAirport() {
        String[] strings = new String[]{"sda", "234", "3246s"};
        int size = strings.length;
        for (int i = size - 1; i >= 0; i--) {
            System.out.println(strings[i]);
        }
    }

}


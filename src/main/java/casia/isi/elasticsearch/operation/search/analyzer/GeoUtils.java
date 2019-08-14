/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package casia.isi.elasticsearch.operation.search.analyzer;

public class GeoUtils {

    /** Earth mean radius defined by WGS 84 in meters */
    public static final double EARTH_MEAN_RADIUS = 6371008.7714D;      // meters (WGS 84)


    /** Return the distance (in meters) between 2 lat,lon geo points using the haversine method implemented by lucene */
    public static double arcDistance(double lat1, double lon1, double lat2, double lon2) {
        return SloppyMath.haversinMeters(lat1, lon1, lat2, lon2);
    }

    /**
     * Return the distance (in meters) between 2 lat,lon geo points using a simple tangential plane
     * this provides a faster alternative to {@link GeoUtils#arcDistance} but is inaccurate for distances greater than
     * 4 decimal degrees
     */
    public static double planeDistance(double lat1, double lon1, double lat2, double lon2) {
        double x = (lon2 - lon1) * SloppyMath.TO_RADIANS * Math.cos((lat2 + lat1) / 2.0 * SloppyMath.TO_RADIANS);
        double y = (lat2 - lat1) * SloppyMath.TO_RADIANS;
        return Math.sqrt(x * x + y * y) * EARTH_MEAN_RADIUS;
    }

}

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

/**
 * Geo distance calculation.
 */
public enum GeoDistance  {

    PLANE, ARC;

    /** compute the distance between two points using the selected algorithm (PLANE, ARC) */
    public double calculate(double srcLat, double srcLon, double dstLat, double dstLon, GeoDistanceUnit unit) {
        if (this == PLANE) {
            return GeoDistanceUnit.convert(GeoUtils.planeDistance(srcLat, srcLon, dstLat, dstLon),
                GeoDistanceUnit.METERS, unit);
        }
        return GeoDistanceUnit.convert(GeoUtils.arcDistance(srcLat, srcLon, dstLat, dstLon), GeoDistanceUnit.METERS, unit);
    }
}

package data.lab.elasticsearch.operation.search.analyzer;
/*
 *
 * Data Lab - graph database organization.
 *
 */

import data.lab.elasticsearch.model.BoundPoint;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: casia.isi.elasticsearch.operation.search.analyzer
 * @Description: TODO(聚合数据分析)
 * @date 2019/7/24 10:13
 */
public class AggsAnalyzer {

    /**
     * @param result:航线数据聚合结果
     * @param countBlankThreshold:航段分隔时允许中间间隔多少个点
     * @return
     * @Description: TODO(分析航线航段数据)
     */
    public static List<List<Map<String, Object>>> flightCourseSegment(List<String[]> result, int countBlankThreshold) {

        result = clearEmptyResult(result);

        // 时间升序的数据
        List<List<Map<String, Object>>> allSegment = new ArrayList<>();

        // 航段加载
        int countBlank = 0;
        List<Map<String, Object>> splitSegment = new ArrayList<>();
        for (String[] info : result) {
            if ("[]".equals(info[2])) {
                countBlank++;
            }
            if ("[]".equals(info[2]) && !splitSegment.isEmpty()) {

                if (countBlank > countBlankThreshold) {
                    allSegment.add(splitSegment);
                    splitSegment = new ArrayList<>();
                    countBlank = 0;
                }

            } else if (!"[]".equals(info[2])) {
                String string = info[2];

//                JSONArray array = JSONArray.parseArray(string);
//                JSONObject location_point = array.getJSONObject(0).getJSONObject("location_point");
//                Map<String, Object> point = new HashMap<>();
//                point.put("site", array.getJSONObject(0).getString("site"));
//                point.put("aircraft", array.getJSONObject(0).getString("aircraft"));
//                point.put("mode_s", array.getJSONObject(0).getString("mode_s"));
//                point.put("lng", location_point.getDouble("lon"));
//                point.put("lat", location_point.getDouble("lat"));

                splitSegment.add(packFlightMap(string));
                countBlank = 0;
            }
        }
        if (!splitSegment.isEmpty()) {
            allSegment.add(splitSegment);
        }
        return allSegment;
    }


    /**
     * @param result:航线数据聚合结果
     * @param startPoint:出发地
     * @param distance:地球距离计算（出发地与航线上某点距离范围在这个值以内则停止计算）单位km
     * @return
     * @Description: TODO(分析航线航段数据)
     */
    public static List<Map<String, Object>> flightCourseSegment(List<String[]> result, BoundPoint startPoint, int distance) {

        // 拿到当前时间指定时间范围的航行分段数据
        List<List<Map<String, Object>>> allSegment = flightCourseSegment(result, 5);

        // 使用出发地弥补航段缺失
        int loop = allSegment.size();

        // 每个航段的结束点（聚合结果是时间升序排列的）
        List<Map<Map<String, Object>, List<Map<String, Object>>>> segmentStopMapList = new ArrayList<>();
        for (int i = loop - 1; i >= 0; i--) {
            List<Map<String, Object>> split = allSegment.get(i);
            Map<String, Object> point = split.get(split.size() - 1);

//            if (distance(point, startPoint, distance)) {
//                Map<String, Object> start = new HashMap<>();
//                point.put("lng", startPoint.getLon());
//                point.put("lat", startPoint.getLat());
//                split.add(start);
//                segment.addAll(split);
//                break;
//            }else {
//                segment.addAll(split);
            Map<Map<String, Object>, List<Map<String, Object>>> segmentStopMap = new HashMap<>();
            segmentStopMap.put(point, split);
            // 最近航段是第一个元素
            segmentStopMapList.add(segmentStopMap);
        }

        List<Map<String, Object>> segment = startPointSegment(segmentStopMapList, startPoint);

        return segment;
    }

    /**
     * @param result:航线数据聚合结果
     * @param startPoint:出发地
     * @return
     * @Description: TODO(分析航线航段数据)
     */
    public static List<Map<String, Object>> flightCourseSegment(List<String[]> result, BoundPoint startPoint) {

        // 拿到当前时间指定时间范围的航行分段数据
        List<List<Map<String, Object>>> allSegment = flightCourseSegment(result, 5);

        // 使用出发地弥补航段缺失
        int loop = allSegment.size();

        // 每个航段的结束点（聚合结果是时间升序排列的）
        List<Map<Map<String, Object>, List<Map<String, Object>>>> segmentStopMapList = new ArrayList<>();
        for (int i = loop - 1; i >= 0; i--) {
            List<Map<String, Object>> split = allSegment.get(i);
            Map<String, Object> point = split.get(0);
            Map<Map<String, Object>, List<Map<String, Object>>> segmentStopMap = new HashMap<>();
            segmentStopMap.put(point, split);
            // 最近航段是第一个元素
            segmentStopMapList.add(segmentStopMap);
        }

        List<Map<String, Object>> segment = startPointSegment(segmentStopMapList, startPoint);

        return segment;
    }

    /**
     * @param
     * @return
     * @Description: TODO(航段的结束点两两计算距离)
     */
    private static List<Map<String, Object>> startPointSegment(List<Map<Map<String, Object>, List<Map<String, Object>>>> segmentStopMapList,
                                                               BoundPoint startPoint) {

        List<Map<String, Object>> segment = new ArrayList<>();

        int size = segmentStopMapList.size();

        if (size > 1) {
            for (int i = 0; i < size - 1; i++) {

                Map<Map<String, Object>, List<Map<String, Object>>> mapListFromMap = segmentStopMapList.get(i);
                Map<String, Object> pointFrom = getPoinByMap(mapListFromMap);

                Map<Map<String, Object>, List<Map<String, Object>>> mapListToMap = segmentStopMapList.get(i + 1);
                Map<String, Object> pointTo = getPoinByMap(mapListToMap);

                double srcLat = Double.parseDouble(String.valueOf(pointFrom.get("lat")));
                double srcLon = Double.parseDouble(String.valueOf(pointFrom.get("lng")));
                double dstLat = Double.parseDouble(String.valueOf(pointTo.get("lat")));
                double dstLon = Double.parseDouble(String.valueOf(pointTo.get("lng")));

                // 上一个航段结束点与出发地的距离
                double disFrom = GeoDistance.PLANE.calculate(srcLat, srcLon, startPoint.getLat(), startPoint.getLon(), GeoDistanceUnit.KILOMETERS);

                // 下一个航段结束点与出发地的距离
                double disTo = GeoDistance.PLANE.calculate(dstLat, dstLon, startPoint.getLat(), startPoint.getLon(), GeoDistanceUnit.KILOMETERS);

                if (disFrom < disTo) {
                    List<Map<String, Object>> mapListFromList = mapListFromMap.get(pointFrom);
                    if (!segment.containsAll(mapListFromList)) {
                        segment.addAll(0, mapListFromList);
                    }
                    break;
                } else if (disFrom >= disTo) {
                    if (!segment.containsAll(mapListFromMap.get(pointFrom))) {
                        segment.addAll(0, mapListFromMap.get(pointFrom));
                    }
                    if (!segment.containsAll(mapListToMap.get(pointTo))) {
                        segment.addAll(0, mapListToMap.get(pointTo));
                    }
                }
            }
        } else {
            for (int i = 0; i < size; i++) {
                Map<Map<String, Object>, List<Map<String, Object>>> mapListFromMap = segmentStopMapList.get(i);
                Map<String, Object> pointFrom = getPoinByMap(mapListFromMap);
                List<Map<String, Object>> mapListFromList = mapListFromMap.get(pointFrom);
                segment.addAll(mapListFromList);
            }
        }
        segment.add(0, packMapPoint(startPoint));
        return segment;
    }

    private static Map<String, Object> packMapPoint(BoundPoint startPoint) {
        Map<String, Object> point = new HashMap<>();
        point.put("lng", startPoint.getLon());
        point.put("lat", startPoint.getLat());
        return point;
    }

    private static Map<String, Object> getPoinByMap(Map<Map<String, Object>, List<Map<String, Object>>> mapListFromMap) {
        for (Map<String, Object> map : mapListFromMap.keySet()) {
            return map;
        }
        return new HashMap<>();
    }

    /**
     * @param point:航段上某点
     * @param startPoint:出发点
     * @param distance:距离范围km
     * @return 是同一个位置返回true，否则返回false
     * @Description: TODO(计算出发点和航段上某点是否为同一个位置)
     */
    private static boolean distance(Map<String, Object> point, BoundPoint startPoint, int distance) {

        double srcLat = Double.parseDouble(String.valueOf(point.get("lat")));
        double srcLon = Double.parseDouble(String.valueOf(point.get("lng")));
        double calculate = GeoDistance.PLANE.calculate(srcLat, srcLon, startPoint.getLat(), startPoint.getLon(), GeoDistanceUnit.KILOMETERS);
        if (distance >= calculate) {
            return true;
        }
        return false;
    }

    /**
     * @param result:航线数据聚合结果
     * @param countBlankThreshold:航段中间允许间隔多少点
     * @param startPoint:出发地
     * @return
     * @Description: TODO(分析航线航段数据 - 加载航班的最近实时航段)
     */
    public static List<Map<String, Object>> flightCourseSegmentByAirport(List<String[]> result, BoundPoint startPoint, int countBlankThreshold) {

        result = clearEmptyResult(result);

        // 没有获取到出发点
        if (startPoint == null) {
            List<List<Map<String, Object>>> mapList = AggsAnalyzer.flightCourseSegment(result, countBlankThreshold);
            return mapList.get(mapList.size() - 1);
        }

        // 时间升序排列的轨迹

        // 航段加载
        List<Map<String, Object>> segment = new ArrayList<>();

        double lastDis;
        double nextDis;

        Map<String, Object> lastPoint = null;
        Map<String, Object> nextPoint;

        int size = result.size();
        int countBlank = 0;

        for (int i = size - 1; i >= 0; i--) {
            String[] info = result.get(i);
            if (!"[]".equals(info[2]) && countBlank <= countBlankThreshold) {
                nextPoint = packFlightMap(info[2]);

                if (size - 1 == 0) {
                    segment.add(nextPoint);
                    break;
                } else {
                    if (lastPoint != null && !lastPoint.equals(nextPoint)) {

                        double latLast = Double.parseDouble(String.valueOf(lastPoint.get("lat")));
                        double lonLast = Double.parseDouble(String.valueOf(lastPoint.get("lng")));
                        lastDis = GeoDistance.PLANE.calculate(latLast, lonLast, startPoint.getLat(), startPoint.getLon(), GeoDistanceUnit.KILOMETERS);

                        double latNext = Double.parseDouble(String.valueOf(nextPoint.get("lat")));
                        double lonNext = Double.parseDouble(String.valueOf(nextPoint.get("lng")));
                        nextDis = GeoDistance.PLANE.calculate(latNext, lonNext, startPoint.getLat(), startPoint.getLon(), GeoDistanceUnit.KILOMETERS);

                        // 计算距离符合条件并且航线在时间上不连续的时候才跳出
                        if (lastDis < nextDis && !isTimeSeriesContinuityDown(result, i, countBlankThreshold)) {
                            break;
                        } else {
                            segment.add(nextPoint);
                            lastPoint = nextPoint;
                        }

                    } else {
                        segment.add(nextPoint);
                        lastPoint = nextPoint;
                    }
                }

                // 空间隔被切断之后重新进行累加
                countBlank = 0;
            } else {
                countBlank++;
            }
        }
        segment.add(packMapPoint(startPoint));
        return segment;
    }

    /**
     * @param
     * @return
     * @Description: TODO(清理掉开头结尾的空数据)
     */
    private static List<String[]> clearEmptyResult(List<String[]> result) {
        int size = result.size();

        int start = 0;
        int end = size;
        // 开头
        for (int i = 0; i < size; i++) {
            String[] info = result.get(i);
            if (!"[]".equals(info[2])) {
                start = i;
                break;
            }
        }
        // 结尾
        for (int i = size - 1; i >= 0; i--) {
            String[] info = result.get(i);
            if (!"[]".equals(info[2])) {
                end = i + 1;
                break;
            }
        }
        return result.subList(start, end);
    }

    /**
     * @param
     * @return true：时间连续 false：时间不连续
     * @Description: TODO(判断当前点和前时刻点是否在时间上连续)
     */
    private static boolean isTimeSeriesContinuityDown(List<String[]> result, int index, int countBlankThreshold) {
        int size = result.size();
        List<String> emptyList = new ArrayList<>();
        for (int i = size - 1; i >= 0; i--) {
            String[] infoEmpty = result.get(i);
            if (i == index) {
                if (emptyList.size() > countBlankThreshold) {
                    return false;
                } else {
                    return true;
                }
            }
            if ("[]".equals(infoEmpty[2])) {
                emptyList.add(infoEmpty[2]);
            } else if (!emptyList.isEmpty()) {
                emptyList.clear();
            }
        }
        return false;
    }

    /**
     * @param
     * @return
     * @Description: TODO(判断当前点和下一个点是否在时间上连续)
     */
    private static boolean isTimeSeriesContinuityUp(List<String[]> result, int index) {
        int size = result.size();
        for (int i = 0; i < size; i++) {
            if (i == index) {
                String[] info = result.get(i + 1);
                if (!"[]".equals(info[2])) {
                    return true;
                }
            }
        }
        return false;
    }

    private static Map<String, Object> packFlightMap(String info) {
        JSONArray array = JSONArray.parseArray(info);
        JSONObject tempPoint = array.getJSONObject(0);
        JSONObject location_point = tempPoint.getJSONObject("location_point");

        Map<String, Object> point = new HashMap<>();
        point.putAll(tempPoint);

        point.put("site", array.getJSONObject(0).getString("site"));
        point.put("aircraft", array.getJSONObject(0).getString("aircraft"));
        point.put("mode_s", array.getJSONObject(0).getString("mode_s"));
        point.put("lng", location_point.getDouble("lon"));
        point.put("lat", location_point.getDouble("lat"));

        point.remove("location_point");
        return point;
    }

    /**
     * @param startPoint:出发地
     * @param endPoint:目的地
     * @param currentFlightLoc:当前飞机位置
     * @param result:航段时间升序聚合的结果
     * @param countBlankThreshold:航段允许的间隔点
     * @return
     * @Description: TODO(根据航班的当前状态加载历史航线)
     */
    public static List<Map<String, Object>> flightHistoryCourseSegment(BoundPoint startPoint, BoundPoint endPoint, BoundPoint currentFlightLoc,

                                                                       List<String[]> result, int countBlankThreshold) {
        result = clearEmptyResult(result);

        List<Map<String, Object>> segment = new ArrayList<>();

        // 找到当前位置以前的航段（历史）
        Map<String, Object> splitAggFlightCourseMap = splitAggFlightCourse(currentFlightLoc, result);
        List<String[]> segmentList = (List<String[]>) splitAggFlightCourseMap.get("segmentList");
        int splitLoc = Integer.parseInt(String.valueOf(splitAggFlightCourseMap.get("splitLoc")));

        List<Map<String, Object>> historyCourse = flightCourseSegmentByAirport(segmentList, startPoint, countBlankThreshold);
        segment.addAll(historyCourse);

        // 找到当前位置以后的航段（未来）
        List<Map<String, Object>> futureCourse = flightCourseSegmentByAirportFuture(result.subList(splitLoc, result.size()), endPoint, countBlankThreshold);
        segment.addAll(0, futureCourse);
        return segment;
    }

    private static List<Map<String, Object>> flightCourseSegmentByAirportFuture(List<String[]> result, BoundPoint startPoint, int countBlankThreshold) {
        // 时间升序排列的轨迹

        // 没有获取到出发点
        if (startPoint == null) {
            List<List<Map<String, Object>>> mapList = AggsAnalyzer.flightCourseSegment(result, countBlankThreshold);
            return mapList.get(0);
        }

        // 航段加载
        List<Map<String, Object>> segment = new ArrayList<>();
        segment.add(packMapPoint(startPoint));

        double lastDis;
        double nextDis;

        Map<String, Object> lastPoint = null;
        Map<String, Object> nextPoint;

        int size = result.size();
        int countBlank = 0;

        for (int i = 0; i < size; i++) {
            String[] info = result.get(i);
            if (!"[]".equals(info[2]) && countBlank <= countBlankThreshold) {
                nextPoint = packFlightMap(info[2]);

                if (size - 1 == 0) {
                    segment.add(nextPoint);
                    break;
                } else {
                    if (lastPoint != null && !lastPoint.equals(nextPoint)) {

                        double latLast = Double.parseDouble(String.valueOf(lastPoint.get("lat")));
                        double lonLast = Double.parseDouble(String.valueOf(lastPoint.get("lng")));
                        lastDis = GeoDistance.PLANE.calculate(latLast, lonLast, startPoint.getLat(), startPoint.getLon(), GeoDistanceUnit.KILOMETERS);

                        double latNext = Double.parseDouble(String.valueOf(nextPoint.get("lat")));
                        double lonNext = Double.parseDouble(String.valueOf(nextPoint.get("lng")));
                        nextDis = GeoDistance.PLANE.calculate(latNext, lonNext, startPoint.getLat(), startPoint.getLon(), GeoDistanceUnit.KILOMETERS);

                        // 计算距离符合条件并且航线在时间上不连续的时候才跳出
                        if (lastDis < nextDis && !isTimeSeriesContinuityUp(result, i)) {
                            break;
                        } else {
                            segment.add(nextPoint);
                            lastPoint = nextPoint;
                        }

                    } else {
                        segment.add(nextPoint);
                        lastPoint = nextPoint;
                    }

                    // 空间隔被切断之后重新进行累加
                    countBlank = 0;
                }

            } else {
                countBlank++;
            }
        }
        return segment;
    }

    private static Map<String, Object> splitAggFlightCourse(BoundPoint currentFlightLoc, List<String[]> result) {
        Map<String, Object> resultMap = new HashMap<>();
        int splitLoc = 0;
        for (int i = 0; i < result.size(); i++) {
            String[] info = result.get(i);
            if (!"[]".equals(info[2])) {
                Map<String, Object> point = packFlightMap(info[2]);

                double lng = Double.parseDouble(String.valueOf(point.get("lng")));
                double lat = Double.parseDouble(String.valueOf(point.get("lat")));
                double currentLng = currentFlightLoc.getLon();
                double currentlLat = currentFlightLoc.getLat();
                if (lng == currentLng && lat == currentlLat) {
                    splitLoc = i;
                    break;
                }
            }
        }
        resultMap.put("splitLoc", splitLoc);
        if (splitLoc != 0) {
            resultMap.put("segmentList", result.subList(0, splitLoc));
        } else {
            resultMap.put("segmentList", result.subList(0, result.size()));
        }
        return resultMap;
    }

}



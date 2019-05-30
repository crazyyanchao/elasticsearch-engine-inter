package casia.isi.elasticsearch.operation.mapping;
/**
 * 　　　　　　　 ┏┓       ┏┓+ +
 * 　　　　　　　┏┛┻━━━━━━━┛┻┓ + +
 * 　　　　　　　┃　　　　　　 ┃
 * 　　　　　　　┃　　　━　　　┃ ++ + + +
 * 　　　　　　 █████━█████  ┃+
 * 　　　　　　　┃　　　　　　 ┃ +
 * 　　　　　　　┃　　　┻　　　┃
 * 　　　　　　　┃　　　　　　 ┃ + +
 * 　　　　　　　┗━━┓　　　 ┏━┛
 * ┃　　  ┃
 * 　　　　　　　　　┃　　  ┃ + + + +
 * 　　　　　　　　　┃　　　┃　Code is far away from     bug with the animal protecting
 * 　　　　　　　　　┃　　　┃ +
 * 　　　　　　　　　┃　　　┃
 * 　　　　　　　　　┃　　　┃　　+
 * 　　　　　　　　　┃　 　 ┗━━━┓ + +
 * 　　　　　　　　　┃ 　　　　　┣┓
 * 　　　　　　　　　┃ 　　　　　┏┛
 * 　　　　　　　　　┗┓┓┏━━━┳┓┏┛ + + + +
 * 　　　　　　　　　 ┃┫┫　 ┃┫┫
 * 　　　　　　　　　 ┗┻┛　 ┗┻┛+ + + +
 */

import casia.isi.elasticsearch.util.FileUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.*;

/**
 * @author YanchaoMa yanchaoma@foxmail.com
 * @PACKAGE_NAME: casia.isi.elasticsearch.operation.mapping
 * @Description: TODO(自动生成mapping映射文件)
 * @date 2019/5/24 13:58
 */
public class AutoMapping {

    /**
     * @param
     * @return
     * @Description: TODO(生成mapping之后写入到文件)
     */
    public void runFlushMappingToFile(String databaseName, String tableName, Connection con) throws IOException, SQLException {
        JSONObject object = run(databaseName, tableName, con);
        writeMappingToFile(object.toJSONString(), object.getString("mappingName"));
    }

    /**
     * @param databaseName:数据库名
     * @param tableName:表名
     * @param con:传入数据库连接
     * @return
     * @Description: TODO(自动生成mapping文件)
     */
    public JSONObject run(String databaseName, String tableName, Connection con) throws SQLException {
        String sql = "SELECT\n" +
                "    a.table_name TableName,\n" +
                "    a.table_comment TabelComment,\n" +
                "    b.COLUMN_NAME TableField,\n" +
                "    b.column_comment TableFieldComment,\n" +
                "    b.column_type TableFieldType,\n" +
                "    b.column_key TableFieldConstraint\n" +
                "FROM\n" +
                "    information_schema. TABLES a\n" +
                "LEFT JOIN information_schema. COLUMNS b ON a.table_name = b.TABLE_NAME\n" +
                "WHERE\n" +
                "    a.table_schema = '" + databaseName + "'\n" +
                "ORDER BY\n" +
                "    a.table_name";

        PreparedStatement pre = con.prepareStatement(sql);
        ResultSet result = pre.executeQuery();

        List<Map<String, Object>> mapList = new ArrayList<>();
        while (result.next()) {
            HashMap<String, Object> map = packSqlModelMap(result);
            if (tableName.equals(String.valueOf(map.get("TABLE_NAME")))) {
                mapList.add(map);
            }
        }
        sqlClose(con, pre, result);
        return fieldToMapping(mapList, databaseName, tableName);
    }

    /**
     * @param
     * @return
     * @Description: TODO(关闭查询对象)
     */
    private void sqlClose(Connection con, PreparedStatement pre, ResultSet result) throws SQLException {
        con.close();
        pre.close();
        result.close();
    }

    /**
     * @param mapList:当前表结构
     * @param databaseName:数据库名
     * @param tableName:表名
     * @return
     * @Description: TODO(数据库表字段映射到MAPPING)
     */
    private JSONObject fieldToMapping(List<Map<String, Object>> mapList, String databaseName, String tableName) {
        JSONObject mappingJsonObject = new JSONObject();

        // settings
        JSONObject settings = new JSONObject();
        settings.put("number_of_shards", 5);
        settings.put("number_of_replicas", 1);
        mappingJsonObject.put("settings", settings);

        // mappings
        JSONObject mappings = new JSONObject();
        JSONObject database = new JSONObject();
        database.put("dynamic", "false");
        JSONObject _source = new JSONObject();
        _source.put("enabled", true);
        database.put("_source", _source);
        database.put("properties", fieldsMapping(mapList));

        mappings.put(databaseName, database);
        mappingJsonObject.put("mappings", mappings);
        mappingJsonObject.put("mappingName", tableName + ".json");

        return mappingJsonObject;
    }

    /**
     * @param
     * @return
     * @Description: TODO(封装MAPPING)
     */
    private JSONObject fieldsMapping(List<Map<String, Object>> mapList) {
        JSONObject mapping = new JSONObject();
        for (int i = 0; i < mapList.size(); i++) {
            Map<String, Object> map = mapList.get(i);
            String fieldName = String.valueOf(map.get("COLUMN_NAME"));
            Object filedType = map.get("COLUMN_TYPE");
            mapping.put(fieldName, mapping(filedType));
        }
        return mapping;
    }

    /**
     * @param
     * @return
     * @Description: TODO(数据库字段类型对应es字段类型)
     */
    private JSONObject mapping(Object filedType) {
        String type = String.valueOf(filedType);
        JSONObject fieldMap = new JSONObject();
        if (type.contains("date") || type.contains("datetime") || type.contains("time") || type.contains("year") || type.contains("timestamp")) {
            fieldMap.put("format", "yyyy-MM-dd HH:mm:ss");
            fieldMap.put("type", "date");
            return fieldMap;

        } else if (type.contains("bigint")) {
            fieldMap.put("index", "not_analyzed");
            fieldMap.put("type", "long");
            return fieldMap;

        } else if (type.contains("int")) {
            fieldMap.put("index", "not_analyzed");
            fieldMap.put("type", "integer");
            return fieldMap;
        } else if (type.contains("varchar") || type.contains("char")) {
            fieldMap.put("index", "not_analyzed");
            fieldMap.put("type", "keyword");
            return fieldMap;
        } else if (type.contains("text")) {
            fieldMap.put("analyzer", "ik_max_word");
            fieldMap.put("search_analyzer", "ik_max_word");
            fieldMap.put("fielddata", true);
            fieldMap.put("type", "text");
            return fieldMap;
        } else {
            fieldMap.put("originalMysqlFieldType", type);
            return fieldMap;
        }
    }

    /**
     * @param
     * @return
     * @Description: TODO(MYSQL的查询结果封装为MAP返回)
     */
    private HashMap<String, Object> packSqlModelMap(ResultSet result) throws SQLException {
        /**
         * {
         *   "TABLE_COMMENT": "facebook信息表",
         *   "TABLE_NAME": "facebook_blog",
         *   "COLUMN_NAME": "auto_id",
         *   "COLUMN_COMMENT": "自增ID",
         *   "COLUMN_KEY": "PRI",
         *   "COLUMN_TYPE": "bigint(20)"
         * }
         * **/
        HashMap<String, Object> map = new HashMap<>();
        int columnNum = result.getMetaData().getColumnCount();
        for (int i = 1; i <= columnNum; i++) {
            Object columnValue = result.getObject(i);
            String columnName = result.getMetaData().getColumnName(i);
            map.put(columnName, columnValue);
        }
        return map;
    }

    /**
     * @param
     * @return
     * @Description: TODO(将mapping写入到文件)
     */
    private void writeMappingToFile(String mapping, String filename) throws IOException {
        File dir = new File("mapping");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, filename);
        FileWriter writer = new FileWriter(file, true);
        writer.write(mapping + "\r\n");
        writer.close();
    }

    /**
     * @param mapping1:mapping1文件路径 (程序自动生成的mapping)
     * @param mapping2:mapping2文件路径 (es集群自动生成的mapping)
     * @return
     * @Description: TODO(查找mapping - 2与mapping - 1不一样的字段中不一样的字段)
     */
    public JSONObject analysisMappingJson2To1(String mapping1, String mapping2) {
        JSONObject mappingOne = JSONObject.parseObject(FileUtil.readAllLine(mapping1, "UTF-8"));
        JSONObject mappingTwo = JSONObject.parseObject(FileUtil.readAllLine(mapping2, "UTF-8"));
        JSONArray mappingOneArray = tansferFieldArray(mappingOne);
        JSONArray mappingTwoArray = tansferFieldArray(mappingTwo);
        JSONObject message = new JSONObject();
        message.put("mappingOneFields", mappingOneArray);
        message.put("mappingTwoFields", mappingTwoArray);
        message.put("diffrenceAndMappingOneLack", getDifferenceFields(mappingOneArray, mappingTwoArray));
        return message;
    }

    /**
     * @param
     * @return
     * @Description: TODO(拿出不在原始mapping的字段)
     */
    private JSONArray getDifferenceFields(JSONArray mappingOneArray, JSONArray mappingTwoArray) {
        JSONArray array = new JSONArray();
        for (int i = 0; i < mappingTwoArray.size(); i++) {
            String field = mappingTwoArray.getString(i);
            if (!mappingOneArray.contains(field)) {
                array.add(field);
            }
        }
        return array;
    }

    /**
     * @param
     * @return
     * @Description: TODO(拿出MAPPING文件中的所有字段)
     */
    private JSONArray tansferFieldArray(JSONObject mapping) {
        JSONObject mappings = mapping.getJSONObject("mappings");
        for (Map.Entry entry : mappings.entrySet()) {
            JSONObject object = (JSONObject) entry.getValue();
            JSONObject properties = object.getJSONObject("properties");
            return getFieldsArray(properties);
        }
        return null;
    }

    /**
     * @param properties:字段配置列表
     * @return
     * @Description: TODO(拿出mapping配置的字段)
     */
    private JSONArray getFieldsArray(JSONObject properties) {
        Set<String> sets = properties.keySet();
        return JSONArray.parseArray(JSON.toJSONString(sets));
    }

}

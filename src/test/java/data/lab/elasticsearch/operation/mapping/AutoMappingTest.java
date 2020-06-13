package data.lab.elasticsearch.operation.mapping;

import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: casia.isi.elasticsearch.operation.mapping
 * @Description: TODO(自动生成mapping工具)
 * @date 2019/5/24 14:37
 */
public class AutoMappingTest {

    @Test
    public void run() {
        AutoMapping autoMapping = new AutoMapping();
        String dbName = "databases";
        String tableName = "tb_linkedin_volunteers";
        String url = "jdbc:mysql://localhost/" + dbName + "?user=root&password=123456&useUnicode=true&characterEncoding=8859_1";
        try {
            Connection con = DriverManager.getConnection(url);
            System.out.println(autoMapping.run(dbName, tableName, con));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void runFlushMappingToFile() {
        AutoMapping autoMapping = new AutoMapping();
        String dbName = "databases";
        String tableName = "tb_linkedin_volunteers";
        String url = "jdbc:mysql://localhost/" + dbName + "?user=root&password=123456&useUnicode=true&characterEncoding=8859_1";
        try {
            Connection con = DriverManager.getConnection(url);
            autoMapping.runFlushMappingToFile(dbName, tableName, con);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void analysisMappingJson2To1() {
        AutoMapping autoMapping = new AutoMapping();
        String mapping1 = "mapping/tb_linkedin_volunteers.json";
        String mapping2 = "mapping/tb_linkedin_volunteers2.json";
        System.out.println(autoMapping.analysisMappingJson2To1(mapping1, mapping2));
    }

}


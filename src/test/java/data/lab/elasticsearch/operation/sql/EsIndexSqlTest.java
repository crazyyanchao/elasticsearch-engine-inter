package data.lab.elasticsearch.operation.sql;

import org.junit.Test;
/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @PACKAGE_NAME: casia.isi.elasticsearch.operation.sql
 * @Description: TODO(SQL- TEST)
 * @author Yc-Ma
 * @date 2019/6/28 16:50
 *
 *
 */

public class EsIndexSqlTest {
    @Test
    public void sql() {
        EsIndexSql esIndexSql = new EsIndexSql("192.168.12.109:9210");
        esIndexSql.queryBySql("select * from news_all limit 10");
        esIndexSql.getResults();
    }
}
package data.lab.elasticsearch.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
/*
 *
 * Data Lab - graph database organization.
 *
 */

/**
 * @author Yc-Ma
 * @PACKAGE_NAME: casia.isi.elasticsearch.util
 * @Description: TODO(FILE STORE DESCRIPTION)
 * @date 2019/12/2 14:46
 */
public class FileUtilTest {

    @Test
    public void name() {
        System.out.println(FileUtil.convertFileSizeDescription(826434856385L));
    }

    @Test
    public void name_2() {
       String string = FileUtil.readAllLine("data/ids/10-43_10-44.txt","UTF-8");
        JSONArray array = JSONArray.parseArray(string);
        StringBuilder builder = new StringBuilder();
        for ( Object obj: array) {
            JSONObject object = (JSONObject) obj;
            JSONObject idObj = object.getJSONObject("_source");
            String id = idObj.getString("id");
            builder.append(id+",");
        }
        System.out.println(builder.toString());
    }

}


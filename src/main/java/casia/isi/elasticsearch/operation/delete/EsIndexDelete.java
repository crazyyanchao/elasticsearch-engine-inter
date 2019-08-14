package casia.isi.elasticsearch.operation.delete;

import casia.isi.elasticsearch.common.FieldOccurs;
import casia.isi.elasticsearch.common.RangeOccurs;
import casia.isi.elasticsearch.util.Validator;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;

public class EsIndexDelete extends EsIndexDeleteImp {

    private Logger logger = Logger.getLogger(EsIndexDelete.class);

    @Deprecated
    public EsIndexDelete(String ip, String port, String indexName, String typeName) {
        super(ip, port, indexName, typeName);
    }

    public EsIndexDelete(String ipAndport, String indexName, String typeName) {
        super(ipAndport, indexName, typeName);
    }


    public String getQueryUrl() {
        return super.delete_url;
    }

    public String getQueryString() {
        return super.queryJson.toJSONString();
    }

    public String getQueryReslut() {
        return super.queryResult;
    }

    /**
     * 筛选某区间内的数据，筛选的字段必须为数字形式。 如时间、 id、 评论数等
     *
     * @param field       筛选的字段
     * @param value       区间结束值
     * @param occurs      是否必须作为过滤条件 一般为must
     * @param rangeOccurs 选择过滤方式（大于/大于等于/小于/小于等于）
     */
    public void addRangeTerms(String field, String value, FieldOccurs occurs, RangeOccurs rangeOccurs) {
        if (!Validator.check(value) && !Validator.check(value)) {
            return;
        }
        JSONObject fieldJson = new JSONObject();
        if (Validator.check(value)) {
            //大于等于
            fieldJson.put(rangeOccurs.getSymbolValue(), value);
        }
        JSONObject json = new JSONObject();
        JSONObject rangejson = new JSONObject();
        json.put(field, fieldJson);
        rangejson.put("range", json);
        if (occurs.equals(FieldOccurs.MUST)) {
            if (!super.queryFilterMustJarr.contains(rangejson)) {
                super.queryFilterMustJarr.add(rangejson);
            }
        } else if (occurs.equals(FieldOccurs.MUST_NOT)) {
            if (!super.queryFilterMustNotJarr.contains(rangejson)) {
                super.queryFilterMustNotJarr.add(rangejson);
            }
        }
    }

    /**
     * @param bool：true表示:立即刷新主分片和副分片 false:表示不刷新 不设置此条件默认不刷新 wait_for:使用集群自动刷新机制（默认1s，在索引级自定义5s或者其它值根据业务决定）
     * @return
     * @Description: TODO(设置自动刷新 - 根据数据数据分片组ID删除数据之后是否刷新shards)
     */
    public void setRefresh(String bool) {
        super.deleteParameters = super.deleteParameters + "?refresh=" + bool + "";
    }

    /**
     * @param scrollSize:批量处理的数据量
     * @return
     * @Description: TODO(自上而下批量scrollSize条数据)
     */
    public void setScrollSize(int scrollSize) {
        super.deleteParameters = super.deleteParameters + "?scroll_size=" + scrollSize + "";
    }

    /**
     * @param proceed:配置参数
     * @return
     * @Description: TODO(执行批量删除的时候 ， 可能会发生版本冲突 - 统计数据版本冲突的数量 ， 并且继续执行)
     */
    public void conflictsProceed(String proceed) {
        super.deleteParameters = super.deleteParameters + "?conflicts=" + proceed + "";
    }


    /**
     * @param //parameters:设置force merge时的配置参数
     * @return
     * @Description: TODO(释放磁盘空间)
     */
    public String forceMerge() {
        String url = this.deleteUrl + "/" + this.IndexName + "/_forcemerge?only_expunge_deletes=true&max_num_segments=1&flush=true";
        System.out.println("_forcemerge:" + url);
        return super.httpRequest.httpPost(url, "");
    }

    /**
     * @param isWaitResponse:是否等待响应
     * @return
     * @Description: TODO(是否在集群中以后台任务的形式执行删除)
     */
    public void setWaitForCompletion(boolean isWaitResponse) {
        super.deleteParameters = super.deleteParameters + "?wait_for_completion=" + isWaitResponse + "";
    }

    /**
     * @param lastTaskId:任务ID
     * @return
     * @Description: TODO(通过任务ID输出任务信息)
     */
    public String outputLastTaskInfo(String lastTaskId) {
        String url = this.deleteUrl + "/_tasks/" + lastTaskId + "";
        System.out.println("_tasks:" + url);
        return super.httpRequest.httpGet(url);
    }

}




package data.lab.elasticsearch.operation.index;

import data.lab.elasticsearch.operation.http.HttpSymbol;

/**
 * ElasticSearch的索引创建接口(Http方式)
 *
 * @author
 * @version elasticsearch - 5.6.3
 */
public class EsIndexCreat extends EsIndexCreatImp {

    @Deprecated
    public EsIndexCreat() {
        super();
    }

    public EsIndexCreat(HttpSymbol httpPoolName, String ipPorts, String indexName, String typeName) {
        super(httpPoolName,ipPorts,indexName,typeName);
    }

    @Deprecated
    public EsIndexCreat(String IP, int Port, String indexName, String typeName) {
        super(IP, Port, indexName, typeName);
    }

    public EsIndexCreat(String IPADRESS, String indexName, String typeName) {
        super(IPADRESS, indexName, typeName);
    }

}

package data.lab.elasticsearch.operation.sql;

import data.lab.elasticsearch.operation.http.HttpSymbol;

public class EsIndexSql extends EsIndexSqlImp {

    @Deprecated
    public EsIndexSql() {
        super();
    }

    public EsIndexSql(String IPADRESS) {
        super(IPADRESS);
    }

    @Deprecated
    public EsIndexSql(String IP, int Port) {
        super(IP, Port);
    }

    public EsIndexSql(HttpSymbol httpPoolName, String ipPorts) {
        super(httpPoolName,ipPorts);
    }
}

package casia.isi.elasticsearch.operation.sql;

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
}

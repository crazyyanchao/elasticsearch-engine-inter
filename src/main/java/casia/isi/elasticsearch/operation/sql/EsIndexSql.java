package casia.isi.elasticsearch.operation.sql;

public class EsIndexSql extends EsIndexSqlImp{
	public EsIndexSql() {
		super();
	}
	public EsIndexSql(String IPADRESS) {
		super(IPADRESS);
	}
	public EsIndexSql(String IP, int Port ){
		super( IP , Port );
	}
}

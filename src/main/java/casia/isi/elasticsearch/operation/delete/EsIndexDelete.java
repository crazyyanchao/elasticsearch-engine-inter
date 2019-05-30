package casia.isi.elasticsearch.operation.delete;

import org.apache.log4j.Logger;

public class EsIndexDelete extends EsIndexDeleteImp{

	private Logger logger = Logger.getLogger(EsIndexDelete.class);

	public EsIndexDelete(String ip,String port,String indexName,String typeName){
		super(ip, port, indexName, typeName);
	}

	public EsIndexDelete(String ipAndport,String indexName,String typeName){
		super(ipAndport, indexName, typeName);
	}


}

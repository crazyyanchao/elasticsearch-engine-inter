package casia.isi.elasticsearch.operation.update;

import org.apache.log4j.Logger;

import casia.isi.elasticsearch.operation.search.EsIndexSearch;

/**
 * 
 * @author wzy
 * @version 5.6.3
 */
public class EsIndexUpdate extends EsIndexUpdateImp{
	private static Logger logger = Logger.getLogger(EsIndexSearch.class);
	public  EsIndexUpdate ( String IP, int Port , String indexName , String typeName ){
		super( IP, Port , indexName ,  typeName  );
	}
	public  EsIndexUpdate ( String IpPort , String indexName , String typeName ){
		super(IpPort, indexName, typeName);
	}
}

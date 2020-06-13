package data.lab.elasticsearch.common;

public enum SortOrder {
	/**
	 * 正序
	 */
	ASC ( "asc" ),
	/**
	 * 倒序
	 */
	DESC ( "desc" );
	
	private String symbol;
	private SortOrder(String symbol) {
		this.symbol = symbol;
	}
	public String getSymbolValue() {
		return this.symbol;
	}
}

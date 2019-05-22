package casia.isi.elasticsearch.common;

/**
 * 枚举类，字段出现情况
 * @author wzy
 *
 */
public enum FieldOccurs {
	/**
	 * 必须出现
	 */
	MUST("+"),
	/**
	 * 必须不能出现
	 */
	MUST_NOT("-");
	
	private String symbol;

	private FieldOccurs(String symbol) {
		this.symbol = symbol;
	}

	public String getSymbolValue() {
		return this.symbol;
	}
}
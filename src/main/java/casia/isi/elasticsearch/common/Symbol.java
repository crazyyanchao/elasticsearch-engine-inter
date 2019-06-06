package casia.isi.elasticsearch.common;
/**
 * 枚举类型,符号
 * @author
 *
 */
public enum Symbol {
	SPACE_CHARACTER("&");
	
	private String symbol;
	private Symbol(String symbol) {
		this.symbol = symbol;
	}
	public String getSymbolValue() {
		return this.symbol;
	}
}

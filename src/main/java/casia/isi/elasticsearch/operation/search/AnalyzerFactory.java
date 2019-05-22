package casia.isi.elasticsearch.operation.search;

import org.wltea.analyzer.lucene.IKAnalyzer;

public class AnalyzerFactory {
	private static IKAnalyzer analyzerMax = new IKAnalyzer(true);
	private static IKAnalyzer analyzerMin = new IKAnalyzer(false);

	private AnalyzerFactory() {
	}

	public static IKAnalyzer getInstanceMax() {
		return analyzerMax;
	}

	public static IKAnalyzer getInstanceMin() {
		return analyzerMin;
	}
}

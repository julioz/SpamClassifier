package br.ufrj.ad.spamclassifier.model.test;

import java.util.List;

public abstract class BaseTestSet {
	
	public abstract Double executeForFeature(String feature);
	public abstract Double executeForFeatures(List<String> features);
}

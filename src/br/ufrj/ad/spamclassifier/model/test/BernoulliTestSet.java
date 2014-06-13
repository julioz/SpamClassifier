package br.ufrj.ad.spamclassifier.model.test;

import java.util.ArrayList;
import java.util.List;

import br.ufrj.ad.spamclassifier.model.Email;
import br.ufrj.ad.spamclassifier.model.training.BernoulliTrainingSet;
import br.ufrj.ad.spamclassifier.model.training.BernoulliTrainingSet.Classification;

public class BernoulliTestSet extends BaseTestSet {

	private BernoulliTrainingSet mTrainingSet;
	private ArrayList<Email> mTestList;

	public BernoulliTestSet(BernoulliTrainingSet trainingSet, ArrayList<Email> testList) {
		this.mTrainingSet = trainingSet;
		this.mTestList = testList;
	}
	
	private Double[] getBayesProbabilities(List<String> words, Email email) {
		Double[] probs = new Double[2];
		
		Double probSpamGivenFeatures = getSumOfLogsForProbabilities(Classification.SPAM, words, email);
		Double probHamGivenFeatures = getSumOfLogsForProbabilities(Classification.HAM, words, email);
		
		// there is no need to divide here since we're interested in which is greater
		Double probabilitySpam = (probSpamGivenFeatures * BernoulliTrainingSet.PROBABILITY_SPAM);
		Double probabilityHam = (probHamGivenFeatures * BernoulliTrainingSet.PROBABILITY_HAM);
		
		probs[BernoulliTrainingSet.Classification.SPAM.ordinal()] = probabilitySpam;
		probs[BernoulliTrainingSet.Classification.HAM.ordinal()] = probabilityHam;
		
		return probs;
	}
	
	private int isClassificationCorrect(Double[] probs, Email email) {
		Double probabilitySpam = probs[BernoulliTrainingSet.Classification.SPAM.ordinal()];
		Double probabilityHam = probs[BernoulliTrainingSet.Classification.HAM.ordinal()];

		if (email.isSpam()) {
			if (probabilitySpam >= probabilityHam) {
				return 1;
			}
		} else {
			if (probabilitySpam <= probabilityHam) {
				return 1;
			}
		}
		
		return 0;
	}
	
	@Override
	public Double executeForFeature(String feature) {
		List<String> words = new ArrayList<String>();
		words.add(feature.toString());

		Integer emailsClassifiedCorrectly = 0;
		Integer emailsContainingFeature = 0;
		for (Email email : mTestList) {
			if (email.getFeatureFrequency(feature.toString()) > 0) {
				emailsContainingFeature++;
			} else {
				continue;
			}
			
			Double[] probs = getBayesProbabilities(words, email);
			emailsClassifiedCorrectly += isClassificationCorrect(probs, email);
		}
		
		return (double) emailsClassifiedCorrectly / emailsContainingFeature;
	}
	
	@Override
	public Double executeForFeatures(List<String> words) {
		if (words.size() == 1) {
			return executeForFeature(words.get(0).toString());
		}
		
		Integer emailsClassifiedCorrectly = 0;
		for (Email email : mTestList) {
			Double[] probs = getBayesProbabilities(words, email);
			emailsClassifiedCorrectly += isClassificationCorrect(probs, email);
		}
		
		return (double) emailsClassifiedCorrectly / mTestList.size();
	}

	private Double getSumOfLogsForProbabilities(Classification classification, List<String> words, Email email) {
		Double product = Math.log(mTrainingSet.getProbability(classification, words.get(0), email));
		for (int i = 1; i < words.size(); i++) {
			product += Math.log(mTrainingSet.getProbability(classification, words.get(i), email));
		}
		return product;
	}
}

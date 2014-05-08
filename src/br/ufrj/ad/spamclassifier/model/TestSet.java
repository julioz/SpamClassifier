package br.ufrj.ad.spamclassifier.model;

import java.util.ArrayList;
import java.util.List;

import br.ufrj.ad.spamclassifier.model.TrainingSet.Classification;

public class TestSet {

	private TrainingSet mTrainingSet;
	private ArrayList<Email> mTestList;

	public TestSet(TrainingSet trainingSet, ArrayList<Email> testList) {
		this.mTrainingSet = trainingSet;
		this.mTestList = testList;
	}
	
	private Double[] getProbabilities(List<String> words, Email email) {
		Double[] probs = new Double[2];
		
		Double probSpamGivenFeatures = getSumOfLogsForProbabilities(Classification.SPAM, words, email);
		Double probHamGivenFeatures = getSumOfLogsForProbabilities(Classification.HAM, words, email);
		
		// double probSpam = (probSpamGivenFeatures * TrainingSet.PROBABILITY_SPAM) + (probHamGivenFeatures * TrainingSet.PROBABILITY_HAM);
		Double probabilitySpam = (probSpamGivenFeatures * TrainingSet.PROBABILITY_SPAM); /* / probSpam; */ // there is no need to divide here since we're interested in which is greater
		Double probabilityHam = (probHamGivenFeatures * TrainingSet.PROBABILITY_HAM); /* / probSpam; */ // same as above :-)
		
		probs[TrainingSet.Classification.SPAM.ordinal()] = probabilitySpam;
		probs[TrainingSet.Classification.HAM.ordinal()] = probabilityHam;
		
		return probs;
	}
	
	private int isClassificationCorrect(Double[] probs, Email email) {
		Double probabilitySpam = probs[TrainingSet.Classification.SPAM.ordinal()];
		Double probabilityHam = probs[TrainingSet.Classification.HAM.ordinal()];

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
			
			Double[] probs = getProbabilities(words, email);
			emailsClassifiedCorrectly += isClassificationCorrect(probs, email);
		}
		
		return (double) emailsClassifiedCorrectly / emailsContainingFeature;
	}
	
	public Double executeForFeatures(List<String> words) {
		if (words.size() == 1) {
			return executeForFeature(words.get(0).toString());
		}
		
		Integer emailsClassifiedCorrectly = 0;
		for (Email email : mTestList) {
			Double[] probs = getProbabilities(words, email);
			emailsClassifiedCorrectly += isClassificationCorrect(probs, email);
		}
		
		// System.out.println("========================================");
		
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

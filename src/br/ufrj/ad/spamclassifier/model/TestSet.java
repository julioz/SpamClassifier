package br.ufrj.ad.spamclassifier.model;

import java.util.ArrayList;
import java.util.List;

import br.ufrj.ad.spamclassifier.model.TrainingSet.Classification;
import br.ufrj.ad.spamclassifier.model.TrainingSet.FeatureType;

public class TestSet {

	private TrainingSet mTrainingSet;
	private ArrayList<Email> mTestList;

	public TestSet(TrainingSet trainingSet, ArrayList<Email> testList) {
		this.mTrainingSet = trainingSet;
		this.mTestList = testList;
	}
	
	public Float executeForFeature(String feature) {
		Integer emailsClassifiedCorrectly = 0;
		Integer emailsContainingFeature = 0;
		
		List<String> words = new ArrayList<String>();
		words.add(feature.toString());

		for (Email email : mTestList) {
			if (feature.equals(FeatureType.AVG_UNINT_CAPT.toString()) ||
					feature.equals(FeatureType.LNGST_UNINT_CAPT.toString()) ||
					feature.equals(FeatureType.NUM_CAPT.toString())) {
				emailsContainingFeature++;
			} else {
				// WORD or CHAR
				if (email.getFeatureFrequency(feature.toString()) > 0) {
					emailsContainingFeature++;
				} else {
					continue;
				}
			}
			
			Float probSpamGivenFeatures = getProductOfProbabilities(Classification.SPAM, words, email);
			Float probHamGivenFeatures = getProductOfProbabilities(Classification.HAM, words, email);
			Float probabilitySpam = (probSpamGivenFeatures * TrainingSet.PROBABILITY_SPAM) / ((probSpamGivenFeatures * TrainingSet.PROBABILITY_SPAM) + (probHamGivenFeatures * TrainingSet.PROBABILITY_HAM));
			Float probabilityHam = (probHamGivenFeatures * TrainingSet.PROBABILITY_HAM) / ((probSpamGivenFeatures * TrainingSet.PROBABILITY_SPAM) + (probHamGivenFeatures * TrainingSet.PROBABILITY_HAM));

			if (email.isSpam()) {
				if (probabilitySpam >= probabilityHam) {
					emailsClassifiedCorrectly++;
				}
			} else {
				if (probabilitySpam <= probabilityHam) {
					emailsClassifiedCorrectly++;
				}
			}
		}
		
		return (float) emailsClassifiedCorrectly / emailsContainingFeature;
	}
	
	/**
	 * return accuracy
	 * @param featureType
	 * @param words
	 * @return
	 */
	public Float executeForFeatures(List<String> words) {
		if (words.size() == 1) return executeForFeature(words.get(0).toString());
		
		Integer emailsClassifiedCorrectly = 0;
		Integer emailsContainingAllFeatures = 0;
		
		for (Email email : mTestList) {
			int containedFeatures = 0;
			for (String word : words) {
				if (word.equals(FeatureType.AVG_UNINT_CAPT.toString())
						|| word.equals(FeatureType.LNGST_UNINT_CAPT.toString())
						|| word.equals(FeatureType.NUM_CAPT.toString())) {
					containedFeatures++;
				} else {
					if (email.getFeatureFrequency(word) > 0) {
						containedFeatures++;
					}
				}
			}
			
			// TODO not correct. We are currently evaluating only the emails that match
			// all the features asked... but almost no email in our dataset actually
			// has all those features... so, how can we execute the classifier for
			// more than a feature at once? Should we ignore features if they're not present or
			// count them in in both ways...?
			if (containedFeatures == words.size()) {
				emailsContainingAllFeatures++;
			} else {
				continue;
			}
			
			Float probSpamGivenFeatures = getProductOfProbabilities(Classification.SPAM, words, email);
			Float probHamGivenFeatures = getProductOfProbabilities(Classification.HAM, words, email);
			Float probabilitySpam = (probSpamGivenFeatures * TrainingSet.PROBABILITY_SPAM) / ((probSpamGivenFeatures * TrainingSet.PROBABILITY_SPAM) + (probHamGivenFeatures * TrainingSet.PROBABILITY_HAM));
			Float probabilityHam = (probHamGivenFeatures * TrainingSet.PROBABILITY_HAM) / ((probSpamGivenFeatures * TrainingSet.PROBABILITY_SPAM) + (probHamGivenFeatures * TrainingSet.PROBABILITY_HAM));

			if (email.isSpam()) {
				if (probabilitySpam >= probabilityHam) {
					emailsClassifiedCorrectly++;
				}
			} else {
				if (probabilitySpam <= probabilityHam) {
					emailsClassifiedCorrectly++;
				}
			}
		}
		System.out.println(emailsClassifiedCorrectly + " / " + emailsContainingAllFeatures);
		
		return (float) emailsClassifiedCorrectly / emailsContainingAllFeatures;
	}

	private float getProductOfProbabilities(Classification classification, List<String> words, Email email) {
		float product = mTrainingSet.getProbability(classification, words.get(0), email);
		for (int i = 1; i < words.size(); i++) {
			product *= mTrainingSet.getProbability(classification, words.get(i), email);
		}
		return product;
	}
}

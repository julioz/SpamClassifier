package br.ufrj.ad.spamclassifier.model.test;

import java.util.ArrayList;
import java.util.List;

import br.ufrj.ad.spamclassifier.model.Email;
import br.ufrj.ad.spamclassifier.model.training.BernoulliTrainingSet.Classification;
import br.ufrj.ad.spamclassifier.model.training.GaussianTrainingSet;

public class GaussianTestSet extends BaseTestSet {

	private GaussianTrainingSet mTrainingSet;
	private ArrayList<Email> mTestList;

	public GaussianTestSet(GaussianTrainingSet trainingSet,
			ArrayList<Email> testList) {
		mTrainingSet = trainingSet;
		mTestList = testList;
	}

	public Double executeForFeature(String feature) {
		Double probSpam = mTrainingSet.getSpamProbability();
		Double probHam = mTrainingSet.getHamProbability();

		double correctGuesses = 0;
		for (Email email : mTestList) {
			Double probabilityOfFeatureGivenSpam = getProbabilityOfFeatureGivenClassification(email, feature, Classification.SPAM);
			Double probabilityOfFeatureGivenHam = getProbabilityOfFeatureGivenClassification(email, feature, Classification.HAM);
			
			Double probabilityOfSpamGivenFeature = getBayesResult(probabilityOfFeatureGivenSpam, probSpam, probHam);
			Double probabilityOfHamGivenFeature = getBayesResult(probabilityOfFeatureGivenHam, probSpam, probHam);
			
			if (probabilityOfSpamGivenFeature >= probabilityOfHamGivenFeature) {
				// classified as spam
				if (email.isSpam()) {
					correctGuesses++;
				}
			} else {
				// classified as ham
				if (!email.isSpam()) {
					correctGuesses++;
				}
			}
		}

		double accuracy =  correctGuesses / mTestList.size();
		return accuracy;
	}
	
	@Override
	public Double executeForFeatures(List<String> features) {
		if (features.size() == 1) {
			return executeForFeature(features.get(0).toString());
		}
		
		Double probSpam = mTrainingSet.getSpamProbability();
		Double probHam = mTrainingSet.getHamProbability();
		double correctGuesses = 0;
		for (Email email : mTestList) {
			Double probabilityOfFeaturesGivenSpam = getProbabilityOfFeaturesGivenClassification(email, features, Classification.SPAM);
			Double probabilityOfFeaturesGivenHam = getProbabilityOfFeaturesGivenClassification(email, features, Classification.HAM);
			
			Double probabilityOfSpamGivenFeatures = getBayesResult(probabilityOfFeaturesGivenSpam, probSpam, probHam);
			Double probabilityOfHamGivenFeatures = getBayesResult(probabilityOfFeaturesGivenHam, probSpam, probHam);
			
			if (probabilityOfSpamGivenFeatures >= probabilityOfHamGivenFeatures) {
				// classified as spam
				if (email.isSpam()) {
					correctGuesses++;
				}
			} else {
				// classified as ham
				if (!email.isSpam()) {
					correctGuesses++;
				}
			}
		}

		double accuracy =  correctGuesses / mTestList.size();
		return accuracy;
	}
	
	public Double getBayesResult(Double probBGivenA, Double probA, Double probB) {
		return probBGivenA * (probA / probB);
	}
	
	private Double getProbabilityOfFeaturesGivenClassification(Email email,
			List<String> features, Classification classification) {
		// reference: http://blog.smola.org/post/987977550/log-probabilities-semirings-and-floating-point-numbers
		Double product = Math.log(getProbabilityOfFeatureGivenClassification(email, features.get(0), classification));
		for (int i = 1; i < features.size(); i++) {
			product += Math.log(getProbabilityOfFeatureGivenClassification(email, features.get(i), classification));
		}
		return product;
	}
	
	public Double getProbabilityOfFeatureGivenClassification(Email email, String feature, Classification classification) {
		Double x, mu, sigma;

		x = (double) email.getFeatureFrequency(feature);
		mu = mTrainingSet.getAverage(feature, classification);
		sigma = mTrainingSet.getDeviation(feature, classification);
		
		return phi(x, mu, sigma);
	}
	
	// return phi(x) = standard Gaussian pdf
	private double phi(double x) {
		return Math.exp(-x * x / 2) / Math.sqrt(2 * Math.PI);
	}

	// return phi(x, mu, signma) = Gaussian pdf with mean mu and stddev sigma
	private double phi(double x, double mu, double sigma) {
		return phi((x - mu) / sigma) / sigma;
	}
}

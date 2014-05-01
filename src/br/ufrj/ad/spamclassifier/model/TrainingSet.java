package br.ufrj.ad.spamclassifier.model;

import java.util.Collection;
import java.util.HashMap;

public class TrainingSet {
	
	// set to true to see prints
	private final static boolean DEBUG = false;
	
	private final static float PROBABILITY_SPAM = 0.5f;
	private final static float PROBABILITY_HAM = 0.5f;

	public enum Classification {
		SPAM, HAM;
	}
	
	private Collection<String> mWords;
	private Collection<String> mCharacters;
	private Collection<? extends Email> mEmails;
	private HashMap<String, Float> mSpamProbability;
	private HashMap<String, Float> mHamProbability;
	private HashMap<Classification, Float> mAvgUnintCaptsBounds;
	private HashMap<Classification, Float> mLngstUnintCaptsLenBounds;
	private HashMap<Classification, Float> mCaptsNumBounds;

	public TrainingSet(Collection<String> words, Collection<String> characters,
			Collection<? extends Email> emails) {
		this.mWords = words;
		this.mCharacters = characters;
		this.mEmails = emails;
		
		this.mSpamProbability = new HashMap<String, Float>();
		this.mHamProbability = new HashMap<String, Float>();
		
		init();
	}

	private void init() {
		calcProbabilityMapForFeatures(mWords);
		calcProbabilityMapForFeatures(mCharacters);
		mAvgUnintCaptsBounds = getRatiosForAverageUninterruptedCapitals();
		mLngstUnintCaptsLenBounds = getRatiosForLongestUninterruptedCapitalsLength();
		mCaptsNumBounds = getRatiosForCapitalsNumber();
	}

	private HashMap<Classification, Float> getRatiosForCapitalsNumber() {
		Integer numSpamsInSet = 0;
		Integer numHamsInSet = 0;
		
		Integer totalCapitalsInSpams = 0;
		Integer totalCapitalsInHams = 0;

		for (Email email : mEmails) {
			if (email.isSpam()) {
				numSpamsInSet++;

				totalCapitalsInSpams += email.getNumberOfCapitals();
			} else {
				numHamsInSet++;
				
				totalCapitalsInHams += email.getNumberOfCapitals();
			}
		}
		
		float spamsRatio = (float) totalCapitalsInSpams / numSpamsInSet;
		float hamsRatio = (float) totalCapitalsInHams / numHamsInSet;
		
		HashMap<Classification, Float> ratios = new HashMap<Classification, Float>();
		ratios.put(Classification.SPAM, spamsRatio);
		ratios.put(Classification.HAM, hamsRatio);
		return ratios;
	}

	private HashMap<Classification, Float> getRatiosForLongestUninterruptedCapitalsLength() {
		Integer numSpamsInSet = 0;
		Integer numHamsInSet = 0;
		
		Integer totalLngstUnintCapitalsInSpams = 0;
		Integer totalLngstUnintCapitalsInHams = 0;

		for (Email email : mEmails) {
			if (email.isSpam()) {
				numSpamsInSet++;

				totalLngstUnintCapitalsInSpams += email.getLongestUninterruptedCapitalsLength();
			} else {
				numHamsInSet++;
				
				totalLngstUnintCapitalsInHams += email.getLongestUninterruptedCapitalsLength();
			}
		}
		
		float spamsRatio = (float) totalLngstUnintCapitalsInSpams / numSpamsInSet;
		float hamsRatio = (float) totalLngstUnintCapitalsInHams / numHamsInSet;
		
		HashMap<Classification, Float> ratios = new HashMap<Classification, Float>();
		ratios.put(Classification.SPAM, spamsRatio);
		ratios.put(Classification.HAM, hamsRatio);
		return ratios;
	}

	private HashMap<Classification, Float> getRatiosForAverageUninterruptedCapitals() {
		Integer numSpamsInSet = 0;
		Integer numHamsInSet = 0;
		
		Float totalAvgUnintCapitalsInSpams = 0.0f;
		Float totalAvgUnintCapitalsInHams = 0.0f;

		for (Email email : mEmails) {
			if (email.isSpam()) {
				numSpamsInSet++;

				totalAvgUnintCapitalsInSpams += email.getAvgUninterruptedCapitals();
			} else {
				numHamsInSet++;
				
				totalAvgUnintCapitalsInHams += email.getAvgUninterruptedCapitals();
			}
		}
		
		float spamsRatio = (float) totalAvgUnintCapitalsInSpams / numSpamsInSet;
		float hamsRatio = (float) totalAvgUnintCapitalsInHams / numHamsInSet;
		
		HashMap<Classification, Float> ratios = new HashMap<Classification, Float>();
		ratios.put(Classification.SPAM, spamsRatio);
		ratios.put(Classification.HAM, hamsRatio);
		return ratios;
	}

	private void calcProbabilityMapForFeatures(Collection<String> features) {
		for (String feature : features) {
			Integer spamsContainingFeature = 0;
			Integer spamsNotContainingFeature = 0;
			
			Integer hamsContainingFeature = 0;
			Integer hamsNotContainingFeature = 0;
			
			for (Email email : mEmails) {
				if (email.isSpam()) {
					if (email.getFeatureFrequency(feature) > 0) {
						spamsContainingFeature++;
					} else {
						spamsNotContainingFeature++;
					}
				} else {
					if (email.getFeatureFrequency(feature) > 0) {
						hamsContainingFeature++;
					} else {
						hamsNotContainingFeature++;
					}
				}
			}

			if (DEBUG) {
				System.out.println("Spams containing '" + feature + "': " + spamsContainingFeature + ", Spams without '" + feature + "': " + spamsNotContainingFeature);
				System.out.println("Hams containing '" + feature + "': " + hamsContainingFeature + ", Hams without '" + feature + "': " + hamsNotContainingFeature);
			}
			
			float probFeatureGivenSpam = getProbabilityOfFeatureGivenSpam(feature, spamsContainingFeature, spamsNotContainingFeature);
			float probFeatureGivenHam = getProbabilityOfFeatureGivenHam(feature, hamsContainingFeature, hamsNotContainingFeature);
			
			float probFeature = getProbabilityOfFeature(feature, probFeatureGivenSpam, probFeatureGivenHam);
			
			float probSpamGivenFeature = getProbabilityOfSpamGivenFeature(feature, probFeatureGivenSpam, probFeature);
			float probHamGivenFeature = getProbabilityOfHamGivenFeature(feature, probFeatureGivenHam, probFeature);
			
			mSpamProbability.put(feature, probSpamGivenFeature);
			mHamProbability.put(feature, probHamGivenFeature);
		}
	}

	private float getProbabilityOfFeatureGivenSpam(String word, Integer spamsContainingWord, Integer spamsNotContainingWord) {
		float probWordGivenSpam = (float) spamsContainingWord / (spamsContainingWord + spamsNotContainingWord);

		if (DEBUG) {
			System.out.println("P('" + word + "' | Spam) = " + probWordGivenSpam);
		}
		return probWordGivenSpam;
	}

	private float getProbabilityOfFeatureGivenHam(String word, Integer hamsContainingWord, Integer hamsNotContainingWord) {
		float probWordGivenHam = (float) hamsContainingWord / (hamsContainingWord + hamsNotContainingWord);
		
		if (DEBUG) {
			System.out.println("P('" + word + "' | ¬Spam) = " + probWordGivenHam);
		}
		return probWordGivenHam;
	}

	private float getProbabilityOfHamGivenFeature(String word, float probWordGivenHam, float probWord) {
		float probHamGivenWord = (float) (probWordGivenHam * PROBABILITY_HAM) / (probWord);

		if (DEBUG) {
			System.out.println("P(¬Spam | '" + word + "') = " + probHamGivenWord);
			System.out.println("==============================================");
			System.out.println();
		}
		return probHamGivenWord;
	}

	private float getProbabilityOfSpamGivenFeature(String word, float probWordGivenSpam, float probWord) {
		float probSpamGivenWord = (float) (probWordGivenSpam * PROBABILITY_SPAM) / (probWord);
		
		if (DEBUG) {
			System.out.println("P(Spam | '" + word + "') = " + probSpamGivenWord);
		}
		return probSpamGivenWord;
	}

	private float getProbabilityOfFeature(String word, float probWordGivenSpam, float probWordGivenHam) {
		float probWord = (PROBABILITY_SPAM * probWordGivenSpam) + (PROBABILITY_HAM * probWordGivenHam);

		if (DEBUG) {
			System.out.println("P('" + word + "') = P('" + word + "' | Spam) * P(Spam) + P('" + word + "' | ¬Spam) * P(¬Spam) = " + probWord);
			System.out.println();
		}
		return probWord;
	}
	
	public Float getProbability(Classification classification, String word) {
		if (classification == Classification.SPAM) {
			return mSpamProbability.get(word);
		} else {
			return mHamProbability.get(word);
		}
	}

	public HashMap<Classification, Float> getAverageUninterruptedCapitalsBounds() {
		return mAvgUnintCaptsBounds;
	}

	public HashMap<Classification, Float> getLongestUninterruptedCapitalsLengthBounds() {
		return mLngstUnintCaptsLenBounds;
	}

	public HashMap<Classification, Float> getCapitalsNumberBounds() {
		return mCaptsNumBounds;
	}
}

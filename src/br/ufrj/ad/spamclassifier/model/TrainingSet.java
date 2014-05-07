package br.ufrj.ad.spamclassifier.model;

import java.util.Collection;
import java.util.HashMap;

import br.ufrj.ad.spamclassifier.database.Parser;

public class TrainingSet {
	
	public enum FeatureType {
		WORD, CHAR, AVG_UNINT_CAPT, LNGST_UNINT_CAPT, NUM_CAPT;
	}
	
	// set to true to see prints
	private final static boolean DEBUG = false;
	
	public final static float PROBABILITY_SPAM = 0.5f;
	public final static float PROBABILITY_HAM = 0.5f;

	public enum Classification {
		SPAM, HAM;
	}
	
	private float MAX_AVG_UNINT_CAPT = Integer.MIN_VALUE;
	private float MIN_AVG_UNINT_CAPT = Integer.MAX_VALUE;
	
	private float MAX_LNGST_UNINT_CAPT = Integer.MIN_VALUE;
	private float MIN_LNGST_UNINT_CAPT = Integer.MAX_VALUE;
	
	private float MAX_NUM_CAPT = Integer.MIN_VALUE;
	private float MIN_NUM_CAPT = Integer.MAX_VALUE;
	
	private Collection<String> mWords;
	private Collection<String> mCharacters;
	private Collection<? extends Email> mEmails;
	private HashMap<String, Float> mSpamProbability;
	private HashMap<String, Float> mHamProbability;

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
	}

	// see comment below
	@SuppressWarnings("unused")
	private void calcProbabilityForCapitalsNumber() {
		Integer numSpamsInSet = 0;
		Integer numHamsInSet = 0;
		
		Integer totalCapitalsInSpams = 0;
		Integer totalCapitalsInHams = 0;

		for (Email email : mEmails) {
			Integer numberOfCapitals = email.getNumberOfCapitals();
			
			MAX_NUM_CAPT = Math.max(MAX_NUM_CAPT, numberOfCapitals);
			MIN_NUM_CAPT = Math.min(MIN_NUM_CAPT, numberOfCapitals);
			
			if (email.isSpam()) {
				numSpamsInSet++;

				totalCapitalsInSpams += numberOfCapitals;
			} else {
				numHamsInSet++;
				
				totalCapitalsInHams += numberOfCapitals;
			}
		}
		
		float spamsRatio = (float) totalCapitalsInSpams / numSpamsInSet;
		float hamsRatio = (float) totalCapitalsInHams / numHamsInSet;
		
		mSpamProbability.put(FeatureType.NUM_CAPT.toString(), spamsRatio);
		mHamProbability.put(FeatureType.NUM_CAPT.toString(), hamsRatio);
	}

	// How to train for this kind of feature? Currently, we are using the following method:
	// sum up all capitals and divide them by the number of emails (spams or hams) to get
	// the average number of capitals in each case.
	// But then, how to define a probability given an email from the Test Set ?
	// Intuitively, just knowing the number of capitals on that email could give us
	// 100 or 0% of chance to define if it is a spam or not... but that's not good
	// for our classifier, is it? So we kinda get 'how close' we are from each value... still not good
	// For the purpose of this work, we will ignore these kind of feature, in a way that we are only
	// going to use the 54 features made of words and characters out of the 57 available.
	@SuppressWarnings("unused")
	private void calcProbabilityForLongestUninterruptedCapitalsLength() {
		Integer numSpamsInSet = 0;
		Integer numHamsInSet = 0;
		
		Integer totalLngstUnintCapitalsInSpams = 0;
		Integer totalLngstUnintCapitalsInHams = 0;

		for (Email email : mEmails) {
			Integer longestUninterruptedCapitalsLength = email.getLongestUninterruptedCapitalsLength();
			
			MAX_LNGST_UNINT_CAPT = Math.max(MAX_LNGST_UNINT_CAPT, longestUninterruptedCapitalsLength);
			MIN_LNGST_UNINT_CAPT = Math.min(MIN_LNGST_UNINT_CAPT, longestUninterruptedCapitalsLength);
			
			if (email.isSpam()) {
				numSpamsInSet++;

				totalLngstUnintCapitalsInSpams += longestUninterruptedCapitalsLength;
			} else {
				numHamsInSet++;
				
				totalLngstUnintCapitalsInHams += longestUninterruptedCapitalsLength;
			}
		}
		
		float spamsRatio = (float) totalLngstUnintCapitalsInSpams / numSpamsInSet;
		float hamsRatio = (float) totalLngstUnintCapitalsInHams / numHamsInSet;
		
		mSpamProbability.put(FeatureType.LNGST_UNINT_CAPT.toString(), spamsRatio);
		mHamProbability.put(FeatureType.LNGST_UNINT_CAPT.toString(), hamsRatio);
	}

	// see comment above
	@SuppressWarnings("unused")
	private void calcProbabilityForAverageUninterruptedCapitals() {
		Integer numSpamsInSet = 0;
		Integer numHamsInSet = 0;
		
		Float totalAvgUnintCapitalsInSpams = 0.0f;
		Float totalAvgUnintCapitalsInHams = 0.0f;

		for (Email email : mEmails) {
			Float avgUninterruptedCapitals = email.getAvgUninterruptedCapitals();
			
			MAX_AVG_UNINT_CAPT = Math.max(MAX_AVG_UNINT_CAPT, avgUninterruptedCapitals);
			MIN_AVG_UNINT_CAPT = Math.min(MIN_AVG_UNINT_CAPT, avgUninterruptedCapitals);
			
			if (email.isSpam()) {
				numSpamsInSet++;

				totalAvgUnintCapitalsInSpams += avgUninterruptedCapitals;
			} else {
				numHamsInSet++;
				
				totalAvgUnintCapitalsInHams += avgUninterruptedCapitals;
			}
		}
		
		float spamsRatio = (float) totalAvgUnintCapitalsInSpams / numSpamsInSet;
		float hamsRatio = (float) totalAvgUnintCapitalsInHams / numHamsInSet;
		
		mSpamProbability.put(FeatureType.AVG_UNINT_CAPT.toString(), spamsRatio);
		mHamProbability.put(FeatureType.AVG_UNINT_CAPT.toString(), hamsRatio);
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
	
	public Float getProbability(Classification classification, String feature, Email email) {
		if (Parser.getWords().contains(feature) || Parser.getChars().contains(feature)) {
			if (classification == Classification.SPAM) {
				return mSpamProbability.get(feature);
			} else {
				return mHamProbability.get(feature);
			}
		} else {
			if (feature.equals(FeatureType.AVG_UNINT_CAPT.toString())) {
				return getProbabilityOutOf(email.getAvgUninterruptedCapitals(),
						MAX_AVG_UNINT_CAPT, MIN_AVG_UNINT_CAPT, classification);
			} else if (feature.equals(FeatureType.LNGST_UNINT_CAPT.toString())) {
				return getProbabilityOutOf(
						(float) email.getLongestUninterruptedCapitalsLength(),
						MAX_LNGST_UNINT_CAPT, MIN_LNGST_UNINT_CAPT,
						classification);
			} else {
				return getProbabilityOutOf((float) email.getNumberOfCapitals(),
						MAX_NUM_CAPT, MIN_NUM_CAPT, classification);
			}
		}
	}

	private Float getProbabilityOutOf(Float value, float maxValue, float minValue, Classification classification) {
		Float spamValue = maxValue;
		Float hamValue = minValue;
		
		float distToSpam = Math.abs(value - spamValue);
		float distToHam = Math.abs(value - hamValue);
		float numberScaleSize = maxValue - minValue;
		
		if (classification == Classification.SPAM) {
			return distToHam / numberScaleSize;
		} else {
			return distToSpam / numberScaleSize;
		}
	}
}

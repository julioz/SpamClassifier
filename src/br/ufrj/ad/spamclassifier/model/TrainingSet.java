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
	
	private Collection<String> words;
	private Collection<? extends Email> emails;
	private HashMap<String, Float> spamProbability;
	private HashMap<String, Float> hamProbability;

	public TrainingSet(Collection<String> words, Collection<? extends Email> emails) {
		this.words = words;
		this.emails = emails;
		this.spamProbability = new HashMap<String, Float>();
		this.hamProbability = new HashMap<String, Float>();
		
		init();
	}

	private void init() {
		for (String word : words) {
			Integer spamsContainingWord = 0;
			Integer spamsNotContainingWord = 0;
			
			Integer hamsContainingWord = 0;
			Integer hamsNotContainingWord = 0;
			
			for (Email email : emails) {
				if (email.isSpam()) {
					if (email.getWordFrequency(word) > 0) {
						spamsContainingWord++;
					} else {
						spamsNotContainingWord++;
					}
				} else {
					if (email.getWordFrequency(word) > 0) {
						hamsContainingWord++;
					} else {
						hamsNotContainingWord++;
					}
				}
			}

			if (DEBUG) {
				System.out.println("Spams containing '" + word + "': " + spamsContainingWord + ", Spams without '" + word + "': " + spamsNotContainingWord);
				System.out.println("Hams containing '" + word + "': " + hamsContainingWord + ", Hams without '" + word + "': " + hamsNotContainingWord);
			}
			
			float probWordGivenSpam = getProbabilityOfWordGivenSpam(word, spamsContainingWord, spamsNotContainingWord);
			float probWordGivenHam = getProbabilityOfWordGivenHam(word, hamsContainingWord, hamsNotContainingWord);
			
			float probWord = getProbabilityOfWord(word, probWordGivenSpam, probWordGivenHam);
			
			float probSpamGivenWord = getProbabilityOfSpamGivenWord(word, probWordGivenSpam, probWord);
			float probHamGivenWord = getProbabilityOfHamGivenWord(word, probWordGivenHam, probWord);
			
			spamProbability.put(word, probSpamGivenWord);
			hamProbability.put(word, probHamGivenWord);
		}
	}

	private float getProbabilityOfWordGivenSpam(String word, Integer spamsContainingWord, Integer spamsNotContainingWord) {
		float probWordGivenSpam = (float) spamsContainingWord / (spamsContainingWord + spamsNotContainingWord);

		if (DEBUG) {
			System.out.println("P('" + word + "' | Spam) = " + probWordGivenSpam);
		}
		return probWordGivenSpam;
	}

	private float getProbabilityOfWordGivenHam(String word, Integer hamsContainingWord, Integer hamsNotContainingWord) {
		float probWordGivenHam = (float) hamsContainingWord / (hamsContainingWord + hamsNotContainingWord);
		
		if (DEBUG) {
			System.out.println("P('" + word + "' | ¬Spam) = " + probWordGivenHam);
		}
		return probWordGivenHam;
	}

	private float getProbabilityOfHamGivenWord(String word, float probWordGivenHam, float probWord) {
		float probHamGivenWord = (float) (probWordGivenHam * PROBABILITY_HAM) / (probWord);

		if (DEBUG) {
			System.out.println("P(¬Spam | '" + word + "') = " + probHamGivenWord);
			System.out.println("==============================================");
			System.out.println();
		}
		return probHamGivenWord;
	}

	private float getProbabilityOfSpamGivenWord(String word, float probWordGivenSpam, float probWord) {
		float probSpamGivenWord = (float) (probWordGivenSpam * PROBABILITY_SPAM) / (probWord);
		
		if (DEBUG) {
			System.out.println("P(Spam | '" + word + "') = " + probSpamGivenWord);
		}
		return probSpamGivenWord;
	}

	private float getProbabilityOfWord(String word, float probWordGivenSpam, float probWordGivenHam) {
		float probWord = (PROBABILITY_SPAM * probWordGivenSpam) + (PROBABILITY_HAM * probWordGivenHam);

		if (DEBUG) {
			System.out.println("P('" + word + "') = P('" + word + "' | Spam) * P(Spam) + P('" + word + "' | ¬Spam) * P(¬Spam) = " + probWord);
			System.out.println();
		}
		return probWord;
	}
	
	public Float getProbability(Classification classification, String word) {
		if (classification == Classification.SPAM) {
			return spamProbability.get(word);
		} else {
			return hamProbability.get(word);
		}
	}
}

package br.ufrj.ad.spamclassifier.model;

import java.util.ArrayList;
import java.util.HashMap;

import br.ufrj.ad.spamclassifier.model.TrainingSet.Classification;

public class TestSet {

	private final static boolean DEBUG = false;

	private TrainingSet mTrainingSet;
	private ArrayList<Email> mTestList;

	public TestSet(TrainingSet trainingSet, ArrayList<Email> testList) {
		this.mTrainingSet = trainingSet;
		this.mTestList = testList;
	}

	public Float executeForWordFeature(String word) {
		Integer emailsContainingWord = 0;
		Integer emailsClassifiedCorrectly = 0;
		for (Email email : this.mTestList) {
			Float wordFrequency = email.getWordFrequency(word);

			if (wordFrequency > 0) {
				emailsContainingWord++;

				Float spamProbability = mTrainingSet.getProbability(
						Classification.SPAM, word);
				Float hamProbability = mTrainingSet.getProbability(
						Classification.HAM, word);

				if (DEBUG) {
					System.out.print("Email " + email.getPosition()
							+ " classified as ");
				}

				if (spamProbability >= hamProbability) {
					if (DEBUG)
						System.out.print("spam.");

					if (email.isSpam()) {
						emailsClassifiedCorrectly++;

						if (DEBUG) {
							System.out.print(" -> Guessed right!");
						}
					}
				} else {
					if (DEBUG)
						System.out.print("not spam.");

					if (!email.isSpam()) {
						emailsClassifiedCorrectly++;

						if (DEBUG) {
							System.out.print(" -> Guessed right!");
						}
					}
				}

				if (DEBUG) {
					System.out.println();
				}
			} else {
				if (DEBUG) {
					System.out.println("Email " + email.getPosition()
							+ " does not contain " + word);
				}
			}
		}

		Float accuracy = (float) emailsClassifiedCorrectly
				/ emailsContainingWord;
		return accuracy;
	}

	public Float executeForCharFeature(String character) {
		Integer emailsContainingChar = 0;
		Integer emailsClassifiedCorrectly = 0;
		for (Email email : this.mTestList) {
			Float charFrequency = email.getCharFrequency(character.charAt(0));

			if (charFrequency > 0) {
				emailsContainingChar++;

				Float spamProbability = mTrainingSet.getProbability(
						Classification.SPAM, character);
				Float hamProbability = mTrainingSet.getProbability(
						Classification.HAM, character);

				if (DEBUG) {
					System.out.print("Email " + email.getPosition()
							+ " classified as ");
				}

				if (spamProbability >= hamProbability) {
					if (DEBUG)
						System.out.print("spam.");

					if (email.isSpam()) {
						emailsClassifiedCorrectly++;

						if (DEBUG) {
							System.out.print(" -> Guessed right!");
						}
					}
				} else {
					if (DEBUG)
						System.out.print("not spam.");

					if (!email.isSpam()) {
						emailsClassifiedCorrectly++;

						if (DEBUG) {
							System.out.print(" -> Guessed right!");
						}
					}
				}

				if (DEBUG) {
					System.out.println();
				}
			} else {
				if (DEBUG) {
					System.out.println("Email " + email.getPosition()
							+ " does not contain " + character);
				}
			}
		}

		Float accuracy = (float) emailsClassifiedCorrectly
				/ emailsContainingChar;
		return accuracy;
	}

	public Float executeForAverageUnintCapitalsFeature() {
		Integer emailsClassifiedCorrectly = 0;

		for (Email email : this.mTestList) {
			Float avgUnintCapts = email.getAvgUninterruptedCapitals();

			HashMap<Classification, Float> bounds = mTrainingSet
					.getAverageUninterruptedCapitalsBounds();
			Float closest = getClosestValue(avgUnintCapts,
					bounds.get(Classification.SPAM),
					bounds.get(Classification.HAM));

			if (closest == bounds.get(Classification.SPAM)) {
				if (email.isSpam()) {
					emailsClassifiedCorrectly++;
				}
			} else {
				if (!email.isSpam()) {
					emailsClassifiedCorrectly++;
				}
			}
		}

		Float accuracy = (float) emailsClassifiedCorrectly / mTestList.size();
		return accuracy;
	}

	public Float executeForLongestUnintCapitalsLengthFeature() {
		Integer emailsClassifiedCorrectly = 0;

		for (Email email : this.mTestList) {
			Integer lngstUnintCapts = email
					.getLongestUninterruptedCapitalsLength();

			HashMap<Classification, Float> bounds = mTrainingSet
					.getLongestUninterruptedCapitalsLengthBounds();
			Float closest = getClosestValue(lngstUnintCapts,
					bounds.get(Classification.SPAM),
					bounds.get(Classification.HAM));

			if (closest == bounds.get(Classification.SPAM)) {
				if (email.isSpam()) {
					emailsClassifiedCorrectly++;
				}
			} else {
				if (!email.isSpam()) {
					emailsClassifiedCorrectly++;
				}
			}
		}

		Float accuracy = (float) emailsClassifiedCorrectly / mTestList.size();
		return accuracy;
	}

	public Float executeForCapitalsNumber() {
		Integer emailsClassifiedCorrectly = 0;

		for (Email email : this.mTestList) {
			Integer captsNum = email.getNumberOfCapitals();

			HashMap<Classification, Float> bounds = mTrainingSet
					.getCapitalsNumberBounds();
			Float closest = getClosestValue(captsNum,
					bounds.get(Classification.SPAM),
					bounds.get(Classification.HAM));

			if (closest == bounds.get(Classification.SPAM)) {
				if (email.isSpam()) {
					emailsClassifiedCorrectly++;
				}
			} else {
				if (!email.isSpam()) {
					emailsClassifiedCorrectly++;
				}
			}
		}

		Float accuracy = (float) emailsClassifiedCorrectly / mTestList.size();
		return accuracy;
	}

	private float getClosestValue(float value, float firstValue,
			float secondValue) {
		float distanceFromFirst = Math.abs(firstValue - value);
		float distanceFromSecond = Math.abs(secondValue - value);
		if (distanceFromFirst < distanceFromSecond) {
			return firstValue;
		} else {
			return secondValue;
		}
	}
}

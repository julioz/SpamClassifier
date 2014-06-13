package br.ufrj.ad.spamclassifier.model.training;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import br.ufrj.ad.spamclassifier.model.Email;
import br.ufrj.ad.spamclassifier.model.training.BernoulliTrainingSet.Classification;

public class GaussianTrainingSet {

	private Collection<String> mWords;
	private Collection<String> mCharacters;
	private ArrayList<Email> mSpams;
	private ArrayList<Email> mHams;
	
	private HashMap<String, Double> mSpamAveragesMap;
	private HashMap<String, Double> mHamAveragesMap;
	private HashMap<String, Double> mSpamDeviationsMap;
	private HashMap<String, Double> mHamDeviationsMap;

	public GaussianTrainingSet(Collection<String> words,
			Collection<String> chars, ArrayList<Email> emails) {
		this.mWords = words;
		this.mCharacters = chars;
		
		this.mSpams = new ArrayList<Email>();
		this.mHams = new ArrayList<Email>();
		for (Email email : emails) {
			if (email.isSpam()) {
				mSpams.add(email);
			} else {
				mHams.add(email);
			}
		}
		
		calcAverageAndDeviationMaps();
	}

	private void calcAverageAndDeviationMaps() {
		this.mSpamAveragesMap = new HashMap<String, Double>();
		this.mHamAveragesMap = new HashMap<String, Double>();
		this.mSpamDeviationsMap = new HashMap<String, Double>();
		this.mHamDeviationsMap = new HashMap<String, Double>();
		
		for (String word : mWords) {
			this.mSpamAveragesMap.put(word, calcAverage(word, mSpams));
			this.mHamAveragesMap.put(word, calcAverage(word, mHams));
		}
		
		for (String character : mCharacters) {
			this.mSpamAveragesMap.put(character, calcAverage(character, mSpams));
			this.mHamAveragesMap.put(character, calcAverage(character, mHams));
		}
		
		for (String word : mWords) {
			Double spamAverage = mSpamAveragesMap.get(word);
			Double hamAverage = mHamAveragesMap.get(word);
			this.mSpamDeviationsMap.put(word, calcStdDeviation(word, spamAverage, mSpams));
			this.mHamDeviationsMap.put(word, calcStdDeviation(word, hamAverage, mHams));
		}
		
		for (String character : mCharacters) {
			Double spamAverage = mSpamAveragesMap.get(character);
			Double hamAverage = mHamAveragesMap.get(character);
			this.mSpamDeviationsMap.put(character, calcStdDeviation(character, spamAverage, mSpams));
			this.mHamDeviationsMap.put(character, calcStdDeviation(character, hamAverage, mHams));
		}
	}
	
	public Double getAverage(String feature, Classification classification) {
		if (classification == Classification.SPAM) {
			return mSpamAveragesMap.get(feature);
		} else {
			return mHamAveragesMap.get(feature);
		}
	}
	
	public Double getDeviation(String feature, Classification classification) {
		if (classification == Classification.SPAM) {
			return mSpamDeviationsMap.get(feature);
		} else {
			return mHamDeviationsMap.get(feature);
		}
	}
	
	private Double calcAverage(String feature, ArrayList<Email> emails) {
		Double average = 0.0;
		
		for (Email email : emails) {
			Float frequency = email.getFeatureFrequency(feature);
			average += frequency;
		}
		
		average = average / emails.size();
		return average;
	}
	
	private Double calcStdDeviation(String feature, Double average, ArrayList<Email> emails) {
		Double deviation = 0.0;
		
		for (Email email : emails) {
			Float frequency = email.getFeatureFrequency(feature);
			deviation = deviation + Math.pow(frequency - average, 2);
		}
		
		deviation = Math.sqrt(deviation / (emails.size() - 1));
		return deviation;
	}

	public Double getSpamProbability() {
		return ((double) mSpams.size() / (mSpams.size() + mHams.size()));
	}
	
	public Double getHamProbability() {
		return ((double) mHams.size() / (mSpams.size() + mHams.size()));
	}
}

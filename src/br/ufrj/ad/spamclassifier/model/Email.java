package br.ufrj.ad.spamclassifier.model;

import java.util.LinkedHashMap;

public class Email {

	private LinkedHashMap<String, Float> wordFreqMap;
	private LinkedHashMap<String, Float> charFreqMap;
	private Float avgUninterruptedCapitals;
	private Integer longestUninterruptedCapitalsLength;
	private Integer numberOfCapitals;
	private Boolean spam;

	public Email() {
		wordFreqMap = new LinkedHashMap<String, Float>();
		charFreqMap = new LinkedHashMap<String, Float>();
	}

	public void setWordFrequency(String word, Float frequency) {
		wordFreqMap.put(word, frequency);
	}

	public void setCharFrequency(char character, Float frequency) {
		charFreqMap.put(String.valueOf(character), frequency);
	}

	public Float getAvgUninterruptedCapitals() {
		return avgUninterruptedCapitals;
	}

	public void setAvgUninterruptedCapitals(Float avgUninterruptedCapitals) {
		this.avgUninterruptedCapitals = avgUninterruptedCapitals;
	}

	public Integer getLongestUninterruptedCapitalsLength() {
		return longestUninterruptedCapitalsLength;
	}

	public void setLongestUninterruptedCapitalsLength(
			Integer longestUninterruptedCapitalsLength) {
		this.longestUninterruptedCapitalsLength = longestUninterruptedCapitalsLength;
	}

	public Integer getNumberOfCapitals() {
		return numberOfCapitals;
	}

	public void setNumberOfCapitals(Integer numberOfCapitals) {
		this.numberOfCapitals = numberOfCapitals;
	}

	public boolean isSpam() {
		return spam;
	}

	public void setSpam(Boolean spam) {
		this.spam = spam;
	}
	
	@Override
	public String toString() {
		return wordFreqMap.toString() + "\n" + charFreqMap.toString() + ", "
				+ avgUninterruptedCapitals + ", "
				+ longestUninterruptedCapitalsLength + ", " + numberOfCapitals
				+ " --> " + spam + "\n";
	}

}

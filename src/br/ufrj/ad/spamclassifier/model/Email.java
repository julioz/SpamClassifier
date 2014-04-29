package br.ufrj.ad.spamclassifier.model;

import java.util.LinkedHashMap;

public class Email {

	/**
	 * 48 continuous real [0,100] attributes of type word_freq_WORD
	 * = percentage of words in the e-mail that match WORD,
	 * i.e. 100 * (number of times the WORD appears in the e-mail) /
	 * total number of words in e-mail.  A "word" in this case is any
	 * string of alphanumeric characters bounded by non-alphanumeric
	 * characters or end-of-string.
	 */
	private LinkedHashMap<String, Float> wordFreqMap;
	
	/**
	 * 6 continuous real [0,100] attributes of type char_freq_CHAR
	 * percentage of characters in the e-mail that match CHAR,
	 * i.e. 100 * (number of CHAR occurences) / total characters in e-mail
	 */
	private LinkedHashMap<String, Float> charFreqMap;
	
	/**
	 * 1 continuous real [1,...] attribute of type capital_run_length_average 
	 * = average length of uninterrupted sequences of capital letters 
	 */
	private Float avgUninterruptedCapitals;
	
	/**
	 * 1 continuous integer [1,...] attribute of type capital_run_length_longest 
	 * = length of longest uninterrupted sequence of capital letters 
	 */
	private Integer longestUninterruptedCapitalsLength;
	
	/**
	 * 1 continuous integer [1,...] attribute of type capital_run_length_total 
	 * = sum of length of uninterrupted sequences of capital letters 
	 * = total number of capital letters in the e-mail
	 */
	private Integer numberOfCapitals;
	
	/**
	 * 1 nominal {0,1} class attribute of type spam 
	 * = denotes whether the e-mail was considered spam (1) or not (0),
	 * i.e. unsolicited commercial e-mail. 
	 */
	private Boolean spam;
	
	/**
	 * Position in dataset (keeping just for future reference if needed)
	 */
	private Integer position;

	public Email(Integer position) {
		wordFreqMap = new LinkedHashMap<String, Float>();
		charFreqMap = new LinkedHashMap<String, Float>();
		this.position = position;
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
	
	public Integer getPosition() {
		return position;
	}
	
	@Override
	public String toString() {
		return getPosition() + ": " + wordFreqMap.toString() + "\n" + charFreqMap.toString() + ", "
				+ avgUninterruptedCapitals + ", "
				+ longestUninterruptedCapitalsLength + ", " + numberOfCapitals
				+ " --> " + spam + "\n";
	}

}

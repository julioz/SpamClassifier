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
	private LinkedHashMap<String, Float> mWordFreqMap;
	
	/**
	 * 6 continuous real [0,100] attributes of type char_freq_CHAR
	 * percentage of characters in the e-mail that match CHAR,
	 * i.e. 100 * (number of CHAR occurences) / total characters in e-mail
	 */
	private LinkedHashMap<String, Float> mCharFreqMap;
	
	/**
	 * 1 continuous real [1,...] attribute of type capital_run_length_average 
	 * = average length of uninterrupted sequences of capital letters 
	 */
	private Float mAverageUninterruptedCapitals;
	
	/**
	 * 1 continuous integer [1,...] attribute of type capital_run_length_longest 
	 * = length of longest uninterrupted sequence of capital letters 
	 */
	private Integer mLongestUninterruptedCapitalsLength;
	
	/**
	 * 1 continuous integer [1,...] attribute of type capital_run_length_total 
	 * = sum of length of uninterrupted sequences of capital letters 
	 * = total number of capital letters in the e-mail
	 */
	private Integer mNumberOfCapitals;
	
	/**
	 * 1 nominal {0,1} class attribute of type spam 
	 * = denotes whether the e-mail was considered spam (1) or not (0),
	 * i.e. unsolicited commercial e-mail. 
	 */
	private Boolean mSpam;
	
	/**
	 * Position in dataset (keeping just for future reference if needed)
	 */
	private Integer mPosition;

	public Email(Integer position) {
		this.mWordFreqMap = new LinkedHashMap<String, Float>();
		this.mCharFreqMap = new LinkedHashMap<String, Float>();
		this.mPosition = position;
	}
	
	public void setWordFrequency(String word, Float frequency) {
		mWordFreqMap.put(word, frequency);
	}
	
	public Float getWordFrequency(String word) {
		return mWordFreqMap.get(word);
	}
	
	public void setCharFrequency(char character, Float frequency) {
		mCharFreqMap.put(String.valueOf(character), frequency);
	}
	
	public Float getCharFrequency(char character) {
		return mCharFreqMap.get(String.valueOf(character));
	}
	
	public Float getFeatureFrequency(String feature) {
		if (mWordFreqMap.keySet().contains(feature)) {
			return getWordFrequency(feature);
		} else {
			return getCharFrequency(feature.charAt(0));
		}
	}
	
	public Float getAvgUninterruptedCapitals() {
		return mAverageUninterruptedCapitals;
	}

	public void setAvgUninterruptedCapitals(Float avgUninterruptedCapitals) {
		this.mAverageUninterruptedCapitals = avgUninterruptedCapitals;
	}

	public Integer getLongestUninterruptedCapitalsLength() {
		return mLongestUninterruptedCapitalsLength;
	}

	public void setLongestUninterruptedCapitalsLength(
			Integer longestUninterruptedCapitalsLength) {
		this.mLongestUninterruptedCapitalsLength = longestUninterruptedCapitalsLength;
	}

	public Integer getNumberOfCapitals() {
		return mNumberOfCapitals;
	}

	public void setNumberOfCapitals(Integer numberOfCapitals) {
		this.mNumberOfCapitals = numberOfCapitals;
	}

	public boolean isSpam() {
		return mSpam;
	}

	public void setSpam(Boolean spam) {
		this.mSpam = spam;
	}
	
	public Integer getPosition() {
		return mPosition;
	}
	
	@Override
	public String toString() {
		return getPosition() + ": " + mWordFreqMap.toString() + "\n" + mCharFreqMap.toString() + ", "
				+ mAverageUninterruptedCapitals + ", "
				+ mLongestUninterruptedCapitalsLength + ", " + mNumberOfCapitals
				+ " --> " + mSpam + "\n";
	}

}

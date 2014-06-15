package br.ufrj.ad.spamclassifier.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import br.ufrj.ad.spamclassifier.database.Parser;
import br.ufrj.ad.spamclassifier.model.Email;
import br.ufrj.ad.spamclassifier.model.test.BaseTestSet;

public abstract class BaseClassifierRunner {
	
	public enum Classifier {
		ONE_FEATURE, TEN_FEATURES, ALL_FEATURES;
	}
	
	private static final double TRAINING_PERCENTAGE = 0.8;
	protected ArrayList<Email> mTrainingList;
	protected ArrayList<Email> mTestList;
	private ArrayList<Email> mEmails;
	
	public BaseClassifierRunner(ArrayList<Email> emails) {
		this.mEmails = emails;
	}

	public void initializeSets() {
		// Shuffle collection so we can pick a percentage for the training set
		// and the rest for the test set
		Collections.shuffle(mEmails);

		Integer percentageIndex = (int) Math.floor(TRAINING_PERCENTAGE
				* mEmails.size());
		mTrainingList = new ArrayList<Email>(mEmails.subList(0,
				percentageIndex));
		mTestList = new ArrayList<Email>(mEmails.subList(
				percentageIndex, mEmails.size()));
	}

	protected static List<Entry<String, Double>> getBestFeatures(int numFeatures,
			HashMap<String, Double> accuracyMap) {
		List<Entry<String, Double>> sortedMapEntries = getSortedMapEntries(
				accuracyMap, true);

		return new ArrayList<Entry<String, Double>>(sortedMapEntries.subList(0,
				numFeatures));
	}
	
	protected Double executeUsingOneFeature(BaseTestSet testSet) {
		// Run the TestSet in each of the features and
		// get their accuracy when classifying
		HashMap<String, Double> accuracyMap = buildAccuracyMap(testSet);

		System.out.println(accuracyMap.size()
				+ " analyzed features (individually)");

		Double avgAccuracy = getAvgAccuracy(accuracyMap);
		// Uncomment to see the accuracy of the
		// classification for each feature
		// printMap(accuracyMap, true);

		// System.out.println();

		int numFeatures = 4;
		List<Entry<String, Double>> bestFeatures = getBestFeatures(numFeatures,
				accuracyMap);
		// System.out.println("Picking " + numFeatures +
		// " best features based on accuracy:");
		// for (int i = 0; i < bestFeatures.size(); i++) {
		// Entry<String, Double> entry = bestFeatures.get(i);
		// System.out.println((i+1) + ": " + entry.getKey() + " -> " +
		// entry.getValue());
		// }

		System.out.println("Best feature: '" + bestFeatures.get(0).getKey()
				+ "'");
		System.out.println("Average accuracy: " + avgAccuracy);
		return bestFeatures.get(0).getValue();
	}
	
	private Double getAvgAccuracy(HashMap<String, Double> accuracyMap) {
		Double acc = 0.0;
		for (Double value : accuracyMap.values()) {
			acc += value;
		}
		
		return acc / accuracyMap.size();
	}

	protected Double executeUsingTenFeatures(BaseTestSet testSet) {
		// select ten features at random
		List<String> features = new ArrayList<String>();
		features.addAll(Parser.getWords());
		features.addAll(Parser.getChars());
		Collections.shuffle(features);
		features = features.subList(0, 10);

		System.out.println(features.size() + " features analyzed:");
		for (int i = 0; i < features.size(); i++) {
			System.out.print(features.get(i));
			if (i != features.size() - 1) {
				System.out.print(", ");
			}
		}
		System.out.println();

		return testSet.executeForFeatures(features);
	}
	
	protected Double executeUsingAllFeatures(BaseTestSet testSet) {
		ArrayList<String> features = new ArrayList<String>();
		features.addAll(Parser.getWords());
		features.addAll(Parser.getChars());

		System.out.println(features.size() + " features analyzed");
		return testSet.executeForFeatures(features);
	}

	protected static List<Entry<String, Double>> getSortedMapEntries(
			HashMap<String, Double> accuracyMap, final boolean descending) {
		List<Map.Entry<String, Double>> entries = new ArrayList<Map.Entry<String, Double>>(
				accuracyMap.entrySet());
		Collections.sort(entries, new Comparator<Map.Entry<String, Double>>() {
			@Override
			public int compare(Entry<String, Double> o1,
					Entry<String, Double> o2) {
				Map.Entry<String, Double> e1 = (Map.Entry<String, Double>) o1;
				Map.Entry<String, Double> e2 = (Map.Entry<String, Double>) o2;
				return (descending ? -1 : 1)
						* e1.getValue().compareTo(e2.getValue());
			}
		});

		return entries;
	}
	
	protected static void printMap(HashMap<String, Double> accuracyMap,
			final boolean descending) {
		List<Map.Entry<String, Double>> entries = getSortedMapEntries(
				accuracyMap, descending);

		System.out.print("{ ");
		for (Map.Entry<String, Double> entry : entries) {
			System.out.print(entry.getKey() + " = " + entry.getValue() + ", ");
		}
		System.out.println(" }");
	}
	
	public HashMap<String, Double> buildAccuracyMap(BaseTestSet testSet) {
		HashMap<String, Double> accuracyMap = new HashMap<String, Double>();

		for (String word : Parser.getWords()) {
			Double accuracy = testSet.executeForFeature(word);
			accuracyMap.put(word, accuracy);
		}

		for (String character : Parser.getChars()) {
			Double accuracy = testSet.executeForFeature(character);
			accuracyMap.put(character, accuracy);
		}

		return accuracyMap;
	}
	
	public abstract Double runClassifier(Classifier classifier);
}

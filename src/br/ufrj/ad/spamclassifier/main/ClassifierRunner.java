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
import br.ufrj.ad.spamclassifier.model.TestSet;
import br.ufrj.ad.spamclassifier.model.TrainingSet;

public class ClassifierRunner {
	
	public enum Classifier {
		ONE_FEATURE, TEN_FEATURES, ALL_FEATURES;
	}

	private static final double TRAINING_PERCENTAGE = 0.8;

	public static Double runClassifier(ArrayList<Email> emails,
			Classifier classifier) {
		// Shuffle collection so we can pick a percentage for the training set
		// and the rest for the test set
		Collections.shuffle(emails);

		Integer percentageIndex = (int) Math.floor(TRAINING_PERCENTAGE
				* emails.size());
		ArrayList<Email> trainingList = new ArrayList<Email>(emails.subList(0,
				percentageIndex));
		ArrayList<Email> testList = new ArrayList<Email>(emails.subList(
				percentageIndex, emails.size()));

		// Initialize TrainingSet and TestSet
		TrainingSet trainingSet = new TrainingSet(Parser.getWords(),
				Parser.getChars(), trainingList);
		TestSet testSet = new TestSet(trainingSet, testList);

		if (classifier == Classifier.ONE_FEATURE) {
			return executeUsingOneFeature(testSet);
		} else if (classifier == Classifier.TEN_FEATURES) {
			return executeUsingTenFeatures(testSet);
		} else {
			return executeUsingAllFeatures(testSet);
		}
	}

	private static Double executeUsingAllFeatures(TestSet testSet) {
		ArrayList<String> features = new ArrayList<String>();
		features.addAll(Parser.getWords());
		features.addAll(Parser.getChars());

		System.out.println(features.size() + " features analyzed");
		return testSet.executeForFeatures(features);
	}

	private static Double executeUsingTenFeatures(TestSet testSet) {
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

	private static Double executeUsingOneFeature(TestSet testSet) {
		// Run the TestSet in each of the features and
		// get their accuracy when classifying
		HashMap<String, Double> accuracyMap = buildAccuracyMap(testSet);

		System.out.println(accuracyMap.size()
				+ " analyzed features (individually)");

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
		return bestFeatures.get(0).getValue();
	}

	private static List<Entry<String, Double>> getBestFeatures(int numFeatures,
			HashMap<String, Double> accuracyMap) {
		List<Entry<String, Double>> sortedMapEntries = getSortedMapEntries(
				accuracyMap, true);

		return new ArrayList<Entry<String, Double>>(sortedMapEntries.subList(0,
				numFeatures));
	}

	private static List<Entry<String, Double>> getSortedMapEntries(
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

	@SuppressWarnings("unused")
	private static void printMap(HashMap<String, Double> accuracyMap,
			final boolean descending) {
		List<Map.Entry<String, Double>> entries = getSortedMapEntries(
				accuracyMap, descending);

		System.out.print("{ ");
		for (Map.Entry<String, Double> entry : entries) {
			System.out.print(entry.getKey() + " = " + entry.getValue() + ", ");
		}
		System.out.println(" }");
	}

	private static HashMap<String, Double> buildAccuracyMap(TestSet testSet) {
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
}

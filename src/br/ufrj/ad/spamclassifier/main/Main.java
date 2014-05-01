package br.ufrj.ad.spamclassifier.main;

import java.io.IOException;
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

public class Main {

	private static final double TRAINING_PERCENTAGE = 0.8;
	private static final String FLAG_AVG_UNINT_CAPTS = "AVG_UNINT_CAPTS";
	private static final String FLAG_LNGST_UNINT_CAPTS_LEN = "LNGST_UNINT_CAPTS_LEN";
	private static final String FLAG_CAPTS_NUM = "CAPTS_NUM";

	public static void main(String[] args) throws IOException {
		ArrayList<Email> emails = Parser.parseDatabase();

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

		// Run the TestSet in each of the features and
		// get their accuracy when classifying
		HashMap<String, Float> accuracyMap = buildAccuracyMap(testSet);

		System.out.println(accuracyMap.size() + " analyzed features");

		// Uncomment to see the accuracy of the
		// classification for each feature
		// printMap(accuracyMap, true);

		System.out.println();

		// Getting the maximum accuracy from the map
		// to choose the best feature to select
		float maxValueInMap = Collections.max(accuracyMap.values());
		for (Entry<String, Float> entry : accuracyMap.entrySet()) {
			if (entry.getValue().floatValue() == maxValueInMap) {
				float percentage = entry.getValue() * 100f;
				System.out.println("Choose feature '" + entry.getKey()
						+ "' for an accuracy of " + percentage + "%");
			}
		}
	}

	private static void printMap(HashMap<String, Float> accuracyMap,
			final boolean descending) {
		List<Map.Entry<String, Float>> entries = new ArrayList<Map.Entry<String, Float>>(
				accuracyMap.entrySet());
		Collections.sort(entries, new Comparator<Map.Entry<String, Float>>() {
			@Override
			public int compare(Entry<String, Float> o1, Entry<String, Float> o2) {
				Map.Entry<String, Float> e1 = (Map.Entry<String, Float>) o1;
				Map.Entry<String, Float> e2 = (Map.Entry<String, Float>) o2;
				return (descending ? -1 : 1)
						* e1.getValue().compareTo(e2.getValue());
			}
		});

		System.out.print("{ ");
		for (Map.Entry<String, Float> entry : entries) {
			System.out.print(entry.getKey() + " = " + entry.getValue() + ", ");
		}
		System.out.println(" }");
	}

	private static HashMap<String, Float> buildAccuracyMap(TestSet testSet) {
		HashMap<String, Float> accuracyMap = new HashMap<String, Float>();

		for (String word : Parser.getWords()) {
			Float accuracy = testSet.executeForWordFeature(word);
			accuracyMap.put(word, accuracy);
		}

		for (String character : Parser.getChars()) {
			Float accuracy = testSet.executeForCharFeature(character);
			accuracyMap.put(character, accuracy);
		}

		accuracyMap.put(FLAG_AVG_UNINT_CAPTS,
				testSet.executeForAverageUnintCapitalsFeature());
		accuracyMap.put(FLAG_LNGST_UNINT_CAPTS_LEN,
				testSet.executeForLongestUnintCapitalsLengthFeature());
		accuracyMap.put(FLAG_CAPTS_NUM, testSet.executeForCapitalsNumber());

		return accuracyMap;
	}
}

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
import br.ufrj.ad.spamclassifier.model.TrainingSet.FeatureType;

public class Main {

	private static final double TRAINING_PERCENTAGE = 0.8;
//	private static final String FLAG_AVG_UNINT_CAPTS = "AVG_UNINT_CAPTS";
//	private static final String FLAG_LNGST_UNINT_CAPTS_LEN = "LNGST_UNINT_CAPTS_LEN";
//	private static final String FLAG_CAPTS_NUM = "CAPTS_NUM";

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
		
		executeUsingAllFeatures(testSet);
	}
	
	private static void executeUsingAllFeatures(TestSet testSet) {
		ArrayList<String> features = new ArrayList<String>();
		features.addAll(Parser.getWords());
		features.addAll(Parser.getChars());
		features.add(FeatureType.AVG_UNINT_CAPT.toString());
		features.add(FeatureType.LNGST_UNINT_CAPT.toString());
		features.add(FeatureType.NUM_CAPT.toString());
		
		System.out.println(testSet.executeForFeatures(features));
	}
	
	private static void executeUsingTenFeatures(TestSet testSet) {
		ArrayList<String> features = new ArrayList<String>();
		features.add(";");
		features.add(FeatureType.AVG_UNINT_CAPT.toString());
		features.add(FeatureType.LNGST_UNINT_CAPT.toString());
		features.add(FeatureType.NUM_CAPT.toString());
		features.add("cs");
		features.add("free");
		
		System.out.println(testSet.executeForFeatures(features));
	}

	private static void executeUsingOneFeature(TestSet testSet) {
		// Run the TestSet in each of the features and
		// get their accuracy when classifying
		HashMap<String, Float> accuracyMap = buildAccuracyMap(testSet);

		System.out.println(accuracyMap.size() + " analyzed features");

		// Uncomment to see the accuracy of the
		// classification for each feature
		// printMap(accuracyMap, true);

		System.out.println();

		int numFeatures = 4;
		List<Entry<String, Float>> bestFeatures = getBestFeatures(numFeatures, accuracyMap);
		System.out.println("Picking " + numFeatures + " best features based on accuracy:");
		for (int i = 0; i < bestFeatures.size(); i++) {
			Entry<String, Float> entry = bestFeatures.get(i);
			System.out.println((i+1) + ": " + entry.getKey() + " -> " + entry.getValue());
		}

		// Getting the maximum accuracy from the map
		// to choose the best feature to select
//		float maxValueInMap = Collections.max(accuracyMap.values());
//		for (Entry<String, Float> entry : accuracyMap.entrySet()) {
//			if (entry.getValue().floatValue() == maxValueInMap) {
//				float percentage = entry.getValue() * 100f;
//				System.out.println("Choose feature '" + entry.getKey()
//						+ "' for an accuracy of " + percentage + "%");
//			}
//		}
	}

	private static List<Entry<String, Float>> getBestFeatures(int numFeatures,
			HashMap<String, Float> accuracyMap) {
		List<Entry<String, Float>> sortedMapEntries = getSortedMapEntries(accuracyMap, true);

		return new ArrayList<Entry<String, Float>>(sortedMapEntries.subList(0, numFeatures));
	}
	
	private static List<Entry<String, Float>> getSortedMapEntries(HashMap<String, Float> accuracyMap,
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
		
		return entries;
	}

	private static void printMap(HashMap<String, Float> accuracyMap,
			final boolean descending) {
		List<Map.Entry<String, Float>> entries = getSortedMapEntries(accuracyMap, descending);
		
		System.out.print("{ ");
		for (Map.Entry<String, Float> entry : entries) {
			System.out.print(entry.getKey() + " = " + entry.getValue() + ", ");
		}
		System.out.println(" }");
	}

	private static HashMap<String, Float> buildAccuracyMap(TestSet testSet) {
		HashMap<String, Float> accuracyMap = new HashMap<String, Float>();

		for (String word : Parser.getWords()) {
			Float accuracy = testSet.executeForFeature(word);
			accuracyMap.put(word, accuracy);
		}

		for (String character : Parser.getChars()) {
			Float accuracy = testSet.executeForFeature(character);
			accuracyMap.put(character, accuracy);
		}

		accuracyMap.put(FeatureType.AVG_UNINT_CAPT.toString(),
				testSet.executeForFeature(FeatureType.AVG_UNINT_CAPT.toString()));

		accuracyMap.put(FeatureType.LNGST_UNINT_CAPT.toString(),
				testSet.executeForFeature(FeatureType.LNGST_UNINT_CAPT.toString()));

		accuracyMap.put(FeatureType.NUM_CAPT.toString(),
				testSet.executeForFeature(FeatureType.NUM_CAPT.toString()));

		return accuracyMap;
	}
}

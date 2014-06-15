package br.ufrj.ad.spamclassifier.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import br.ufrj.ad.spamclassifier.database.Parser;
import br.ufrj.ad.spamclassifier.main.BaseClassifierRunner.Classifier;
import br.ufrj.ad.spamclassifier.model.Email;

public class Main {
	
	private final static int MEAN = 0;
	private final static int VARIANCE = 1;
	
	public static void main(String[] args) throws IOException {
		ArrayList<Email> emails = Parser.parseDatabase();
		
		Scanner scanner = new Scanner(System.in);
		System.out.println("Welcome to the Naive Spam Classifier, based on the UCI Spam Dataset.");
		
		System.out.println("We currently have " + Parser.getNumberOfFeatures() + " you can evaluate with.\n");
		int userFeatureChoice = -1;
		while(userFeatureChoice < 1) {
			System.out.println("Please choose how many features you would like to use:");
			System.out.println("1 - One feature");
			System.out.println("2 - Ten features");
			System.out.println("3 - All features");
			
			String input = scanner.next();
			try {
				Integer choice = Integer.valueOf(input);
				if (choice == 1 || choice == 2 || choice == 3) {
					userFeatureChoice = choice;
				} else {
					throw new NumberFormatException();
				}
			} catch (NumberFormatException e) {
				System.out.println("'" + input + "' is not a valid option.");
			}
		}
		
		System.out.println("Okay, you chose #" + userFeatureChoice + ".");
		
		int distributionType = -1;
		while(distributionType < 1) {
			System.out.println("What kind of distribution do you want to use:");
			System.out.println("1 - Bernoulli Distribution");
			System.out.println("2 - Gaussian Distribution");
			
			String input = scanner.next();
			try {
				Integer choice = Integer.valueOf(input);
				if (choice == 1 || choice == 2 || choice == 3) {
					distributionType = choice;
				} else {
					throw new NumberFormatException();
				}
			} catch (NumberFormatException e) {
				System.out.println("'" + input + "' is not a valid option.");
			}
		}
		System.out.println("Okay, you chose #" + distributionType + ".");
		
		System.out.println("And how many times would you like to run our algorithm?");
		int userTimesChoice = 0;
		while(userTimesChoice < 1) {
			System.out.println("Enter a value bigger than 1.");
			
			String input = scanner.next();
			try {
				Integer choice = Integer.valueOf(input);
				if (choice > 1) {
					userTimesChoice = choice;
				} else {
					throw new NumberFormatException();
				}
			} catch (NumberFormatException e) {
				System.out.println("'" + input + "' is not a valid option.");
			}
		}
		scanner.close();
		
		System.out.print("Great! We will run " + userTimesChoice + " time(s) the algorithm with ");
		switch (userFeatureChoice) {
		case 1:
			System.out.print("one feature");
			break;
		case 2:
			System.out.print("ten features");
			break;
		case 3:
			System.out.print("all features");
			break;
		default:
			break;
		}
		System.out.println(" using the " + (distributionType == 1 ? "Bernoulli" : "Gaussian") + " distribution.");
		System.out.println();
		System.out.println();
		
		Double[] meanAndVariance = runAlgorithm(emails, userTimesChoice, distributionType, Classifier.values()[userFeatureChoice-1]);
		System.out.println();
		System.out.println("The mean was " + meanAndVariance[MEAN] + ".");
		System.out.println("The variance was " + meanAndVariance[VARIANCE] + ".");
	}

	private static Double[] runAlgorithm(ArrayList<Email> emails, int times, int distributionType, Classifier classifier) {
		List<Double> accuracyList;
		
		if (distributionType == 1) {
			BernoulliClassifierRunner runner = new BernoulliClassifierRunner(emails);
			accuracyList = runDistribution(runner, times,
					classifier);
		} else {
			GaussianClassifierRunner runner = new GaussianClassifierRunner(emails);
			accuracyList = runDistribution(runner, times,
					classifier);
		}
		
		return calculateMeanAndVariance(accuracyList);
	}

	private static List<Double> runDistribution(
			BaseClassifierRunner runner, int times, Classifier classifier) {
		List<Double> accuracyList = new ArrayList<Double>();
		for (int i = 0; i < times; i++) {
			runner.initializeSets();
			Double accuracy = runner.runClassifier(classifier);
			accuracyList.add(accuracy);
			System.out.println("Run #" + (i + 1)
					+ (classifier == Classifier.ONE_FEATURE ? " best" : "")
					+ " accuracy: " + accuracy);
			System.out.println("==========================================");
		}
		return accuracyList;
	}
	
	private static Double[] calculateMeanAndVariance(List<Double> accuracyList) {
		Double[] data = new Double[2];
		
		double n = 0;
		double sum1 = 0;
		double sum2 = 0;

		for (Double accuracy : accuracyList) {
			n = n + 1;
			sum1 = sum1 + accuracy;
		}

		double mean = sum1/n;
		data[MEAN] = mean;
		
		for (Double accuracy : accuracyList) {
			sum2 = sum2 + (accuracy - mean)*(accuracy - mean);
		}

		double variance = sum2/(n - 1);
		data[VARIANCE] = variance;
		
		return data;
	}
}

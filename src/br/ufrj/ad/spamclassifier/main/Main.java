package br.ufrj.ad.spamclassifier.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import br.ufrj.ad.spamclassifier.database.Parser;
import br.ufrj.ad.spamclassifier.model.Email;
import br.ufrj.ad.spamclassifier.model.TrainingSet;
import br.ufrj.ad.spamclassifier.model.TrainingSet.Classification;

public class Main {

	private static final double TRAINING_PERCENTAGE = 0.8;

	public static void main(String[] args) throws IOException {
		ArrayList<Email> emails = Parser.parseDatabase();
		
		Collections.shuffle(emails);
		
		Integer percentageIndex = (int) Math.floor(TRAINING_PERCENTAGE * emails.size());
		ArrayList<Email> trainingList = new ArrayList<Email>(emails.subList(0, percentageIndex));
		ArrayList<Email> testList = new ArrayList<Email>(emails.subList(percentageIndex, emails.size()));
		
		TrainingSet trainingSet = new TrainingSet(Parser.getWords(), trainingList);
		
		// example: getting the probability of emails with the word 'conference' be a SPAM
		System.out.println(trainingSet.getProbability(Classification.SPAM, "conference"));
		
	}

}

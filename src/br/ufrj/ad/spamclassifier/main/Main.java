package br.ufrj.ad.spamclassifier.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import br.ufrj.ad.spamclassifier.database.Parser;
import br.ufrj.ad.spamclassifier.model.Email;

public class Main {

	private static final double TRAINING_PERCENTAGE = 0.8;

	public static void main(String[] args) throws IOException {
		ArrayList<Email> emails = Parser.parseDatabase();
		
		Collections.shuffle(emails);
		
		Integer percentageIndex = (int) Math.floor(TRAINING_PERCENTAGE * emails.size());
		HashSet<Email> trainingSet = new HashSet<Email>(emails.subList(0, percentageIndex));
		HashSet<Email> testSet = new HashSet<Email>(emails.subList(percentageIndex, emails.size()));
	}

}

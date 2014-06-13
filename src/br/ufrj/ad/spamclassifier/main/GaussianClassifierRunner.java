package br.ufrj.ad.spamclassifier.main;

import java.util.ArrayList;

import br.ufrj.ad.spamclassifier.database.Parser;
import br.ufrj.ad.spamclassifier.model.Email;
import br.ufrj.ad.spamclassifier.model.test.GaussianTestSet;
import br.ufrj.ad.spamclassifier.model.training.GaussianTrainingSet;

public class GaussianClassifierRunner extends BaseClassifierRunner {
	
	public GaussianClassifierRunner(ArrayList<Email> emails) {
		super(emails);
	}

	@Override
	public Double runClassifier(Classifier classifier) {
		GaussianTrainingSet trainingSet = new GaussianTrainingSet(Parser.getWords(),
				Parser.getChars(), mTrainingList);
		GaussianTestSet testSet = new GaussianTestSet(trainingSet, mTestList);

		if (classifier == Classifier.ONE_FEATURE) {
			return executeUsingOneFeature(testSet);
		} else if (classifier == Classifier.TEN_FEATURES) {
			return executeUsingTenFeatures(testSet);
		} else {
			return executeUsingAllFeatures(testSet);
		}
	}
}

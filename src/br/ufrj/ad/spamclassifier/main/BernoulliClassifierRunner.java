package br.ufrj.ad.spamclassifier.main;

import java.util.ArrayList;

import br.ufrj.ad.spamclassifier.database.Parser;
import br.ufrj.ad.spamclassifier.model.Email;
import br.ufrj.ad.spamclassifier.model.test.BernoulliTestSet;
import br.ufrj.ad.spamclassifier.model.training.BernoulliTrainingSet;

public class BernoulliClassifierRunner extends BaseClassifierRunner {
	
	public BernoulliClassifierRunner(ArrayList<Email> emails) {
		super(emails);
	}

	@Override
	public Double runClassifier(Classifier classifier) {
		BernoulliTrainingSet trainingSet = new BernoulliTrainingSet(Parser.getWords(),
				Parser.getChars(), mTrainingList);
		BernoulliTestSet testSet = new BernoulliTestSet(trainingSet, mTestList);

		if (classifier == Classifier.ONE_FEATURE) {
			return executeUsingOneFeature(testSet);
		} else if (classifier == Classifier.TEN_FEATURES) {
			return executeUsingTenFeatures(testSet);
		} else {
			return executeUsingAllFeatures(testSet);
		}
	}
}

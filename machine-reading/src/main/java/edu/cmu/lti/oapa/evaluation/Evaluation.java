package edu.cmu.lti.oapa.evaluation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import edu.cmu.lti.oaqa.corpus.BuildPseudoDocument;
import edu.cmu.lti.oaqa.expansion.ReadData;

public class Evaluation {
	static boolean done = false;

	public static double computeBinaryAnswerRecall(String path,
			HashMap<String, HashMap<String, Integer>> answers, int total)
			throws FileNotFoundException {
		System.out.println("Computing Binary Answer Recall!");
		done = false;

		int appear = 0;
		Scanner scan = new Scanner(new File(path));
		String line = null;

		do {
			line = scan.nextLine();
			appear += getAppearance(answers, line, total);
			if (done)
				break;
		} while (scan.hasNext());

		scan.close();

		return (double) appear / total;
	}

	public static int getAppearance(
			HashMap<String, HashMap<String, Integer>> map, String text,
			int total) {
		if (map == null || text == null || text.length() == 0) {
			return 0;
		}
		int count = 0;
		int found = 0;
		for (String qid : map.keySet()) {
			HashMap<String, Integer> answers = map.get(qid);
			for (String answer : answers.keySet()) {
				if (answers.get(answer) > 0) {
					if (text.contains(answer)) {
						// System.out.println(answer);
						answers.put(answer, 0);
						found++;
						count++;
					}
				} else {
					found++;
				}
			}
			map.put(qid, answers);
		}

		if (found == total) {
			done = true;
		}

		return count;
	}

	public  void baseline(HashMap<String, String> AnswerPath,
			String CorpusPath) throws IOException, URISyntaxException,
			BoilerpipeProcessingException {
		double recall = 0.0;

		// Training Set
		ReadData rd = new ReadData();
		HashMap<String, HashMap<String, Integer>> trainingset = rd
				.readAnswer(AnswerPath.get("Training"));

		recall = computeBinaryAnswerRecall(CorpusPath, trainingset,
				rd.getNumberOfAnswer());
		System.out.println("The recall of training set is :" + recall);

		// Development Set
		HashMap<String, HashMap<String, Integer>> devset = rd
				.readAnswer(AnswerPath.get("Development"));
		recall = computeBinaryAnswerRecall(CorpusPath, devset,
				rd.getNumberOfAnswer());
		System.out.println("The recall of development set is :" + recall);
		System.out.println("Number of Answers:" + rd.getNumberOfAnswer());

		// Test Set
		HashMap<String, HashMap<String, Integer>> testset = rd
				.readAnswer(AnswerPath.get("Test"));
		recall = computeBinaryAnswerRecall(CorpusPath, testset,
				rd.getNumberOfAnswer());
		System.out.println("The recall of training set is :" + recall);
		System.out.println("Number of Answers:" + rd.getNumberOfAnswer());

	}

	public void iterateExpasion(HashMap<String, String> AnswerPath,
			String CorpusPath) throws IOException,
			URISyntaxException, BoilerpipeProcessingException {
	
	}

}

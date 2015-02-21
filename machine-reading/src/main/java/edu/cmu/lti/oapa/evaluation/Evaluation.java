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

public class Evaluation {
	static boolean done = false;

	public void setDone(boolean flag) {
		done = flag;
	}

	public double computeBinaryAnswerRecall(String path,
			HashMap<String, HashMap<String, Integer>> answers, int total)
			throws FileNotFoundException {
		System.out.println("Computing Binary Answer Recall!");
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

	public int getAppearance(HashMap<String, HashMap<String, Integer>> map,
			String text, int total) {
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

	public static void baseline() throws IOException, URISyntaxException,
			BoilerpipeProcessingException {
		// training 70, dev 10, test 16

		// Step 1: read input file, load training questions
		HashMap<String, String> questions = readInputData(QuestionPath, 0, 69);
		HashMap<String, HashMap<String, Integer>> answers = new HashMap<>();
		int total = 0;
		double recall = 0.0;
		// Step 2: evaluate training set, combine questions and answers
		total = readAnswer(AnswerPath, answers, 0, 69);
		sourceExpansion(questions, answers, true, 1);
		recall = computeBinaryAnswerRecall(corpusPath, answers, total);
		System.out.println("The recall of training set is :" + recall);

		// Step 3: evaluate dev set, only questions
		// done = false;
		// total = readAnswer(AnswerPath, answers, 70, 79);
		// recall = computeBinaryAnswerRecall(corpusPath, answers, total);
		// System.out.println("The recall of development set is :" + recall);
		//
		// // // Step 4: evaluate test set, only questions
		// done = false;
		// total = readAnswer(AnswerPath, answers, 80, 96);
		// recall = computeBinaryAnswerRecall(corpusPath, answers, total);
		// System.out.println("The recall of test set is :" + recall);
	}

	public static void iterateExpasion() throws IOException,
			URISyntaxException, BoilerpipeProcessingException {
		// Step 1: read input file, load training questions
		HashMap<String, String> questions = readInputData(QuestionPath, 0, 69);
		HashMap<String, HashMap<String, Integer>> answers = new HashMap<>();
		int total = readAnswer(AnswerPath, answers, 0, 69);

		// Step 2: evaluate training set, combine questions and answers
		sourceExpansion(questions, answers, true, 1);

		// get keywords from relevant sentences
		BuildPseudoDocument bpd = new BuildPseudoDocument();
		// bpd.setVerbose(true);
		// RELOAD QUESTIONS HERE FOR DEV & TEST SET
		HashMap<String, ArrayList<String>> pseduoQueries = bpd.buildPseduoDoc(
				corpusPath, questions);

		// do another iteration
		questions.clear();
		for (String q : pseduoQueries.keySet()) {
			ArrayList<String> keywords = pseduoQueries.get(q);
			String query = "";
			for (String word : keywords) {
				query += word + " ";
			}
			// System.out.println(query);
			questions.put(q, query);
		}

		// RELOAD ANSWERS HERE FOR DEV & TEST SET
		sourceExpansion(questions, answers, false, 2);
		double recall = computeBinaryAnswerRecall(corpusPath, answers, total);

		System.out.println("The recall of traing set is :" + recall);
	}

}

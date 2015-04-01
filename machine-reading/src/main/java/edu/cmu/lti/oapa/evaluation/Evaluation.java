package edu.cmu.lti.oapa.evaluation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import edu.cmu.lti.oaqa.corpus.BuildPseudoDocument;
import edu.cmu.lti.oaqa.expansion.ReadData;
import edu.cmu.lti.oaqa.watson.QAApiQuerier;

public class Evaluation {
	private static boolean done = false;
	private static String resultPath = "../data/dso";
	public static double computeBinaryAnswerRecall(String path,
			HashMap<String, HashSet<String>> answers)
			throws FileNotFoundException {
		done = false;
		int total = 0;
		int appear = 0;

		HashMap<String, Boolean> found = new HashMap<>();
		for (String qid : answers.keySet()) {
			found.put(qid, false);
			total++;
		}

		Scanner scan = new Scanner(new File(path));
		String line = null;

		do {
			line = scan.nextLine();
			appear += getAppearance(answers, line, found, total);
			if (done)
				break;
		} while (scan.hasNext());

		scan.close();

		return (double) appear / total;
	}

	public double computeBinaryAnswerRecallWatson(
			HashMap<String, String> question,
			HashMap<String, HashSet<String>> answers) throws IOException {
		done = false;
		int total = 0;
		int appear = 0;
		QAApiQuerier querier = new QAApiQuerier();
		JSONObject watsonAnswer = null;

		HashMap<String, Boolean> found = new HashMap<>();
		for (String qid : answers.keySet()) {
			found.put(qid, false);
			total++;
		}

		for(String qid : question.keySet()){
			System.out.println("question id:" + qid);
			watsonAnswer = querier.fetch(question.get(qid), true);
			watsonAnswer = watsonAnswer.getJSONObject("question");
		    JSONArray ja = watsonAnswer.getJSONArray("evidencelist");
		    for (int i = 0; i < ja.length(); i++) {
		    	JSONObject temp = (JSONObject) ja.get(i);
		    	try {
		    		appear += getAppearance(answers, temp.getString("text"), found, total);
				} catch (Exception e) {

				}	
			}
			if (done)
				break;
		}
			

		return (double) appear / total;
	}

	public static int getAppearance(HashMap<String, HashSet<String>> answers,
			String text, HashMap<String, Boolean> found, int total) {
		if (answers == null || text == null || text.length() == 0) {
			return 0;
		}

		int count = 0;
		int alreadyFound = 0;

		for (String qid : found.keySet()) {
			if (found.get(qid) == false) {
				HashSet<String> candidates = answers.get(qid);
				for (String candidate : candidates) {
					if (text.contains(candidate)) {
						found.put(qid, true);
						count++;
						alreadyFound++;
						break;
					}
				}
			} else {
				alreadyFound++;
			}
		}

		if (alreadyFound == total) {
			done = true;
		}

		return count;
	}

	public void BinaryAnswerRecallWatson(HashMap<String, String> QuestionPath,
			HashMap<String, String> AnswerPath) throws IOException {

		System.out.println("Computing binary answer recall...");
		double recall = 0.0;

		// Training Set
		ReadData rd = new ReadData();
		HashMap<String, HashSet<String>> answer = rd.readAnswer(AnswerPath
				.get("Training"));
		HashMap<String, String> question = rd.readQuestions(QuestionPath
				.get("Training"));

		recall = computeBinaryAnswerRecallWatson(question, answer);
		System.out.println("The recall of training set is :" + recall);

		// Development Set
		answer = rd.readAnswer(AnswerPath.get("Development"));
		question = rd.readQuestions(QuestionPath.get("Development"));

		recall = computeBinaryAnswerRecallWatson(question, answer);
		System.out.println("The recall of development set is :" + recall);

		// Test Set
		answer = rd.readAnswer(AnswerPath.get("Test"));
		question = rd.readQuestions(QuestionPath.get("Test"));

		recall = computeBinaryAnswerRecallWatson(question, answer);
		System.out.println("The recall of test set is :" + recall);
	}

	public void BinaryAnswerRecall(HashMap<String, String> AnswerPath,
			String CorpusPath) throws IOException, URISyntaxException,
			BoilerpipeProcessingException {
		FileWriter fw = new FileWriter(new File(resultPath));
		
		System.out.println("Computing binary answer recall...");
		double recall = 0.0;

		// Training Set
		ReadData rd = new ReadData();
		HashMap<String, HashSet<String>> trainingset = rd.readAnswer(AnswerPath
				.get("Training"));

		recall = computeBinaryAnswerRecall(CorpusPath, trainingset);
		System.out.println("The recall of training set is :" + recall);
		fw.write("The recall of training set is :" + recall+"\n");
		
		// Development Set
		HashMap<String, HashSet<String>> devset = rd.readAnswer(AnswerPath
				.get("Development"));
		recall = computeBinaryAnswerRecall(CorpusPath, devset);
		System.out.println("The recall of development set is :" + recall);
		fw.write("The recall of development set is :" + recall+"\n");
		
		// Test Set
		HashMap<String, HashSet<String>> testset = rd.readAnswer(AnswerPath
				.get("Test"));
		recall = computeBinaryAnswerRecall(CorpusPath, testset);
		System.out.println("The recall of test set is :" + recall);
		fw.write("The recall of test set is :" + recall+"\n");
		
		fw.close();
	}

}

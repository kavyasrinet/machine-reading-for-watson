package edu.cmu.lti.oapa.evaluation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import edu.cmu.lti.oaqa.corpus.BuildPseudoDocument;
import edu.cmu.lti.oaqa.expansion.ReadData;

public class Evaluation {
	static boolean done = false;

	
	public static double computeBinaryAnswerRecall(String path,
			HashMap<String, HashSet<String>> answers)
			throws FileNotFoundException {
		done = false;
		int total = 0;
		int appear = 0;
		
		HashMap<String, Boolean> found = new HashMap<>();
		for(String qid: answers.keySet()) {
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

	public static int getAppearance(
			HashMap<String, HashSet<String>> answers, String text, HashMap<String, Boolean> found, int total) {
		if (answers == null || text == null || text.length() == 0) {
			return 0;
		}
		
		int count = 0;
		int alreadyFound = 0;
		
		for (String qid : found.keySet()) {
			if(found.get(qid) == false) {
				HashSet<String> candidates = answers.get(qid);
				for(String candidate: candidates) {
					if (text.contains(candidate)){
						found.put(qid, true);
						count++;
						alreadyFound++;
						break;
					}
				}
			}else{
				alreadyFound++;
			}
		}
		
		if(alreadyFound == total) {
			done = true;
		}
		
		return count;
	}
	

	public  void BinaryAnswerRecall(HashMap<String, String> AnswerPath,
			String CorpusPath) throws IOException, URISyntaxException,
			BoilerpipeProcessingException {
		System.out.println("Computing binary answer recall...");
		double recall = 0.0;

		// Training Set
		ReadData rd = new ReadData();
		HashMap<String, HashSet<String>> trainingset = rd
				.readAnswer(AnswerPath.get("Training"));

		recall = computeBinaryAnswerRecall(CorpusPath, trainingset);
		System.out.println("The recall of training set is :" + recall);
		
		// Development Set
		HashMap<String, HashSet<String>> devset = rd
				.readAnswer(AnswerPath.get("Development"));
		recall = computeBinaryAnswerRecall(CorpusPath, devset);
		System.out.println("The recall of development set is :" + recall);
		
		// Test Set
		HashMap<String, HashSet<String>> testset = rd
				.readAnswer(AnswerPath.get("Test"));
		recall = computeBinaryAnswerRecall(CorpusPath, testset);
		System.out.println("The recall of test set is :" + recall);
	
	}

}

package edu.cmu.lti.oaqa.workflow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.hdfs.server.namenode.status_jsp;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.DefaultExtractor;
import edu.cmu.lti.oaqa.agent.BoilerpipeCachedClient;
import edu.cmu.lti.oaqa.corpus.BuildPseudoDocument;
import edu.cmu.lti.oaqa.search.BingSearch;
import edu.cmu.lti.oaqa.search.BingSearchAgent;
import edu.cmu.lti.oaqa.search.RetrievalResult;

/**
 * @author Di Wang, Qiang Zhu, Xiaoqiu Huang, Liping Xiong, Yepeng Yin
 * @since 2015-2-7
 */

public class RetrievalBaselineWorkflow {
	static boolean done = false;
	static String accountKey = "TT15WkPtBHfSFTPRJLAMsEgjeaII0J8A7wAEP9J/Hk4";
	static String QuestionPath = "/Users/patrick/Documents/workspace/machine-reading-for-watson/data/dso/questions/TERRORISM-Questions.txt";
	static String AnswerPath = "/Users/patrick/Documents/workspace/machine-reading-for-watson/data/dso/gold_standard/TERRORISM-Questions-key.txt";
	static String corpusPath = "/Users/patrick/Documents/workspace/machine-reading-for-watson/explored-corpus/file";

	public static void main(String[] args) throws URISyntaxException,
			IOException, BoilerpipeProcessingException {

		baseline();
		// iterateExpasion();

	}

	public static void iterateExpasion() throws IOException,
			URISyntaxException, BoilerpipeProcessingException {
		// Step 1: read input file, load training questions
		HashMap<String, String> questions = readInputData(QuestionPath, 0, 69);
		HashMap<String, HashMap<String, Integer>> answers = new HashMap<>();
		int total = readAnswer(AnswerPath, answers, 0, 69);
	
		// Step 2: evaluate training set, combine questions and answers
		sourceExpansion(questions, answers, true,1);

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
		sourceExpansion(questions, answers, false,2);
		double recall = computeBinaryAnswerRecall(corpusPath, answers, total);

		System.out.println("The recall of traing set is :" + recall);
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
		done = false;
		total = readAnswer(AnswerPath, answers, 70, 79);
		recall = computeBinaryAnswerRecall(corpusPath, answers, total);
		System.out.println("The recall of development set is :" + recall);

		// // Step 4: evaluate test set, only questions
		done = false;
		total = readAnswer(AnswerPath, answers, 80, 96);
		recall = computeBinaryAnswerRecall(corpusPath, answers, total);
		System.out.println("The recall of test set is :" + recall);
	}

	public static double computeBinaryAnswerRecall(String path,
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

	/*
	 * Do source expansion from questions and answers
	 * 
	 * @param questions List of questions.
	 * 
	 * @param answers List of answers.
	 * 
	 * @param combine Whether combine questions and answers to do source expansion 
	 * 
	 * @param mode Method of writing corpus. 
	 * 			   1 - rewrite 
	 * 			   2 - append
	 * 
	 * @return null
	 */
	public static void sourceExpansion(HashMap<String, String> questions,
			HashMap<String, HashMap<String, Integer>> answers, 
			boolean combine, int mode) throws URISyntaxException, IOException {
		FileWriter corpusfwTotal = new FileWriter(corpusPath);
		FileWriter corpusfwSingle = null;

		BingSearchAgent bsa = new BingSearchAgent();
		bsa.initialize(accountKey);
		// set retrieve document size
		bsa.setResultSetSize(10);

		ArrayList<String> keyTerms = new ArrayList<>();
		ArrayList<String> keyPhrases = new ArrayList<>();
		keyTerms.add("dummy");

		int index = 0;
		String content = null;
		BoilerpipeCachedClient client = new BoilerpipeCachedClient();
		for (String qid : questions.keySet()) {
			System.out.println("Processing " + index);
			List<RetrievalResult> result = bsa.retrieveDocuments(qid,
					questions.get(qid), keyTerms, keyPhrases);
			corpusfwSingle = new FileWriter(corpusPath + index);
			for (int i = 0; i < result.size(); i++) {
				RetrievalResult rr = result.get(i);
				// get web page content
				content = client.fetch(rr.getUrl());
				// write corpus to file
				if (mode == 1) {
					corpusfwTotal.write(rr.getText() + content+"\n");
					corpusfwSingle.write(rr.getText() + content + "\n");
				} else if(mode == 2) {
					corpusfwTotal.append(rr.getText() + content);
					corpusfwSingle.append(rr.getText() + content + "\n");
				}
			}

			if (combine) {
				HashMap<String, Integer> answer = answers.get(qid);
				for (String candidate : answer.keySet()) {
					result = bsa.retrieveDocuments(qid, candidate, keyTerms,
							keyPhrases);
					for (int i = 0; i < result.size(); i++) {
						RetrievalResult rr = result.get(i);
						content = client.fetch(rr.getUrl());
						// write corpus to file
						if (mode == 1) {
							corpusfwTotal.write(rr.getText() + content+"\n");
							corpusfwSingle.write(rr.getText() + content + "\n");
						} else if(mode == 2) {
							corpusfwTotal.append(rr.getText() + content);
							corpusfwSingle.append(rr.getText() + content + "\n");
						}
					}
				}
			}
			corpusfwSingle.close();
			index++;
		}
		corpusfwTotal.close();
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

	public static HashMap<String, String> readInputData(String path, int start,
			int end) throws FileNotFoundException {
		HashMap<String, String> result = new HashMap<>();
		Scanner scan = new Scanner(new File(path));
		int read = 0;
		String line = null;
		do {
			line = scan.nextLine();
			if (read >= start) {
				result.put(line.split("\\|")[0], line.split("\\|")[1]);
			}
			read++;
		} while (scan.hasNext() && read < end);
		scan.close();

		return result;
	}

	/*
	 * Read Answer from file that in range of line [start, end].
	 * 
	 * @param path Path of file
	 * 
	 * @param answer Read answer. The key of outer hashmap is qid. The key of
	 * inner hashmap is candidate answer and the value is appearance time.
	 * 
	 * @param start Start line number.
	 * 
	 * @param end End line number.
	 * 
	 * @return Total number of candidate answers.
	 */
	public static int readAnswer(String path,
			HashMap<String, HashMap<String, Integer>> answer, int start, int end)
			throws FileNotFoundException {
		int count = 0;
		int read = 0;
		Scanner scan = new Scanner(new File(path));

		String line = null;
		do {
			line = scan.nextLine();
			if (read >= start) {
				HashMap<String, Integer> result = new HashMap<>();
				String answers[] = line.substring(line.indexOf(' ') + 1).split(
						"[|]");
				for (int i = 0; i < answers.length; i++) {
					// remove the '-' in the date
					String temp = answers[i].replace("-", " ");
					if (!result.containsKey(temp)) {
						result.put(answers[i], 1);
						count++;
					}
				}
				answer.put(line.substring(0, line.indexOf(' ')), result);
			}
			read++;
		} while (scan.hasNext() && read < end);
		scan.close();

		return count;
	}
}

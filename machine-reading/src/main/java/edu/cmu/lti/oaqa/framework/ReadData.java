package edu.cmu.lti.oaqa.framework;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

/**
 * This class is used to read data from local file.
 * 
 * @author Yepeng Yin
 */
public class ReadData {
	public ArrayList<QuestionData> readQuestionData(String questionPath, String answerPath) throws Exception {
		ArrayList<QuestionData> data = new ArrayList<QuestionData>();
		
		HashMap<String, String> questions = readQuestions(questionPath);
		HashMap<String, HashSet<String>> answers = readAnswers(answerPath);
		
		if(questions.size() != answers.size()){
			System.out.println("Questions and answers are not corresponding.");
			System.exit(1);
		}
		
		for(String qid: questions.keySet()){
			if(answers.containsKey(qid)){
				data.add(new QuestionData(questions.get(qid), new ArrayList<String>(answers.get(qid))));
			}else{
				System.err.println("Question and answer are not corresponding:");
				System.err.println("Have qid: "+ qid + ", question: "+questions.get(qid));
			}
		}
		
		return data;
	}
	
	/*
	 * Read Answer from file that in range of line [start, end].
	 * 
	 * @param path Path of file
	 * 
	 * @param answer Read answer. The key is qid and the value is a set of candidate answers.
	 * 
	 * @return Hashmap of candidate answers.
	 */
	public HashMap<String, HashSet<String>> readAnswers(String path)
			throws FileNotFoundException {
	
		HashMap<String, HashSet<String>> answer = new HashMap<String, HashSet<String>>();

		Scanner scan = new Scanner(new File(path));

		String line = null;
		do {
			line = scan.nextLine();

			HashSet<String> result = new HashSet<>();
			String answers[] = line.substring(line.indexOf(' ') + 1).split(
					"[|]");
			for (int i = 0; i < answers.length; i++) {
				// remove the '-' in the date
				String temp = answers[i].replace("-", " ");
				result.add(temp);
			}
			answer.put(line.substring(0, line.indexOf(' ')), result);

		} while (scan.hasNext());
		scan.close();

		return answer;
	}

	public HashMap<String, String> readQuestions(String path)
			throws FileNotFoundException {
		HashMap<String, String> result = new HashMap<>();
		Scanner scan = new Scanner(new File(path));

		String line = null;
		do {
			line = scan.nextLine();
			result.put(line.split("\\|")[0], line.split("\\|")[1]);
		} while (scan.hasNext());
		scan.close();

		return result;
	}
}

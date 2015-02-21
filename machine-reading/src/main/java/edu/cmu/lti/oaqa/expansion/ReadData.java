package edu.cmu.lti.oaqa.expansion;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class ReadData {
	static String QuestionPath = "../data/dso/questions/TERRORISM-Questions.txt";
	static String AnswerPath = "../data/dso/gold_standard/TERRORISM-Questions-key.txt";
	
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
	
	public static HashMap<String, String> readQuestions(String path, int start,
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
	

	public void setQuestionPath(String path) {
		this.QuestionPath = path;
	}
	
	public void setAnswerPath(String path) {
		this.AnswerPath = path;
	}
	
}

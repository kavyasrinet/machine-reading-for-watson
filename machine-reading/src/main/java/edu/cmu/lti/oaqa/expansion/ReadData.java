package edu.cmu.lti.oaqa.expansion;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class ReadData {
	private static int numOfAnswer;

	public ReadData() {
		numOfAnswer = 0;
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
	public HashMap<String, HashMap<String, Integer>> readAnswer(String path)
			throws FileNotFoundException {
		numOfAnswer = 0;

		HashMap<String, HashMap<String, Integer>> answer = new HashMap<String, HashMap<String, Integer>>();

		Scanner scan = new Scanner(new File(path));

		String line = null;
		do {
			line = scan.nextLine();

			HashMap<String, Integer> result = new HashMap<>();
			String answers[] = line.substring(line.indexOf(' ') + 1).split(
					"[|]");
			for (int i = 0; i < answers.length; i++) {
				// remove the '-' in the date
				String temp = answers[i].replace("-", " ");
				if (!result.containsKey(temp)) {
					result.put(answers[i], 1);
					numOfAnswer++;
				}
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

	public int getNumberOfAnswer() {
		return numOfAnswer;
	}
}

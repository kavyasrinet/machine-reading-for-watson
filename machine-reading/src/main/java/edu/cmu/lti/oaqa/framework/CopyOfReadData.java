package edu.cmu.lti.oaqa.framework;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

/**
 * This class is used to read data from local file.
 * 
 * @author Yepeng Yin
 */
public class CopyOfReadData {
	/*
	 * Read Answer from file that in range of line [start, end].
	 * 
	 * @param path Path of file
	 * 
	 * @param answer Read answer. The key of outer hashmap is qid. The key of
	 * inner hashmap is candidate answer and the value is appearance time.
	 * 
	 * @return Hashmap of candidate answers.
	 */
	public HashMap<String, HashSet<String>> readAnswer(String path)
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

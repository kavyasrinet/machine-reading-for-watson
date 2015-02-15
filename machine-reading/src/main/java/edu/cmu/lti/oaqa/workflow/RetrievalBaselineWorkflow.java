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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.hdfs.server.namenode.status_jsp;

import edu.cmu.lti.oaqa.search.BingSearch;
import edu.cmu.lti.oaqa.search.BingSearchAgent;
import edu.cmu.lti.oaqa.search.RetrievalResult;

/**
 * @author Di Wang.
 */

public class RetrievalBaselineWorkflow {
	static boolean done = false;

	public static void main(String[] args) throws URISyntaxException,
			IOException {
		baseline();
		// Step 2: do explore search
		// See XpSearcher as an example

		// Step 3: read gold standard files, evaluate recalls of training, dev,
		// testing sets

	}
	
	public static void baseline() throws IOException, URISyntaxException {
		String accountKey = "wVTtEz9nOezJcYCoTfWSiZ4d3LKRihTXVGyShzIc19E";
		String InputPath = "/Users/patrick/Documents/workspace/MachineReading/machine-reading-for-watson/data/dso/questions/TERRORISM-Questions.txt";
		String AnswerPath = "/Users/patrick/Documents/workspace/MachineReading/machine-reading-for-watson/data/dso/gold_standard/TERRORISM-Questions-key.txt";
		String corpusPath = "/Users/patrick/Documents/workspace/MachineReading/machine-reading-for-watson/explored-corpus";
		
		// Step 1: read input file, load training questions
		FileWriter corpusfw = null;
		HashMap<String, String> data = readInputData(InputPath, 0,81);
		HashMap<String, Integer> answer = new HashMap<>();
		int total = readAnswer(AnswerPath, answer, 0,81);

		BingSearchAgent bsa = new BingSearchAgent();
		bsa.initialize(accountKey);
		bsa.setResultSetSize(10);

		ArrayList<String> keyTerms = new ArrayList<>();
		ArrayList<String> keyPhrases = new ArrayList<>();
		keyTerms.add("dummy");

		int appear = 0;
		int index = 0;
		String content = null;
		for (String qid : data.keySet()) {
			List<RetrievalResult> result = bsa.retrieveDocuments(qid,
					data.get(qid), keyTerms, keyPhrases);
			corpusfw = new FileWriter(corpusPath+index);
			for (int i = 0; i < result.size(); i++) {
				RetrievalResult rr = result.get(i);
				if (!done) {
					appear += getAppearance(answer, rr.getText());
					content = getContent(rr);
					appear += getAppearance(answer,content);
				}
				// write corpus to file
				corpusfw.write(rr.getText()+content);
			}
			corpusfw.close();
		}
		
		
		System.out.println("the recall is :" + ((double) appear / total));

	}

	public static int getAppearance(HashMap<String, Integer> map, String text) {
		int count = 0;
		int already = 0;

		for (String answer : map.keySet()) {
			if (map.get(answer) > 0) {
				if (text.contains(answer)) {
					map.put(answer, 0);
					count++;
				}
			} else {
				already++;
			}
		}

		if (already == map.size()) {
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
			if (read >= start) {
				line = scan.nextLine();
				result.put(line.split("\\|")[0], line.split("\\|")[1]);
			}
			read++;
		} while (scan.hasNext() && read < end);
		scan.close();

		return result;
	}

//	public static int countWords(String word, String text) {
//		Pattern p = Pattern.compile(word);
//		Matcher m = p.matcher(text);
//		
//		if (m.find()) {
//			return 1;
//		}else{
//			return 0;
//		}
//	}

	public static int readAnswer(String path, HashMap<String,Integer> result,
			int start, int end) throws FileNotFoundException {
		int count = 0;
		int read = 0;
		Scanner scan = new Scanner(new File(path));

		String line = null;
		do {
			if (read >= start) {
				line = scan.nextLine();
				String answers[] = line.split("[|]");
				for (int i = 0; i < answers.length; i++) {
					String temp = answers[i].replace("-", " ");
					if (!result.containsKey(temp)) {
						result.put(answers[i], 1);
						count++;
					} 
				}
			}
			read++;
		} while (scan.hasNext() && read < end);
		scan.close();

		return count;
	}
	
	public static String getContent(RetrievalResult rr) {
		URL url;
		String result = "";
		try {
			// get URL content
			url = new URL(rr.getUrl());
			URLConnection conn = url.openConnection();
 
			// open the stream and put it into BufferedReader
			 conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-GB;     rv:1.9.2.13) Gecko/20101203 Firefox/3.6.13 (.NET CLR 3.5.30729)");
			 BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			 
			String inputLine = "";
			while ((inputLine = br.readLine()) != null) {
				result += inputLine;
			}
 
			br.close();
  
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;
	}
}

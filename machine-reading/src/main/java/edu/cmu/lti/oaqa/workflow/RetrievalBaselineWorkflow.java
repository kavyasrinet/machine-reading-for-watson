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
 * @since  2015-2-7
 */

public class RetrievalBaselineWorkflow {
	static boolean done = false;

	public static void main(String[] args) throws URISyntaxException,
			IOException, BoilerpipeProcessingException {
		baseline();
		// Step 2: do explore search
		// See XpSearcher as an example

		// Step 3: read gold standard files, evaluate recalls of training, dev,
		// testing sets

	}
	
	public static void baseline() throws IOException, URISyntaxException, BoilerpipeProcessingException {
		String accountKey = "wVTtEz9nOezJcYCoTfWSiZ4d3LKRihTXVGyShzIc19E";
		String InputPath = "/Users/patrick/Documents/workspace/machine-reading-for-watson/data/dso/questions/TERRORISM-Questions.txt";
		String AnswerPath = "/Users/patrick/Documents/workspace/machine-reading-for-watson/data/dso/gold_standard/TERRORISM-Questions-key.txt";
		String corpusPath = "/Users/patrick/Documents/workspace/machine-reading-for-watson/explored-corpus/file";
		
		// Step 1: read input file, load training questions
		FileWriter corpusfw = null;
		HashMap<String, String> questions = readInputData(InputPath, 0,81);
		HashMap<String, Integer> answers = new HashMap<>();
		int total = readAnswer(AnswerPath, answers, 0,81);

		BingSearchAgent bsa = new BingSearchAgent();
		bsa.initialize(accountKey);
		bsa.setResultSetSize(10);

		ArrayList<String> keyTerms = new ArrayList<>();
		ArrayList<String> keyPhrases = new ArrayList<>();
		keyTerms.add("dummy");

		int appear = 0;
		int index = 0;
		String content = null;
		BoilerpipeCachedClient client = new BoilerpipeCachedClient();
		for (String qid : questions.keySet()) {
			System.out.println("Processing " + index);
			List<RetrievalResult> result = bsa.retrieveDocuments(qid,
					questions.get(qid), keyTerms, keyPhrases);
			corpusfw = new FileWriter(corpusPath+index);
			for (int i = 0; i < result.size(); i++) {
				RetrievalResult rr = result.get(i);
				if (!done) {
					appear += getAppearance(answers, rr.getText());
					content = client.fetch(rr.getUrl());
					appear += getAppearance(answers,content);
				}
				// write corpus to file
				corpusfw.write(rr.getText()+content);
			}
			corpusfw.close();
			index++;
		}
		
		// generate pseudo document
		BuildPseudoDocument bpd = new BuildPseudoDocument();
		bpd.setVerbose(true);
		HashMap<String, ArrayList<String>> pseduoQueries = bpd.buildPseduoDoc(corpusPath, questions);
		
		// get keywords from relevant sentences
		index = 0;
		for (String q : pseduoQueries.keySet()) {
			System.out.println("Processing " + index);
			List<RetrievalResult> result = bsa.retrieveDocuments(q,
					pseduoQueries.get(q).toString(), keyTerms, keyPhrases);
			//corpusfw = new FileWriter(corpusPath+index);
			for (int i = 0; i < result.size(); i++) {
				RetrievalResult rr = result.get(i);
				if (!done) {
					appear += getAppearance(answers, rr.getText());
					content = client.fetch(rr.getUrl());
					appear += getAppearance(answers,content);
				}
				// write corpus to file
				//corpusfw.write(rr.getText()+content);
			}
			//corpusfw.close();
			index++;
		}
		
		System.out.println("the recall is :" + ((double) appear / total));

	}

	public static int getAppearance(HashMap<String, Integer> map, String text) {
		if (map == null || text == null || text.length() == 0) {
			return 0 ;
		}
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
	
	public static String getContent(RetrievalResult rr) throws BoilerpipeProcessingException {
		URL url;
		String html = "";
		try {
			// get URL content
			url = new URL(rr.getUrl());
			URLConnection conn = url.openConnection();
 
			// open the stream and put it into BufferedReader
			 conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-GB;     rv:1.9.2.13) Gecko/20101203 Firefox/3.6.13 (.NET CLR 3.5.30729)");
			 BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			 
			String inputLine = "";
			while ((inputLine = br.readLine()) != null) {
			html += inputLine;
			}
 
			br.close();
  
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return DefaultExtractor.getInstance().getText(html);
	}
}

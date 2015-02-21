package edu.cmu.lti.oaqa.expansion;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.cmu.lti.oaqa.agent.BoilerpipeCachedClient;
import edu.cmu.lti.oaqa.search.BingSearchAgent;
import edu.cmu.lti.oaqa.search.RetrievalResult;

public class Expansion {
	static String accountKey = "wVTtEz9nOezJcYCoTfWSiZ4d3LKRihTXVGyShzIc19E";
	static String corpusPath = "..../data/dso/explored-corpus/file";
	
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
	public void sourceExpansion(HashMap<String, String> questions,
			HashMap<String, HashMap<String, Integer>> answers, String corpusPath,
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
	
	public void setCorpusPath(String path) {
		this.corpusPath = path;
	}
	
	public void setAccountKey(String key) {
		this.accountKey = key;
	}
}

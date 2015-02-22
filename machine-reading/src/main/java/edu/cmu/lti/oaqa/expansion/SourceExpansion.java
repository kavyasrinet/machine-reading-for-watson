package edu.cmu.lti.oaqa.expansion;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import edu.cmu.lti.oaqa.agent.BoilerpipeCachedClient;
import edu.cmu.lti.oaqa.corpus.BuildPseudoDocument;
import edu.cmu.lti.oaqa.search.BingSearchAgent;
import edu.cmu.lti.oaqa.search.RetrievalResult;

public class SourceExpansion {
	private static String accountKey = "8WDj5gva1guOq+un0mhRx75ozDz7Sd4BmJwhgY0T2wY";
	private static String corpusPath = "../data/dso/explored-corpus/file";

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

	public void iterateExpasion(HashMap<String, String> questions,
			HashMap<String, HashMap<String, Integer>> answers, int mode) 
			throws IOException, URISyntaxException, BoilerpipeProcessingException {
		// Step 2: evaluate training set, combine questions and answers
		this.sourceExpansion(questions, answers, true,1);

		// get keywords from relevant sentences
		BuildPseudoDocument bpd = new BuildPseudoDocument();
		// bpd.setVerbose(true);
		
		/* different keyword expansion methods
		 * 			   0 - overlapping 
		 * 			   1 - tf-idf
		 * 			   2 - NER
		 */
		HashMap<String, ArrayList<String>> pseduoQueries = bpd.buildPseduoDoc(
				corpusPath, questions, 2);

		// do another iteration
		questions.clear();
		for (String q : pseduoQueries.keySet()) {
			ArrayList<String> keywords = pseduoQueries.get(q);
			String query = "";
			for (String word : keywords) {
				query += word + " ";
			}
			System.out.println(query);
			questions.put(q, query);
		}

		// RELOAD ANSWERS HERE FOR DEV & TEST SET
		this.sourceExpansion(questions, answers, false,2);
	}

	public void setCorpusPath(String path) {
		SourceExpansion.corpusPath = path;
	}

	public void setAccountKey(String key) {
		SourceExpansion.accountKey = key;
	}

	public String getCorpusPath() {
		return SourceExpansion.corpusPath;	
	}

	public String getAccountKey(String key) {
		return SourceExpansion.accountKey;
	}
}

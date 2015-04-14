package edu.cmu.lti.oaqa.framework;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import edu.cmu.lti.oaqa.search.BingSearchAgent;
import edu.cmu.lti.oaqa.search.RetrievalResult;


/**
 * This class is used to do source expansion.
 * 
 * @author Yepeng Yin, Qiang Zhu, Kavya Srinet.
 */

public class SourceExpansioner {
	private  String accountKey = null;
	private  String corpusPath = null;
	private static boolean VERBOSE = false;
	public static void setVerbose( boolean flag ){
		VERBOSE = flag;
	}
	
	public SourceExpansioner(String accountKey, String corpusPath) {
		this.accountKey = accountKey;
		this.corpusPath = corpusPath;
	}
	/*
	 * Do source expansion from questions and answers
	 * 
	 * @param questions List of questions.
	 * 
	 * @param answers List of candidate answers.
	 * 
	 * @param retrieveNum Number of results retrieve from SearchEngine
	 * 
	 * @return List of retrieved documents.
	 */
	public ArrayList<Document> sourceExpansion(HashMap<String, String> questions,
			HashMap<String, HashSet<String>> answers,
	        int retrieveNum) throws URISyntaxException,
			IOException {
		// expanded corpus
		ArrayList<Document> newCorpus = new ArrayList<Document>();
		
		if(VERBOSE)
			System.out.println("Source Expansion...");
		
		if (questions.size() == 0) {
			System.out.println("Empty questions list");
			return newCorpus;
		}

		BingSearchAgent bsa = new BingSearchAgent();
		bsa.initialize(accountKey);
		// set retrieve document size
		bsa.setResultSetSize(retrieveNum);

		ArrayList<String> keyTerms = new ArrayList<>();
		ArrayList<String> keyPhrases = new ArrayList<>();
		keyTerms.add("dummy");

		for (String qid : questions.keySet()) {
			if(VERBOSE){
				System.out.println("Processing question:\n" + questions.get(qid));
			}
			
			// Get the results from bing search
			List<RetrievalResult> result = bsa.retrieveDocuments(qid,
					questions.get(qid), keyTerms, keyPhrases);
			
			for (int i = 0; i < result.size(); i++) {
				RetrievalResult rr = result.get(i);
				// add to newCorpus
				newCorpus.add(new Document(rr));	
			}

			// do source expansion on answer
			if (answers != null && answers.size() != 0) {
				HashSet<String> answer = answers.get(qid);
				for (String candidate : answer) {
					result = bsa.retrieveDocuments(qid, candidate, keyTerms,
							keyPhrases);
					for (int i = 0; i < result.size(); i++) {
						RetrievalResult rr = result.get(i);
						newCorpus.add(new Document(rr));
					}
				}
			}
		}
		
		return newCorpus;
	}

	public void setCorpusPath(String path) {
		this.corpusPath = path;
	}
	
	public void setAccountKey(String key) {
		this.accountKey = key;
	}

	public String getCorpusPath() {
		return corpusPath;
	}

	public String getAccountKey(String key) {
		return accountKey;
	}
}

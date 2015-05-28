package edu.cmu.lti.oaqa.framework;

import java.io.File;
import java.io.FileWriter;
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
	public Corpus sourceExpansion(HashMap<String, String> questions,
			HashMap<String, HashSet<String>> answers,
	        int retrieveNum) throws URISyntaxException,
			IOException {
		// expanded corpus
		Corpus corpus = new Corpus();
		ArrayList<Document> newCorpus = new ArrayList<Document>();
		
		
		if(VERBOSE)
			System.out.println("Source Expansion...");
		
		
		
		if (questions.size() == 0) {
			System.out.println("Empty questions list");
			return corpus;
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
			System.out.println(result.size());
			
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
		
//		FileWriter fw = new FileWriter(new File("urls.txt"));
//		for(Document doc : newCorpus){	
//			fw.write(doc.getUrl()+"\n");
//		}
//		fw.close();
		
		corpus.setDocs(newCorpus);
		return corpus;
//		return newCorpus;
	}
	public Corpus sourceExpansionSeed(ArrayList<Seed> seeds,int retrieveNum) throws URISyntaxException{
		Corpus corpus = new Corpus();
		
		ArrayList<Document> newCorpus = new ArrayList<Document>();
		
		if(VERBOSE)
			System.out.println("Source Expansion...");
		
		BingSearchAgent bsa = new BingSearchAgent();
		bsa.initialize(accountKey);
		// set retrieve document size
		bsa.setResultSetSize(retrieveNum);
		
		ArrayList<String> keyTerms = new ArrayList<>();
		ArrayList<String> keyPhrases = new ArrayList<>();
		keyTerms.add("dummy");

		for (int i = 0; i < seeds.size(); i++) {
			
			Seed seed = seeds.get(i);
			
			if(VERBOSE){
				System.out.println("Processing question:\n" + seed.getQuery());
			}
			
			// Get the results from bing search
			List<RetrievalResult> result = bsa.retrieveDocuments(String.valueOf(i),
					seed.getQuery(), keyTerms, keyPhrases);
			
			for (int j = 0; j < result.size(); j++) {
				RetrievalResult rr = result.get(j);
				// add to newCorpus
				newCorpus.add(new Document(rr));	
			}
			
			System.out.println(result.size());
			
		}
		
		corpus.setDocs(newCorpus);
		return corpus;
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

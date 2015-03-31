package edu.cmu.lti.oaqa.expansion;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import edu.cmu.lti.oaqa.agent.BoilerpipeCachedClient;
import edu.cmu.lti.oaqa.corpus.BuildPseudoDocument;
import edu.cmu.lti.oaqa.search.BingSearchAgent;
import edu.cmu.lti.oaqa.search.RetrievalResult;

/**
 * This class is used to do source expansion.
 * 
 * @author Yepeng Yin, Qiang Zhu, Kavya Srinet.
 */

public class SourceExpansion {
	private  String accountKey = null;
	private  String corpusPath = null;
	
	public SourceExpansion(String accountKey, String corpusPath) {
		this.accountKey = accountKey;
		this.corpusPath = corpusPath;
	}
	/*
	 * Do source expansion from questions and answers
	 * 
	 * @param questions List of questions.
	 * 
	 * @param answers List of answers.
	 * 
	 * @param getContent Whether get content from url true - the expansion
	 * corpus will be snippet + web page content false - the expansion corpus
	 * just snippet
	 * 
	 * @param mode Method of writing corpus. 1 - rewrite 2 - append
	 * 
	 * @return null
	 */
	public void sourceExpansion(HashMap<String, String> questions,
			HashMap<String, HashSet<String>> answers,
			boolean getContent, int mode) throws URISyntaxException,
			IOException {
		if (questions.size() == 0) {
			System.err.println("Empty questions list");
			return;
		}
		FileWriter corpusfwTotal = null;
		FileWriter corpusfwSingle = null;

		if (mode == 1) {
			corpusfwTotal = new FileWriter(corpusPath);
		} else if (mode == 2) {
			corpusfwTotal = new FileWriter(corpusPath, true);
		}

		BingSearchAgent bsa = new BingSearchAgent();
		bsa.initialize(accountKey);
		// set retrieve document size
		bsa.setResultSetSize(5);

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
			if (mode == 1) {
				corpusfwSingle = new FileWriter(corpusPath + index);
			} else if (mode == 2) {
				corpusfwSingle = new FileWriter(corpusPath + index, true);
			}
			for (int i = 0; i < result.size(); i++) {
				RetrievalResult rr = result.get(i);
				// get web page content
				if (getContent) {
				  
					content = client.fetch(rr.getUrl());
				} else {
					content = "";
				}
				
				// write corpus to file
				String temp = rr.getText();
				
				corpusfwTotal.write("<section>\n");
				if(!temp.contains("...")){
					corpusfwTotal.write("<h1>"+rr.getText().split("\n")[0]);
				}else{
					corpusfwTotal.write("<h1>"+ temp.substring(0, temp.indexOf("...")));
				}
				corpusfwTotal.write("</h1>\n");
				corpusfwTotal.write(content);
				corpusfwTotal.write("</section>\n");
				corpusfwSingle.write(rr.getText() + content + "\n");
			}

			// do source expansion on answer
			if (answers.size() != 0) {
				HashSet<String> answer = answers.get(qid);
				for (String candidate : answer) {
					result = bsa.retrieveDocuments(qid, candidate, keyTerms,
							keyPhrases);
					for (int i = 0; i < result.size(); i++) {
						RetrievalResult rr = result.get(i);
						if (getContent) {
				//		  content = getContent(new URL(rr.getUrl()));
							content = client.fetch(rr.getUrl());
						} else {
							content = "";
						}
				//		System.out.println(content);
						// write corpus to file
<<<<<<< Updated upstream
						String x  = rr.getText();
					//	System.out.println("******************");
						//System.out.println("\n"+x);
						String title  = x.substring(0, x.indexOf("..."));
						corpusfwTotal.write(rr.getText() + content + "\n");
=======
						String temp = rr.getText();
						
						corpusfwTotal.write("<section>\n");
						if(!temp.contains("...")){
							corpusfwTotal.write("<h1>"+rr.getText().split("\n")[0]);
						}else{
							corpusfwTotal.write("<h1>"+ temp.substring(0, temp.indexOf("...")));
						}
						corpusfwTotal.write("</h1>\n");
						corpusfwTotal.write(content);
						corpusfwTotal.write("</section>\n");
						
>>>>>>> Stashed changes
						corpusfwSingle.write(rr.getText() + content + "\n");

					}
				}
			}
			
			corpusfwSingle.close();
			index++;
		}
		corpusfwTotal.close();
	}

	public void iterateExpasion(HashMap<String, String> questions,
			HashMap<String, HashSet<String>> answers, int mode)
			throws IOException, URISyntaxException,
			BoilerpipeProcessingException, ClassCastException,
			ClassNotFoundException {
		//  source expansion, combine questions and answers
		this.sourceExpansion(questions, answers, true, 1);

		// get keywords from relevant sentences
		BuildPseudoDocument bpd = new BuildPseudoDocument();
		// bpd.setVerbose(true);

		/*
		 * different keyword expansion methods
		 *                 0 - overlapping 
		 *                 1 - tf-idf 
		 *                 2 - NER
		 */
		HashMap<String, ArrayList<String>> pseduoQueries = bpd.buildPseduoDoc(
				corpusPath, questions, mode);

		// do another iteration
		questions.clear();
		answers.clear();
		for (String q : pseduoQueries.keySet()) {
			ArrayList<String> keywords = pseduoQueries.get(q);
			String query = "";
			for (String word : keywords) {
				query += word + " ";
			}
			System.out.println(query);
			questions.put(q, query);
		}

		this.sourceExpansion(questions, answers, true, 2);
	}
<<<<<<< Updated upstream

	public void setCorpusPath(String path) {
		SourceExpansion.corpusPath = path;
	}
	
	public static String getContent(URL url) throws IOException{
	   BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
	   ArrayList<String> lines = new ArrayList<String>();
	   String line;
	    while ((line = in.readLine()) != null) {
	      lines.add(line);
	      System.out.println(line);
	    }
	    in.close();
	    return lines.toString();

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
=======
>>>>>>> Stashed changes
}

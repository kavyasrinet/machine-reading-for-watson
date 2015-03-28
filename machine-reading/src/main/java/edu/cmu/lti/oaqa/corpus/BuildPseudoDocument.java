package edu.cmu.lti.oaqa.corpus;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class BuildPseudoDocument {
	private static boolean VERBOSE = false;
	public static void setVerbose( boolean flag ){
		VERBOSE = flag;
	}
	
	public HashMap<String, ArrayList<String>> buildPseduoDoc(String corpus_path,
			HashMap<String, String> queries, int mode) throws IOException, ClassCastException, ClassNotFoundException {

//			HashMap<String, String> queries) throws IOException {
//>>>>>>> Stashed changes
		int index = 0;
		HashMap<String, ArrayList<String>> result = new HashMap<>();
		for (String query : queries.values()) {
			if(VERBOSE)
				System.out.println("Processing "+index);
			ArrayList<String> keywords = build(corpus_path + index, query);
			result.put(query, keywords);
			index++;
		}
		return result;
	}

//<<<<<<< Updated upstream
//	public ArrayList<String> build(String corpus_address, String query, int mode) throws IOException, ClassCastException, ClassNotFoundException {
//		if (query.endsWith("?"))
//=======
	public ArrayList<String> build(String corpus_address, String query) throws IOException {
		if (query.endsWith("\\?"))
//>>>>>>> Stashed changes
			query = query.substring(0, query.length() - 1);
		ArrayList<String> relSentences = new ArrayList<>();
		BufferedReader br = new BufferedReader(new FileReader(corpus_address));
		String[] query_word = query.split(" ");
		String line;
		while ((line = br.readLine()) != null) {
			String[] sentences = line.split("[.|\\?]");
			for (String sentence : sentences) {
				for (String word : query_word) {
					if (sentence.contains(word)) {
						relSentences.add(sentence);
						break;
					}
				}
			}
		}
		br.close();
		String idfdict_path = "../data/dso/idf_dictionary.txt";
		BuildPseudoDocument_keywordsTFIDF bpd_tfidf = new BuildPseudoDocument_keywordsTFIDF();
		return bpd_tfidf.getkeywords(relSentences, 10, idfdict_path);
//		return getKeywords(relSentences, query, 10);
	}

	private HashSet<String> get_query_word(String query) {
		// TODO Auto-generated method stub
		String[] words = query.split(" ");
		HashSet<String> query_words = new HashSet<String>();
		for (String w : words)
			query_words.add(w);
		return query_words;
	}
	
	public  ArrayList<String> getKeywords(ArrayList<String> sentences, String question, int topK){
		String[] terms = question.split(" ");
		double Pw=0,Pt=0,Ptw=0;
		int numSen = sentences.size();
		HashMap<String,Integer> mapTerm = new HashMap<>();
		HashMap<String,Integer> mapWord = new HashMap<>();;
		HashMap<String,Integer> mapCo = new HashMap<>();
		final HashMap<String,Double> mapScore = new HashMap<>();
		
		//t1,w1
		for(int i=0;i<sentences.size();i++){
			String sent=sentences.get(i);
			String[] words = sent.trim().split(" ");
			for(String word : words){
				// remove non-relevant characters
				word = word.replaceAll("[(),;<>!]", "");
				if(!mapWord.containsKey(word)){
					mapWord.put(word, 1);
				}
				else{
					mapWord.put(word,mapWord.get(word)+1);
				}
			}
		}
		
		for(String term : terms){
			for(int i=0;i<sentences.size();i++){
				String sent=sentences.get(i);
				if(sent.contains(term)){
					if(!mapTerm.containsKey(term)) mapTerm.put(term,1);
					else mapTerm.put(term,mapTerm.get(term)+1);
					
					for(String word : sent.trim().split(" ")){
						String co = term+" "+word;
						if(!mapCo.containsKey(co)) mapCo.put(co, 1);
						else mapCo.put(co, mapCo.get(co)+1);
					}
				}
			}
		}//end for
		
		//compute score
		for(int i=0;i<sentences.size();i++){
			String sent=sentences.get(i);
			String[] words = sent.trim().split(" ");
			double score=0;
			
			for(String word : words){
				word = word.replaceAll("[(),;<>!\"/\\\\]", "");
				if(mapScore.containsKey(word)) continue;
				for(String term:terms){
					if(mapWord.containsKey(word) )  Pw= (double)mapWord.get(word) / (double)numSen;
					if(mapTerm.containsKey(term)) Pt = (double)mapTerm.get(term) / (double)numSen; 
					if(mapCo.containsKey(term+" "+word)) Ptw = (double)mapCo.get(term+" "+word) / (double)numSen;
					if(Pw*Pt!=0) score += Math.log(Ptw / (Pw*Pt) ) ;
					
					//score+=(double)mapCo.get(term+" "+word) / (double)mapWord.get(word) *  (double)mapTerm.get(term) ;
				}
				mapScore.put(word,score);
			}
		}//end for
		
		List<String> list = new ArrayList<String>(mapScore.keySet());
		
		Collections.sort(list, new Comparator<String>(){
			public int compare(String o1, String o2)
			{
				if(mapScore.get(o1) < mapScore.get(o2))
					return 1;
				else
					return -1;
			}
		});
		
		ArrayList<String> res=new ArrayList<>();
		for(int i=0;i<topK;i++){
			res.add(list.get(i));
			if (VERBOSE)
			   System.out.println(list.get(i)+" " + mapScore.get(list.get(i)) ) ;
		}
		return res;
		
	}//end main 
}
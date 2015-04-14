package edu.cmu.lti.oaqa.corpus;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class BuildPseudoDocument {
	
	public static String serializedClassifier = "../data/classifiers/english.all.3class.distsim.crf.ser.gz";
	public static AbstractSequenceClassifier<CoreLabel> classifier;
	public static MaxentTagger tagger = new MaxentTagger("../data/classifiers/left3words-wsj-0-18.tagger");
	
	private static boolean VERBOSE = false;
	public static void setVerbose( boolean flag ){
		VERBOSE = flag;
	}

	public HashMap<String, ArrayList<String>> buildPseduoDoc(String corpus_path,
			HashMap<String, String> queries, int mode) throws IOException, ClassCastException, ClassNotFoundException {
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

	public ArrayList<String> build(String corpus_address, String query) throws IOException, ClassCastException, ClassNotFoundException {
		classifier = CRFClassifier.getClassifier(serializedClassifier);
		HashSet<String> common_verbs = new HashSet<String>(Arrays.asList("may","is","are","was","were","has"));
		if (query.endsWith("\\?"))
			query = query.substring(0, query.length() - 1);
		ArrayList<String> relSentences = new ArrayList<>();
		BufferedReader br = new BufferedReader(new FileReader(corpus_address));
		HashMap<String, HashSet<String>> query_word = getNER(query,common_verbs);
		String line;
		while ((line = br.readLine()) != null) {
			String[] sentences = line.split("[.|\\?]");
			for (String sentence : sentences) {
				HashMap<String, HashSet<String>> sent = getNER(sentence, common_verbs);
				int score = scoreSentences(sent, query_word);
				if (score >= 6) {
					relSentences.add(sentence);
					System.out.println("score:"+score+"	"+sentence);
				}
			}
		}
		br.close();
//		String idfdict_path = "../data/dso/idf_dictionary.txt";
//		BuildPseudoDocument_keywordsTFIDF bpd_tfidf = new BuildPseudoDocument_keywordsTFIDF();
//		return bpd_tfidf.getkeywords(relSentences, 10, idfdict_path);
		//		return getKeywords(relSentences, query, 10);
		return relSentences;
	}

	public int scoreSentences(HashMap<String, HashSet<String>> sent, HashMap<String, HashSet<String>> query_word) {
		HashMap<String, Integer> score = new HashMap<String, Integer>();
		score.put("NER2",4);
		score.put("NER1",5);
		score.put("Noun",2);
		score.put("Verb",1);

		int finalScore = 0;
		Iterator<String> it = sent.keySet().iterator();
		while((it.hasNext())) {
			// type of the word
			String type = it.next();
			HashSet<String> doc = sent.get(type);
			HashSet<String> que = query_word.get(type);
			for (String word : doc) {
				if(que.contains(word))
					finalScore += score.get(type);
			}
		}

		return finalScore;
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

	public HashMap<String, HashSet<String>> getNER(String line, HashSet<String> common_verbs) throws ClassCastException, ClassNotFoundException, IOException{
		HashMap<String, HashSet<String>> setOfTags = new HashMap<String, HashSet<String>>();
		HashSet<String> set = new HashSet<String>();
		HashSet<String> set2 = new HashSet<String>();

		String NERStr = classifier.classifyToString(line);
		String tagged = tagger.tagTokenizedString(line);

		//System.out.println(NERStr);
		String [] words = NERStr.split("\\s");
		String prev_tag = "";
		String prev_str = "";
		for(String word : words)
		{
			String[] NER = word.split("/");
			if(NER.length > 1)
			{
				String t = NER[1];
				if(t.compareTo("O") != 0)
				{
					set.add(NER[0]);
					if (t.equals(prev_tag)){

						String newName = prev_str+" "+NER[0];
						set2.add(newName);
						prev_str = newName;
						prev_tag  = t;
					}
					prev_tag = t;
					prev_str = NER[0];
				}

			}

		}
		setOfTags.put("NER1",set);
		setOfTags.put("NER2", set2);
		HashSet<String> verb = new HashSet<String>();
		HashSet<String> noun = new HashSet<String>();
		words = tagged.split("\\s");
		for(String word : words){
			String[] tg = word.split("_");
			if (tg[1].startsWith("NN")){
				noun.add(tg[0]);
			}
			else if (tg[1].startsWith("VB") && !common_verbs.contains(tg[1]))
				verb.add(tg[0]);
		}
		setOfTags.put("Verb",verb);
		setOfTags.put("Noun",noun);

		return setOfTags;
	}
	
	public static void main(String [] args) throws ClassCastException, ClassNotFoundException, IOException {
		BuildPseudoDocument bpd = new BuildPseudoDocument();
		String corpus_address = "/Users/XiaoqiuHuang/Google_Drive/workspace/machine-reading-for-watson/data/dso/test";
		String query = "Where was the meeting between Amrozi Nurhasyim and Abu Bakar Bashir ?";
		ArrayList<String> sentences = bpd.build(corpus_address, query);
//		for (String sent : sentences) {
//			System.out.println(sent);
//		}
	}
}

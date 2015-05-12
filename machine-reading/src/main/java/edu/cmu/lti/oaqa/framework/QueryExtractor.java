package edu.cmu.lti.oaqa.framework;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;

public class QueryExtractor {
	private static String idfDictPath = null;
	private static boolean VERBOSE = false;

//	public static void setVerbose(boolean flag) {
//		VERBOSE = flag;
//	}
//	
//	public QueryExtractor() {
//		Properties prop = new Properties();
//	    try {
//			prop.load(new FileInputStream(WatsonMachineReader.settingPath));
//			idfDictPath = prop.getProperty("idfdict").trim();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
//	public ArrayList<String> TFIDF(String corpus_path,HashMap<String,String> queries, int topK) throws IOException{	
//		HashMap<String, ArrayList<String>> query_keywords = new HashMap<String, ArrayList<String>>();
//		int index = 0;
//		for(String query : queries.keySet()){
//			String path = corpus_path+index;
//			HashMap<String,Integer> word_tf = get_word_map(path);
//			HashMap<String, Double> word_tfidfscore = get_score(word_tf,idfdict_path);
//			ArrayList<String> keywords = get_keywords(word_tfidfscore,topK);
//			query_keywords.put(query, keywords);
//			index++;
//		}  
//		return query_keywords;
//	}
//
//	public static ArrayList<String> NER(ArrayList<String> sentences, int topK){
//		String serializedClassifier = "../data/classifiers/english.all.3class.distsim.crf.ser.gz";
//		AbstractSequenceClassifier<CoreLabel> classifier = null;
//		try {
//			classifier = CRFClassifier.getClassifier(serializedClassifier);
//		} catch (Exception e) {
//			System.err.println(e);
//			return null;
//		}
//		
//		final HashMap<String,Integer> mapWord = new HashMap<>();
//		List<String> candidate = new ArrayList<String>();
//		ArrayList<String> result = new ArrayList<String>();
//		/* For either a file to annotate or for the hardcoded text example,
//    	   this demo file shows two ways to process the output, for teaching
//    	   purposes.  For the file, it shows both how to run NER on a String
//    	   and how to run it on a whole file.  For the hard-coded String,
//    	   it shows how to run it on a single sentence, and how to do this
//    	   and produce an inline XML output format.
//		 */
//
//		for (String str : sentences) {
//			str = str.replaceAll("\\W+", " ");
//			String NERStr = classifier.classifyToString(str);
//			//System.out.println(NERStr);
//			String [] words = NERStr.split("\\s");
//			for(String word : words)
//			{
//				String[] NER = word.split("/");
//				if(NER.length > 1)
//				{
//					if(NER[1].compareTo("O") != 0)
//					{
//						//System.out.println(NER[1]);
//						if(mapWord.containsKey(NER[0]))
//							mapWord.put(NER[0],mapWord.get(NER[0])+1);
//						else
//							mapWord.put(NER[0],1);
//					}
//				}
//			}
//		}
//
//		candidate = new ArrayList<String>(mapWord.keySet());
//		Collections.sort(candidate, new Comparator<String>(){
//			public int compare(String o1, String o2)
//			{
//				if(mapWord.get(o1) < mapWord.get(o2))
//					return 1;
//				else if(mapWord.get(o1) == mapWord.get(o2))
//					return 0;
//				else
//					return -1;
//			}
//		});
//
//		for(int i=0;i<topK && i < candidate.size();i++){
//			result.add(candidate.get(i));
//			if (VERBOSE){
//				System.out.println(candidate.get(i)+" " + mapWord.get(candidate.get(i)) ) ;
//			}
//		}
//		return result;
//	}
//	
//	
//
//	public static void main(String[] args)
//	{
//		ArrayList<String> sentences = new ArrayList<String>();
//		sentences.add("Where was the meeting between Amrozi Nurhasyim and Abu Bakar Bashir?");
//		NER(sentences, 2);
//	}
}

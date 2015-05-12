package edu.cmu.lti.oaqa.corpus;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Qiang Zhu.
 */
public class BuildPseudoDocument_keywordsTFIDF {
	/**
	 * corpus_path: retrieved corpus pathes' prefix
	 * queries: key-queryid, value-query
	 * idfdict_path: idf dictionary path
	 * keywords_num: the number of keywords need to be returned
	 * return: key-queryid,value-keywords
	 * @throws IOException 
	 */
//	public static void main(String[] args) throws IOException{
//		String QuestionPath = "../data/dso/questions/TERRORISM-Questions.txt";
//		String corpus_path = "../explored-corpus/file";
//		HashMap<String, String> questions = rd.readQuestions(QuestionPath.get("Training"));
////		HashMap<String, String> questions = RetrievalBaselineWorkflow.readInputData(QuestionPath, 0, 69);
//		BuildPseudoDocument bpd = new BuildPseudoDocument();
//		HashMap<String,ArrayList<String>> keywords = bpd.buildPseduoDoc(corpus_path, questions);
//		for(Entry entry : keywords.entrySet()){
//			System.out.println(entry.getKey());
//			ArrayList<String> a = (ArrayList<String>) entry.getValue();
//			for(String str : a){
//				System.out.println(str);
//			}
//		}
//	}
//	
	public HashMap<String, ArrayList<String>> buildPseudoDocument(String corpus_path,HashMap<String,String> queries,String idfdict_path,int keywords_num) throws IOException{	
		HashMap<String, ArrayList<String>> query_keywords = new HashMap<String, ArrayList<String>>();
		int index = 0;
		for(String query : queries.keySet()){
			String path = corpus_path+index;
			HashMap<String,Integer> word_tf = get_word_map(path);
			HashMap<String, Double> word_tfidfscore = get_score(word_tf,idfdict_path);
			ArrayList<String> keywords = get_keywords(word_tfidfscore,keywords_num);
			query_keywords.put(query, keywords);
			index++;
		}  
		return query_keywords;
	}
	public ArrayList<String> getkeywords(ArrayList<String> rel_sentences,int keynum,String idfdict_path) throws IOException{
		StringBuffer sb = new StringBuffer();
		for(String sentence : rel_sentences){
			sb.append(sentence);
		}
		HashMap<String, Integer> word_tf = get_map(sb.toString()); 
		HashMap<String, Double> word_tfidfscore = get_score(word_tf,idfdict_path);
		ArrayList<String> keywords = get_keywords(word_tfidfscore,keynum);
		return keywords;
	}
	private ArrayList<String> get_keywords(
			HashMap<String, Double> word_tfidfscore,int keywordsnum) {
		// TODO Auto-generated method stub
		ArrayList<String> keywords = new ArrayList<String>();
		List<Map.Entry<String,Double>> list=new ArrayList<>();  
		list.addAll(word_tfidfscore.entrySet());  
		ValueComparator vc = new ValueComparator();  
		Collections.sort(list,vc);
		for(int i = 0; i < keywordsnum; i++){
			keywords.add(list.get(i).getKey());
		}
		return keywords;
	}
	private class ValueComparator implements Comparator<Map.Entry<String, Double>>    
    {    
        public int compare(Map.Entry<String, Double> mp1, Map.Entry<String, Double> mp2)     
        {    
        	if(mp2.getValue() > mp1.getValue())
        		return 1;  
        	else if(mp2.getValue() < mp1.getValue())
        		return -1;
        	else
        		return 0;
       //     return mp2.getValue() - mp1.getValue();  
        }    
    } 
	private HashMap<String, Double> get_score(HashMap<String, Integer> word_tf,
			String idfdict_path) throws IOException {
		// TODO Auto-generated method stub
		HashMap<String, Double> idf_dic = read_idfdictionary(idfdict_path);
		HashMap<String, Double> word_score = new HashMap<String, Double>();
		for(Entry<String, Integer> entry : word_tf.entrySet()){
			String word = entry.getKey();
			int tf = entry.getValue();
			double tfidf = 0;
			if(!idf_dic.containsKey(word))
				tfidf = (double)tf*Math.log(942.0);
			else
				tfidf = (double)tf*idf_dic.get(word);
			word_score.put(word, tfidf);
		}
		return word_score;
	}

	private HashMap<String, Double> read_idfdictionary(String idfdict_path) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader br = new BufferedReader(new FileReader(idfdict_path));
		HashMap<String, Double> word_idf = new HashMap<String, Double>();
		String line;
		while((line = br.readLine()) != null){
			String[] words = line.split("\t");
			double idf = Double.parseDouble(words[1]);
			word_idf.put(words[0], idf);
		}
		br.close();
		return word_idf;
	}

	private HashMap<String, Integer> get_word_map(String corpus_path) throws IOException {
		// TODO Auto-generated method stub
		StringBuffer sb = new StringBuffer();
		BufferedReader br = new BufferedReader(new FileReader(corpus_path));
//		for(int index = 0; index < corpus_docnum; index++){
//			String path = corpus_path+corpus_docnum;
//			br = new BufferedReader(new FileReader(path));
//			String line;
//			while((line = br.readLine()) != null){
//				sb.append(line.toLowerCase());
//			}
//			br.close();
//		}
		String line;
		while((line = br.readLine()) != null){
			sb.append(line.toLowerCase());
		}
		br.close();
		HashMap<String, Integer> word_map = get_map(sb.toString());
		return word_map;
	}

	private HashMap<String, Integer> get_map(String corpus) {
		// TODO Auto-generated method stub
		HashMap<String, Integer> word_map = new HashMap<String, Integer>();
		String[] words = corpus.split(" |;|\\?");
		for(String word : words){
			String str = word.replaceAll("\\W| ", "");
			if(str.length() < 1)
				continue;
			if(word_map.containsKey(str)){
				int value = word_map.get(str);
				value++;
				word_map.put(str, value);
			}
			else
				word_map.put(str, 1);
		}
		return word_map;
	}
}

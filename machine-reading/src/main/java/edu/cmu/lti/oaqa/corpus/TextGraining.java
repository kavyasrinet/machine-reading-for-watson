package edu.cmu.lti.oaqa.corpus;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class TextGraining {
	public enum Shrinking_type{
		Whole_corpus,
		Sentence,
		Passage;
	}
	public void text_shriking(Shrinking_type st, String path,HashMap<String, String> queries) throws IOException{
		switch(st){
		case Whole_corpus : put_whole(path, queries);
		case Sentence: put_sentence(path,queries);
		case Passage: put_passage(path,queries);
		}
	}
	private void put_passage(String path,HashMap<String, String> queries) throws IOException {
		// TODO Auto-generated method stub
		int index = 0;
		ArrayList<String> relSentences = null;
		for (String query : queries.values()) {
			relSentences = build_passage(path + index, query);
			index++;
		}
	}
	private ArrayList<String> build_passage(String corpus_address, String query) throws IOException {
		// TODO Auto-generated method stub
		if (query.endsWith("\\?"))
			query = query.substring(0, query.length() - 1);
		ArrayList<String> relSentences = new ArrayList<>();
		BufferedReader br = new BufferedReader(new FileReader(corpus_address));
		String[] query_word = query.split(" ");
		String line;
		while ((line = br.readLine()) != null) {
			//String[] sentences = line.split("[.|\\?]");
			//for (String sentence : sentences) {
				for (String word : query_word) {
					if (line.contains(word)) {
						relSentences.add(line);
						break;
						}
					}
				}
//			}
		br.close();
		return relSentences;
	}
	private void put_sentence(String path,HashMap<String, String> queries) throws IOException {
		// TODO Auto-generated method stub
		int index = 0;
		ArrayList<String> relSentences = null;
		for (String query : queries.values()) {
			relSentences = build_sentence(path + index, query);
			index++;
		}
	}
	private ArrayList<String> build_sentence(String corpus_address,String query) throws IOException{
		if (query.endsWith("\\?"))
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
		return relSentences;
	}
	private void put_whole(String path,HashMap<String, String> queries) throws IOException {
		// TODO Auto-generated method stub
		int index = 0;
		ArrayList<String> relSentences = null;
		for (String query : queries.values()) {
			relSentences = build_whole(path + index, query);
			index++;
		}
	}
	private ArrayList<String> build_whole(String corpus_address, String query) throws IOException {
		// TODO Auto-generated method stub
		ArrayList<String> relSentences = new ArrayList<>();
		BufferedReader br = new BufferedReader(new FileReader(corpus_address));
		String[] query_word = query.split(" ");
		String line;
		while ((line = br.readLine()) != null) {
			relSentences.add(line);
		}
		br.close();
		return relSentences;
	}
}

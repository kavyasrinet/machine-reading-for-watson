/*
 * This is the seed class for each Machine Reading State.
 * */
package edu.cmu.lti.oapa.framework;

import java.util.ArrayList;

public class Seed {
	private String query;  
	private ArrayList<Seed> neighbors;
	private double weight;
	private int questionCount;
	private int corpusCount;
	private int answerCount;
	private int topic;
	// other attributes add here
	
	public Seed(String query) {
		this.query = query;
		this.neighbors = new ArrayList<Seed>();
		this.weight = 0.0;
		this.questionCount = 0;
		this.answerCount = 0;
		this.corpusCount = 0;
		this.topic = -1;     // not belonged to any topic
	}
	// other constructors add here
	
	
	/*
	 * Following functions are Getters and Setters
	 * */
	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public ArrayList<Seed> getNeighbors() {
		return neighbors;
	}

	public void setNeighbors(ArrayList<Seed> neighbors) {
		this.neighbors = neighbors;
	}
	
	public int addNeighbor(Seed neighbor) {
		if (neighbor == null) {
			return -1;
		}else{
			neighbors.add(neighbor);
			return 1;
		}
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public int getQuestionCount() {
		return questionCount;
	}

	public void setQuestionCount(int questionCount) {
		this.questionCount = questionCount;
	}

	public int getCorpusCount() {
		return corpusCount;
	}

	public void setCorpusCount(int corpusCount) {
		this.corpusCount = corpusCount;
	}

	public int getAnswerCount() {
		return answerCount;
	}

	public void setAnswerCount(int answerCount) {
		this.answerCount = answerCount;
	}

	public int getTopic() {
		return topic;
	}

	public void setTopic(int topic) {
		this.topic = topic;
	}
}

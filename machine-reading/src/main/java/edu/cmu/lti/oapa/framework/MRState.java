package edu.cmu.lti.oapa.framework;

import java.util.ArrayList;

public class MRState {
	private ArrayList<Seed> seedSet;
	private Corpus corpus;
	private KnowledgeGraph kg;
	
	// initialize
	public MRState(){
		seedSet = new ArrayList<Seed>();
		corpus = new Corpus();
		kg = new KnowledgeGraph();
	}
	// other constructors add here
	
	// Main work flow for Machine Reading State to transit from current state
	// to next state based on different action
	public void transition() {
		
	}
	
	// Main actions
	public void expandSeeds() {
		
	}
	
	public void updateCorpus() {
		
	}
	
	// Main functions
	public void pruneCorpus() {
		
	}
	
	public void evaluateCorpus() {
		
	}
	
	public void saveCorpus() {
		
	}
	
	public void updateKnowledgeGraph() {
		
	}
}

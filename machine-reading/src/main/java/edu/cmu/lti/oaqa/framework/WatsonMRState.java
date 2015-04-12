package edu.cmu.lti.oaqa.framework;

import java.util.ArrayList;

public class WatsonMRState implements MRState{
	private ArrayList<Seed> seeds;
	private Corpus corpus;
	private KnowledgeGraph kg;
	private MRState parentState;
	private ArrayList<MRState> childrenStates;
	private double threshold;
	
	// Other fields add here
	
	// Constructor
	public WatsonMRState(ArrayList<Seed> seeds, Corpus corpus, KnowledgeGraph kg, double threshold) {
		this.seeds = seeds;
		this.corpus = corpus;
		this.kg = kg;
		this.threshold = threshold;
	}

	@Override
	public void transition() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateSeeds() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateCorpus() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pruneCorpus() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void evaluateCorpus() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveCorpus() {
		// TODO Auto-generated method stub
		
	}

	/*
	 * Following functions are getters and setters.
	 * */
	public ArrayList<Seed> getSeeds() {
		return seeds;
	}

	public void setSeeds(ArrayList<Seed> seeds) {
		this.seeds = seeds;
	}

	public Corpus getCorpus() {
		return corpus;
	}

	public void setCorpus(Corpus corpus) {
		this.corpus = corpus;
	}

	public KnowledgeGraph getKg() {
		return kg;
	}

	public void setKg(KnowledgeGraph kg) {
		this.kg = kg;
	}

	public MRState getParentState() {
		return parentState;
	}

	public void setParentState(MRState parentState) {
		this.parentState = parentState;
	}

	public ArrayList<MRState> getChildrenStates() {
		return childrenStates;
	}

	public void setChildrenStates(ArrayList<MRState> childrenStates) {
		this.childrenStates = childrenStates;
	}

	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}
}

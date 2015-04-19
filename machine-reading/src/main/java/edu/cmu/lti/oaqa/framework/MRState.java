package edu.cmu.lti.oaqa.framework;

public interface MRState {
	// Main work flow for Machine Reading State to transit from current state
	// to next state based on different action
	public MRState transition() throws Exception;
	
	// Main actions
	public void updateSeeds();
	
	public void updateCorpus();
	
	// Main functions
	public void pruneCorpus();
	

	public void saveCorpus();

	public double evaluateCorpus(String mode) throws Exception;
}

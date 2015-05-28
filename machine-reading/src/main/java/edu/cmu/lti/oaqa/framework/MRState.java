package edu.cmu.lti.oaqa.framework;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

public interface MRState {
	// Main work flow for Machine Reading State to transit from current state
	// to next state based on different action
	public MRState transition() throws Exception;
	
	// Main actions
	public void updateSeeds() throws FileNotFoundException;
	
	public void updateCorpus();
	
	// Main functions
	public void pruneCorpus();
	

	public void saveCorpus() throws FileNotFoundException, IOException;

	public double evaluateCorpus(String mode) throws Exception;

	public void sourceExpansion() throws FileNotFoundException, IOException, URISyntaxException;
}

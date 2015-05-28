package edu.cmu.lti.oaqa.framework;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;

import edu.cmu.lti.oaqa.agent.BoilerpipeCachedClient;

public class WatsonMRState implements MRState{
	private ArrayList<Seed> seeds;
	private Corpus corpus;
	private Corpus newcorpus;
	private KnowledgeGraph kg;
	private MRState parentState;
	private ArrayList<MRState> childrenStates;
	private double threshold;
	private boolean doneReading = false;
	
	// Other fields add here
	
	// Constructor
	public WatsonMRState(ArrayList<Seed> seeds, Corpus corpus, KnowledgeGraph kg, double threshold) {
		this.seeds = seeds;
		this.corpus = corpus;
		this.kg = kg;
		this.threshold = threshold;
	}

	@Override
	public MRState transition() throws FileNotFoundException, IOException, URISyntaxException
	{
		// TODO Auto-generated method stub
		
		MRState new_state =  new WatsonMRState(this.seeds, this.corpus, this.kg, this.threshold);
		double a = 0.0;
		if((a = evaluateCorpus("Development")) < threshold)
		{
			System.out.println("BAR now is:" + a );
			
			new_state.sourceExpansion();
			new_state.updateCorpus();
		//	new_state.pruneCorpus();
		//	new_state.updateSeeds();

		}
		else
		{
			doneReading = true;
		}

		return new_state;
	}
	@Override
	public void sourceExpansion() throws URISyntaxException, IOException{
		Properties prop = new Properties();
		
	    try {
			prop.load(new FileInputStream(WatsonMachineReader.settingPath));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    String corpusPath = prop.getProperty("corpus");
	    String accountKey = prop.getProperty("account_key");
	    int retrieveNum = Integer.parseInt(prop.getProperty("retrieve_number"));
	    
	    ReadData rd = new ReadData();
	    HashMap<String, String> questions = rd.readQuestions(prop.getProperty("q_training").trim());
	    HashMap<String, HashSet<String>> answers = rd.readAnswers(prop.getProperty("a_training").trim());
		
		SourceExpansioner se = new SourceExpansioner(accountKey, corpusPath);
		
		se.setVerbose(true);
		if(this.seeds.size() <= 0)
			this.newcorpus = se.sourceExpansion(questions, answers, retrieveNum);
		else
			this.newcorpus = se.sourceExpansionSeed(seeds,retrieveNum);
		
	}
	@Override
	public void updateSeeds() throws FileNotFoundException {
		// TODO Auto-generated method stub
		Properties prop = new Properties();
		
	    try {
			prop.load(new FileInputStream(WatsonMachineReader.settingPath));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    String corpusPath = prop.getProperty("corpus");
	    String accountKey = prop.getProperty("account_key");
	    int retrieveNum = Integer.parseInt(prop.getProperty("retrieve_number"));
	    
	    ReadData rd = new ReadData();
	    HashMap<String, String> questions = rd.readQuestions(prop.getProperty("q_training").trim());
	    HashMap<String, HashSet<String>> answers = rd.readAnswers(prop.getProperty("a_training").trim());
	    
		QueryExtractor qe = new QueryExtractor(answers);
		qe.setVerbose(true);
		
		this.seeds = qe.getOverlapTerms(newcorpus);
	}

	@Override
	public void updateCorpus() {
		// TODO Auto-generated method stub
		BoilerpipeCachedClient client = new BoilerpipeCachedClient();
		
		for(Document doc : newcorpus.getDocs()){
			System.out.println("Fetching: " + doc.getUrl() +"...");
			String content = client.fetch(doc.getUrl());
			doc.setContent(content);
		}
		
		ArrayList<Document> docs = corpus.getDocs();
		
		for(Document doc : newcorpus.getDocs()){
			docs.add(doc);
		}
	}

	@Override
	public void pruneCorpus() {
		// TODO Auto-generated method stub
		CorpusPruner cp = new CorpusPruner();
		cp.setVerbose(true);
		
		this.corpus = cp.prune(corpus);
	}

	@Override
	public double evaluateCorpus(String mode){
		// TODO Auto-generated method stub
		double recall = 0.0;
		try{
			Evaluation eval = new Evaluation(corpus,WatsonMachineReader.developmentData,WatsonMachineReader.testData);
			recall =  eval.BinaryAnswerRecall(mode);
		}catch( Exception e){
			e.printStackTrace();
			System.exit(1);
		}finally{		
			return recall;
		}
	}

	@Override
	public void saveCorpus() throws IOException {
		// TODO Auto-generated method stub
		BufferedReader br = new BufferedReader(new FileReader("urls.txt"));
		FileWriter fw = new FileWriter(new File("corpus.txt"));
		
		BoilerpipeCachedClient client = new BoilerpipeCachedClient();
		
		String line = null;
		while((line = br.readLine()) != null){
			System.out.println("Fetching: " + line +"...");
			
			String content = client.fetch(line);
			//String content = getContent(line);
			
			fw.write(line);
			fw.write(content);
		}
		
		br.close();
		fw.close();
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

	public boolean isDoneReading()
	{
		return doneReading;
	}

	public void setDoneReading(boolean doneReading)
	{
		this.doneReading = doneReading;
	}

	public Corpus getNewcorpus() {
		return newcorpus;
	}

	public void setNewcorpus(Corpus newcorpus) {
		this.newcorpus = newcorpus;
	}
}

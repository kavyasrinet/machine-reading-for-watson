package edu.cmu.lti.oaqa.framework;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

public class WatsonMachineReader implements MachineReader
{
	
	static ArrayList<QuestionData> trainingData;
	static ArrayList<QuestionData> developmentData;
	static ArrayList<QuestionData> testData;
	static WatsonMRState rootState;
	public static String settingPath = "src/main/config/setting";
	@Override
	public void initializeReader(ArrayList<QuestionData> TrainingData)
	{
		ArrayList<Seed> Seeds = new ArrayList<Seed>();
		
		for(QuestionData q : TrainingData)
		{

			Seeds.add(new Seed(q.question));
			for(String answer : q.answer)	
				Seeds.add(new Seed(answer));			
		}
		Corpus corpus = new Corpus();
		KnowledgeGraph kg = new KnowledgeGraph();
		double threshold = 0.9;
		rootState = new WatsonMRState(Seeds, corpus, kg, threshold);
		
	}
	
	public static void main(String args[]) throws Exception
	{
		Properties prop = new Properties();
	    prop.load(new FileInputStream(settingPath));
	    		
		ReadData rd = new ReadData();
		trainingData = rd.readQuestionData(prop.getProperty("q_training").trim(), prop.getProperty("a_training").trim());
		developmentData = rd.readQuestionData(prop.getProperty("q_development").trim(), prop.getProperty("a_development").trim());
		testData = rd.readQuestionData(prop.getProperty("q_test").trim(), prop.getProperty("a_test").trim());

		
	}

}

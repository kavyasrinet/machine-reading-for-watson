package edu.cmu.lti.oaqa.framework;

import java.util.ArrayList;
import java.util.HashMap;

public class WatsonMachineReader implements MachineReader
{
	
	static ArrayList<QuestionData> TrainingData;
	static ArrayList<QuestionData> DevelopmentData;
	static ArrayList<QuestionData> TestData;
	static WatsonMRState RootState;
	@Override
	public void initializeReader(ArrayList<QuestionData> TrainingData)
	{
		ArrayList<Seed> Seeds = new ArrayList<Seed>();
		
		for(QuestionData q : TrainingData)
		{

			Seeds.add(new Seed(q.Question));
			for(String answer : q.Answer)	
				Seeds.add(new Seed(answer));			
		}
		Corpus corpus = new Corpus();
		KnowledgeGraph kg = new KnowledgeGraph();
		double threshold = 0.9;
		RootState = new WatsonMRState(Seeds, corpus, kg, threshold)
		
	}

	public static void makePath(HashMap<String,String> QuestionPath,HashMap<String, String> AnswerPath ) {
		QuestionPath.put("Training", "../data/dso/questions/train_set.txt");
		QuestionPath.put("Development", "../data/dso/questions/dev_set.txt");
		QuestionPath.put("Test", "../data/dso/questions/test_set.txt");
		
		AnswerPath.put("Training", "../data/dso/gold_standard/train_set.txt");
		AnswerPath.put("Development", "../data/dso/gold_standard/dev_set.txt");
		AnswerPath.put("Test", "../data/dso/gold_standard/test_set.txt");
	}
	public static void main(String args[]) throws Exception
	{
		
		HashMap<String,String> QuestionPath = new HashMap<String, String>();
		HashMap<String, String> AnswerPath = new HashMap<String, String>();
		makePath(QuestionPath,AnswerPath);
		
		ReadData rd = new ReadData();
		TrainingData = rd.readQuestionData(QuestionPath.get("Training"), AnswerPath.get("Training"));
		DevelopmentData = rd.readQuestionData(QuestionPath.get("Development"), AnswerPath.get("Development"));
		TestData = rd.readQuestionData(QuestionPath.get("Test"), AnswerPath.get("Test"));
		
	}

}

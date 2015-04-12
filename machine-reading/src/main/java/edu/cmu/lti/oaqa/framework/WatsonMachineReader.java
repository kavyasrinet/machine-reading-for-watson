package edu.cmu.lti.oaqa.framework;

import java.util.ArrayList;

public class WatsonMachineReader implements MachineReader
{
	
	ArrayList<QuestionData> TrainingData;
	ArrayList<QuestionData> DevelopmentData;
	ArrayList<QuestionData> TestData;
	
	@Override
	public void initializeReader(String Questionpath, String Answerpath)
	{
		
	}

}

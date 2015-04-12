package edu.cmu.lti.oaqa.framework;

import java.util.ArrayList;

public class QuestionData
{
	public String Question;
	public ArrayList<String> Answer;
	
	public QuestionData(String question, ArrayList<String> answer){
		Question = question;
		Answer = answer;
	}
}

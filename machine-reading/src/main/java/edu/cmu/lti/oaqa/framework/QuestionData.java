package edu.cmu.lti.oaqa.framework;

import java.util.ArrayList;

public class QuestionData
{
	public String question;
	public ArrayList<String> answer;
	
	public QuestionData(String question, ArrayList<String> answer){
		this.question = question;
		this.answer = answer;
	}
}

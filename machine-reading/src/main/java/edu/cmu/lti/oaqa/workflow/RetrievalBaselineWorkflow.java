package edu.cmu.lti.oaqa.workflow;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import edu.cmu.lti.oapa.evaluation.Evaluation;
import edu.cmu.lti.oaqa.expansion.ReadData;
import edu.cmu.lti.oaqa.expansion.SourceExpansion;

/**
 * @author Di Wang, Qiang Zhu, Xiaoqiu Huang, Liping Xiong, Yepeng Yin, Kavay
 * @since 2015-2-7
 */

public class RetrievalBaselineWorkflow {
	static boolean done = false;
	static String accountKey = "8WDj5gva1guOq+un0mhRx75ozDz7Sd4BmJwhgY0T2wY";
	static String QuestionPath = "../data/dso/questions/TERRORISM-Questions.txt";
	static String AnswerPath = "../data/dso/gold_standard/TERRORISM-Questions-key.txt";
	static String corpusPath = "../explored-corpus/file";
	
	public static void main(String[] args) throws URISyntaxException,
			IOException, BoilerpipeProcessingException {
		HashMap<String,String> QuestionPath = new HashMap<>();
		HashMap<String, String> AnswerPath = new HashMap<>();
		makePath(QuestionPath, AnswerPath);
		// step 1: read data
		ReadData rd = new ReadData();
		HashMap<String, String> questions = rd.readQuestions(QuestionPath.get("Training"));
		HashMap<String, HashMap<String, Integer>> answers = rd.readAnswer(AnswerPath.get("Training"));
	
		// source expansion
		SourceExpansion se = new SourceExpansion();
		//se.sourceExpansion(questions, answers, true, 1);
		
		// iterate expansion
		se.iterateExpasion(questions, answers, 2);
		
		// evaluation
		Evaluation eva = new Evaluation();
		eva.baseline(AnswerPath, se.getCorpusPath());
	}	
	
	public static void makePath(HashMap<String,String> QuestionPath,HashMap<String, String> AnswerPath ) {
		QuestionPath.put("Training", "../data/dso/questions/train_set.txt");
		QuestionPath.put("Development", "../data/dso/questions/dev_set.txt");
		QuestionPath.put("Test", "../data/dso/questions/test_set.txt");
		
		AnswerPath.put("Training", "../data/dso/gold_standard/train_set.txt");
		AnswerPath.put("Development", "../data/dso/gold_standard/dev_set.txt");
		AnswerPath.put("Test", "../data/dso/gold_standard/test_set.txt");
	}
}

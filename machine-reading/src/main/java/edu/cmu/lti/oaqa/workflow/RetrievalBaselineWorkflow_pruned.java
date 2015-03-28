package edu.cmu.lti.oaqa.workflow;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import edu.cmu.lti.oapa.evaluation.Evaluation;
import edu.cmu.lti.oaqa.corpus.CorpusPruner;
import edu.cmu.lti.oaqa.expansion.ReadData;
import edu.cmu.lti.oaqa.expansion.SourceExpansion;

/**
 * @author Di Wang, Qiang Zhu, Xiaoqiu Huang, Liping Xiong, Yepeng Yin, Kavay
 * @since 2015-2-7
 */

public class RetrievalBaselineWorkflow_pruned {
	public static void main(String[] args) throws URISyntaxException,
			IOException, BoilerpipeProcessingException, ClassCastException, ClassNotFoundException {
		HashMap<String,String> QuestionPath = new HashMap<>();
		HashMap<String, String> AnswerPath = new HashMap<>();
		makePath(QuestionPath, AnswerPath);
		// step 1: read data
		ReadData rd = new ReadData();
		HashMap<String, String> questions = rd.readQuestions(QuestionPath.get("Training"));
		HashMap<String, HashSet<String>> answers = rd.readAnswer(AnswerPath.get("Training"));
	
		// source expansion
		SourceExpansion se = new SourceExpansion();
	se.sourceExpansion(questions, answers, true, 1);
		
		// iterate expansion
		se.iterateExpasion(questions, answers,2);
		
		// evaluation
		
		System.out.println( new File(se.getCorpusPath()).length());

		Evaluation eva = new Evaluation();
		eva.BinaryAnswerRecall(AnswerPath, se.getCorpusPath());
		
		CorpusPruner cp = new CorpusPruner(se);
		String corpus_pruned = cp.prune_corpus();

		System.out.println( new File(corpus_pruned).length());
		
		eva = new Evaluation();
		eva.BinaryAnswerRecall(AnswerPath, corpus_pruned);
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

package edu.cmu.lti.oaqa.workflow;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.hadoop.hdfs.server.namenode.status_jsp;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import edu.cmu.lti.oaqa.evaluation.Evaluation;
import edu.cmu.lti.oaqa.framework.ReadData;
import edu.cmu.lti.oaqa.framework.SourceExpansion;

/**
 * @author Di Wang, Qiang Zhu, Xiaoqiu Huang, Liping Xiong, Yepeng Yin, Kavay
 * @since 2015-2-7
 */

public class RetrievalBaselineWorkflow {
	private static String accountKey = "TT15WkPtBHfSFTPRJLAMsEgjeaII0J8A7wAEP9J/Hk4";
	private static String corpusPath = "../data/dso/explored-corpus/file.html";
	
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
		SourceExpansion se = new SourceExpansion(accountKey, corpusPath);
		//se.sourceExpansion(questions, answers, true, 1);
		
		// iterate expansion
//		se.iterateExpasion(questions, answers,1);
		
		// evaluation
		Evaluation eva = new Evaluation();
		eva.BinaryAnswerRecallWatson(QuestionPath, AnswerPath);
		
		// write html format
		RandomAccessFile raf = new RandomAccessFile(new File(corpusPath), "rw");
		raf.seek(0);
		raf.write("<!DOCTYPE html>\n<html>\n".getBytes());
		raf.seek(raf.length());
		raf.write("</html>".getBytes());
		raf.close();
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

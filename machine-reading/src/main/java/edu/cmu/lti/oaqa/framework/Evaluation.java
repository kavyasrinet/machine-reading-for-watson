package edu.cmu.lti.oaqa.framework;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

import edu.cmu.lti.oaqa.watson.QAApiQuerier;

public class Evaluation {
	private static ArrayList<QuestionData> developmentSet = null;
	private static ArrayList<QuestionData> testSet = null;
	private ArrayList<Document> documents;

	private static boolean VERBOSE = false;

	public static void setVerbose(boolean flag) {
		VERBOSE = flag;
	}

	public Evaluation(ArrayList<QuestionData> devSet,
			ArrayList<QuestionData> testSet) {
		Evaluation.developmentSet = devSet;
		Evaluation.testSet = testSet;
	}

	public Evaluation(Corpus corpus) {
		this.documents = corpus.getDocs();
	}

	private double computeBinaryAnswerRecall(ArrayList<QuestionData> dataSet) {
		if (dataSet.size() == 0) {
			return 0;
		}
		// create a temp List to compute the BAR
		ArrayList<QuestionData> tempDataSet = new ArrayList<QuestionData>(
				dataSet);

		// Iterate all the corpus to compute the BAR.
		for (int i = 0; i < documents.size(); i++) {
			String curDocText = documents.get(i).getTitle()
					+ documents.get(i).getContent();

			// start from the end to avoid errors due to remove in the middle of
			// the process
			int start = tempDataSet.size() - 1;
			for (int j = start; j >= 0; j--) {
				ArrayList<String> curAnswerList = tempDataSet.get(j).answer;
				for (int k = 0; k < curAnswerList.size(); k++) {
					// if contain one candidate answer, then this question can
					// be answered
					if (curDocText.contains(curAnswerList.get(k))) {
						tempDataSet.remove(j);
						break;
					}
				}
			}

			// all question can be answered
			if (tempDataSet.size() == 0) {
				break;
			}
		}

		return (double) (dataSet.size() - tempDataSet.size()) / dataSet.size();
	}

	private double computeBinaryAnswerRecallWatson(ArrayList<QuestionData> dataSet) throws Exception {
		// create a temp List to compute the BAR
		ArrayList<QuestionData> tempDataSet = new ArrayList<QuestionData>(dataSet);
		
		// Watson querier
		QAApiQuerier querier = new QAApiQuerier();
		JSONObject watsonAnswer = null;
		
		int start = tempDataSet.size() - 1;
		
		for (int i = start; i >= 0; i--) {
			String question = tempDataSet.get(i).question;
			ArrayList<String> answers = tempDataSet.get(i).answer;
			if(VERBOSE){
				System.out.println("question :" + question);
			}
			
			// ask Waston
			watsonAnswer = querier.fetch(question, true);
			watsonAnswer = watsonAnswer.getJSONObject("question");
			JSONArray ja = watsonAnswer.getJSONArray("evidencelist");
			
			// use the evidencelist to compute the BAR
			for (int j = 0; j < ja.length(); j++) {
				JSONObject temp = (JSONObject) ja.get(j);
				
				// if contain one candidate answer, then this question can
				// be answered
				for (int k = 0; k < answers.size(); k++) {
					if (temp.getString("text").contains(answers.get(k))) {
						tempDataSet.remove(i);
						answers = null;
						break;
					}
				}
				
				if(answers == null){
					break;
				}
			}
		}

		return (double) (dataSet.size() - tempDataSet.size()) / dataSet.size();
	}

	public double BinaryAnswerRecallWatson(String mode) throws Exception {
		if (VERBOSE) {
			System.out.println("Computing binary answer recall using Watson...");
		}

		double recall = 0.0;

		if (mode.equals("Development")) {
			recall = computeBinaryAnswerRecallWatson(developmentSet);
			System.out.println("The recall of development set is :" + recall);
		} else if (mode.equals("Test")) {
			recall = computeBinaryAnswerRecallWatson(testSet);
			System.out.println("The recall of test set is :" + recall);
		} else {
			System.err.println("Invalid evaluation type:" + mode);
			System.err
					.println("The type should be either Development or Test.");
			System.exit(1);
		}

		return recall;
	}

	/*
	 * Compute BAR based on different set of data.
	 * 
	 * @param mode - Define the type of data.
	 */
	public double BinaryAnswerRecall(String mode) throws Exception {
		if (VERBOSE) {
			System.out.println("Computing binary answer recall...");
		}

		double recall = 0.0;

		if (mode.equals("Development")) {
			recall = computeBinaryAnswerRecall(developmentSet);
			System.out.println("The recall of development set is :" + recall);
		} else if (mode.equals("Test")) {
			recall = computeBinaryAnswerRecall(testSet);
			System.out.println("The recall of test set is :" + recall);
		} else {
			System.err.println("Invalid evaluation type:" + mode);
			System.err
					.println("The type should be either Development or Test.");
			System.exit(1);
		}

		return recall;
	}

}

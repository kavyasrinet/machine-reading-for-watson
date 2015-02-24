package edu.cmu.lti.oaqa.corpus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;


/** This is a demo of calling CRFClassifier programmatically.
 *
 *  @author Xiaoqiu Huang
 */

public class BuildPseudoDocument_keywordsNER {
	private static boolean VERBOSE = true;

	public static ArrayList<String> getKeywords(ArrayList<String> sentences, int topK) throws ClassCastException, ClassNotFoundException, IOException{

		String serializedClassifier = "../data/classifiers/english.all.3class.distsim.crf.ser.gz";

		AbstractSequenceClassifier<CoreLabel> classifier = CRFClassifier.getClassifier(serializedClassifier);
		final HashMap<String,Integer> mapWord = new HashMap<>();
		List<String> candidate = new ArrayList<String>();
		ArrayList<String> result = new ArrayList<String>();
		/* For either a file to annotate or for the hardcoded text example,
    	   this demo file shows two ways to process the output, for teaching
    	   purposes.  For the file, it shows both how to run NER on a String
    	   and how to run it on a whole file.  For the hard-coded String,
    	   it shows how to run it on a single sentence, and how to do this
    	   and produce an inline XML output format.
		 */

		for (String str : sentences) {
			str = str.replaceAll("\\W+", " ");
			String NERStr = classifier.classifyToString(str);
			//System.out.println(NERStr);
			String [] words = NERStr.split("\\s");
			for(String word : words)
			{
				String[] NER = word.split("/");
				if(NER.length > 1)
				{
					if(NER[1].compareTo("O") != 0)
					{
						//System.out.println(NER[1]);
						if(mapWord.containsKey(NER[0]))
							mapWord.put(NER[0],mapWord.get(NER[0])+1);
						else
							mapWord.put(NER[0],1);
					}
				}
			}
		}

		candidate = new ArrayList<String>(mapWord.keySet());
		Collections.sort(candidate, new Comparator<String>(){
			public int compare(String o1, String o2)
			{
				if(mapWord.get(o1) < mapWord.get(o2))
					return 1;
				else if(mapWord.get(o1) == mapWord.get(o2))
					return 0;
				else
					return -1;
			}
		});

		for(int i=0;i<topK && i < candidate.size();i++){
			result.add(candidate.get(i));
			if (VERBOSE)
				System.out.println(candidate.get(i)+" " + mapWord.get(candidate.get(i)) ) ;
		}
		return result;
	}

	public static void main(String[] args) throws ClassCastException, ClassNotFoundException, IOException
	{
		ArrayList<String> sentences = new ArrayList<String>();
		sentences.add("Where was the meeting between Amrozi Nurhasyim and Abu Bakar Bashir?");
		getKeywords(sentences, 2);
	}
}

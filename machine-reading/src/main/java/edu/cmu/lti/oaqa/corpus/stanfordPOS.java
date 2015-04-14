package edu.cmu.lti.oaqa.corpus;

import java.util.List;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class stanfordPOS {
	
	public static void main(String [] args) {
		String posClassifier = "../data/classifiers/left3words-wsj-0-18.tagger";
		
		MaxentTagger tagger = new MaxentTagger(posClassifier);
//		List<List<HasWord>> sentences = MaxentTagger.tokenizeText(new BufferedReader(new FileReader(args[1])));
		String Sentence = "Where was the aba";
		String tSentence = tagger.tagTokenizedString(Sentence);
		System.out.println(tSentence);
	}
}

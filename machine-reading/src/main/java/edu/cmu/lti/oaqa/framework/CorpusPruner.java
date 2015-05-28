package edu.cmu.lti.oaqa.framework;

import java.util.ArrayList;
import java.util.HashMap;

public class CorpusPruner {
	private static boolean VERBOSE = false;

	public static void setVerbose(boolean flag) {
		VERBOSE = flag;
	}
	
	public Corpus prune(Corpus rawcorpus){
		Corpus pruned = new Corpus();
		
		ArrayList<Document> docs = rawcorpus.getDocs();
		HashMap<String, Document> urlmap = new HashMap<String, Document>();
		
		for(Document doc : docs){
			if(urlmap.containsKey(doc.getUrl()))
				continue;
			else{
				urlmap.put(doc.getUrl(), doc);
			}
		}
		
		ArrayList<Document> pruneddocs = new ArrayList<Document>();
		
		for(String url : urlmap.keySet()){
			pruneddocs.add(urlmap.get(url));
		}
		
		return pruned;
	}
}

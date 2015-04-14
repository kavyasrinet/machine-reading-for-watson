/*
 * This is the class of corpus for Machine Reading State.
 * */
package edu.cmu.lti.oaqa.framework;

import java.util.ArrayList;

public class Corpus {
	private ArrayList<Document> docs;  // may be change to different types, such as String
	
	public Corpus(){
		this.docs = new ArrayList<Document>();
	}
	
	public void addDoc(Document doc){
		docs.add(doc);
	}
	
	/*
	 * Following functions are Getters and Setters
	 * */
	public ArrayList<Document> getDocs() {
		return docs;
	}

	public void setDocs(ArrayList<Document> docs) {
		this.docs = docs;
	}
	
	
}

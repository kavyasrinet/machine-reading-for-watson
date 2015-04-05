/*
 * This is the class of corpus for Machine Reading State.
 * */
package edu.cmu.lti.oapa.framework;

import java.util.ArrayList;
import org.w3c.dom.Document;


public class Corpus {
	private ArrayList<String> urls;
	private ArrayList<Document> docs;  // may be change to different types, such as String
	
	public Corpus(){
		this.urls = new ArrayList<String>();
		this.docs = new ArrayList<Document>();
	}
	
	public void addDoc(String url, Document doc){
		urls.add(url);
		docs.add(doc);
	}
	
	/*
	 * Following functions are Getters and Setters
	 * */
	public ArrayList<String> getUrls() {
		return urls;
	}

	public void setUrls(ArrayList<String> urls) {
		this.urls = urls;
	}

	public ArrayList<Document> getDocs() {
		return docs;
	}

	public void setDocs(ArrayList<Document> docs) {
		this.docs = docs;
	}
	
	
}

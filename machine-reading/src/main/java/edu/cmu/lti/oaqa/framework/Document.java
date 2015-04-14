package edu.cmu.lti.oaqa.framework;

import edu.cmu.lti.oaqa.search.RetrievalResult;


public class Document {
	private String text;
	private String url;
	private String queryString;
	
	public Document(RetrievalResult rr){
		this.text = rr.getText();
		this.url = rr.getUrl();
		this.setQueryString(rr.getQueryString());
	}

	/*
	 * Following are getters and setters.
	 * */
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getQueryString() {
		return queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}
}

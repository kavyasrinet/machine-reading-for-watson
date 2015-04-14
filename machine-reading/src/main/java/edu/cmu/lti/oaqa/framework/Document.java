package edu.cmu.lti.oaqa.framework;

import edu.cmu.lti.oaqa.agent.BoilerpipeCachedClient;
import edu.cmu.lti.oaqa.search.RetrievalResult;

public class Document {
	private String title;
	private String content = null; // Web page content
	private String url;
	private String queryString;

	private static boolean VERBOSE = false;

	public static void setVerbose(boolean flag) {
		VERBOSE = flag;
	}

	public Document(RetrievalResult rr) {
		this.title = rr.getText();
		this.url = rr.getUrl();
		this.queryString = rr.getQueryString();
	}

	/*
	 * Following are getters and setters.
	 */
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		if (content == null) {
			BoilerpipeCachedClient client = new BoilerpipeCachedClient();
			if (VERBOSE) {
				System.out.println("Fetching: " + url);
			}
			this.content = client.fetch(url);
			client = null;
		}
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}

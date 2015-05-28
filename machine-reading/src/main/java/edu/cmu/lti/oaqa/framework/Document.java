package edu.cmu.lti.oaqa.framework;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

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
	public static String getContent(String url){
		BoilerpipeCachedClient client = new BoilerpipeCachedClient();
		if (VERBOSE) {
			System.out.println("Fetching: " + url);
		}
		String content = null;
		try{
			if(url.contains("dtatravel")){
				client = null;
				return content;
				}
			content = client.fetch(url);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			client = null;
			return content;
		}
	}
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("urls.txt"));
		FileWriter fw = new FileWriter(new File("corpus.txt"));
		BoilerpipeCachedClient client = new BoilerpipeCachedClient();
		
		String line = null;
		while((line = br.readLine()) != null){
			System.out.println("Fetching: " + line +"...");
			
			String content = client.fetch(line);
			//String content = getContent(line);
			
			fw.write(line);
			fw.write(content);
		}
		
		br.close();
		fw.close();
	}
}

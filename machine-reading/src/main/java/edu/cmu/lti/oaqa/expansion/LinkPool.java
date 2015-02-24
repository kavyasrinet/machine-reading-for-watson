package edu.cmu.lti.oaqa.expansion;

import java.util.ArrayList;

/**
 * This class is used to create link pool.
 * 
 * @author Yepeng Yin
 */

public class LinkPool {
	private ArrayList<String> urlPool;
	
	public LinkPool() {
		urlPool = new ArrayList<String>();
	}
	
	
	
	public ArrayList<String> getUrlPool() {
		return urlPool;
	}
	
}

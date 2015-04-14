package edu.cmu.lti.oaqa.framework;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import org.rendersnake.HtmlCanvas;

public class HtmlSaver {
	
	private static HtmlSaver htmlSaver = null;
	private static String corpusPath;
	
	private HtmlSaver(){
		
	}
	
	// Singleton Design Pattern
	public static HtmlSaver getInstance() throws FileNotFoundException, IOException{
		if (htmlSaver == null){
			htmlSaver = new HtmlSaver();
			Properties prop = new Properties();
		    prop.load(new FileInputStream(WatsonMachineReader.settingPath));
		    corpusPath = prop.getProperty("corpus");
		    prop = null;
		}
		return htmlSaver;
	}

	public void saveToFile(Corpus corpus) throws IOException{
		ArrayList<Document> documents = corpus.getDocs();
		HtmlCanvas html = new HtmlCanvas();
		FileWriter fw = new FileWriter(new File(corpusPath));
		fw.write("<!DOCTYPE html>\n<html>\n");
		
		for (int i = 0; i < documents.size(); i++) {
			html.section()						 			 // section
				.h1().content((documents.get(i).getTitle())) // title
				.write(documents.get(i).getContent())
				._section();
			fw.write(html.toHtml());
			
			html = null;
			html = new HtmlCanvas();
		}
		
		fw.write("</html>");
		fw.close();
	}
	
	public static void main(String[] args) throws IOException {
		// Example to use renderSnake
		HtmlCanvas html = new HtmlCanvas();
		html
		.html()
		  .section()
		  .h1().content("Title")
		  .write("Content")
		  ._section()
		._html();
		
		System.out.println(html.toHtml());
	}
}

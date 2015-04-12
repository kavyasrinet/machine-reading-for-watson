package edu.cmu.lti.oaqa.corpus;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import edu.cmu.lti.oaqa.framework.SourceExpansion;

public class CorpusPruner {

	String corpus_path;
	public CorpusPruner(SourceExpansion se)
	{
		this.corpus_path = se.getCorpusPath();
	}
	
	public String prune_corpus() throws IOException
	{
		ArrayList<String> pruned_passages = new ArrayList<String>();
		{
			String corpus;
			try(BufferedReader br = new BufferedReader(new FileReader(corpus_path))) 
			{
		        StringBuilder sb = new StringBuilder();
		        String line = br.readLine();
	
		        while (line != null) 
		        {
		            sb.append(line);
		            sb.append(System.lineSeparator());
		            line = br.readLine();
		        }
		        corpus= sb.toString();
		        br.close();
			}
		
	
		
			
			String[] passages = corpus.split("\n");
			System.out.println(passages.length);
			Set<String> passageset = new HashSet<String>();
			for(int i=0;i<passages.length;i++)
			{
				String sentence = passages[i];
				if(!passageset.contains(sentence)) 
				{
					passageset.add(sentence);
					pruned_passages.add(sentence);
				}
				else
					passages[i] = "";
			}
		
			System.out.println(pruned_passages.size());
		StringBuilder builder = new StringBuilder();
		for(String s : pruned_passages) 
		{
		    builder.append(s +" \n ");
		}
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(corpus_path+"_pruned"));
		
        writer.write(builder.toString());
       
        writer.close();
		}
/*		
		System.out.println("HERE");
		
		Set<String> sentenceset = new HashSet<String>();
        ArrayList<String> pruned_sentences = new ArrayList<String>();
        
       
        for(String passage: pruned_passages)
        {
        
        	String[] sentences = passage.split(".");
        	for(int i = 0; i<sentences.length;i++)
        		if(!sentenceset.contains(sentences[i]))
        			{
        			
        				sentenceset.add(sentences[i]);
        				pruned_sentences.add(sentences[i]);
        			}

        		
        }
        
        StringBuilder builder = new StringBuilder();
		for(String s : pruned_sentences) 
		{
		    builder.append(s +" . ");
		}
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(corpus_path+"_pruned"));
		
        writer.write(builder.toString());
        writer.close();
    	*/
		
        return corpus_path+"_pruned";
	}
	
}

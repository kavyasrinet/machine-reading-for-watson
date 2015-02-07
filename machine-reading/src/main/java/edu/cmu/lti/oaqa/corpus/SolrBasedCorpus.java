
package edu.cmu.lti.oaqa.corpus;

import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.MapSolrParams;
import org.apache.solr.common.params.SolrParams;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Di Wang.
 */
public class SolrBasedCorpus {

  public static void main(String[] args) throws SolrServerException {

    SolrServer solrServer = new HttpSolrServer("http://ur.lti.cs.cmu.edu:8984/solr/WikipediaFull");

    String query = "Pittsburgh";
    int batch = 1000;

    String docFolder = "./data/Pittsburgh/";

    for (int i = 0; i < (double) 34407; i += batch) {
      HashMap<String, String> hshParams = new HashMap<String, String>();
      hshParams.put("q", query);
      hshParams.put("start", String.valueOf(i));
      hshParams.put("rows", String.valueOf(batch));
      hshParams.put("fl", "Text");
      SolrParams solrParams = new MapSolrParams(hshParams);
      QueryResponse qryResponse = solrServer.query(solrParams, SolrRequest.METHOD.POST);
      SolrDocumentList results = qryResponse.getResults();

      int count = results.size();
      System.out.println(count + " received hits");
      for (int j = 0; j < count; j++) {
        SolrDocument hitDoc = results.get(j);
        //System.out.println("#" + (j + 1) + ":" + hitDoc.getFieldValue("Text"));
        ArrayList<String> contents = (ArrayList<String>) hitDoc.getFieldValue("Text");
        StringBuffer sb = new StringBuffer();
        for (String content : contents) {
          sb.append(content).append('\n');
        }

        String content = sb.toString();
        if (content.length() < 1000) {
          continue;
        }
        //saveDoc(docFolder + String.valueOf(i + j) + ".txt", (ArrayList<String>) hitDoc.getFieldValue("Text"));
        saveDoc(docFolder + String.valueOf(i + j) + ".txt", sb.toString());
      }

    }
  }

  public static void saveDoc(String fileName, String content) {
    //System.out.println(fileName);
    FileWriter output = null;
    try {
      output = new FileWriter(fileName);
      BufferedWriter writer = new BufferedWriter(output);
      writer.write(content);
      writer.close();

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (output != null) {
        try {
          output.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

  }

  public static void saveDoc(String fileName, ArrayList<String> contents) {
    //System.out.println(fileName);
    FileWriter output = null;
    try {
      output = new FileWriter(fileName);
      BufferedWriter writer = new BufferedWriter(output);
      for (String content : contents) {
        writer.write(content);
        writer.newLine();
      }
      writer.close();

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (output != null) {
        try {
          output.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

  }
}

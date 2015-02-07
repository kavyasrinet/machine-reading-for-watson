package edu.cmu.lti.oaqa.agent;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Fetch Wikipedia plain text content
 * @author Di Wang
 *
 */
public class WikipediaTextCachedClient extends AbstractCachedJsonServiceFetcher {

  @Override
  protected String makeQueryUrl(String query) {
    return "http://en.wikipedia.org/w/api.php?action=query&prop=extracts&format=json&explaintext=&exsectionformat=wiki&redirects=&titles="
            + query;
  }

  public Map<String, String> getWikipediaPlainText(String title){
    JSONObject jsonResult = fetch(title);
    JSONObject pages = jsonResult.getJSONObject("query").getJSONObject("pages");
    Iterator<?> pageIds = pages.keys();
    Map<String, String> titleDocument = new HashMap<String, String>();
    while(pageIds.hasNext()){
      String pageId = (String) pageIds.next();
      String docText = pages.getJSONObject(pageId).getString("extract");
      String docTitle = pages.getJSONObject(pageId).getString("title");
      titleDocument.put(docTitle, docText);
    }
    return titleDocument;
  }

//  public static void main(String[] args) throws UnknownHostException {
//    WikipediaTextCachedClient aClient = new WikipediaTextCachedClient();
//    aClient.getWikipediaPlainText("Ouyang_Xiu");
//  }

}

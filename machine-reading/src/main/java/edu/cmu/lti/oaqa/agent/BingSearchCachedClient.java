package edu.cmu.lti.oaqa.agent;

//import edu.cmu.lti.neal.dexp.search.BingSearch;
//import edu.cmu.lti.neal.dexp.search.Result;
import edu.cmu.lti.oaqa.search.BingSearch;
import edu.cmu.lti.oaqa.search.Result;
import edu.cmu.lti.oaqa.server.RetrievalResult;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class BingSearchCachedClient extends AbstractCachedFetcher<ArrayList<Result>>{

  private String AccountKey;

  private boolean VERBOSE = false;

  public void setVerbose(boolean flag) {
    VERBOSE = flag;
  }

  private int resultSetSize;

//  private MongoObjectCache<List<Result>> cacheServer;

  public void initialize(String accountKey) {
    this.AccountKey = accountKey;

    resultSetSize = 10;
    //  cacheServer = new MongoObjectCache<List<Result>>(this.getClass().getSimpleName());
  }

  public BingSearchCachedClient() {
    super();
    initialize("8rGwctRzE7AS3BpOLiFqmyVu2Vk3i9INMaPzT/dcsX4=");
    setResultSetSize(10);
  }

  public void setResultSetSize(int size) {
    resultSetSize = size;
  }

  public List<RetrievalResult> retrieveDocuments(String question) throws URISyntaxException {

    List<RetrievalResult> documents = new ArrayList<RetrievalResult>(0);
    if (question.isEmpty())
      return documents;

    List<Result> resultL; // = new ArrayList<Result>();

    // Sending the request to the Web Search Service and get the response.
    // 1. Executing the direct question as the query
    String requestURL = BingSearch.buildRequest(question, resultSetSize);
    if (VERBOSE)
      System.out.println("Bing Search : " + question);

    resultL = getResults(requestURL, question);

    // remove characters that are not supported by UIMA
    for (Result result : resultL) {
      String s = result.getAnswer();
      StringBuilder sb = new StringBuilder();
      for (char c : s.toCharArray()) {
        if (c > 0x1f) {
          sb.append(c);
        } else {
          sb.append(' ');
        }
      }

      documents.add(new RetrievalResult(result.getURL(), result.getScore(), sb.toString(), result
              .getRank(), result.getQuery()));
    }

    return documents;
  }

  private List<Result> getResults(String requestURL, String question) {
    ArrayList<Result> results = null;
    try {
      results = cacheServer.get(requestURL);
      if (results == null) {
        results = fetchOnline(requestURL, question);
        cacheServer.put(requestURL, results);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return results;
  }

  private ArrayList<Result> fetchOnline(String requestURL, String question)
          throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
    ArrayList<Result> resultL = new ArrayList<Result>();
    Document doc = BingSearch.getResponse(requestURL, this.AccountKey);
    if (doc != null) {
      List<Result> tmpResult = BingSearch.processResponse(doc, question);
      resultL.addAll(tmpResult);
    }
    return resultL;
  }

  public static void main(String[] args) throws URISyntaxException {
    BingSearchCachedClient bing = new BingSearchCachedClient();
    List<RetrievalResult> results = bing
            .retrieveDocuments("Where was the attack on West Gate Mall staged?");

    for (RetrievalResult result : results) {
      System.out.println(result.getDocID());
      System.out.println(result.getText());
    }
  }

  @Override
  ArrayList<Result> fetchOnline(String key) {
    return null;
  }

}

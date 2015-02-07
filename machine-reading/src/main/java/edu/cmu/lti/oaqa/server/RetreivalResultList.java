package edu.cmu.lti.oaqa.server;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Wrapper of ArrayList<RetrievalResult> for web service
 * @author Di Wang
 *
 */
public class RetreivalResultList{

  ArrayList<RetrievalResult> results;

  public RetreivalResultList() {
    super();
    results = new ArrayList<RetrievalResult>();
  }

  public ArrayList<RetrievalResult> getResults() {
    return results;
  }

  public void setResults(ArrayList<RetrievalResult> results) {
    this.results = results;
  }

  public void add(RetrievalResult retrievalResult) {
    results.add(retrievalResult);
  }
  
  public void addAll(Collection<RetrievalResult> retrievalResults) {
    results.addAll(retrievalResults);
  }
  
  @Override
  public String toString() {
    StringBuilder out = new StringBuilder();
    for (RetrievalResult o : results)
    {
      out.append(o.toString());
      out.append("\n ++++++++++++++++++++\n");
    }
    return out.toString();
  }
  
}

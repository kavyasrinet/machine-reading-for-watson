package edu.cmu.lti.oaqa.agent;

import com.google.common.collect.ArrayListMultimap;
import com.hp.hpl.jena.query.*;

import java.util.Iterator;
import java.util.Map.Entry;

/**
 * TODO Try to replace ARQ which seems badly engineered
 *
 * @author Di Wang
 *
 */
public class SparqlCachedClient extends
        AbstractCachedFetcher<ArrayListMultimap<String, String>> {

  // static final String endpoint = "http://dbpedia.org/sparql";
  static final String endpoint = "http://gold.lti.cs.cmu.edu:8892/sparql";

  @Override
  public ArrayListMultimap<String, String> fetchOnline(String query) {

    QueryExecution exec = QueryExecutionFactory.sparqlService(endpoint, QueryFactory.create(query));

    ResultSet results = exec.execSelect();
    ArrayListMultimap<String, String> queryResults = ArrayListMultimap.create();

    while (results.hasNext()) {
      QuerySolution soln = results.nextSolution();
      Iterator<String> varNames = soln.varNames();
      while (varNames.hasNext()) {
        String varName = varNames.next();
        String solution = soln.get(varName).toString();
        queryResults.put(varName, solution);
      }
    }
    return queryResults;
  }

  public ArrayListMultimap<String, String> queryDbpediaExternalLink(String resourceUri) {
    String query = " SELECT DISTINCT ?link WHERE { \n <" + resourceUri
            + "> <http://dbpedia.org/ontology/wikiPageExternalLink> ?link  \n}";
    return fetch(query);
  }

  public ArrayListMultimap<String, String> mapDbpediaToFreebase(String resourceUri) {
    String query = " SELECT DISTINCT ?fb WHERE { \n <" + resourceUri
            + "> <http://www.w3.org/2002/07/owl#sameAs> ?fb  \n}";
    return fetch(query);
  }

  public ArrayListMultimap<String, String> mapFreebaseToDbpedia(String resourceUri) {
    String query = " SELECT DISTINCT ?dp WHERE { \n ?dp <http://www.w3.org/2002/07/owl#sameAs> <"
            + resourceUri + ">  \n}";
    return fetch(query);
  }

  public ArrayListMultimap<String, String> mapFreebaseMIDToDbpedia(String mid) {
    return mapFreebaseToDbpedia("http://rdf.freebase.com/ns/" + mid.replaceFirst("/", "."));
  }

  public ArrayListMultimap<String, String> mapDbpediaToFreebaseMID(String resourceUri) {
    ArrayListMultimap<String, String> midResults = ArrayListMultimap.create();
    ArrayListMultimap<String, String> results = mapDbpediaToFreebase(resourceUri);
    for (String fb : results.values()) {
      String mid = fb.substring(fb.lastIndexOf("/") + 1).replaceFirst("\\.", "/");
      midResults.put("mid", mid);
    }
    return midResults;
  }

  public static void main(String[] args) {
    SparqlCachedClient sparql = new SparqlCachedClient();
    ArrayListMultimap<String, String> results = sparql.
    // .queryDbpediaExternalLink("http://dbpedia.org/resource/Westgate_Mall_(Bethlehem)");
    // .queryDbpediaExternalLink("http://dbpedia.org/resource/Machine_learning");
    // mapDbpediaToFreebase("http://dbpedia.org/resource/Machine_learning");
    // mapFreebaseMIDToDbpedia("m/0bx1k");
            mapDbpediaToFreebaseMID("http://dbpedia.org/resource/Tang_Dynasty");
    System.out.println(results.size());
    for (Entry<String, String> entry : results.entries()) {
      System.out.println(entry.getKey());
      System.out.println(entry.getValue());
    }
  }
}

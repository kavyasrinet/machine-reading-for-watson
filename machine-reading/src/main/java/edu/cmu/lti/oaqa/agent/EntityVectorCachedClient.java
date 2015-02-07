package edu.cmu.lti.oaqa.agent;

import com.google.common.collect.ArrayListMultimap;
import edu.cmu.lti.oaqa.vector.WordEntry;
import edu.cmu.lti.oaqa.vector.WordVectorServerMain;


import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * Fetch DBPedia entity vector from its URI
 *
 * @author diwang
 */
public class EntityVectorCachedClient extends AbstractCachedFetcher<List<WordEntry>> {

  private SparqlCachedClient sparql;

  public EntityVectorCachedClient() {
    super();
    sparql = new SparqlCachedClient();
  }

  public static void main(String[] args) {
    EntityVectorCachedClient client = new EntityVectorCachedClient();
    List<WordEntry> results = client.fetch("http://dbpedia.org/resource/Tang_Dynasty");
    System.out.println(results);
  }

  public List<WordEntry> querySimilarMid(String word) {
    WebTarget target = ClientBuilder.newClient().target(WordVectorServerMain.BASE_URI);
    List<WordEntry> results = target.path("FreebaseVector").request()
            .post(Entity.entity(word, MediaType.TEXT_PLAIN), new GenericType<List<WordEntry>>() {
            });

    if (results == null) {
      results = new ArrayList<WordEntry>();
    }

    return results;
  }

  @Override
  public List<WordEntry> fetchOnline(String concept) {

    ArrayListMultimap<String, String> midResults = sparql.mapDbpediaToFreebaseMID(concept);
    String mid = getFirstValue(midResults);
    List<WordEntry> simEntities = querySimilarMid(mid);
    for (WordEntry entry : simEntities) {
      ArrayListMultimap<String, String> dps = sparql.mapFreebaseMIDToDbpedia(entry.getName());
      entry.setName(getFirstValue(dps));
    }
    return simEntities;
  }

  String getFirstValue(ArrayListMultimap<String, String> midResults) {
    String result = null;
    for (String mid : midResults.values()) {
      result = mid;
      break;
    }
    return result;
  }

}

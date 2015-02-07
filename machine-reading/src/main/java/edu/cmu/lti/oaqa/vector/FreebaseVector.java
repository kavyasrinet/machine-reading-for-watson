package edu.cmu.lti.oaqa.vector;

import javax.inject.Singleton;
import javax.ws.rs.Path;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;

@Path("FreebaseVector")
@Singleton
public class FreebaseVector extends GWordVector {

  public FreebaseVector() {
    super();
    try {
      loadModel("/usr0/home/diw1/data/freebase-vectors-skipgram1000.bin.gz");
      // loadModel("/usr0/home/diw1/data/alzheimer_vector.bin");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static List<WordEntry> querySimilarMid(String word) {
    WebTarget target = ClientBuilder.newClient().target(WordVectorServerMain.BASE_URI);
    List<WordEntry> results = target.path("FreebaseVector").request()
            .post(Entity.entity(word, MediaType.TEXT_PLAIN), new GenericType<List<WordEntry>>() {
            });
    return results;
  }

  public static void main(String[] args) {

    // WebTarget target = ClientBuilder.newClient().target(WordVectorServerMain.BASE_URI);
    // List<WordEntry> results = target.path("FreebaseVector").request()
    // .post(Entity.entity("mouse", MediaType.TEXT_PLAIN), new GenericType<List<WordEntry>>() {
    // });

    List<WordEntry> results = querySimilarMid("m/0bx1k");
    System.out.println(results);

  }

  // @POST
  // @Produces(MediaType.APPLICATION_JSON)
  // @Override
  // public Set<WordEntry> similarWordsMultiThread(String word) {
  // return super.similarWordsMultiThread(word);
  // }

}

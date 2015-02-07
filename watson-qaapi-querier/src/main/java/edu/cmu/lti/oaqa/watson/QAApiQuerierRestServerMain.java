package edu.cmu.lti.oaqa.watson;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import java.io.IOException;
import java.net.URI;

/**
 * QApi Querier Rest Server Main class.
 */
@Path("answer")
public class QAApiQuerierRestServerMain {

  // Base URI the Grizzly HTTP server will listen on
//  public static final String BASE_URI = "http://gold.lti.cs.cmu.edu:8070/qa/";
  public static final String BASE_URI = "http://localhost:8070/qa/";

  /**
   * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
   *
   * @return Grizzly HTTP server.
   */
  public static HttpServer startServer() {
    // create a resource config that scans for JAX-RS resources and providers
    // in com.example package
    final ResourceConfig rc = new ResourceConfig(QAApiQuerierRestServerMain.class);

    // create and start a new instance of grizzly http server
    // exposing the Jersey application at BASE_URI
    return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
  }

  /**
   * Main method.
   *
   * @param args
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {
    final HttpServer server = startServer();
    System.out.println(String.format("Jersey app started with WADL available at "
            + "%sapplication.wadl\nHit q to stop it...", BASE_URI));

    while (System.in.read() != 'q') {
    }
    server.shutdownNow();
  }

  @GET
  @Produces("text/html")
  public String simpleAnswerer(@QueryParam("q") String q) {
    String answer;
    try {
      QAApiQuerier querier = new QAApiQuerier();
      JSONObject jsonOutput = querier.fetch(q);
      answer = jsonOutput.toString();
    } catch (IOException e) {
      e.printStackTrace();
      answer = e.toString();
    }
    System.out.println("answer = " + answer);
    return answer;
  }
}

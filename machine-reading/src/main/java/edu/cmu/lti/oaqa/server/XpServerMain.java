package edu.cmu.lti.oaqa.server;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;

/**
 * Main class.
 * 
 */
public class XpServerMain {
  // Base URI the Grizzly HTTP server will listen on
  // public static final String BASE_URI = "http://localhost:8080/xp/";
  public static final String BASE_URI = "http://gold.lti.cs.cmu.edu:8070/xp/";

  /**
   * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
   * 
   * @return Grizzly HTTP server.
   */
  public static HttpServer startServer() {
    // create a resource config that scans for JAX-RS resources and providers
    // in com.example package
    final ResourceConfig rc = new ResourceConfig(XpSearcher.class);

    // create and start a new instance of grizzly http server
    // exposing the Jersey application at BASE_URI
    return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
  }

  /**
   * Main method.
   * 
   * @param args
   * @throws java.io.IOException
   */
  public static void main(String[] args) throws IOException {
    final HttpServer server = startServer();
    System.out.println(String.format("Jersey app started with WADL available at "
            + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));

    while (System.in.read() != 'q') {
    }

    server.shutdownNow();
  }
}

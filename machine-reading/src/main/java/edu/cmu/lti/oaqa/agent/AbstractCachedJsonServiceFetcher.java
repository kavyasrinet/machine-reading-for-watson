

package edu.cmu.lti.oaqa.agent;

import edu.cmu.lti.oaqa.cache.MongoJsonCache;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Abstract web service agent.
 *
 * @author Di Wang
 */

public abstract class AbstractCachedJsonServiceFetcher extends AbstractCachedFetcher<JSONObject> {

  // Create an instance of HttpClient.
  protected static HttpClient client = new HttpClient();

  public Logger LOG = Logger.getLogger(this.getClass());

  protected AbstractCachedJsonServiceFetcher() {
    cacheServer = new MongoJsonCache(this);
  }

  public String request(HttpMethod method) {
    String response = null;
    // Provide custom retry handler is necessary
    method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
            new DefaultHttpMethodRetryHandler(3, false));
    try {
      // Execute the method.
      int statusCode = client.executeMethod(method);

      if (statusCode != HttpStatus.SC_OK) {
        System.err.println("Method failed: " + method.getStatusLine());
      }
      // Read the response body.
      byte[] responseBody = method.getResponseBody();
      // Deal with the response.
      // Use caution: ensure correct character encoding and is not binary data
      response = new String(responseBody);
    } catch (HttpException e) {
      System.err.println("Fatal protocol violation: " + e.getMessage());
    } catch (IOException e) {
      System.err.println("Fatal transport error: " + e.getMessage());
      System.err.println(method.getQueryString());
    } finally {
      // Release the connection.
      method.releaseConnection();
    }
    return response;
  }

  @Override
  public JSONObject fetchOnline(String text) {

    String spotlightResponse = null;
    GetMethod getMethod = new GetMethod(makeQueryUrl(text));
    getMethod.addRequestHeader(new Header("Accept", "application/json"));
    try {
      System.out.println("querying online: " + getMethod.getURI());
    } catch (URIException e1) {
      e1.printStackTrace();
    }
    spotlightResponse = request(getMethod);

    assert spotlightResponse != null;

    JSONObject resultJSON = null;

    try {
      resultJSON = new JSONObject(spotlightResponse);
      //System.out.println(spotlightResponse);
    } catch (JSONException e) {
      System.err.println("Received invalid response from DBpedia Spotlight API.");
    }
    return resultJSON;
  }

  protected abstract String makeQueryUrl(String query);

}

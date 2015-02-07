package edu.cmu.lti.oaqa.watson;

import edu.cmu.lti.oaqa.cache.MongoJsonCache;
import edu.cmu.lti.oaqa.agent.AbstractCachedFetcher;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Properties;

/**
 * Watson QAAPI client
 *
 * @author Di Wang.
 */
public class QAApiQuerier extends AbstractCachedFetcher<JSONObject> {

  static final String QAPI_URL = "https://watson-wdc01.ihost.com/instance/508/deepqa/v1/question";

  static final HttpHost QAPI_TARGET = new HttpHost("watson-wdc01.ihost.com", 443, "https");

  static String defaultCredentialPropPath = "src/main/config/credential.properties";

  static boolean renewCache = true;

  Logger logger = LoggerFactory.getLogger(QAApiQuerier.class);

  CloseableHttpClient httpClient;

  int xSyncTimeout = 30;

  public QAApiQuerier() throws IOException {
    this(defaultCredentialPropPath);
  }

  public QAApiQuerier(String credentialPropPath) throws IOException {
    super(new MongoJsonCache(QAApiQuerier.class));
    Properties prop = new Properties();
    prop.load(new FileInputStream(credentialPropPath));
    String username = prop.getProperty("user").trim();
    String password = prop.getProperty("password").trim();

    CredentialsProvider credsProvider = new BasicCredentialsProvider();

    credsProvider.setCredentials(
            new AuthScope(QAPI_TARGET.getHostName(), QAPI_TARGET.getPort()),
            new UsernamePasswordCredentials(username, password));

    httpClient = HttpClientBuilder
            .create().setDefaultCredentialsProvider(credsProvider).build();

    // Create AuthCache instance
    AuthCache authCache = new BasicAuthCache();
    BasicScheme basicAuth = new BasicScheme();
    authCache.put(QAPI_TARGET, basicAuth);

    // Add AuthCache to the execution context
    HttpClientContext localContext = HttpClientContext.create();
    localContext.setAuthCache(authCache);
  }

  public static void main(String[] args) throws IOException {
    QAApiQuerier querier = new QAApiQuerier();
    JSONObject jsonOutput = querier.fetch(
            "Where is Carnegie Mellon University?", renewCache);
    System.out.println(jsonOutput);
  }

  public JSONObject fetchJsonFromPost(HttpPost httpPost) throws IOException {
    CloseableHttpResponse response = httpClient.execute(httpPost);
    if (response.getStatusLine().getStatusCode() != 200) {
      throw new RuntimeException("Failed : HTTP error code : "
              + response.getStatusLine().getStatusCode());
    }
    String responseString = "";
    try {
      HttpEntity entity = response.getEntity();
      BufferedReader rd = new BufferedReader(
              new InputStreamReader(response.getEntity().getContent()));
      StringBuffer result = new StringBuffer();
      String line;
      while ((line = rd.readLine()) != null) {
        result.append(line);
      }
      responseString = result.toString();
      EntityUtils.consume(entity);
    } finally {
      response.close();
    }

    JSONObject resultJSON = null;
    try {
      resultJSON = new JSONObject(responseString);
    } catch (JSONException e) {
      logger.error("Received invalid response from service.");
    }
    return resultJSON;
  }

  String createJsonQuery(String question) {
    HashMap<String, Object> question_info = new HashMap<String, Object>();
    question_info.put("questionText", question);
    JSONObject questionObject = new JSONObject().put("question", question_info);
    return questionObject.toString();
  }

  @Override
  public JSONObject fetchOnline(String question) {
    JSONObject fetchedJson = null;
    try {
      HttpPost httpPost = new HttpPost(QAPI_URL);
      httpPost.setHeader("Accept", "application/json");
      httpPost.setHeader("X-SyncTimeout", String.valueOf(xSyncTimeout));

      StringEntity input = new StringEntity(createJsonQuery(question));
      input.setContentType("application/json");
      httpPost.setEntity(input);
      fetchedJson = fetchJsonFromPost(httpPost);
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return fetchedJson;
  }
}

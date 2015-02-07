package edu.cmu.lti.oaqa.server;

import com.google.common.collect.ArrayListMultimap;
import edu.cmu.lti.oaqa.agent.*;
import edu.cmu.lti.oaqa.passage.PassageExtractor;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Root resource (exposed at "xpsearcher" path)
 */

@Path("xpsearcher")
public class XpSearcher {

  static final private DBpediaSpotlightClient spotlight  = new DBpediaSpotlightClient();

  static private WikipediaTextCachedClient wikiClient= new WikipediaTextCachedClient();

  static private SparqlCachedClient sparql = new SparqlCachedClient();

  static private BoilerpipeCachedClient boilerpipe = new BoilerpipeCachedClient();

  static private BingSearchCachedClient bing = new BingSearchCachedClient();

  static private CharArraySet stopwords = StopAnalyzer.ENGLISH_STOP_WORDS_SET;

  static private PassageExtractor pe = new PassageExtractor();



  public XpSearcher() {
      System.out.println("Initialization Done.");
  }

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public RetreivalResultList retrieveDocuments(RetrievalQuery query) {

    RetreivalResultList results = new RetreivalResultList();
    String question = query.getQuestionText();

    // ArrayList<String> keyWords = new ArrayList<String>();
    // StringTokenizer st = new StringTokenizer(question);
    // while (st.hasMoreElements()) {
    // String word = st.nextElement().toString().toLowerCase();
    // if (stopwords.contains(word)) {
    // keyWords.add(word);
    // }
    // }

    List<RetrievalResult> entityDocs = retrieveEntityDocuments(question);
    results.addAll(entityDocs);

    List<RetrievalResult> bingDocs = retrieveBingDocuments(question);
    results.addAll(bingDocs);

    // CacheControl cc = new CacheControl();
    // cc.setNoCache(true);
    // return javax.ws.rs.core.Response.ok().cacheControl(cc).entity(results).build();

/*    try {
      Directory dir = pe.createIndex(results);
      results = pe.retrieve(dir, question);
    } catch (IOException e) {
      e.printStackTrace();
    }*/

    return results;
  }

  public List<RetrievalResult> retrieveEntityDocuments(String question) {
    List<RetrievalResult> results = new ArrayList<RetrievalResult>();

    try {
      ArrayList<String> entities = spotlight.extractEntities(question);
      for (String uri : entities) {
        System.out.println(uri);
        String title = uri.substring(uri.lastIndexOf("/") + 1);
        Map<String, String> titleDoc = wikiClient.getWikipediaPlainText(title);
        for (Entry<String, String> entry : titleDoc.entrySet()) {
          // RetrievalResult wikiPage = new RetrievalResult(entry.getKey(), 1, entry.getValue(), 1,
          // question);
          // results.addAll(splitRetrievalResult(wikiPage, keyWords)); / entry.getValue().length()
          if (!entry.getValue().isEmpty()) {
            results.add(new RetrievalResult(entry.getKey(), 1.0 / entry.getValue().length(), entry
                    .getValue(), 1, question));
          }
        }

        System.out.println(title);
        ArrayListMultimap<String, String> refLinks = sparql.queryDbpediaExternalLink(uri);
        System.out.println("refLinks.size(): "+refLinks.size());
        for (Entry<String, String> refLink : refLinks.entries()) {
          System.out.println(refLink.getValue());
          String pageContent = boilerpipe.fetch(refLink.getValue());
          if (pageContent == null || pageContent.isEmpty()) {
            continue;
          }
          // RetrievalResult linkedPage = new RetrievalResult(refLink.getValue(), 0.9,
          // pageContent,
          // 2, question);
          // results.addAll(splitRetrievalResult(linkedPage, keyWords));/
          //
          results.add(new RetrievalResult(refLink.getValue(), 1.0 / (pageContent.length()
                  * refLinks.size() / 2), pageContent, refLinks.size() / 2, question));
        }

      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return results;
  }

  public List<RetrievalResult> retrieveBingDocuments(String query) {
    List<RetrievalResult> results = new ArrayList<RetrievalResult>();
    try {
      List<RetrievalResult> searchSnippets = bing.retrieveDocuments(query);
      results.addAll(searchSnippets);

      ArrayList<String> urls = new ArrayList<String>();
      for (RetrievalResult snippet : searchSnippets) {
        urls.add(snippet.getDocID());
      }
      System.out.println("urls.size() = " + urls.size());
      LinkedHashMap<String, String> url2contents = boilerpipe.batchFetch(urls);

      for (RetrievalResult snippet : searchSnippets) {
        String url = snippet.getDocID();
        String pageContent = url2contents.get(url);
        if (pageContent == null || pageContent.isEmpty()) {
          continue;
        }
        results.add(new RetrievalResult(url,
                1.0 / (pageContent.length() * (snippet.getRank() + 1)), pageContent, snippet
                        .getRank(), query));
        // RetrievalResult searchedPage = new RetrievalResult(url, 0.9, pageContent,
        // snippet.getRank(), question);
        // results.addAll(splitRetrievalResult(searchedPage, keyWords));
      }

    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
    return results;
  }

  List<RetrievalResult> splitRetrievalResult(RetrievalResult largePage, ArrayList<String> keyWords) {
    // HashSet<String> keyWords = new HashSet<String>();

    List<RetrievalResult> smallRetrievalResults = new ArrayList<RetrievalResult>();
    String largeText = largePage.getText();
    String[] lines = largeText.split("\\r?\\n\\n+"); // TODO use real sentence split

    for (String line : lines) {
      line = line.trim();
      if (line.isEmpty()) {
        continue;
      }

      boolean containKeyword = false;
      String lowerLine = line.toLowerCase();
      for (String keyWord : keyWords) {
        if (lowerLine.contains(keyWord)) {
          containKeyword = true;
          break;
        }
      }

      containKeyword = true; // XXX
      if (containKeyword) {
        smallRetrievalResults.add(new RetrievalResult(largePage.getDocID(), largePage.getScore(),
                line, largePage.getRank(), largePage.getQueryString()));
      }

    }

    return smallRetrievalResults;
  }

  public static void main(String[] args) throws IOException {

    XpSearcher XpSearcher = new XpSearcher();
    RetrievalQuery query = new RetrievalQuery();
    // query.setQuestionText("Where was the meeting between Amrozi Nurhasyim and Abu Bakar Bashir?");
    // RetreivalResultList results = XpSearcher.retrieveDocuments(query);
    // System.out.println(results);

    File folder = new File("july108/");
    for (final File fileEntry : folder.listFiles()) {
      System.out.println(fileEntry.getName());
      for (String line : Files.readAllLines(fileEntry.toPath(), Charset.defaultCharset())) {
        System.out.println(line);
        String[] id_q = line.split("\\|");
        query.setQuestionText(id_q[1]);
        RetreivalResultList results = XpSearcher.retrieveDocuments(query);
        // System.out.println(results);
        PrintWriter out = new PrintWriter("xp-july108/" + fileEntry.getName() + "-" + id_q[0]);
        out.println(results);
        out.close();
        // break;
      }
    }

  }
}

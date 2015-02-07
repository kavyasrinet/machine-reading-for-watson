package edu.cmu.lti.oaqa.passage;

import edu.cmu.lti.oaqa.annotator.ScnlpAnnotator;
import edu.cmu.lti.oaqa.server.RetreivalResultList;
import edu.cmu.lti.oaqa.server.RetrievalResult;
import edu.cmu.lti.oaqa.cache.MongoPojoCache;
import edu.stanford.nlp.ling.CoreAnnotations.CharacterOffsetBeginAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.CharacterOffsetEndAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PassageExtractor {

  static final String CONTENT_FIELD = "contents";

  Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_45);

  private ScnlpAnnotator scnlp;

  public PassageExtractor() {
    super();
    scnlp = new ScnlpAnnotator();
    MongoPojoCache<Annotation> cacheServer = new MongoPojoCache<Annotation>(
            this.getClass());
    scnlp.setCacheServer(cacheServer);
    scnlp.setPipeline("tokenize, ssplit");
  }

  public static void main(String[] args) {
    PassageExtractor pe = new PassageExtractor();
    for (String sent : pe
            .splitSentence(
                    "Barack Hussein Obama II (Listeni born August 4, 1961) is the 44th and current President of the United States, and the first African American to hold the office. Born in Honolulu, Hawaii, Obama is a graduate of Columbia University and Harvard Law School, where he served as president of the Harvard Law Review. He was a community organizer in Chicago before earning his law degree. He worked as a civil rights attorney and taught constitutional law at the University of Chicago Law School from 1992 to 2004. He served three terms representing the 13th District in the Illinois Senate from 1997 to 2004, running unsuccessfully for the United States House of Representatives in 2000.")) {
      System.out.println(sent);
    }
  }

  public List<String> splitSentence(String document) {
    List<String> sents = new ArrayList<String>();
    try {
      Annotation annotations = scnlp.getAnnotation(document);
      for (CoreMap sentenceAnn : annotations.get(SentencesAnnotation.class)) {
        int sentBegin = sentenceAnn.get(CharacterOffsetBeginAnnotation.class);
        int sentEnd = sentenceAnn.get(CharacterOffsetEndAnnotation.class);
        sents.add(document.substring(sentBegin, sentEnd));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return sents;
  }

  @Deprecated
  public Directory createIndex(RetreivalResultList results) throws IOException {
    return createSentenceIndex(results.getResults());
  }

  public Directory createSentenceIndex(List<RetrievalResult> results) {
    Directory dir = new RAMDirectory();
    Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_45);
    IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_45, analyzer);
    iwc.setOpenMode(OpenMode.CREATE);
    try {
      IndexWriter writer = new IndexWriter(dir, iwc);
      for (RetrievalResult result : results) {
        Document doc = new Document();

        for (String sentence : splitSentence(result.getText())) {
          doc.add(new TextField(CONTENT_FIELD, sentence, Field.Store.YES));
        }
        writer.addDocument(doc);
      }
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return dir;
  }

  public RetreivalResultList retrieve(Directory dir, String query) {

    RetreivalResultList results = new RetreivalResultList();

/*    try {
      IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(dir));
      Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_45);

      QueryParser parser = new QueryParser(Version.LUCENE_45, CONTENT_FIELD, analyzer);
      query = query.replaceAll("\\?", "");
      Query luceneQuery = parser.parse(query);
      System.out.println("Searching: " + luceneQuery.toString());
      final int topHitsNum = 50;
      TopDocs topHits = searcher.search(luceneQuery, topHitsNum);
      topHits.getMaxScore();
      // AssertionScore assertScore = createAssertionScore(aJCas, topHits.getMaxScore(),
      // "MaxScore");
      // assertScores.add(assertScore);

      ScoreDoc[] hits = topHits.scoreDocs;
      System.out.println("hits.length " + hits.length);

      int numOutputs = Math.min(hits.length, topHitsNum);
      for (int i = 0; i < numOutputs; i++) {
        Document doc = searcher.doc(hits[i].doc);
        String topSent = doc.get(CONTENT_FIELD);
        System.out.println("topSent " + topSent);
        results.add(new RetrievalResult("" + hits[i].doc, hits[i].score, topSent, 1, query));
      }

    } catch (IOException e) {
      e.printStackTrace();
    } catch (ParseException e) {
      e.printStackTrace();
    }*/

    return results;
  }

  public TopDocs retrieveTopHits(Directory dir, String query) {

    if (query == null || query.isEmpty()) {
      System.err.println("Searching Empty query!");
      query = "SearchingEmptyQuery";
    }

    try {
      IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(dir));

      query = query.replaceAll("\\?", "");

//      QueryParser parser = new QueryParser(Version.LUCENE_45, CONTENT_FIELD, analyzer);
//      Query luceneQuery = parser.parse(query);

      Query luceneQuery = buildQuery(query);


      System.out.println("Searching: " + luceneQuery.toString());
      final int topHitsNum = 50;
      TopDocs topHits = searcher.search(luceneQuery, topHitsNum);
      return topHits;
    } catch (IOException e) {
      e.printStackTrace();
    }

    return null;
  }

  BooleanQuery buildQuery(String text) throws IOException {
    BooleanQuery termsQuery = new BooleanQuery();
    TokenStream tokStream
            = analyzer.tokenStream(CONTENT_FIELD, text);
    try {
      tokStream.reset();
      CharTermAttribute terms =
              tokStream.addAttribute(CharTermAttribute.class);
//      OffsetAttribute offsetAttribute = tokenStream.addAttribute(OffsetAttribute.class);
      while (tokStream.incrementToken()) {
//        int startOffset = offsetAttribute.startOffset();
//        int endOffset = offsetAttribute.endOffset();
        termsQuery.
                add(new TermQuery(new Term(CONTENT_FIELD,
                                terms.toString())),
                        BooleanClause.Occur.SHOULD
                );
      }
      tokStream.end();
    } finally {
      tokStream.close();
    }
    return termsQuery;
  }
}

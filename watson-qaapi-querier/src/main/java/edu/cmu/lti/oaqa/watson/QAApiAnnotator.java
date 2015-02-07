package edu.cmu.lti.oaqa.watson;

import java.io.IOException;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.json.JSONObject;

import edu.cmu.lti.oaqa.watson.types.Query;
import edu.cmu.lti.oaqa.watson.types.Response;

public class QAApiAnnotator extends JCasAnnotator_ImplBase {

  QAApiQuerier querier;

  @Override
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    super.initialize(aContext);
    try {
      querier = new QAApiQuerier();
    } catch (IOException e) {
      throw new ResourceInitializationException();
    }

  }

  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    Query query = JCasUtil.selectSingle(aJCas, Query.class);
    String questionText = query.getQuestion().getQuestionText();

    JSONObject responseJson = querier.fetch(questionText);
    Response response = new Response(aJCas);
    response.setResponsedJson(responseJson.toString());
  }

}

package edu.cmu.lti.oaqa.server;

import java.util.List;

public class RetrievalQuery {
  List<String> keyterms;

  List<String> keyphrases;

  String questionText;

  String answerType;

  public List<String> getKeyterms() {
    return keyterms;
  }

  public void setKeyterms(List<String> keyterms) {
    this.keyterms = keyterms;
  }

  public List<String> getKeyphrases() {
    return keyphrases;
  }

  public void setKeyphrases(List<String> keyphrases) {
    this.keyphrases = keyphrases;
  }

  public String getQuestionText() {
    return questionText;
  }

  public void setQuestionText(String questionText) {
    this.questionText = questionText;
  }

  public String getAnswerType() {
    return answerType;
  }

  public void setAnswerType(String answerType) {
    this.answerType = answerType;
  }

}

package edu.cmu.lti.oaqa.vector;

import java.io.Serializable;

public class WordEntry implements Comparable<WordEntry>, Serializable{

  private static final long serialVersionUID = 1L;

  public String name;

  public float score;

  public WordEntry(String name, float score) {
    this.name = name;
    this.score = score;
  }

  public WordEntry() {
    super();
  }

  @Override
  public String toString() {

    return this.name + " ::" + score;
  }

  @Override
  public int compareTo(WordEntry o) {

    if (this.score > o.score) {
      return -1;
    } else {
      return 1;
    }
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public float getScore() {
    return score;
  }

  public void setScore(float score) {
    this.score = score;
  }

}
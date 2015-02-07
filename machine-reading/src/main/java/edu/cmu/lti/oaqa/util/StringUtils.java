package edu.cmu.lti.oaqa.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class StringUtils {
  static public void writeStirngToFile(String content, String path) {
    try {
      FileWriter output = new FileWriter(path);
      BufferedWriter writer = new BufferedWriter(output);
      writer.write(content);
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  static public void writeStirngToFile(StringBuffer content, String path) {
    writeStirngToFile(content.toString(), path);
  }

  static public String cleanQuestion(String question) {
    question = question.replaceAll("[①②③④⑤]", " ");
    question = question.replaceAll("[(][0-9]*[)]", " ");
    question = question.replaceAll("[(][a-zA-Z][)]", " ");
    question = question.replaceAll("[\"()\\[\\]:]", " ");
    question = question.replaceAll("[\\n\\r ]+", " ");
    return question.trim().toLowerCase();
  }


  static public String replace(String sentence, int offset, String surface, String replacement) {
    return replace(sentence, offset, surface.length(), replacement);
  }

  static public String replace(String sentence, int offset, int length, String replacement) {
    StringBuffer sb = new StringBuffer();
    sb.append(sentence.substring(0, offset));
    sb.append(replacement);
    sb.append(sentence.substring(offset + length));
    return sb.toString();
  }


}

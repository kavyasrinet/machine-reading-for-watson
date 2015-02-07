package edu.cmu.lti.oaqa.util;

public class UrlUtils {

  static public String getUriTitlePart(String uri) {
    return uri.substring(uri.lastIndexOf("/") + 1);
  }

  static public String guessUriTitle(String uri) {
    return getUriTitlePart(uri).replaceAll("_", " ");
  }
}

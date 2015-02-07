/**
 * Copyright 2011 Pablo Mendes, Max Jakob
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.cmu.lti.oaqa.agent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Simple web service agent for DBpedia Spotlight.
 *
 * @author diwang
 */
public class DBpediaSpotlightClient extends AbstractCachedJsonServiceFetcher {

  //  private final static String API_URL = "http://spotlight.dbpedia.org/";
  private final static String API_URL = "http://ur.lti.cs.cmu.edu:2222/";

  private static final double CONFIDENCE = 0.0;

  private static final int SUPPORT = 0;

/*  public static void main(String[] args) throws UnknownHostException {
    DBpediaSpotlightClient aClient = new DBpediaSpotlightClient();
    aClient.fetch(
            "First documented in the 13th century, Berlin was the capital of the Kingdom of Prussia (1701–1918), the German Empire (1871–1918), the Weimar Republic (1919–33) and the Third Reich (1933–45). ");
  }*/

  public ArrayList<String> extractEntities(String text) {

    if (text == null || text.isEmpty()) {
      return new ArrayList<String>();
    }

    JSONObject spotlightResponse = fetch(text);

    System.out.println(spotlightResponse.toString());

    ArrayList<String> entities;
    try {
      entities = extractEntities(spotlightResponse);
    } catch (JSONException e) {
      entities = new ArrayList<String>();
    }
    return entities;
  }

  public ArrayList<String> extractEntities(JSONObject spotlightResponse) throws JSONException {
    ArrayList<String> uriList = new ArrayList<String>();
    JSONArray resources = spotlightResponse.getJSONArray("Resources");
    for (int i = 0; i < resources.length(); i++) {
      JSONObject res = (JSONObject) resources.get(i);
      String uri = (String) res.get("@URI");
      uriList.add(uri);
    }
    return uriList;
  }

  @Override
  protected String makeQueryUrl(String query) {
    String url = null;
    try {
      url = API_URL + "rest/annotate/?" + "confidence=" + CONFIDENCE + "&support=" + SUPPORT
              + "&text=" + URLEncoder.encode(query, "utf-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return url;
  }

}

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

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.DefaultExtractor;

/**
 * agent to retrieve html and extract text
 *
 * @author diwang
 *
 */
public class BoilerpipeCachedClient extends AbstractCachedFetcher<String> {

  private HtmlCachedClient htmlClient;

  public BoilerpipeCachedClient() {
    super();
    htmlClient = new HtmlCachedClient();
  }

  @Override
  String fetchOnline(String url)  {
    System.out.println("Fetching URL:" + url);
    String html = htmlClient.fetch(url);
    String content = "";
    try {
      content = DefaultExtractor.getInstance().getText(html);
    } catch (BoilerpipeProcessingException e) {
      e.printStackTrace();
    }
    return content;
  }

  public static void main(String[] args) throws Exception {
    BoilerpipeCachedClient client = new BoilerpipeCachedClient();
    System.out.println(client.fetch("http://en.wikipedia.org/wiki/Mesoamerica"));

  }

}

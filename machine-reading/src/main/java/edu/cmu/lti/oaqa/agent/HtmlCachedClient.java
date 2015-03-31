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

import org.jsoup.Jsoup;

import java.io.IOException;

/**
 * Download html page
 * @author Di Wang
 *
 */
public class HtmlCachedClient extends AbstractCachedFetcher<String> {

  public String fetchOnline(String url) {
    String html = "";
    try {
      html = Jsoup.connect(url).timeout(10000).get().html();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return html;
  }

  public static void main(String[] args) throws IOException {
    HtmlCachedClient client = new HtmlCachedClient();
    System.out.println(client
            .fetch("http://www.cnn.com/2003/WORLD/asiapcf/southeast/08/07/amrozi.profile/"));
  }

}

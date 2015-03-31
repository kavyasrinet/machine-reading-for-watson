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

import edu.cmu.lti.oaqa.cache.MongoPojoCache;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.MapSolrParams;
import org.apache.solr.common.params.SolrParams;

import java.util.HashMap;

/**
 * Retrieval with Solr
 * @author diwang
 */
public class SolrCachedClient extends AbstractCachedFetcher<QueryResponse> {

  int topSearchResult;

  SolrServer solrServer;

  public SolrCachedClient(int topSearchResult, SolrServer solrServer, String coreName) {
    this.topSearchResult = topSearchResult;
    this.solrServer = solrServer;
    cacheServer = new MongoPojoCache<QueryResponse>(this.getClass().getSimpleName() + "-" + coreName);
  }


  @Override
public
  QueryResponse fetchOnline(String query) {
    HashMap<String, String> hshParams = new HashMap<String, String>();
    hshParams.put("q", query);
    hshParams.put("rows", String.valueOf(topSearchResult));
    hshParams.put("fl", "ID,score");
    SolrParams solrParams = new MapSolrParams(hshParams);
    QueryResponse qryResponse = null;
    try {
      qryResponse = solrServer.query(solrParams, SolrRequest.METHOD.POST);
    } catch (SolrServerException e) {
      e.printStackTrace();
    }
    return qryResponse;
  }

}

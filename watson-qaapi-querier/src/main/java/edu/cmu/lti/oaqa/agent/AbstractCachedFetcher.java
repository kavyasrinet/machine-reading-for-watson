package edu.cmu.lti.oaqa.agent;

import edu.cmu.lti.oaqa.cache.KeyObjectCache;

/**
 * @param <V>
 * @author Di Wang
 */
public abstract class AbstractCachedFetcher<V> {

  protected KeyObjectCache<V> cacheServer;

  public AbstractCachedFetcher(KeyObjectCache<V> cacheServer) {
    this.cacheServer = cacheServer;
  }

  public V fetch(String key) {
    return fetch(key, false);
  }

  public V fetch(String key, boolean renewCache) {
    V val = null;
    try {
      if (!renewCache) {
        val = cacheServer.get(key);
      }
      if (val == null) {
        val = fetchOnline(key);
        cacheServer.put(key, val);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return val;
  }

  abstract public V fetchOnline(String key);

}

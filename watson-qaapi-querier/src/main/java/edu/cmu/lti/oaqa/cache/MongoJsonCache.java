package edu.cmu.lti.oaqa.cache;

import com.mongodb.*;
import com.mongodb.util.JSON;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.Date;

/**
 * @author Di Wang
 */
public class MongoJsonCache implements KeyObjectCache<JSONObject> {

  String dbName = "WatsonCloud";

  String mongodb_host = "gold.lti.cs.cmu.edu";
//  String mongodb_host = "localhost";

  int mongodb_port = 27017;

  DBCollection cacheStore;

  public MongoJsonCache(String collectionName) {
    try {
      MongoClient mongo = new MongoClient(mongodb_host, mongodb_port);
      DB db = mongo.getDB(dbName);
      db.authenticate("user", "composer".toCharArray());
      cacheStore = db.getCollection(collectionName);
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }
  }

  public MongoJsonCache(Class<?> aClass) {
    this(aClass.getSimpleName());
  }

  @Override
  public void put(String key, JSONObject resultJSON) {
    DBObject dbObject = (DBObject) JSON.parse(resultJSON.toString());
    dbObject.put("createdDate", new Date());
    dbObject.put("_id", key);
    BasicDBObject updateQuery = new BasicDBObject().append("_id", key);
    cacheStore.update(updateQuery, dbObject, true, false);
  }

  @Override
  public JSONObject get(String key) {
    BasicDBObject updateQuery = new BasicDBObject().append("_id", key);
    DBObject doc = cacheStore.findOne(updateQuery);
    if (doc == null) {
      return null;
    }
    return new JSONObject(doc.toString());
  }

}

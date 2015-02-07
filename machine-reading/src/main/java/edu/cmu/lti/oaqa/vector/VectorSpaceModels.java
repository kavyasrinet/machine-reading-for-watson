package edu.cmu.lti.oaqa.vector;

import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.jcas.cas.TOP;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

/**
 * @author Di Wang
 *
 */
public class VectorSpaceModels {

  
  private GWordVector tokenVector;

  private static boolean useWordVector = false;

  private static boolean useSrl = false;
  
  private static boolean useStopWordsFilter = false;
  
  private static boolean useAnswerTypeFilter = false;
  
  HashSet<String> stopWordsSet = new HashSet<String>();
  
  public VectorSpaceModels() {
    super();
    
    if (useWordVector) {
      tokenVector = new GWordVector();
      try {
        //tokenVector.loadModel("/usr0/home/diw1/data/alzheimer_vector.bin");
        tokenVector.loadModel("/usr0/home/diw1/data/genomics_vector.bin");
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    
    
    if(useStopWordsFilter){
      try {
        BufferedReader stopword_Reader;    
        stopword_Reader = new BufferedReader(new FileReader("src/main/resources/stopWord.txt"));
        String s;
        while((s = stopword_Reader.readLine()) != null){
          stopWordsSet.add(s);
        }
        stopword_Reader.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    
  }

  public static HashMap<String, Integer> getTFMap(String sent) {
    HashMap<String, Integer> resultMap = new HashMap<String, Integer>();
    StringTokenizer st = new StringTokenizer(sent);
    while (st.hasMoreTokens()) {
      String keyString = st.nextToken();
      if (resultMap.containsKey(keyString)) {
        resultMap.put(keyString, resultMap.get(keyString) + 1);
      } else {
        resultMap.put(keyString, 1);
      }
    }
    return resultMap;
  }

  static public HashMap<String, Integer> getTFMap(FSList fsList, String featureName) {
    HashMap<String, Integer> resultMap = new HashMap<String, Integer>();
    int index = 0;
    while (true) {
      String keyString = null;
      try {
        TOP element = fsList.getNthElement(index);
        keyString = element.getFeatureValueAsString(element.getType().getFeatureByBaseName(
                featureName));
      } catch (Exception e) {
        break;
      }

      if (resultMap.containsKey(keyString)) {
        resultMap.put(keyString, resultMap.get(keyString) + 1);
      } else {
        resultMap.put(keyString, 1);
      }
      index++;
    }
    return resultMap;
  }
  
  static public double getLength(Map<String, Integer> bag) {
    double result = 0;
    for (Entry<String, Integer> tokenEntry : bag.entrySet()) {
      Integer count = tokenEntry.getValue();
      result += count * count;
    }
    return result;
  }
  
  double getMaxVectorCosine(HashMap<String, Integer> ansTfMap,
          HashMap<String, Integer> sentTfMap, HashMap<String, Double> idfMap) {
    if (ansTfMap.isEmpty() || sentTfMap.isEmpty()) {
      return 0;
    }

    double score = 0.0;
    for (Entry<String, Integer> entry : ansTfMap.entrySet()) {
      String tokenString = entry.getKey();
      Integer count = entry.getValue();
      // score += tokenVector.maxCosineSimilarity(tokenString,
      // tfMap2.keySet());

      double maxSim = 0;
      double maxSimConut = 1;
      String maxWord2 = tokenString;
      for (String word2 : sentTfMap.keySet()) {
        double sim = tokenVector.cosineSimilarity(tokenString, word2);
        if (sim > maxSim) {
          maxSim = sim;
          maxSimConut = sentTfMap.get(word2);
          maxWord2 = word2;
        }
      }

      double swIdf = getIdf(idfMap, maxWord2, 1);
      double awIdf = getIdf(idfMap, tokenString, swIdf);
      score += maxSim * maxSimConut * count * swIdf * awIdf;
      // score += maxSim;
    }

    return score / (getLength(ansTfMap) + getLength(sentTfMap));
    // / Math.sqrt(getLength(ansTfMap) * getLength(sentTfMap));
  }
  
  public ArrayList<Double> getSim(HashMap<String, Integer> answerTokenCountMap, HashMap<String, Integer> docSentTokenCountMap,
          HashMap<String, Double> idfMap) {
  
    ArrayList<Double> resultList = new ArrayList<Double>();
    
    double tokenConsineSim = getCosine(answerTokenCountMap, docSentTokenCountMap);
    double tokenDiceSim = getDice(answerTokenCountMap, docSentTokenCountMap);
    double tokenJaccardSim = getJaccard(answerTokenCountMap, docSentTokenCountMap);
    resultList.add(tokenConsineSim);
    resultList.add(tokenDiceSim);
    resultList.add(tokenJaccardSim);
    
    if (useWordVector) {
      resultList.add(getMaxVectorCosine(answerTokenCountMap, docSentTokenCountMap, idfMap));
    } else {
      resultList.add(0.0);
    }
    

    return resultList;
  }
  
  double getIdf(HashMap<String, Double> idfMap, String term, double backoff) {
    if (idfMap.containsKey(term)) {
      return idfMap.get(term);
    } else {
      // System.err.println( "no idf:" + term );
      return backoff;
    }
  }
  
  private double getDice(HashMap<String, Integer> tfMap1, HashMap<String, Integer> tfMap2) {
    int match = 0;
    for (Entry<String, Integer> entry : tfMap1.entrySet()) {
      String keyString = entry.getKey();
      if (tfMap2.containsKey(keyString)) {
        match++;
      }
    }
    return (double) 2 * match / (tfMap1.size() + tfMap2.size());
  }

  private double getJaccard(HashMap<String, Integer> tfMap1, HashMap<String, Integer> tfMap2) {
    int match = 0;
    for (Entry<String, Integer> entry : tfMap1.entrySet()) {
      String keyString = entry.getKey();
      if (tfMap2.containsKey(keyString)) {
        match++;
      }
    }
    Set<String> union = new HashSet<String>(tfMap1.keySet());
    union.addAll(tfMap2.keySet());
    return (double) match / (union.size());
  }

  private double getCosine(HashMap<String, Integer> tfMap1, HashMap<String, Integer> tfMap2) {
    if (tfMap1.isEmpty() || tfMap2.isEmpty()) {
      return 0;
    }

    double score = 0.0;
    for (Entry<String, Integer> entry : tfMap1.entrySet()) {
      String tokenString = entry.getKey();
      Integer count = entry.getValue();
      if (tfMap2.containsKey(tokenString)) {
        score += tfMap2.get(tokenString) * count;
      }
    }
    return score / Math.sqrt(getLength(tfMap1) * getLength(tfMap2));
  }
  

}

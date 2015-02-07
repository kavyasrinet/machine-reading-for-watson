package edu.cmu.lti.oaqa.vector;

import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.*;
import java.util.zip.GZIPInputStream;

public class GWordVector {

  public static void main(String[] args) throws IOException {
    GWordVector vec = new GWordVector();
    // vec.loadModel("/usr0/home/diw1/data/alzheimer_vector.bin");
    vec.loadModel("/usr0/home/diw1/data/freebase-vectors-skipgram1000.bin.gz");
    System.out.println(vec.similarWords("terrorist"));
    System.out.println(vec.analogy("he", "his", "her"));

    // open up standard input
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    String line = null;
    while ((line = br.readLine()) != null) {
      long startTime = System.currentTimeMillis();
      for (int i = 0; i < 9; i++) {
        vec.similarWordsMultiThread(line);
      }
      System.out.println(vec.similarWordsMultiThread(line));
      long endTime = System.currentTimeMillis();
      long totalTime = endTime - startTime;
      System.out.println("Time*10: " + totalTime);

    }

  }

  private HashMap<String, float[]> wordMap = new LinkedHashMap<String, float[]>();

  private int words;

  private int size;

  private int topNSize = 40;

  /**
   * Load Model
   *
   * @param path
   *          The path model
   * @throws java.io.IOException
   */
  public void loadModel(String path) throws IOException {
    DataInputStream dis = null;
    BufferedInputStream bis = null;
    double len = 0;
    float vector = 0;
    try {

      InputStream fin = new FileInputStream(path);
      if (path.endsWith(".gz")) {
        fin = new GZIPInputStream(fin);
      }

      bis = new BufferedInputStream(fin);
      dis = new DataInputStream(bis);
      // //Number of words read
      words = Integer.parseInt(readString(dis));
      // //Size
      size = Integer.parseInt(readString(dis));
      System.out.println(">>> Loading vectors words:" + words + " size:" + size);
      String word;
      float[] vectors = null;
      for (int i = 0; i < words; i++) {
        word = readString(dis);
        // System.out.println(word);
        vectors = new float[size];
        len = 0;
        for (int j = 0; j < size; j++) {
          vector = readFloat(dis);
          len += vector * vector;
          vectors[j] = (float) vector;
        }
        // XXX need normalize here?
        len = Math.sqrt(len);
        for (int j = 0; j < vectors.length; j++) {
          vectors[j] = (float) (vectors[j] / len);
        }
        wordMap.put(word, vectors);
        dis.read();
      }
      System.out.println("<<< Finished Loading vectors.");
    } finally {
      bis.close();
      dis.close();
    }
  }

  private static final int MAX_SIZE = 50;

  private static final int NTHREDS = 8;

  public double maxCosineSimilarity(String word1, Collection<String> wordSet) {
    double maxSim = 0;
    for (String word2 : wordSet) {
      double sim = cosineSimilarity(word1, word2);
      if (sim > maxSim) {
        maxSim = sim;
      }
    }
    return maxSim;
  }

  public double cosineSimilarity(String word1, String word2) {
    float[] wordVector1 = getWordVector(word1);
    float[] wordVector2 = getWordVector(word2);
    if (wordVector1 == null || wordVector2 == null) {
      return 0;
    }

    double cosineSimilarity = 0;
    double dotProduct = 0;
    double magnitude1 = 0;
    double magnitude2 = 0;
    for (int i = 0; i < wordVector1.length; i++) {
      dotProduct += wordVector1[i] * wordVector2[i];
      magnitude1 += Math.pow(wordVector1[i], 2); // (a^2)
      magnitude2 += Math.pow(wordVector2[i], 2); // (b^2)
    }

    magnitude1 = Math.sqrt(magnitude1);// sqrt(a^2)
    magnitude2 = Math.sqrt(magnitude2);// sqrt(b^2)

    if (magnitude1 != 0.0 | magnitude2 != 0.0) {
      cosineSimilarity = dotProduct / (magnitude1 * magnitude2);
    } else {
      return 0.0;
    }

    return cosineSimilarity;
  }

  /**
   * Get synonyms
   *
   * @param word
   * @return
   */
  public Set<WordEntry> similarWords(String word) {
    float[] wordVector = getWordVector(word);
    if (wordVector == null) {
      return null;
    }
    Set<Entry<String, float[]>> entrySet = wordMap.entrySet();
    float[] tempVector = null;
    List<WordEntry> wordEntrys = new ArrayList<WordEntry>(topNSize);
    String name = null;
    for (Entry<String, float[]> entry : entrySet) {
      name = entry.getKey();
      if (name.equals(word)) {
        continue;
      }
      float dist = 0;
      tempVector = entry.getValue();
      for (int i = 0; i < wordVector.length; i++) {
        dist += wordVector[i] * tempVector[i];
      }

      insertTopN(name, dist, wordEntrys);
    }
    return new TreeSet<WordEntry>(wordEntrys);
  }


  @POST
  @Produces(MediaType.APPLICATION_JSON)
  public List<WordEntry> similarWordsMultiThread(String word) {
    float[] wordVector = getWordVector(word);
    if (wordVector == null) {
      return null;
    }

    Set<Entry<String, float[]>> entrySet = wordMap.entrySet();

    ExecutorService executor = Executors.newFixedThreadPool(NTHREDS);
    List<Future<List<WordEntry>>> list = new ArrayList<Future<List<WordEntry>>>();
    for (int i = 0; i < NTHREDS; i++) {
      Callable<List<WordEntry>> worker = new SimilarWordsCallable(entrySet, i, wordVector);
      Future<List<WordEntry>> submit = executor.submit(worker);
      list.add(submit);
    }
    List<WordEntry> wordEntrys = new ArrayList<WordEntry>(topNSize);
    for (Future<List<WordEntry>> future : list) {
      try {
        List<WordEntry> subSimEntrys = future.get();
        for (WordEntry wordEntry : subSimEntrys) {
          insertTopN(wordEntry.name, wordEntry.score, wordEntrys);
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      } catch (ExecutionException e) {
        e.printStackTrace();
      }
    }
    executor.shutdown();

//    TreeSet<WordEntry> sortedSet = new TreeSet<WordEntry>(wordEntrys);
//    removeWord(sortedSet, word);

    removeWord(wordEntrys, word);
    Collections.sort(wordEntrys);

    return wordEntrys;
  }

  void removeWord(Collection<WordEntry> sortedSet, String word) {
    WordEntry targetEntry = null;
    for (WordEntry wordEntry : sortedSet) {
      if (wordEntry.name.equals(word)) {
        targetEntry = wordEntry;
        break;
      }
    }
    if (targetEntry != null) {
      sortedSet.remove(targetEntry);
    }
  }

  public class SimilarWordsCallable implements Callable<List<WordEntry>> {

    List<WordEntry> wordEntrys = new ArrayList<WordEntry>(topNSize);

    Set<Entry<String, float[]>> entrySet;

    int threadId;

    float[] wordVector;

    public SimilarWordsCallable(Set<Entry<String, float[]>> entrySet, int threadId,
            float[] wordVector) {
      super();
      this.entrySet = entrySet;
      this.threadId = threadId;
      this.wordVector = wordVector;
    }

    @Override
    public List<WordEntry> call() throws Exception {
      float[] tempVector = null;
      String name = null;

      int count = -1;
      for (Entry<String, float[]> entry : entrySet) {
        if (++count == NTHREDS) {
          count = 0;
        }
        if (count != threadId) {
          continue;
        }

        name = entry.getKey();
        float dist = 0;
        tempVector = entry.getValue();
        for (int i = 0; i < wordVector.length; i++) {
          dist += wordVector[i] * tempVector[i];
        }
        insertTopN(name, dist, wordEntrys);
      }
      return wordEntrys;
    }
  }

  /**
   * Synonyms
   *
   * @return
   */
  public TreeSet<WordEntry> analogy(String word0, String word1, String word2) {
    float[] wv0 = getWordVector(word0);
    float[] wv1 = getWordVector(word1);
    float[] wv2 = getWordVector(word2);

    if (wv1 == null || wv2 == null || wv0 == null) {
      return null;
    }
    float[] wordVector = new float[size];
    for (int i = 0; i < size; i++) {
      wordVector[i] = wv1[i] - wv0[i] + wv2[i];
    }
    float[] tempVector;
    String name;
    List<WordEntry> wordEntrys = new ArrayList<WordEntry>(topNSize);
    for (Entry<String, float[]> entry : wordMap.entrySet()) {
      name = entry.getKey();
      if (name.equals(word0) || name.equals(word1) || name.equals(word2)) {
        continue;
      }
      float dist = 0;
      tempVector = entry.getValue();
      for (int i = 0; i < wordVector.length; i++) {
        dist += wordVector[i] * tempVector[i];
      }
      insertTopN(name, dist, wordEntrys);
    }
    return new TreeSet<WordEntry>(wordEntrys);
  }

  private void insertTopN(String name, float score, List<WordEntry> wordsEntrys) {

    if (wordsEntrys.size() < topNSize) {
      wordsEntrys.add(new WordEntry(name, score));
      return;
    }
    float min = Float.MAX_VALUE;
    int minOffe = 0;
    for (int i = 0; i < topNSize; i++) {
      WordEntry wordEntry = wordsEntrys.get(i);
      if (min > wordEntry.score) {
        min = wordEntry.score;
        minOffe = i;
      }
    }

    if (score > min) {
      wordsEntrys.set(minOffe, new WordEntry(name, score));
    }

  }

  /**
   * Get the word vector
   *
   * @param word
   * @return
   */
  public float[] getWordVector(String word) {
    return wordMap.get(word);
  }

  public static float readFloat(InputStream is) throws IOException {
    byte[] bytes = new byte[4];
    is.read(bytes);
    return getFloat(bytes);
  }

  /**
   * Read a float
   *
   * @param b
   * @return
   */
  public static float getFloat(byte[] b) {
    int accum = 0;
    accum = accum | (b[0] & 0xff) << 0;
    accum = accum | (b[1] & 0xff) << 8;
    accum = accum | (b[2] & 0xff) << 16;
    accum = accum | (b[3] & 0xff) << 24;
    return Float.intBitsToFloat(accum);
  }

  /**
   * Read a string
   *
   * @param dis
   * @return
   * @throws java.io.IOException
   */
  private static String readString(DataInputStream dis) throws IOException {

    byte[] bytes = new byte[MAX_SIZE];
    byte b = dis.readByte();
    int i = -1;
    StringBuilder sb = new StringBuilder();
    while (b != 32 && b != 10) {
      i++;
      bytes[i] = b;
      b = dis.readByte();
      if (i == 49) {
        sb.append(new String(bytes));
        i = -1;
        bytes = new byte[MAX_SIZE];
      }
    }
    sb.append(new String(bytes, 0, i + 1));
    return sb.toString();
  }

  public int getTopNSize() {
    return topNSize;
  }

  public void setTopNSize(int topNSize) {
    this.topNSize = topNSize;
  }

  public HashMap<String, float[]> getWordMap() {
    return wordMap;
  }

  public int getWords() {
    return words;
  }

  public int getSize() {
    return size;
  }

}

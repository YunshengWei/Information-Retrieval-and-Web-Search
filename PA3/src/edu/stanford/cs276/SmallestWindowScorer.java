package edu.stanford.cs276;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A skeleton for implementing the Smallest Window scorer in Task 3. Note: The
 * class provided in the skeleton code extends BM25Scorer in Task 2. However,
 * you don't necessarily have to use Task 2. (You could also use Task 1, in
 * which case, you'd probably like to extend CosineSimilarityScorer instead.)
 * Also, feel free to modify or add helpers inside this class.
 */
public class SmallestWindowScorer extends BM25Scorer {
  double boostingFactor = 3.0;

  public SmallestWindowScorer(Map<String, Double> idfs,
      Map<Query, Map<String, Document>> queryDict) {
    super(idfs, queryDict);
  }

  private double getWindow(Set<String> queryWords,
      Map<String, List<Integer>> postingDict) {
    if (!postingDict.keySet().equals(queryWords)) {
      return Double.POSITIVE_INFINITY;
    }
    return Double.POSITIVE_INFINITY;
  }

  // This is essentially minimum window substring problem
  private double getWindow(Set<String> queryWords, String[] text) {
    Map<String, Integer> map = new HashMap<>();
    int left = 0;
    double window = Double.POSITIVE_INFINITY;

    int count = 0;

    for (int i = 0; i < text.length; i++) {
      String term = text[i];

      if (queryWords.contains(term)) {
        if (map.containsKey(term)) {
          map.put(term, map.get(term) + 1);
        } else {
          map.put(term, 1);
          count++;
        }

        if (count == queryWords.size()) {
          String t = text[left];
          while (!queryWords.contains(t) || map.get(t) > 1) {
            if (map.containsKey(t)) {
              map.put(t, map.get(t) - 1);
            }
            left++;
            t = text[left];
          }
          
          window = Math.min(window, i - left + 1);
          map.remove(t);
          count--;
          left++;
        }
      }
    }

    return window;
  }

  /**
   * get smallest window of one document and query pair.
   * 
   * @param d:
   *          document
   * @param q:
   *          query
   */
  private double getWindow(Document d, Query q) {
    Set<String> queryWords = new HashSet<>();
    for (String queryWord : q.queryWords) {
      queryWords.add(queryWord.toLowerCase());
    }

    double window = Double.POSITIVE_INFINITY;

    if (d.url != null) {
      String[] tokens = d.url.toLowerCase().split("[^0-9a-zA-Z]+");
      window = Math.min(window, getWindow(queryWords, tokens));
    }

    if (d.title != null) {
      String[] tokens = d.title.toLowerCase().split("\\s+");
      window = Math.min(window, getWindow(queryWords, tokens));
    }

    if (d.headers != null) {
      for (String header : d.headers) {
        String[] tokens = header.toLowerCase().split("\\s+");
        window = Math.min(window, getWindow(queryWords, tokens));
      }
    }

    if (d.anchors != null) {
      for (String anchor : d.anchors.keySet()) {
        String[] tokens = anchor.toLowerCase().split("\\s+");
        window = Math.min(window, getWindow(queryWords, tokens));
      }
    }

    if (d.body_hits != null) {
      window = Math.min(window, getWindow(queryWords, d.body_hits));
    }

    return window;
  }

  /**
   * get boost score of one document and query pair.
   * 
   * @param d:
   *          document
   * @param q:
   *          query
   */
  private double getBoostScore(Document d, Query q) {
    Set<String> queryWords = new HashSet<>();
    for (String queryWord : q.queryWords) {
      queryWords.add(queryWord.toLowerCase());
    }

    double smallestWindow = getWindow(d, q);
    double a = 1.0 / (boostingFactor - 1) - queryWords.size();
    double boostScore = 1.0 / (smallestWindow + a) + 1;

    return boostScore;
  }

  @Override
  public double getSimScore(Document d, Query q) {
    Map<String, Map<String, Double>> tfs = this.getDocTermFreqs(d, q);
    this.normalizeTFs(tfs, d, q);
    Map<String, Double> tfQuery = getQueryFreqs(q);
    double boost = getBoostScore(d, q);
    double rawScore = this.getNetScore(tfs, q, tfQuery, d);
    return boost * rawScore;
  }

}

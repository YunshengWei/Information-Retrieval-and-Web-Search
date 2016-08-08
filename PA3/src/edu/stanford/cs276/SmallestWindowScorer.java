package edu.stanford.cs276;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import edu.stanford.cs276.util.Pair;

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

  // a variant of minimum window substring problem
  private double getWindow(Set<String> queryWords,
      Map<String, List<Integer>> postingDict) {
    double window = Double.POSITIVE_INFINITY;

    if (postingDict.keySet().containsAll(queryWords)) {
      // int, String, int, List
      PriorityQueue<Object[]> pq =
          new PriorityQueue<>(queryWords.size(), new Comparator<Object[]>() {
            @Override
            public int compare(Object[] o1, Object[] o2) {
              return (int) o1[0] - (int) o2[0];
            }
          });

      // initialize priority queue
      for (Map.Entry<String, List<Integer>> postings : postingDict.entrySet()) {
        String term = postings.getKey();
        List<Integer> postingList = postings.getValue();
        pq.add(new Object[] { postingList.get(0), term, 0, postingList });
      }

      int count = 0;
      int left = 0;
      Map<String, Integer> counter = new HashMap<>();
      for (String term : queryWords) {
        counter.put(term, 0);
      }
      LinkedList<Pair<Integer, String>> poss = new LinkedList<>();

      while (!pq.isEmpty()) {
        Object[] o = pq.poll();
        int pos = (int) o[0];
        String term = (String) o[1];
        int index = (int) o[2];
        @SuppressWarnings("unchecked")
        List<Integer> postings = (List<Integer>) o[3];
        if (index < postings.size() - 1) {
          pq.add(new Object[] { postings.get(index + 1), term, index + 1,
              postings });
        }

        counter.put(term, counter.get(term) + 1);
        poss.add(new Pair<>(pos, term));
        if (counter.get(term) == 1) {
          count++;
          if (count == queryWords.size()) {
            String t;
            do {
              Pair<Integer, String> pair = poss.pollFirst();
              left = pair.getFirst();
              t = pair.getSecond();
              
              counter.put(t, counter.get(t) - 1);
            } while (counter.get(t) >= 1);

            window = Math.min(window, pos + 1 - left);
            count--;
          }
        }
      }
    }
    
    return window;
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
      // Use smallest window in body seems to destory performance
      //window = Math.min(window, getWindow(queryWords, d.body_hits));
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
    
    double boostScore;
    if (boostingFactor > 1) {
      double smallestWindow = getWindow(d, q);
      double a = 1.0 / (boostingFactor - 1) - queryWords.size();
      boostScore = 1.0 / (smallestWindow + a) + 1;
    } else {
      boostScore = 1.0;
    }

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

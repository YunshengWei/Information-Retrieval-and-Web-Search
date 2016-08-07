package edu.stanford.cs276;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An abstract class for a scorer. 
 * Needs to be extended by each specific implementation of scorers.
 */
public abstract class AScorer {

	// Map: term -> idf
	Map<String,Double> idfs; 

  // Various types of term frequencies that you will need
	String[] TFTYPES = {"url","title","body","header","anchor"};
	
/**
  * Construct an abstract scorer with a map of idfs.
  * @param idfs the map of idf scores
  */
	public AScorer(Map<String,Double> idfs) {
		this.idfs = idfs;
	}
	
/**
	* Score each document for each query.
  * @param d the Document
  * @param q the Query
  */
	public abstract double getSimScore(Document d, Query q);
	
/**
	* Get frequencies for a query.
  * @param q the query to compute frequencies for
  */
	public Map<String,Double> getQueryFreqs(Query q) {

    // queryWord -> term frequency
		Map<String,Double> tfQuery = new HashMap<String, Double>(); 		

		/*
		 * TODO : Your code here
     *        Compute the raw term (and/or sublinearly scaled) frequencies
     *        Additionally weight each of the terms using the idf value
     *        of the term in the query (we use the PA1 corpus to determine
     *        how many documents contain the query terms which is stored
     *        in this.idfs).
		 */
		for (String term : q.queryWords) {
		  term = term.toLowerCase();
		  if (!tfQuery.containsKey(term)) {
		    tfQuery.put(term, 1.);
		  } else {
		    double count = tfQuery.get(term);
		    tfQuery.put(term, count + 1);
		  }
		}
		
		// sublinear scaling
		sublinearScaleFreqs(tfQuery);
		
		// idf weighting
		for (Map.Entry<String, Double> e : tfQuery.entrySet()) {
		  double sublinearScaledCount = e.getValue();
		  double idf =idfs.get(e.getKey());
		  double score = idf * sublinearScaledCount;
		  e.setValue(score);
		}
		
		return tfQuery;
	}
	
	
	/*
	 * TODO : Your code here
   *        Include any initialization and/or parsing methods
   *        that you may want to perform on the Document fields
   *        prior to accumulating counts.
   *        See the Document class in Document.java to see how
   *        the various fields are represented.
	 */
	private static double sublinearScale(double rawCount) {
	  if (rawCount > 0.) {
	    return 1 + Math.log(rawCount);
	  } else {
	    return 0;
	  }
	}
	
	private static void sublinearScaleFreqs(Map<String, Double> tf) {
	  for (Map.Entry<String, Double> e : tf.entrySet()) {
      double rawCount = e.getValue();
      double sublinearScaledCount = sublinearScale(rawCount);
      e.setValue(sublinearScaledCount);
    }
	}
	
	private static Map<String, Double> initEmptyFreqs(Set<String> queryWords) {
	  Map<String, Double> tf = new HashMap<>();
	  for (String word : queryWords) {
	    tf.put(word, 0.);
	  }
	  return tf;
	}
	
	private static void updateFreqs(Map<String, Double> tf, String[] tokens) {
	  for (String token : tokens) {
      if (!token.isEmpty() && tf.containsKey(token)) {
        tf.put(token, tf.get(token) + 1);
      }
    }
	}
	
	private static Map<String, Double> getDocUrlFreqs(Document d, Set<String> queryWords) {
	  Map<String, Double> tfUrl = initEmptyFreqs(queryWords);
	  String[] tokens = d.url.toLowerCase().split("[^0-9a-zA-Z]+");
	  updateFreqs(tfUrl, tokens);
	  sublinearScaleFreqs(tfUrl);
	  return tfUrl;
  }
	
	private static Map<String, Double> getDocTitleFreqs(Document d, Set<String> queryWords) {
	  Map<String, Double> tfTitle = initEmptyFreqs(queryWords);
	  String[] tokens = d.title.toLowerCase().split("\\s");
	  updateFreqs(tfTitle, tokens);
	  sublinearScaleFreqs(tfTitle);
	  return tfTitle;
	}
	
	private static Map<String, Double> getDocBodyFreqs(Document d, Set<String> queryWords) {
	  Map<String, Double> tfBody = initEmptyFreqs(queryWords);
	  for (Map.Entry<String, List<Integer>> e : d.body_hits.entrySet()) {
	    tfBody.put(e.getKey().toLowerCase(), (double) e.getValue().size());
	  }
	  sublinearScaleFreqs(tfBody);
    return tfBody;
	}
	
	private static Map<String, Double> getDocHeaderFreqs(Document d, Set<String> queryWords) {
	  Map<String, Double> tfHeader = initEmptyFreqs(queryWords);
    for (String header : d.headers) {
      String[] tokens = header.toLowerCase().split("\\s");
      updateFreqs(tfHeader, tokens);
    }
    sublinearScaleFreqs(tfHeader);
    return tfHeader;
	}
	
	private static Map<String, Double> getDocAnchorFreqs(Document d, Set<String> queryWords) {
	  Map<String, Double> tfAnchor = initEmptyFreqs(queryWords);
    for (Map.Entry<String, Integer> e : d.anchors.entrySet()) {
      String anchorText = e.getKey();
      int anchorCount = e.getValue();
      String[] tokens = anchorText.split("\\s");
      for (String token : tokens) {
        if (!token.isEmpty() && tfAnchor.containsKey(token)) {
           tfAnchor.put(token, tfAnchor.get(token) + anchorCount);
        }
      }
    }
	  
    sublinearScaleFreqs(tfAnchor);
    return tfAnchor;
	}

	
	/**
	 * Accumulate the various kinds of term frequencies 
   * for the fields (url, title, body, header, and anchor).
	 * You can override this if you'd like, but it's likely 
   * that your concrete classes will share this implementation.
   * @param d the Document
   * @param q the Query
	 */
	public Map<String,Map<String, Double>> getDocTermFreqs(Document d, Query q) {

		// Map from tf type -> queryWord -> score
		Map<String,Map<String, Double>> tfs = new HashMap<>();
		
		Set<String> queryWords = new HashSet<>();
		for (String queryWord : q.queryWords) {
			queryWords.add(queryWord.toLowerCase());
		}
		
		Map<String, Double> tfUrl = getDocUrlFreqs(d, queryWords);
    Map<String, Double> tfTitle = getDocTitleFreqs(d, queryWords);;
    Map<String, Double> tfBody = getDocBodyFreqs(d, queryWords);
    Map<String, Double> tfHeader = getDocHeaderFreqs(d, queryWords);
    Map<String, Double> tfAnchor = getDocAnchorFreqs(d, queryWords);
    tfs.put("url", tfUrl);
    tfs.put("title", tfTitle);
    tfs.put("body", tfBody);
    tfs.put("header", tfHeader);
    tfs.put("anchor", tfAnchor);
    
		return tfs;
	}

}

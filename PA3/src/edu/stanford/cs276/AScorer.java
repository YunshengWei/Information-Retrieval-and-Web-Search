package edu.stanford.cs276;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		Map<String,Map<String, Double>> tfs = new HashMap<String,Map<String, Double>>();
		
		/*
		 * TODO : Your code here
     *        Initialize any variables needed
		 */
		
		for (String queryWord : q.queryWords) {
			/*
			 * Your code here
		   * Loop through query terms and accumulate term frequencies. 
       * Note: you should do this for each type of term frequencies,
       * i.e. for each of the different fields.
       * Don't forget to lowercase the query word.
			 */
			
		}
		return tfs;
	}

}

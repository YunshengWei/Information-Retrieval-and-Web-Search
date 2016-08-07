package edu.stanford.cs276;

import java.util.Map;

/**
  * Skeleton code for the implementation of an extra
  * credit scorer as a part of Extra Credit.
  */
public class ExtraCreditScorer extends AScorer {

  /**
    * Constructs a scorer for Extra Credit.
    * @param idfs the map of idf values
    */
	public ExtraCreditScorer(Map<String,Double> idfs) {
		super(idfs);
	}
	
	@Override
  /**
    * Get the awesome similarity score using the ranking
    * algorithm you have derived incorporating other
    * signals indicating relevance of a doc to a query.
    * @param d the Document
    * @param q the Query
    * @return the similarity score
    */
	public double getSimScore(Document d, Query q) {
    /*
     * TODO : Your code here
     */
		
		return 0;
	}

}

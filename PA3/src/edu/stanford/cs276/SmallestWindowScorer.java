package edu.stanford.cs276;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A skeleton for implementing the Smallest Window scorer in Task 3.
 * Note: The class provided in the skeleton code extends BM25Scorer in Task 2. However, you don't necessarily
 * have to use Task 2. (You could also use Task 1, in which case, you'd probably like to extend CosineSimilarityScorer instead.)
 * Also, feel free to modify or add helpers inside this class.
 */
public class SmallestWindowScorer extends BM25Scorer {
	
	public SmallestWindowScorer(Map<String, Double> idfs, Map<Query,Map<String, Document>> queryDict) {
		super(idfs, queryDict);
	}

	/**
	 * get smallest window of one document and query pair.
	 * @param d: document
	 * @param q: query
	 */	
	private int getWindow(Document d, Query q) {
		/*
		 * @//TODO : Your code here
		 */
		return -1;
	}

	
	/**
	 * get boost score of one document and query pair.
	 * @param d: document
	 * @param q: query
	 */	
	private double getBoostScore (Document d, Query q) {
		int smallestWindow = getWindow(d, q);
		double boostScore = 0;
		/*
		 * @//TODO : Your code here, calculate the boost score.
		 *
		 */
		return boostScore;
	}
	
	
	@Override
	public double getSimScore(Document d, Query q) {
		Map<String,Map<String, Double>> tfs = this.getDocTermFreqs(d,q);
		this.normalizeTFs(tfs, d, q);
		Map<String,Double> tfQuery = getQueryFreqs(q);
		double boost = getBoostScore(d, q);
		double rawScore = this.getNetScore(tfs, q, tfQuery, d);
		return boost * rawScore;
	}

}

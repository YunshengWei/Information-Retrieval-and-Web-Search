package edu.stanford.cs276;

/**
 * A dummy baseline scorer that uses only the count of body hits to score documents.
 */
public class BaselineScorer extends AScorer {
	
	public BaselineScorer() {
		// Don't need idfs for the baseline
		super(null);
	}
	
	// We sum over the length of the body_hits array for all query terms
	@Override
	public double getSimScore(Document d, Query q) {
		double score = 0.0;
		if (d.body_hits!=null) {
			for (String term : d.body_hits.keySet())
				score += d.body_hits.get(term).size();
		}
		
		return score;
	}

}

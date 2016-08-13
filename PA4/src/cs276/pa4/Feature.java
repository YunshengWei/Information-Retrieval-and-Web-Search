package cs276.pa4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.apple.eawt.AppEvent.SystemSleepEvent;

public class Feature {
	
	public static boolean isSublinearScaling = true;
	private Parser parser = new Parser();
	double smoothingBodyLength = 800;

	Map<String,Double> idfs;
		
		// might want to add more features
		/*
		 * @TODO: Your code here
		 */
	
	public Feature(Map<String,Double> idfs){
		this.idfs = idfs;
	}
	
	public double[] extractFeatureVector(Document d, Query q){
		
		/* Compute doc_vec and query_vec */
		Map<String,Map<String, Double>> tfs = Util.getDocTermFreqs(d,q);	
		Map<String,Double> queryVector = getQueryVec(q);

		// normalize term-frequency
		this.normalizeTFs(tfs, d, q);
		
		/* [url, title, body, header, anchor] */
		double[] result = new double[5];
		for (int i = 0; i < result.length; i++) { result[i] = 0.0; }
		for (String queryWord : q.queryWords){
			double queryScore = queryVector.get(queryWord);
			result[0]  += tfs.get("url").get(queryWord) * queryScore;
			result[1]  += tfs.get("title").get(queryWord) * queryScore;
			result[2]  += tfs.get("body").get(queryWord) * queryScore;
			result[3]  += tfs.get("header").get(queryWord) * queryScore;
			result[4]  += tfs.get("anchor").get(queryWord) * queryScore;
		}

		return result;
	}

	/* Generate query vector */
	public Map<String,Double> getQueryVec(Query q) {
		/* Count word frequency within the query, in most cases should be 1 */
		
		Map<String, Double> tfVector = new HashMap<String, Double>();
		String[] wordInQuery = q.query.toLowerCase().split(" ");
		for (String word : wordInQuery){
			if (tfVector.containsKey(word))
				tfVector.put(word, tfVector.get(word) + 1);
			else
				tfVector.put(word, 1.0);
		}
		
		/* Sublinear Scaling */
		if(isSublinearScaling){
  		for (String word : tfVector.keySet()) {
  			tfVector.put(word, 1 + Math.log(tfVector.get(word)));
  		}
		}
		
		/* Compute idf vector */
		Map<String,Double> idfVector = new HashMap<String,Double>();
		
		for (String queryWord : q.queryWords) {
			if (this.idfs.containsKey(queryWord))
				idfVector.put(queryWord, this.idfs.get(queryWord));
			else {
				idfVector.put(queryWord, Math.log(98998.0)); /* Laplace smoothing */
			}

		}
		
		/* Do dot-product */
		Map<String, Double> queryVector = new HashMap<String, Double>();
		for (String word : q.queryWords) {
			queryVector.put(word, tfVector.get(word) * idfVector.get(word));
		}
		
		return queryVector;
	}
	
	public void normalizeTFs(Map<String,Map<String, Double>> tfs,Document d, Query q)
	{
		double normalizationFactor = (double)(d.body_length) + (double)(smoothingBodyLength);

		for (String queryWord : q.queryWords)
			for (String tfType : tfs.keySet())
				tfs.get(tfType).put(queryWord, tfs.get(tfType).get(queryWord)/normalizationFactor);
	}

	public double[] extractMoreFeatures(Document d, Query q, Map<Query,Map<String, Document>> dataMap) {
		
		double[] basic = extractFeatureVector(d, q);
		// BM25F, PageRank, Smallest Window features
		double[] more = new double[1];
		// PageRank
		
		double[] concat = new double[basic.length + more.length];
		System.arraycopy(basic, 0, concat, 0, basic.length);
		System.arraycopy(more, 0, concat, basic.length, more.length);
		return concat;
	}
	
}

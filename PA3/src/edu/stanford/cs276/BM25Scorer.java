package edu.stanford.cs276;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Skeleton code for the implementation of a BM25 Scorer in Task 2.
 */
public class BM25Scorer extends AScorer {

	/*
	*  TODO: You will want to tune these values
	*/
	double urlweight = 0.1;
	double titleweight  = 0.1;
	double bodyweight = 0.1;
	double headerweight = 0.1;
	double anchorweight = 0.1;
	
	// BM25-specific weights
	double burl = 0.1;
	double btitle = 0.1;
	double bheader = 0.1;
	double bbody = 0.1;
	double banchor = 0.1;
	
	double k1 = 0.1;
	double pageRankLambda = 0.1;
	double pageRankLambdaPrime = 0.1;
	
	// query -> url -> document
	Map<Query,Map<String, Document>> queryDict; 

	// BM25 data structures--feel free to modify these
	// Document -> field -> length
	Map<Document,Map<String,Double>> lengths;	

	// field name -> average length
	Map<String,Double> avgLengths;  	

	// Document -> pagerank score
	Map<Document,Double> pagerankScores; 
	
	/**
	   * Construct a BM25Scorer.
	   * @param idfs the map of idf scores
	   * @param queryDict a map of query to url to document
	   */
		public BM25Scorer(Map<String,Double> idfs, Map<Query,Map<String, Document>> queryDict) {
			super(idfs);
			this.queryDict = queryDict;
			this.calcAverageLengths();
		}

  /**
    * Set up average lengths for BM25, also handling PageRank.
    */
	public void calcAverageLengths() {
		lengths = new HashMap<Document,Map<String,Double>>();
		avgLengths = new HashMap<String,Double>();
		pagerankScores = new HashMap<Document,Double>();
		
		/*
		 * TODO : Your code here
     *        Initialize any data structures needed, perform
     *        any preprocessing you would like to do on the fields,
     *        handle pagerank, accumulate lengths of fields in documents.
     *        
		 */
		
		for (String tfType : this.TFTYPES) {
			/*
			 * TODO : Your code here
       *        Normalize lengths to get average lengths for
       *        each field (body, url, title, header, anchor)
       *        using the training set (PA1 corpus).
			 */
		}

	}

  /**
    * Get the net score. 
    * @param tfs the term frequencies
    * @param q the Query 
    * @param tfQuery
    * @param d the Document
    * @return the net score
    */
	public double getNetScore(Map<String,Map<String, Double>> tfs, Query q, Map<String,Double> tfQuery,Document d) {

		double score = 0.0;
		
		/*
		 * TODO : Your code here
     *        Use equation 5 in the writeup to compute the overall score
     *        of a document d for a query q.
		 */
		
		return score;
	}

  /**
    * Do BM25 Normalization.
    * @param tfs the term frequencies
    * @param d the Document
    * @param q the Query
    */
	public void normalizeTFs(Map<String,Map<String, Double>> tfs,Document d, Query q) {
		/*
		 * TODO : Your code here
     *        Use equation 3 in the writeup to normalize the raw term frequencies
     *        in fields in document d.
		 */
	}
	
	
	/**
	    * Write the tuned parameters of BM25 to file.
	    * Only used for grading purpose, you should NOT modify this method.
	    * @param filePath the output file path.
	    */
	private void writeParaValues(String filePath) {
		try {
			File file = new File(filePath);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			String[] names = {"urlweight", "titleweight", "bodyweight", 
        "headerweight", "anchorweight", "burl", "btitle", 
        "bheader", "bbody", "banchor", "k1", "pageRankLambda", "pageRankLambdaPrime"};
			double[] values = {this.urlweight, this.titleweight, this.bodyweight, 
        this.headerweight, this.anchorweight, this.burl, this.btitle, 
        this.bheader, this.bbody, this.banchor, this.k1, this.pageRankLambda, 
        this.pageRankLambdaPrime};
			BufferedWriter bw = new BufferedWriter(fw);
			for (int idx = 0; idx < names.length; ++ idx) {
				bw.write(names[idx] + " " + values[idx]);
				bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
  /**
    * Get the similarity score.
    * @param d the Document
    * @param q the Query
    * @return the similarity score
    */
	public double getSimScore(Document d, Query q) {
		Map<String,Map<String, Double>> tfs = this.getDocTermFreqs(d,q);
		this.normalizeTFs(tfs, d, q);
		Map<String,Double> tfQuery = getQueryFreqs(q);

		// Write out the tuned BM25 parameters
    // This is only used for grading purposes.
		// You should NOT modify the writeParaValues method.
		writeParaValues("bm25Para.txt");
		return getNetScore(tfs,q,tfQuery,d);
	}
	
}

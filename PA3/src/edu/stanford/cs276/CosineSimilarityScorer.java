package edu.stanford.cs276;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Skeleton code for the implementation of a 
 * Cosine Similarity Scorer in Task 1.
 */
public class CosineSimilarityScorer extends AScorer {

	/*
	 *  TODO: You will want to tune the values for
   *        the weights for each field.
	 */
  	double urlweight = 0.1;
  	double titleweight  = 0.1;
  	double bodyweight = 0.1;
  	double headerweight = 0.1;
  	double anchorweight = 0.1;
	  double smoothingBodyLength = 1.0; 

  /**
    * Construct a Cosine Similarity Scorer.
    * @param idfs the map of idf values
    */
	public CosineSimilarityScorer(Map<String,Double> idfs) {
		super(idfs);
	}

  /**
    * Get the net score for a query and a document.
    * @param tfs the term frequencies
    * @param q the Query
    * @param tfQuery the term frequencies for the query
    * @param d the Document
    * @return the net score
    */
	public double getNetScore(Map<String, Map<String, Double>> tfs, Query q, Map<String,Double> tfQuery, Document d) {
		double score = 0.0;
		
		/*
		 * TODO : Your code here
     *        See Equation 2 in the handout regarding the net score
     *        between a query vector and the term score vectors
     *        for a document.
		 */
		
		return score;
	}

  /**
	  * Normalize the term frequencies. 
    * @param tfs the term frequencies
    * @param d the Document
    * @param q the Query
    */
	public void normalizeTFs(Map<String,Map<String, Double>> tfs,Document d, Query q) {
		/*
		 * TODO : Your code here
     *        Note that we should give uniform normalization to all 
     *        fields as discussed in the assignment handout.
		 */
	}
	
	/**
	 * Write the tuned parameters of cosineSimilarity to file.
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
        "headerweight", "anchorweight", "smoothingBodyLength"};
			double[] values = {this.urlweight, this.titleweight, this.bodyweight, 
        this.headerweight, this.anchorweight, this.smoothingBodyLength};
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
  /** Get the similarity score between a document and a query.
    * @param d the Document
    * @param q the Query
    * @return the similarity score.
    */
	public double getSimScore(Document d, Query q) {
		Map<String,Map<String, Double>> tfs = this.getDocTermFreqs(d,q);
		this.normalizeTFs(tfs, d, q);
		Map<String,Double> tfQuery = getQueryFreqs(q);

		// Write out tuned cosineSimilarity parameters
    // This is only used for grading purposes.
		// You should NOT modify the writeParaValues method.
		writeParaValues("cosinePara.txt");
		return getNetScore(tfs,q,tfQuery,d);
	}
}

package edu.stanford.cs276;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Comparator;

import edu.stanford.cs276.util.Pair;

/**
 * The entry class for this programming assignment.
 */
public class Rank {

	/**
	 * Call this function to score and rank documents for some queries, 
	   * using a specified scoring function.
	   * @param queryDict
	   * @param scoreType
	   * @param idfs
	   * @return a mapping of queries to rankings
		 */
	private static Map<Query,List<String>> score(Map<Query,Map<String, Document>> queryDict, String scoreType,
			Map<String,Double> idfs) {
		AScorer scorer = null;
		if (scoreType.equals("baseline"))
			scorer = new BaselineScorer();
		else if (scoreType.equals("cosine"))
			scorer = new CosineSimilarityScorer(idfs);
		else if (scoreType.equals("bm25"))
			scorer = new BM25Scorer(idfs, queryDict);
		else if (scoreType.equals("window"))
			// Feel free to change this if your smallest window extends cosine class.
			scorer = new SmallestWindowScorer(idfs, queryDict);	
		else if (scoreType.equals("extra"))
			scorer = new ExtraCreditScorer(idfs);

		// Put completed rankings here
		Map<Query,List<String>> queryRankings = new HashMap<Query,List<String>>();
		
		// Loop through urls for query, getting scores
		for (Query query : queryDict.keySet()) {
			List<Pair<String,Double>> urlAndScores = new ArrayList<Pair<String,Double>>(queryDict.get(query).size());
			for (String url : queryDict.get(query).keySet()) {
				double score = scorer.getSimScore(queryDict.get(query).get(url), query);
				urlAndScores.add(new Pair<String,Double>(url,score));
			}

			/* Sort urls for query based on scores. */
			Collections.sort(urlAndScores, new Comparator<Pair<String,Double>>() {
				@Override
				public int compare(Pair<String, Double> o1, Pair<String, Double> o2) {
					/*
					 * TODO : Your code here
           *        Define a custom compare function to help sort urls
           *        urls for a query based on scores.
					 */
					return -1;
				}	
			});
			
			/* Put completed rankings into map. */
			List<String> curRankings = new ArrayList<String>();
			for (Pair<String,Double> urlAndScore : urlAndScores)
				curRankings.add(urlAndScore.getFirst());
			queryRankings.put(query, curRankings);
		}
		return queryRankings;
	}

  /**
    * Print ranked results.
    * @param queryRankings the mapping of queries to rankings
    */
	public static void printRankedResults(Map<Query,List<String>> queryRankings) {
		for (Query query : queryRankings.keySet()) {
			StringBuilder queryBuilder = new StringBuilder();
			for (String s : query.queryWords) {
				queryBuilder.append(s);
				queryBuilder.append(" ");
			}
			System.out.println("query: " + queryBuilder.toString());
			for (String res : queryRankings.get(query))
				System.out.println("  url: " + res);
		}	
	}
	
  /**
    * Writes ranked results to file.
    * @param queryRankings the mapping of queries to rankings
    * @param outputFilePath the destination file path
    */
	public static void writeRankedResultsToFile(Map<Query,List<String>> queryRankings,String outputFilePath) {
		try {
			File file = new File(outputFilePath);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			for (Query query : queryRankings.keySet()) {
				StringBuilder queryBuilder = new StringBuilder();
				for (String s : query.queryWords) {
					queryBuilder.append(s);
					queryBuilder.append(" ");
				}
				String queryStr = "query: " + queryBuilder.toString() + "\n";
				bw.write(queryStr);
				for (String res : queryRankings.get(query)) {
					String urlString = "  url: " + res + "\n";
					bw.write(urlString);
				}
			}	
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	  * Main method for Rank.
	  * args[0] : signal files for ranking query, url pairs
	  * args[1] : ranking function choice from {baseline, cosine, bm25, extra, window}
	  * args[2] : PA1 corpus to build idf values (when args[3] is true), or existing idfs file to load (when args[3] is false)
	  * args[3] : true: build from PA1 corpus, false: load from existing idfs.
	  */
	public static void main(String[] args) throws IOException {
		if (args.length != 4) {
			System.err.println("Incorrect number of arguments: <sigFile> <taskOption> <idfPath> <buildFlag>");
			return;
		}
		
		/* sigFile : args[0], path for signal file. */
		String sigPath = args[0]; 
		/* taskOption : args[1], baseline, cosine (Task 1), bm25 (Task 2), window (Task 3), or extra (Extra Credit). */
		String taskOption = args[1];
		/*  idfPath : args[2].
		 	When args[3] is "true", set this as your PA1 corpus path.
		 	When args[3] is "false", set this as your existing idfs file.
		 */
		String idfPath = args[2];
		/* buildFlag : args[3].
		   Set to "true", will build idf from PA1 corpus.
		   Set to "false", load from existing idfs file.
		 */
		String buildFlag = args[3];
		
		/* start building or loading idfs information. */
		Map<String, Double> idfs = null;
		/* idfFile you want to dump or already stored. */
		String idfFile = "idfs";
		if (buildFlag.equals("true")) {
			/* Finish this method in LoadHandler.java Class. */
			idfs = LoadHandler.buildDFs(idfPath, idfFile);
		} else {
			idfs = LoadHandler.loadDFs(idfPath);
		}
				
		if (!(taskOption.equals("baseline") || taskOption.equals("cosine") || taskOption.equals("bm25")
				|| taskOption.equals("extra") || taskOption.equals("window"))) {
			System.err.println("Invalid scoring type; should be either 'baseline', 'bm25', 'cosine', 'window', or 'extra'");
			return;
		}
			
		/* start loading query pages to be ranked. */
		Map<Query,Map<String, Document>> queryDict = null;
		/* Populate map with features from file */
		try {
			queryDict = LoadHandler.loadTrainData(args[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/* score documents for queries */
		Map<Query,List<String>> queryRankings = score(queryDict, taskOption, idfs);
		
		//print results and save them to file "ranked.txt" (to run with NdcgMain.java)
		String outputFilePath = "ranked.txt";
		writeRankedResultsToFile(queryRankings,outputFilePath);
		
		/* print out ranking result, keep this stdout format in your submission */
		printRankedResults(queryRankings);
	}
}

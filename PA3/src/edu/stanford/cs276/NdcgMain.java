package edu.stanford.cs276;

import java.io.*;
import java.util.*;

/**
 * This is the class you can use to evaluate 
 * your ranking function on the training data.
 * You DO NOT need to modify this class.
 */
public class NdcgMain {
	public static void main(String[] args) throws IOException {
		if (args.length < 2) {
			System.out.println("Please specify two files: (i) the ranked input file and (ii) the input file containing the " +
					"relevance scores.");
			System.exit(1);
		}
		
		HashMap<Integer, Double> relevScores = new HashMap<Integer, Double>();
		BufferedReader br = new BufferedReader(new FileReader(args[1]));
		String strLine;
		String query = "";
		while ((strLine = br.readLine()) != null) {
			if (strLine.trim().charAt(0) == 'q') {
				query = strLine.substring(strLine.indexOf(":")+1).trim();
			}	
			else {
				String[] tokens = strLine.substring(strLine.indexOf(":")+1).trim().split(" ");
				String url = tokens[0].trim();
				double relevance = Double.parseDouble(tokens[1]);
				if (relevance < 0)
					relevance = 0;
				relevScores.put(query.hashCode() + url.hashCode(), relevance);
			}
		}
		br.close();
		
		br = new BufferedReader(new FileReader(args[0]));
		int totalQueries = 0;
		ArrayList<Double> rels = new ArrayList<Double>();
		double totalSum = 0;
		
		while ((strLine = br.readLine()) != null) {
			if (strLine.trim().charAt(0) == 'q') {
				if (rels.size() > 0) {
					totalSum = getNdcgQuery(rels, totalSum);
					rels.clear();
				}
				query = strLine.substring(strLine.indexOf(":")+1).trim();
				totalQueries++;
			}	
			else {
				String url = strLine.substring(strLine.indexOf(":")+1).trim();
				if (relevScores.containsKey(query.hashCode() + url.hashCode())) {
					double relevance = relevScores.get(query.hashCode() + url.hashCode());
					rels.add(relevance);
				}
				else {
					System.err.printf("Warning. Cannot find query %s with url %s in %s. Ignoring this line.\n", query, url, args[1]);
				}
			}
		}
		br.close();
		if (rels.size() > 0) {
			totalSum = getNdcgQuery(rels, totalSum);
		}
		
		System.out.println(totalSum/totalQueries);
	}

	private static double getNdcgQuery(ArrayList<Double> rels, double totalSum) {
		double localSum = 0, sortedSum = 0;
		for (int i = 0; i < rels.size(); i++)
			localSum += (Math.pow(2, rels.get(i))-1)/(Math.log(1+i+1)/Math.log(2));
		Collections.sort(rels, Collections.reverseOrder());
		for (int i = 0; i < rels.size(); i++)
			sortedSum += (Math.pow(2, rels.get(i))-1)/(Math.log(1+i+1)/Math.log(2));
		if (sortedSum == 0)
			totalSum += 1;
		else
			totalSum += localSum/sortedSum;
		return totalSum;
	}
}

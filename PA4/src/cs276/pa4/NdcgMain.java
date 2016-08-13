package cs276.pa4;

import java.io.*;
import java.util.*;

public class NdcgMain {
  private HashMap<Integer, Double> relevantScores;

  public NdcgMain(String relFile) throws IOException {
    relevantScores = new HashMap<Integer, Double>();

    BufferedReader br = new BufferedReader(new FileReader(relFile));
    String strLine;
    String query = "";
    while ((strLine = br.readLine()) != null) {
      if (strLine.trim().charAt(0) == 'q') {
        query = strLine.substring(strLine.indexOf(":") + 1).trim();
      } else {
        String[] tokens =
            strLine.substring(strLine.indexOf(":") + 1).trim().split(" ");
        String url = tokens[0].trim();
        double relevance = Double.parseDouble(tokens[1]);
        if (relevance < 0)
          relevance = 0;
        relevantScores.put(query.hashCode() + url.hashCode(), relevance);
      }
    }
    br.close();
  }

  private double getNdcgQuery(ArrayList<Double> rels, double totalSum) {
    double localSum = 0, sortedSum = 0;
    for (int i = 0; i < rels.size(); i++)
      localSum +=
          (Math.pow(2, rels.get(i)) - 1) / (Math.log(1 + i + 1) / Math.log(2));
    Collections.sort(rels, Collections.reverseOrder());
    for (int i = 0; i < rels.size(); i++)
      sortedSum +=
          (Math.pow(2, rels.get(i)) - 1) / (Math.log(1 + i + 1) / Math.log(2));
    if (sortedSum == 0)
      totalSum += 1;
    else
      totalSum += localSum / sortedSum;
    return totalSum;
  }

  public double score(String rankedFile) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(rankedFile));
    int totalQueries = 0;
    ArrayList<Double> rels = new ArrayList<Double>();
    double totalSum = 0;
    String strLine;
    String query = "";
    while ((strLine = br.readLine()) != null) {
      if (strLine.trim().charAt(0) == 'q') {
        if (rels.size() > 0) {
          totalSum = getNdcgQuery(rels, totalSum);
          rels.clear();
        }
        query = strLine.substring(strLine.indexOf(":") + 1).trim();
        totalQueries++;
      } else {
        String url = strLine.substring(strLine.indexOf(":") + 1).trim();
        if (relevantScores.containsKey(query.hashCode() + url.hashCode())) {
          double relevance =
              relevantScores.get(query.hashCode() + url.hashCode());
          rels.add(relevance);
        } else {
          System.err.printf(
              "Warning. Cannot find query %s with url %s. Ignoring this line.\n",
              query, url);
        }
      }
    }
    br.close();
    if (rels.size() > 0) {
      totalSum = getNdcgQuery(rels, totalSum);
    }

    return totalSum / totalQueries;
  }

  public static void main(String[] args) throws IOException {
    if (args.length < 2) {
      System.out.println(
          "Please specify two files: (i) the ranked input file and (ii) the input file containing the "
              + "relevance scores.");
      System.exit(1);
    }
    String rankedFile = args[0];
    String relFile = args[1];
    NdcgMain ndcg = new NdcgMain(relFile);
    System.err.println(ndcg.score(rankedFile));
  }
}
